package shared;

import java.util.ArrayList;

public class Gebaeude {
    // Attribute
    private String name;
    private ArrayList<Raum> raeume;
    
    // Konstruktor
    public Gebaeude(String name){
        this.name = name;
        this.raeume = new ArrayList<>();
    }

    // Methoden
    // Getter und Setter
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void addRaum(Raum raum) {
        this.raeume.add(raum);
    }

    public Raum getRaum(String name) {
        // return Raum mit dem übergebenen Namen
        for (Raum raum : this.raeume) {
            if (raum.getName().equals(name)) {
                return raum;
            }
        }
        return null;
    }
}
