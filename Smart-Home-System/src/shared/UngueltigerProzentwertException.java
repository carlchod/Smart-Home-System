package shared;

public class UngueltigerProzentwertException extends IllegalArgumentException {
    public UngueltigerProzentwertException(double prozent) {
        super("Der Wert " + prozent + "% ist ungültig! Eine Jalousie kann nur Werte von 0.0 bis 100.0 Prozent annehmen.");
    }
}