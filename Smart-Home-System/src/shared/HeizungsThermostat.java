package shared;

public class HeizungsThermostat extends SmartDevice implements Schaltbar{
    // Attribute
    private double zielTemperatur;
    private double aktuelleTemperatur;

    // Konstruktor
    public HeizungsThermostat(String name, double zielTemperatur, double aktuelleTemperatur) {
        super(name);
        this.zielTemperatur = zielTemperatur;
        this.aktuelleTemperatur = aktuelleTemperatur;
    }

    // Methoden
    public void schalte() {
        if (aktuelleTemperatur < zielTemperatur) {
            aktuelleTemperatur += 0.5; // Heizung erhöht die Temperatur
        } else if (aktuelleTemperatur > zielTemperatur) {
            aktuelleTemperatur -= 0.5; // Heizung senkt die Temperatur
        }
    }

    public void waermer() {
        aktuelleTemperatur += 0.5; // Heizung erhöht die Temperatur
    }

    public void kuehler() {
        aktuelleTemperatur -= 0.5; // Heizung senkt die Temperatur
    }

    public boolean getStatusAsBoolean() {
        return aktuelleTemperatur < zielTemperatur;
    }

    public String getStatusAsString() {
        return String.format("[] Ziel Temperatur: %.1f°C, Aktuelle Temperatur: %.1f°C", zielTemperatur, aktuelleTemperatur);
    }

    // Getter und Setter
    public void setZielTemperatur(double zielTemperatur) {
        this.zielTemperatur = zielTemperatur;
    }
    
    public double getZielTemperatur() {
        return zielTemperatur;
    }

    public void setAktuelleTemperatur(double aktuelleTemperatur) {
        this.aktuelleTemperatur = aktuelleTemperatur;
    }

    public double getAktuelleTemperatur() {
        return aktuelleTemperatur;
    }
}