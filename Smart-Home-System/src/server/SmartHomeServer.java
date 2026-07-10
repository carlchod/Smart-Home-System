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
        if ("superadmin".equals(passwort)) {
            return Rolle.ADMIN;
        } else if ("bewohner".equals(passwort)) {
            return Rolle.BEDIENER;
        } else {
            return Rolle.GAST; // falsches/leeres Passwort = Gast
        }
    }

    public String befehlAusfuehren(Rolle rolle, String raumName, String geraetName, String befehl, String wert) throws RemoteException {
        befehl = befehl.toLowerCase();

        if (rolle == Rolle.GAST) {
            return "Sicherheits-Fehler: Zugriff verweigert! Als GAST haben sie keine Berechtigung, Befehle auszuführen.";
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