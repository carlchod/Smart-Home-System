package client;

import server.SmartHomeService;
import shared.Raum;
import shared.SmartDevice;

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
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
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
                prompt = "SmartHome> ";
            } else {
                prompt = "SmartHome/" + aktuellerRaumKontext + "> ";
            }
            System.out.print(prompt); // Prompt wo der User sich befindet ausgeben
            
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
                    System.out.println("Bitte wechseln Sie zuerst in einen Raum, um die Geräte aufzulisten.");
                } else {
                    zeigeGeraete(aktuellerRaumKontext);
                }
                break;
            case "cd":
                break; // TODO: Implementieren
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

    private void zeigeHilfe() {
        System.out.println("\n--- VERFÜGBARE BEFEHLE ---");
        System.out.println("cd <raum>           : Wechselt in einen Raum (z.B. cd wohnzimmer)");
        System.out.println("cd ..               : Verlässt den aktuellen Raum");
        System.out.println("ls                  : Listet alle Geräte im aktuellen Raum auf");
        System.out.println("schalte <gerät>     : Schaltet ein Gerät an/aus (z.B. schalte Deckenlampe)");
        System.out.println("set <gerät> <wert>  : Setzt einen Wert (z.B. set Heizung-Sofa 22.5)");
        System.out.println("exit                : Beendet den Client\n");
    }

    public static void main(String[] args) {
        CLIClient client = new CLIClient();
        client.mainLoop();
    }
}