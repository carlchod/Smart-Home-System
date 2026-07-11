package shared;

import java.util.ArrayList;
import java.io.Serializable;

public class Raum implements Serializable {
    // Attribute
    private static final long serialVersionUID = 1L;

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
        if (geraet != null && geraet.getName() != null) {
            for (SmartDevice vorhandenesGeraet : this.geraete) {
                if (vorhandenesGeraet.getName().equalsIgnoreCase(geraet.getName())) {
                    throw new GeraetExistiertBereitsException(geraet.getName());
                }
            }
            this.geraete.add(geraet);
        }
    }

    // nur um einzelnes Gerät zu bekommen
    public SmartDevice getGeraet(int index) {
        return this.geraete.get(index);
    }
    // um alle Geräte zu bekommen
    public ArrayList<SmartDevice> getGeraete() {
        return geraete;
    }
}