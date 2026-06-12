package shared;

public class Lichtschalter extends SmartDevice implements Schaltbar {
    // Attribute
    private boolean status;
    
    // Konstruktor
    public Lichtschalter(String name) {
        super(name);
        this.status = false; // standardmäßig ausgeschaltet
    }
    
    // Methoden
    public void schalte() {
        this.status = !this.status; // Status wechseln
    }

    public boolean getStatusAsBoolean() {
        return this.status;
    }

    public String getStatusAsString() {
        if (this.status) {
            return "[] Eingeschaltet";
        } else {
            return "[] Ausgeschaltet";
        }
    }
    // Getter und Setter
}