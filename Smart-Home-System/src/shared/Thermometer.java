package shared;

import java.util.Random;

public class Thermometer extends SensorDevice {
    private double basisTemperatur;
    private Random random;

    public Thermometer(String name) {
        super(name);
        this.basisTemperatur = 21.0; // Standardwert
        this.random = new Random();
    }

    public String getStatusAsString() {
        double schwankung = (random.nextDouble() * 0.6) - 0.3;
        double aktuell = this.basisTemperatur + schwankung;
        
        // runden auf eine Nachkommastelle
        double gerundet = Math.round(aktuell * 10.0) / 10.0; 
        return "[*] Gemessene Raumtemperatur: " + gerundet + "°C";
    }
}