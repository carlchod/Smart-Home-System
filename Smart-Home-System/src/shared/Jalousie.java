package shared;

public class Jalousie extends SmartDevice {
    // Attribute
    private double oeffnungsGrad; // 0.0 = offen, 100.0 = geschlossen

    // Konstruktor
    public Jalousie(String name) {
        super(name);
        this.oeffnungsGrad = 0.0; // standardmäßig ganz offen
    }

    // Getter und Setter
    public void setOeffnungsGrad(double oeffnungsGrad) {
        if (oeffnungsGrad < 0.0 || oeffnungsGrad > 100.0) {
            throw new UngueltigerProzentwertException(oeffnungsGrad);
        }
        this.oeffnungsGrad = oeffnungsGrad;
    }

    public double getOeffnungsGrad() {
        return this.oeffnungsGrad;
    }

    public String getStatusAsString() {
        if (oeffnungsGrad == 0.0) {
            return "[=] Geöffnet (0.0%)";
        } else if (oeffnungsGrad == 100.0) {
            return "[|] Geschlossen (100.0%)";
        } else {
            return "[/] Halb offen (" + oeffnungsGrad + "%)";
        }
    }
}