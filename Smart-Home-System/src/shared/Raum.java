package shared;

import java.util.ArrayList;

public class Raum {
    // Attribute
    private String name;
    private ArrayList<SmartDevice> geraete;
    
    // Konstruktor
    public Raum(String name) {
        this.name = name;
        this.geraete = new ArrayList<>();
    }

    // Methoden
    // Getter und Setter
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addGeraet(SmartDevice geraet) {
        this.geraete.add(geraet);
    }

    public SmartDevice getGeraet(int index) {
        return this.geraete.get(index);
    }
}