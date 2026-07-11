package shared;

public class Thermometer extends SensorDevice {
    private double basisTemperatur;
    
    // Das Random-Attribut wurde komplett entfernt!

    public Thermometer(String name) {
        super(name);
        this.basisTemperatur = 21.0; // Standardwert
    }

    public String getStatusAsString() {
        double schwankung = (Math.random() * 0.6) - 0.3;
        double aktuell = this.basisTemperatur + schwankung;
        
        // Runden auf eine Nachkommastelle
        double gerundet = Math.round(aktuell * 10.0) / 10.0; 
        return "[*] Gemessene Raumtemperatur: " + gerundet + "°C";
    }
}