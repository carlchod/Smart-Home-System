package server;

import shared.Gebaeude;

// Gson von Google genutzt
// wird viel verwendet und wir wollten nicht auf Maven oder Gradle umsteigen
// daher hier die externe Bibliothek als JAR-Datei im Projektordner lib
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PersistenzManager {
    // Attribute
    private static final String DATEI_NAME = "smart_home_data.json";
    
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // Methoden
    public void speichereGebaeude(Gebaeude gebaeude) {
        try (FileWriter writer = new FileWriter(DATEI_NAME)) {
            gson.toJson(gebaeude, writer);
            System.out.println("Erfolg: Systemzustand erfolgreich in '" + DATEI_NAME + "' gespeichert.");
        } catch (IOException e) {
            System.err.println("Fehler: Speichern des Systemzustands in '" + DATEI_NAME + "' fehlgeschlagen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Gebaeude ladeGebaeude() {
        File datei = new File(DATEI_NAME);
        if (!datei.exists()) {
            System.out.println("Info: Keine gespeicherten JSON-Daten gefunden. Es wird ein neues Gebäude erstellt.");
            return new Gebaeude("leer"); 
        }

        try (FileReader reader = new FileReader(datei)) {
            Gebaeude gebaeude = gson.fromJson(reader, Gebaeude.class);
            System.out.println("Erfolg: Systemzustand erfolgreich aus '" + DATEI_NAME + "' geladen.");
            return gebaeude;
        } catch (IOException e) {
            System.err.println("Fehler: Laden des Systemzustands aus '" + DATEI_NAME + "' fehlgeschlagen: " + e.getMessage());
            e.printStackTrace();
            return new Gebaeude("leer"); 
        }
    }
}