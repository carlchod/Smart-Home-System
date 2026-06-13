package shared;

public class HeizungsThermostat extends SmartDevice implements Schaltbar{
    // Attribute ---
    private double zielTemperatur;
    private double aktuelleTemperatur;

    // Konstruktor ---
    public HeizungsThermostat(String name, double zielTemperatur, double aktuelleTemperatur) {
        super(name);
        if (zielTemperatur < 0.5 || zielTemperatur > 30.0) {
            throw new IllegalArgumentException("Zieltemperatur muss zwischen 0.5 und 30.0 Grad Celsius liegen.");
        }
        else {
            this.zielTemperatur = zielTemperatur;
        }
        this.aktuelleTemperatur = aktuelleTemperatur;
    }

    public HeizungsThermostat(String name, double zielTemperatur) {
        super(name);
        this.zielTemperatur = zielTemperatur;
    }

    // Methoden ---
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

    // Getter und Setter ---
    public void setZielTemperatur(double zielTemperatur) {
        if (zielTemperatur < 0.5 || zielTemperatur > 30.0) {
            throw new IllegalArgumentException("Zieltemperatur muss zwischen 0.5 und 30.0 Grad Celsius liegen.");
        }
        else {
            this.zielTemperatur = zielTemperatur;
        }
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