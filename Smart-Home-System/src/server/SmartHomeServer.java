package server;

import shared.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class SmartHomeServer extends UnicastRemoteObject implements SmartHomeService {
    // Attribute
    private static final long serialVersionUID = 1L;

    private Gebaeude meinGebaeude;
    private final PersistenzManager persistenzManager;

    // Konstruktor
    public SmartHomeServer() throws RemoteException {
        super();
        this.persistenzManager = new PersistenzManager();
        this.meinGebaeude = persistenzManager.ladeGebaeude();
        if (meinGebaeude.getRaeume().isEmpty()) {
            initDummyData(); // Initialisiert Testdaten, wenn keine Daten vorhanden sind
        }
    }

    public static void main(String[] args) {
        try {
            SmartHomeServer smhServer = new SmartHomeServer();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("smhServer", smhServer);
            System.out.println("Smart Home Server gestartet und im RMI-Registry registriert.");
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

    public String befehlAusfuehren(String raumName, String geraetName, String befehl, String wert) throws RemoteException {
        Raum raum = meinGebaeude.getRaum(raumName);
        if (raum == null) {
            return "Fehler:Raum '" + raumName + "' nicht gefunden.";
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

        if (zielGeraet instanceof Schaltbar) {
            if(befehl.equals("toggle")) {
                Schaltbar schaltGeraet = (Schaltbar) zielGeraet;
                schaltGeraet.schalte();
                return "Erfolg: " + zielGeraet.getName() + " im Raum '" + raumName + "' wurde geschaltet.\nNeuer Status: " + zielGeraet.getStatusAsString();
            }
            else {
            return "Fehler: Gerät '" + geraetName + "' im Raum '" + raumName + "' ist nicht schaltbar.";
            }
        }
        else if ( zielGeraet instanceof HeizungsThermostat) {
            if (befehl.equals("set")) {
                try {
                    double temperatur = Double.parseDouble(wert);
                    HeizungsThermostat thermostat = (HeizungsThermostat) zielGeraet;
                    thermostat.setZielTemperatur(temperatur);
                    return "Erfolg: Temperatur für " + zielGeraet.getName() + " auf " + temperatur + "°C gesetzt.";
                } catch (NumberFormatException e) {
                    return "Fehler: Ungültige Temperaturangabe.";
                }
            }
            else {
                return "Fehler: Für Heizungen ist nur der Befehl 'set <Wert>' erlaubt.";
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
        Gebaeude meinGebaeude = new Gebaeude("Mein Smart Home Gebaude");
        // 2. Räume instanzieren
        Raum meinRaum = new Raum("Wohnzimmer");
        // 3. Geräte instanzieren
        Lichtschalter meinLichtschalter = new Lichtschalter("Wohnzimmer Lichtschalter");
        HeizungsThermostat meinHeizungsThermostat = new HeizungsThermostat("Wohnzimmer Heizungs Thermostat", 22.0);
        // 4. Geräte zu Räumen hinzufügen
        meinRaum.addGeraet(meinLichtschalter);
        meinRaum.addGeraet(meinHeizungsThermostat);
        // 5. Räume zu Gebäude hinzufügen
        meinGebaeude.addRaum(meinRaum);
    }
    // Getter und Setter
}