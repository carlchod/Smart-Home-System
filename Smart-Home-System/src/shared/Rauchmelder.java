package shared;

public class Rauchmelder extends SensorDevice {
    private boolean alarmAktiv;

    public Rauchmelder(String name) {
        super(name);
        this.alarmAktiv = false; // standardmäßig kein Alarm
    }

    @Override
    public String getStatusAsString() {
        if (alarmAktiv) {
            return "[!] ALARM: Rauch erkannt!";
        } else {
            return "[v] OK: Kein Rauch erkannt";
        }
    }
}