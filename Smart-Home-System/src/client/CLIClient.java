package client;

import server.SmartHomeService;
import shared.Raum;
import shared.SmartDevice;

import static client.KonsoleDesign.*;

import java.rmi.registry.Registry;
import java.util.Scanner;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class CLIClient {
    // Attribute
    private SmartHomeService serverStub;
    private String aktuellerRaumKontext = null; // sichert aktuellen Raum für Befehle; null -> Gebäude-Übersicht anzeigen

    // Konstruktor
    public CLIClient() {
        verbindungHerstellen();
    }

    private void verbindungHerstellen() {
        try {
            System.out.println("Verbinde zum Smart-Home-Server...");
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1099); // "localhost" = 127.0.0.1
            serverStub = (SmartHomeService) registry.lookup("SmartHomeService");
            System.out.println("Erfolg: Verbindung zum Smart Home Server hergestellt.");
        } catch (Exception e) {
            System.err.println("Fehler: Verbindung zum Smart Home Server fehlgeschlagen: " + e.getMessage());
            e.printStackTrace();
            System.exit(1); // Beendet die Anwendung, wenn die Verbindung fehlschlägt
        }
    }

    // MainLoop: wartet auf Benutzereingaben und führt Befehle aus
    public void mainLoop() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Willkommen in Ihrem Smart Home CLI-Client!");
        System.out.println("Geben Sie 'help' ein, um eine Liste der verfügbaren Befehle zu erhalten.");

        while(true) {
            String prompt;
            
            if (aktuellerRaumKontext == null) {
                // Blauer Hintergrund, Weißer Text, Fett wenn im SmartHome "Verzeichnis"
                prompt =
                    BG_BLUE + BOLD + " SmartHome "
                    + RESET + BLUE + PL_ARROW
                    + RESET + " ";
            } else { // wenn Raum betreten wird neuer Prompt
                prompt =
                    BG_BLUE + BOLD + " SmartHome " + RESET
                    + BG_GREEN + BLUE + PL_ARROW + RESET
                    + BG_GREEN + BOLD + " " + aktuellerRaumKontext.toUpperCase() + " "
                    + RESET + GREEN + PL_ARROW
                    + RESET + " ";
            }
            System.out.print(prompt);

            String eingabe = scanner.nextLine(); // Eingabe einlesen
            if (eingabe.trim().isEmpty()) {
                continue; // Leere Eingaben ignorieren
            }

            // Eingaben in Befehl und Parameter aufteilen
            String[] teile = eingabe.split("\\s+", 2); // Aufteilen in Befehl und Rest
            String befehl = teile[0].toLowerCase(); // Befehl in lowercase

            if (befehl.equals("exit")) {
                System.out.println("Beende Smart Home CLI-Client. Auf Wiedersehen!");
                break; // Beendet die Schleife und damit die Anwendung
            }

            try {
                verarbeiteBefehl(befehl, teile);
            } catch (RemoteException e) {
                System.err.println("Fehler: Netzwerkfehler während Ausführung des Befehls: " + e.getMessage());
                e.printStackTrace();
                aktuellerRaumKontext = null; // Kontext zurücksetzen, um Fehler zu vermeiden
            } catch (Exception e) {
                System.err.println("Fehler: Fehler während Ausführung des Befehls: " + e.getMessage());
                e.printStackTrace();
            }
        }
        scanner.close();
    }

    // Befehle verarbeiten
    private void verarbeiteBefehl(String befehl, String[] teile) throws RemoteException {
        switch (befehl) {
            case "help":
                zeigeHilfe();
                break;
            case "list", "ls":
                if (aktuellerRaumKontext == null) {
                    zeigeRaeume();
                } else {
                    zeigeGeraete(aktuellerRaumKontext);
                }
                break;
            case "cd":
                wechselRaum(teile);
                break;
        }
    }

    private void wechselRaum(String[] teile) throws RemoteException {
        if (teile.length < 2) {
            System.out.println("Fehler: Kein Raumname angegeben. Nutzung: cd <raumname> oder cd ..");
            return;
        }
        String ziel = teile[1].trim().toLowerCase();
        if (ziel.equals("..")) {
            aktuellerRaumKontext = null; // Kontext zurücksetzen, um Gebäude-Übersicht anzuzeigen
            System.out.println("Sie haben den Raum verlassen. Sie befinden sich jetzt im Flur.");
        } else {
            Raum raum = serverStub.getRaum(ziel);
            if (raum == null) {
                System.out.println("Fehler: Raum '" + ziel + "' nicht gefunden.");
            } else {
                aktuellerRaumKontext = ziel; // Kontext auf neuen Raum setzen
                System.out.println("Sie haben den Raum '" + ziel + "' betreten.");
                zeigeGeraete(ziel);
            }
        }
    }

    private void zeigeGeraete(String raumName) throws RemoteException {
        Raum raum = serverStub.getRaum(raumName);
        if (raum == null) {
            return;
        }
        else if (raum.getGeraete().isEmpty()) {
            System.out.println("Der Raum '" + raumName + "' enthält keine Geräte.");
        }
        else {
            System.out.println("+-----------------+--------------------------------+");
            System.out.printf("| %-15s | %-30s |%n", "Gerätename", "Aktueller Status");
            System.out.println("+-----------------+--------------------------------+");

            for (SmartDevice device : raum.getGeraete()) {
                System.out.printf("| %-15s | %-30s |%n", device.getName(), device.getStatusAsString());
            }
            System.out.println("+-----------------+--------------------------------+");
        }
    }

    private void zeigeRaeume() throws RemoteException { // vollkommen AI
        // Wir holen uns das gesamte Gebäude-Objekt über RMI
        shared.Gebaeude gebaude = serverStub.getGebaeude();
        
        if (gebaude == null || gebaude.getRaeume().isEmpty()) {
            System.out.println("Das Gebäude ist aktuell leer. Es gibt keine Räume.");
            return;
        }

        System.out.println("\nSie befinden sich im Flur. Verfügbare Räume:");
        System.out.println("+--------------------------------+");
        System.out.printf("| %-30s |%n", "Raumname");
        System.out.println("+--------------------------------+");

        // Wir iterieren über alle Schlüssel (Raumnamen) der HashMap
        for (String raumName : gebaude.getRaeume().keySet()) {
            // Den ersten Buchstaben groß machen, damit es schöner aussieht (da wir sie in Kleinbuchstaben speichern)
            String anzeigeName = raumName.substring(0, 1).toUpperCase() + raumName.substring(1);
            System.out.printf("| %-30s |%n", anzeigeName);
        }
        System.out.println("+--------------------------------+");
        System.out.println("Tipp: Nutzen Sie 'cd <raumname>', um einen Raum zu betreten.\n");
    }

    private void zeigeHilfe() {
        System.out.println("\n--- VERFÜGBARE BEFEHLE ---");
        System.out.println("cd <raum>               : Wechselt in einen Raum");
        System.out.println("cd ..                   : Verlässt den aktuellen Raum");
        System.out.println("ls                      : Listet alle Geräte im aktuellen Raum auf");
        System.out.println("schalte <gerätname>     : Schaltet ein Gerät an/aus");
        System.out.println("set <gerätname> <wert>  : Setzt einen Wert");
        System.out.println("exit                    : Beendet den Client\n");
    }

    public static void main(String[] args) {
        CLIClient client = new CLIClient();
        client.mainLoop();
    }
}