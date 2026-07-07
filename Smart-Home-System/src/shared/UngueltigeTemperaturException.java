package shared;

public class UngueltigeTemperaturException extends IllegalArgumentException {
    // Konstruktor -> baut falschen Wert direkt in Fehlermeldung ein
    public UngueltigeTemperaturException(double versuchteTemperatur) {
        super("Die Zieltemperatur von " + versuchteTemperatur + "°C ist nicht erlaubt! (Nur 5.0 bis 30.0 Grad möglich).");
    }
}