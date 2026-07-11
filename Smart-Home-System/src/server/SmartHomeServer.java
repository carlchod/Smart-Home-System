package server;

import shared.Gebaeude;
import shared.HeizungsThermostat;
import shared.Lichtschalter;
import shared.Raum;
import shared.Rolle;
import shared.Schaltbar;
import shared.SmartDevice;
import shared.SmartHomeCallback;
import shared.SmartHomeService;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import java.util.ArrayList;
import java.util.List;

public class SmartHomeServer extends UnicastRemoteObject implements SmartHomeService {
    // Attribute
    private static final long serialVersionUID = 1L;

    private Gebaeude meinGebaeude;
    private final PersistenzManager persistenzManager;

    private List<SmartHomeCallback> registrierteClients;

    // Konstruktor
    public SmartHomeServer() throws RemoteException {
        super();
        this.persistenzManager = new PersistenzManager();
        this.registrierteClients = new ArrayList<>();
        this.meinGebaeude = persistenzManager.ladeGebaeude();
        if (meinGebaeude.getRaeume().isEmpty()) {
            initDummyData(); // Initialisiert Testdaten, wenn keine Daten vorhanden sind
        }
    }

    public static void main(String[] args) {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");

            SmartHomeServer smhServer = new SmartHomeServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("SmartHomeService", smhServer); // rebind() -> bind() um nicht zu überschreiben
            System.out.println("Smart Home Server gestartet und im RMI-Registry registriert.");

            // User Story 2
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nAbbruchsignal empfangen! Fahre Server sicher herunter...");
                smhServer.serverBeenden(); // Speichermethode aufrufen
            }));
        } catch (Exception e) {
            System.err.println("Server-Fehler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Methoden
    public Gebaeude getGebaeude() throws RemoteException {
        return meinGebaeude;
    }

    public Raum getRaum(String raumName) throws RemoteException {
        return this.meinGebaeude.getRaum(raumName);
    }

    public void registriereClient(SmartHomeCallback client) throws RemoteException {
        if (!registrierteClients.contains(client)) {
            registrierteClients.add(client);
            System.out.println("Info: Ein neuer Client hat sich für Updates registriert. (Gesamt: " + registrierteClients.size() + ")");
        }
    }

    public void meldeClientAb(SmartHomeCallback client) throws RemoteException {
        registrierteClients.remove(client);
        System.out.println("Info: Ein Client hat sich abgemeldet.");
    }

    private void sendeUpdateAnAlle(String nachricht) {
        // rückwärts iterieren, falls fehlerhafte Clients entfernt werden müssen
        for (int i = registrierteClients.size() - 1; i >= 0; i--) {
            try {
                registrierteClients.get(i).empfangeUpdate(nachricht);
            } catch (RemoteException e) {
                // wenn der Client nicht mehr erreichbar (Terminal geschlossen ohne exit oder Verbbindungsabbruch):
                System.out.println("Warnung: Client nicht erreichbar, wird aus der Liste entfernt.");
                registrierteClients.remove(i);
            }
        }
    }

    public Rolle login(String passwort) throws RemoteException {
        Rolle rolle;
        if ("superadmin".equals(passwort)) {
            rolle = Rolle.ADMIN;
        } else if ("bewohner".equals(passwort)) {
            rolle = Rolle.BEDIENER;
        } else {
            rolle = Rolle.GAST; 
        }

        ServerLogger.log(rolle, "Ein Benutzer hat sich angemeldet.");
        return rolle;
    }

    public String raumHinzufuegen(Rolle rolle, String raumName) throws RemoteException {
        ServerLogger.log(rolle, "Versucht den Raum '" + raumName + "' zu erstellen.");
        // Sicherheitsprüfung -> nur admins
        if (rolle != Rolle.ADMIN) {
            return "Sicherheits-Fehler: Zugriff verweigert! Nur Administratoren dürfen neue Räume anlegen.";
        }

        // Raum erstellen und einfügen
        try {
            Raum neuerRaum = new Raum(raumName);
            this.meinGebaeude.addRaum(neuerRaum); 
            // Die Methode addRaum wirft automatisch deine RaumExistiertBereitsException, 
            // falls jemand versucht, ein zweites "Wohnzimmer" anzulegen!
            
            return "Erfolg: Der Raum '" + raumName + "' wurde erfolgreich angelegt.";
        } catch (shared.RaumExistiertBereitsException e) {
            return "Fehler: " + e.getMessage();
        }
    }

    public String raumLoeschen(Rolle rolle, String raumName) throws RemoteException {
        ServerLogger.log(rolle, "Versucht den Raum '" + raumName + "' zu löschen.");
        // Sicherheitsprüfung -> nur admins
        if (rolle != Rolle.ADMIN) {
            return "Sicherheits-Fehler: Zugriff verweigert! Nur Admins dürfen Räume löschen.";
        }
        
        if (this.meinGebaeude.getRaeume().remove(raumName.toLowerCase()) != null) { // HashMap.remove gibt null zurück, wenn der Schlüssel existierte
            return "Erfolg: Der Raum '" + raumName + "' wurde unwiderruflich gelöscht.";
        } else {
            return "Fehler: Ein Raum mit dem Namen '" + raumName + "' wurde nicht gefunden.";
        }
    }

    public String geraetHinzufuegen(Rolle rolle, String raumName, String geraetTyp, String geraetName) throws RemoteException {
        ServerLogger.log(rolle, "Versucht das Gerät '" + geraetName + "' (" + geraetTyp + ") im Raum '" + raumName + "' zu installieren.");
        // Sicherheitsprüfung -> nur admins
        if (rolle != Rolle.ADMIN) {
            return "Sicherheits-Fehler: Zugriff verweigert! Nur Admins dürfen Geräte installieren.";
        }
        
        Raum raum = this.meinGebaeude.getRaum(raumName);
        if (raum == null) {
            return "Fehler: Der Raum '" + raumName + "' existiert nicht.";
        }

        shared.SmartDevice neuesGeraet;
        // Factory für mögliche Gerätetypen
        switch (geraetTyp.toLowerCase()) {
            case "Heizung", "heizung":
                neuesGeraet = new shared.HeizungsThermostat(geraetName, 20.0); // Standard: 20 Grad
                break;
            case "Licht", "licht":
                neuesGeraet = new shared.Lichtschalter(geraetName);
                break;
            case "Jalousie", "jalousie":
                neuesGeraet = new shared.Jalousie(geraetName);
                break;
            case "Thermometer", "thermometer":
                neuesGeraet = new shared.Thermometer(geraetName);
                break;
            case "Rauchmelder", "rauchmelder":
                neuesGeraet = new shared.Rauchmelder(geraetName);
                break;
            default:
                return "Fehler: Unbekannter Gerätetyp. Erlaubt sind: Heizung o. heizung, Licht o. licht, Jalousie o. jalousie.";
        }

        try {
            raum.addGeraet(neuesGeraet);
            return "Erfolg: Gerät '" + geraetName + "' (Typ: " + geraetTyp + ") im Raum '" + raumName + "' installiert.";
        } catch (shared.GeraetExistiertBereitsException e) {
            return e.getMessage(); 
        }
    }

    public String geraetLoeschen(Rolle rolle, String raumName, String geraetName) throws RemoteException {
        ServerLogger.log(rolle, "Versucht das Gerät '" + geraetName + "' im Raum '" + raumName + "' zu entfernen.");
        // Sicherheitsprüfung -> nur admins
        if (rolle != Rolle.ADMIN) {
            return "Sicherheits-Fehler: Zugriff verweigert! Nur Admins dürfen Geräte entfernen.";
        }

        Raum raum = this.meinGebaeude.getRaum(raumName);
        if (raum == null) {
            return "Fehler: Der Raum '" + raumName + "' existiert nicht.";
        }

        // entfernt Gerät anhand des Namens (case insensitive)
        boolean entfernt = raum.getGeraete().removeIf(g -> g.getName().equalsIgnoreCase(geraetName));
        
        if (entfernt) {
            return "Erfolg: Das Gerät '" + geraetName + "' wurde aus dem Raum entfernt.";
        } else {
            return "Fehler: Ein Gerät mit dem Namen '" + geraetName + "' wurde im Raum nicht gefunden.";
        }
    }

    public String befehlAusfuehren(Rolle rolle, String raumName, String geraetName, String befehl, String wert) throws RemoteException {
        if (befehl.equals("set")) {
            ServerLogger.log(rolle, "Versucht '" + geraetName + "' in '" + raumName + "' auf den Wert '" + wert + "' zu setzen.");
        } else {
            ServerLogger.log(rolle, "Versucht '" + geraetName + "' in '" + raumName + "' zu schalten (Befehl: " + befehl + ").");
        }
        
        Raum raum = meinGebaeude.getRaum(raumName);
        if (raum == null) {
            return "Fehler: Raum '" + raumName + "' nicht gefunden.";
        }

        SmartDevice zielGeraet = null;
        for (SmartDevice geraet : raum.getGeraete()) {
            if (geraet.getName().equalsIgnoreCase(geraetName)) {
                zielGeraet = geraet;
                break; // Gerät gefunden -> Schleife verlassen
            }
        }

        if (zielGeraet == null) {
            return "Fehler: Gerät '" + geraetName + "' wurde im Raum '" + raumName + "' nicht gefunden.";
        }

        befehl = befehl.toLowerCase();

        if (befehl.equals("toggle") || befehl.equals("schalte")) {
            if (zielGeraet instanceof Schaltbar) {
                Schaltbar schaltGeraet = (Schaltbar) zielGeraet;
                schaltGeraet.schalte();
                sendeUpdateAnAlle("Live-Update: " + zielGeraet.getName() + " wurde soeben geschaltet!");
                return "Erfolg: " + zielGeraet.getName() + " im Raum '" + raumName + "' wurde geschaltet.\nNeuer Status: " + zielGeraet.getStatusAsString();
            } else {
                return "Fehler: Das Gerät '" + geraetName + "' besitzt keinen Schalter.";
            }
        }
        else if (zielGeraet instanceof shared.SensorDevice) {
            return "Sicherheits-Fehler: Das Gerät '" + zielGeraet.getName() + "' ist ein Sensor. Sensoren messen nur Daten und können nicht manuell gesteuert werden!";
        }
        else if (zielGeraet instanceof HeizungsThermostat) {
            if (befehl.equals("set")) {
                try {
                    double temperatur = Double.parseDouble(wert);
                    HeizungsThermostat thermostat = (HeizungsThermostat) zielGeraet;
                    thermostat.setZielTemperatur(temperatur);
                    sendeUpdateAnAlle("Live-Update: " + zielGeraet.getName() + " wurde soeben geschaltet!");
                    return "Erfolg: Temperatur für " + zielGeraet.getName() + " auf " + temperatur + "°C gesetzt.";
                } catch (NumberFormatException e) {
                    return "Fehler: Ungültige Temperaturangabe.";
                }
            }
            else {
                return "Fehler: Für Heizungen ist nur der Befehl 'set <wert>' erlaubt.";
            }
        }
        else if (zielGeraet instanceof shared.Jalousie) {
            if (befehl.equals("set")) {
                try {
                    double prozent = Double.parseDouble(wert);
                    shared.Jalousie jalousie = (shared.Jalousie) zielGeraet;
                    
                    jalousie.setOeffnungsGrad(prozent); 
                    
                    return "Erfolg: Jalousie '" + zielGeraet.getName() + "' auf " + prozent + "% gesetzt.";
                } catch (NumberFormatException e) {
                    return "Fehler: Ungültige Prozentangabe. Bitte geben Sie eine Zahl ein (z.B. 50.0).";
                } catch (shared.UngueltigerProzentwertException e) {
                    return "Fehler: " + e.getMessage();
                }
            } else {
                return "Fehler: Für die Jalousie ist nur der Befehl 'set <wert>' erlaubt (z.B. set jalousie 50).";
            }
        }
        return "Fehler: Unbekannter Befehl oder Gerätetyp.";
    }

    public void serverBeenden() {
        System.out.println("Sichere Zustand vor dem Beenden...");
        persistenzManager.speichereGebaeude(meinGebaeude);
    }

    private void initDummyData() {
        // Test Daten für Live Demo
        // 1. Gebäude instanzieren
        this.meinGebaeude = new Gebaeude("Mein Smart Home Gebaude");
        // 2. Räume instanzieren
        Raum meinRaum = new Raum("Wohnzimmer");
        // 3. Geräte instanzieren
        Lichtschalter meinLichtschalter = new Lichtschalter("Wohnzimmer Lichtschalter");
        HeizungsThermostat meinHeizungsThermostat = new HeizungsThermostat("Wohnzimmer Heizungsthermostat", 22.0, 19.0);
        // 4. Geräte zu Räumen hinzufügen
        meinRaum.addGeraet(meinLichtschalter);
        meinRaum.addGeraet(meinHeizungsThermostat);
        // 5. Räume zu Gebäude hinzufügen
        meinGebaeude.addRaum(meinRaum);
    }
}