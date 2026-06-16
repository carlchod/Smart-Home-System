package shared;

public class HeizungsThermostat extends SmartDevice implements Schaltbar {
    // Attribute
    private double zielTemperatur;
    private double aktuelleTemperatur;
    private double letzteZielTemperatur = 5.0;
    private boolean frostschutzmodus = true; // Standardmäßig im Frostschutzmodus

    // Konstruktor
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

    // Methoden
    public void schalte() {
        if (frostschutzmodus) {
            setZielTemperatur(letzteZielTemperatur);
        }
        else {
            letzteZielTemperatur = zielTemperatur;
            setZielTemperatur(5.0);
        }
    }

    public void waermer() {
        if (zielTemperatur + 0.5 <= 30.0) {
            setZielTemperatur(zielTemperatur + 0.5);
        }
        if (frostschutzmodus) {
            frostschutzmodus = !frostschutzmodus;
        }
    }

    public void kuehler() {
        if (zielTemperatur - 0.5 >= 5.0) {
            setZielTemperatur(zielTemperatur - 0.5);
        }
    }

    public boolean getStatusAsBoolean() {
        return frostschutzmodus;
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
            heizStatus = "[Inaktiv]";
        }
        return String.format("%s Aktuell: %.1f°C", heizStatus, aktuelleTemperatur);
    }

    // Getter und Setter
    public void setZielTemperatur(double zielTemperatur) {
        if (zielTemperatur < 5.0 || zielTemperatur > 30.0) {
            throw new IllegalArgumentException("Zieltemperatur muss zwischen 5.0 und 30.0 Grad Celsius liegen.");
        }
        else {
            this.zielTemperatur = zielTemperatur;
        }

        if (this.zielTemperatur == 5.0) {
            this.frostschutzmodus = true;
        }
        else {
            this.frostschutzmodus = false;
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
    
    public double getLetzteTemperatur() {
        return letzteZielTemperatur;
    }
}