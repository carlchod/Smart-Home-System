package server;

import shared.Rolle;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLogger {
    private static final String LOG_FILE = "server_audit.log";
    // Format
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(Rolle rolle, String aktion) {
        String zeitstempel = LocalDateTime.now().format(FORMATTER);
        
        // Format: [ZEIT] [ROLLE] AKTION
        String logEintrag = String.format("[%s] [%s] %s", zeitstempel, rolle.name(), aktion);
        
        System.out.println(logEintrag); // in Konsole ausgeben
        
        // in txt Datei schreiben
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.println(logEintrag);
        } catch (IOException e) {
            System.err.println("Fehler beim Schreiben der Log-Datei: " + e.getMessage());
        }
    }
}