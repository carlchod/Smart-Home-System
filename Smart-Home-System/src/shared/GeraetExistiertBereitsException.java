package shared;

public class GeraetExistiertBereitsException extends RuntimeException {
    public GeraetExistiertBereitsException(String geraetName) {
        super("Fehler: Ein Gerät mit dem Namen '" + geraetName + "' existiert bereits in diesem Raum.");
    }
}