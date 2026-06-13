package server;

import shared.Gebaeude;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class PersistenzManager {
    // Attribute
    private static final String DATEI_NAME = "smart_home_data.dat";
    
    // Methoden
    public void speichereGebaeude(Gebaeude gebaeude) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATEI_NAME));
            oos.writeObject(gebaeude);
            oos.close();
            System.out.println("Erfolg: Systemzustand erfolgreich in '" + DATEI_NAME + "' gespeichert.");
        } catch (IOException e) {
            System.err.println("Fehler: Speichern des Systemzustands in '" + DATEI_NAME + "' fehlgeschlagen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Gebaeude ladeGebaeude() {
        File datei = new File(DATEI_NAME);
        if (!datei.exists()) {
            System.out.println("Info: Keine gespeicherten Daten gefunden. Es wird ein neues Gebäude erstellt.");
            return new Gebaeude("leer"); // Rückgabe eines leeren Gebäudes im Fall das keine Datei existiert
        }

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(datei));
            Gebaeude gebaeude = (Gebaeude) ois.readObject();
            ois.close();
            System.out.println("Erfolg: Systemzustand erfolgreich aus '" + DATEI_NAME + "' geladen.");
            return gebaeude;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Fehler: Laden des Systemzustands aus '" + DATEI_NAME + "' fehlgeschlagen: " + e.getMessage());
            e.printStackTrace();
            return new Gebaeude("leer"); // Rückgabe eines leeren Gebäudes im Fehlerfall
        }
    }
}
