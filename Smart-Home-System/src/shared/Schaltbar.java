package shared;

public interface Schaltbar {
    // Methoden
    /**
     * Schaltet das Gerät an und aus.
     * @return
     */
    public void schalte();
    
    /**
     * Boolschen Wert des Status erhalten.
     * @return boolean Wert des Status
     */
    public boolean getStatusAsBoolean();
}