package shared;

public class RaumExistiertBereitsException extends RuntimeException {
    public RaumExistiertBereitsException(String raumName) {
        super("Fehler: Ein Raum mit dem Namen '" + raumName + "' existiert bereits in diesem Gebäude.");
    }
}