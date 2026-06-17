package client;

import shared.Raum;
import shared.SmartDevice;
import shared.SmartHomeService;

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
        clearConsole();
        druckeHeader("HAUPTMENÜ");
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
            String[] argument = eingabe.split("\\s+", 2); // Aufteilen in Befehl und Rest
            String befehl = argument[0].toLowerCase(); // Befehl in lowercase

            if (befehl.equals("exit")) {
                System.out.println("Beende Smart Home CLI-Client. Auf Wiedersehen!");
                break; // Beendet die Schleife und damit die Anwendung
            }

            try {
                verarbeiteBefehl(befehl, argument);
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
    private void verarbeiteBefehl(String befehl, String[] argument) throws RemoteException {
        clearConsole();
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
                wechselRaum(argument);
                break;
            case "toggle", "schalte":
                schalteGeraet(argument);
                break;
            case "set":
                setzeGeraet(argument);
                break;
        }
    }

    private void wechselRaum(String[] argument) throws RemoteException {
        if (argument.length < 2) {
            System.out.println("Fehler: Kein Raumname angegeben. Nutzung: cd <raumname> oder cd ..");
            return;
        }
        String ziel = argument[1].trim().toLowerCase();
        if (ziel.equals("..")) {
            aktuellerRaumKontext = null; // Kontext zurücksetzen, um Gebäude-Übersicht anzuzeigen
            System.out.println("Sie haben den Raum verlassen.");
            druckeHeader("HAUPTMENÜ");
        } else {
            Raum raum = serverStub.getRaum(ziel);
            if (raum == null) {
                System.out.println("Fehler: Raum '" + ziel + "' nicht gefunden.");
            } else {
                aktuellerRaumKontext = ziel; // Kontext auf neuen Raum setzen
                System.out.println("Sie befinden sich jetzt im Raum '" + ziel + "'.");
                zeigeGeraete(ziel);
            }
        }
    }

    private void zeigeGeraete(String raumName) throws RemoteException {
        if (serverStub.getRaum(raumName) == null) {
            return;
        }
        else if (serverStub.getRaum(raumName).getGeraete().isEmpty()) {
            System.out.println("Der Raum '" + raumName + "' enthält keine Geräte.");
        }
        else {
            druckeHeader(raumName);
            System.out.printf(BLUE + BOLD + "|" + RESET + BOLD + " %-30s " + BLUE + "|" + RESET + BOLD + " %-64s" + BLUE + "|\n" + RESET, "Gerätename", "Aktueller Status");
            druckeHeader("");

            for (SmartDevice device : serverStub.getRaum(raumName).getGeraete()) {
                System.out.printf(BLUE + BOLD + "|" + RESET + " %-30s " + BLUE + BOLD + "|" + RESET + " %-64s" + BLUE + BOLD + "|\n", device.getName(), device.getStatusAsString());
            }
            druckeHeader("");
        }
    }

    private void zeigeRaeume() throws RemoteException {
        if (serverStub.getGebaeude() == null || serverStub.getGebaeude().getRaeume().isEmpty()) {
            System.out.println(YELLOW + "Das Gebäude ist aktuell leer. Es gibt keine Räume." + RESET);
            return;
        }

        druckeHeader("GEBÄUDE-ÜBERSICHT");
        System.out.printf(BLUE + "|" + BOLD + " %-97s" + RESET + BLUE + "|\n" + RESET, "Verfügbare Räume");
        druckeHeader("");

        // über alle Schlüssel (Raumnamen) der HashMap iterieren
        for (String raumName : serverStub.getGebaeude().getRaeume().keySet()) {
            // ersten Buchstaben groß machen, damit schöner aussieht
            String anzeigeName = raumName.substring(0, 1).toUpperCase() + raumName.substring(1);
            System.out.printf(BLUE + BOLD + "|" + RESET + BOLD + " %-71s " + BLUE + BOLD + "%25s|\n" + RESET, anzeigeName, "");
        }
        druckeHeader("");
        System.out.println("Tipp: Nutzen Sie 'cd <raumname>', um einen Raum zu betreten.\n");
    }

    private void schalteGeraet(String[] argument) throws RemoteException {
        if (aktuellerRaumKontext == null) {
            System.out.println(RED + "Fehler: Sie befinden sich in keinem Raum. Bitte wechseln Sie zuerst in einen Raum." + RESET);
            return;
        }
        else if (argument.length < 2 || argument[1].trim().isEmpty()) {
            System.out.println(RED + "Fehler: Kein Gerät angegeben. Nutzung: schalte <gerätname>" + RESET);
            return;
        }
        String antwort = serverStub.befehlAusfuehren(aktuellerRaumKontext, argument[1].trim(), argument[0].trim(), "");
        if (antwort.startsWith("Erfolg")) {
            System.out.println(GREEN + BOLD + antwort + RESET);
        } else if (antwort.startsWith("Fehler")) {
            System.out.println(RED + BOLD + antwort + RESET);
        } else {
            System.out.println(antwort); // falls der Server was anderes schickt
        }
    }

    private void setzeGeraet(String[] argument) throws RemoteException {
        if (aktuellerRaumKontext == null) {
            System.out.println(RED + "Fehler: Sie befinden sich in keinem Raum." + RESET);
            return;
        }
        else if (argument.length < 2 || argument[1].trim().isEmpty()) {
            System.out.println(RED + "Fehler: Kein Gerät/Wert angegeben. Nutzung: set <gerätname> <wert>" + RESET);
            return;
        }
        
        String eingabe = argument[1].trim();
        int letztesLeerzeichenIndex = eingabe.lastIndexOf(' ');

        if (letztesLeerzeichenIndex == -1) {
            System.out.println(RED + "Fehler: Es fehlt ein Wert. Nutzung: set <gerätname> <wert>" + RESET);
            return;
        }

        String geraetName = eingabe.substring(0, letztesLeerzeichenIndex).trim();
        String wert = eingabe.substring(letztesLeerzeichenIndex + 1).trim();

        try {
            Double.parseDouble(wert);
        } catch (NumberFormatException e) {
            System.out.println(RED + "Fehler: Der Wert '" + wert + "' ist keine gültige Zahl! Bitte verwenden Sie einen Punkt (z.B. 22.5)." + RESET);
            return; // Abbruch: Server wird gar nicht erst kontaktiert
        }

        String antwort = serverStub.befehlAusfuehren(aktuellerRaumKontext, geraetName, "set", wert);
        
        if (antwort.startsWith("Erfolg")) {
            System.out.println(GREEN + BOLD + antwort + RESET);
        }
        else if (antwort.startsWith("Fehler")) {
            System.out.println(RED + BOLD + antwort + RESET);
        }
        else {
            System.out.println(antwort);
        }
    }

    private void zeigeHilfe() {
        druckeHeader("VERFÜGBARE BEFEHLE");
        System.out.printf(BLUE + BOLD + "|" + RESET + " %-23s" + BLUE + BOLD + "|" + RESET + " %-72s" + BLUE + BOLD + "|\n" + RESET, "cd <raum>", "Wechselt in einen Raum");
        System.out.printf(BLUE + BOLD + "|" + RESET + " %-23s" + BLUE + BOLD + "|" + RESET + " %-72s" + BLUE + BOLD + "|\n" + RESET, "cd ..", "Verlässt den aktuellen Raum");
        System.out.printf(BLUE + BOLD + "|" + RESET + " %-23s" + BLUE + BOLD + "|" + RESET + " %-72s" + BLUE + BOLD + "|\n" + RESET, "ls", "Listet alle Geräte im aktuellen Raum auf");
        System.out.printf(BLUE + BOLD + "|" + RESET + " %-23s" + BLUE + BOLD + "|" + RESET + " %-72s" + BLUE + BOLD + "|\n" + RESET, "schalte <gerätname>", "Schaltet ein Gerät an/aus");
        System.out.printf(BLUE + BOLD + "|" + RESET + " %-23s" + BLUE + BOLD + "|" + RESET + " %-72s" + BLUE + BOLD + "|\n" + RESET, "set <gerätname> <wert>", "Setzt einen Wert");
        System.out.printf(BLUE + BOLD + "|" + RESET + " %-23s" + BLUE + BOLD + "|" + RESET + " %-72s" + BLUE + BOLD + "|\n" + RESET, "exit", "Beendet den Client");
        druckeHeader("");
    }

    public static void main(String[] args) {
        CLIClient client = new CLIClient();
        client.mainLoop();
    }
}