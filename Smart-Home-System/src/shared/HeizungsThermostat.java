package shared;

public class HeizungsThermostat extends SmartDevice implements Schaltbar {
    // Attribute ---
    private double zielTemperatur;
    private double aktuelleTemperatur;
    private boolean frostschutzmodus = true; // Standardmäßig im Frostschutzmodus

    // Konstruktor ---
    public HeizungsThermostat(String name, double zielTemperatur, double aktuelleTemperatur) {
        super(name);
        if (zielTemperatur < 5.0 || zielTemperatur > 30.0) {
            throw new IllegalArgumentException("Zieltemperatur muss zwischen 5.0 und 30.0 Grad Celsius liegen.");
        }
        else {
            this.zielTemperatur = zielTemperatur;
            this.frostschutzmodus = false;
        }
        this.aktuelleTemperatur = aktuelleTemperatur;
    }

    public HeizungsThermostat(String name, double zielTemperatur) {
        super(name);
        if (zielTemperatur < 5.0 || zielTemperatur > 30.0) {
            throw new IllegalArgumentException("Zieltemperatur muss zwischen 5.0 und 30.0 Grad Celsius liegen.");
        }
        else {
            this.zielTemperatur = zielTemperatur;
            this.frostschutzmodus = false;
        }
    }

    // Methoden ---
    public void schalte() {
        zielTemperatur = 5.0; // Heizung ausschalten (Frostschutzmodus)
        frostschutzmodus = true;
    }

    public void waermer() {
        if (zielTemperatur + 0.5 <= 30.0) {
            zielTemperatur += 0.5;
        }
        if (frostschutzmodus) {
            frostschutzmodus = !frostschutzmodus;
        }
    }

    public void kuehler() {
        if (zielTemperatur - 0.5 >= 5.0) {
            zielTemperatur -= 0.5; // Heizung senkt die Temperatur
        }
    }

    public boolean getStatusAsBoolean() {
        return aktuelleTemperatur < zielTemperatur;
    }

    public String getStatusAsString() {
        String heizStatus;
        if (this.zielTemperatur <= 5.0) {
            heizStatus = "[AUS / Frostschutz aktiviert]";
        }
        else if (this.aktuelleTemperatur < this.zielTemperatur) {
            heizStatus = "[Heize auf "+ zielTemperatur + "]";
        }
        else {
            heizStatus = "[Inaktiv aktuelle Temperatur == ziel Temperatur]";
        }
        return String.format("%s Ziel: %.1f°C | Aktuell: %.1f°C", heizStatus, zielTemperatur, aktuelleTemperatur);
    }

    // Getter und Setter ---
    public void setZielTemperatur(double zielTemperatur) {
        if (zielTemperatur < 5.0 || zielTemperatur > 30.0) {
            throw new IllegalArgumentException("Zieltemperatur muss zwischen 5.0 und 30.0 Grad Celsius liegen.");
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