package client;

// Hilfsklasse für Farben und Design der Konsole in CLIClient
public class KonsoleDesign {
    private KonsoleDesign() {} // -> kein "new ConsoleColor()" möglich

    // ANSI Farb-Codes
    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    
    // Textfarben
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
    
    // Hintergrundfarben
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_GRAY = "\u001B[47m";
    
    // Powerline Symbol
    public static final String PL_ARROW = "\uE0B0";
}
}
