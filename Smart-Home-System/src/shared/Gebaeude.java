package shared;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class Gebaeude implements Serializable {
    // Attribute
    private static final long serialVersionUID = 1L;

    private String name;
    // Aufbau: [[Raum1 Name: Raum-Objekt1],
    //          [Raum2 Name: Raum-Objekt2]...]
    private HashMap<String, Raum> raeume;

    //Aufbau: [[Benutzername1: [Szenenname1, Szenenname2, ...]],
    //          [Benutzername2: [Szenenname1, Szenenname2, ...]]...]
    private HashMap<String, List<String>> benutzerSzenen;
    
    // Konstruktor
    public Gebaeude(String name){
        this.name = name.toLowerCase();
        this.raeume = new HashMap<>();
        this.benutzerSzenen = new HashMap<>();
    }

    // Methoden
    public HashMap<String, List<String>> getBenutzerSzenen() {
        if (this.benutzerSzenen == null) {
            this.benutzerSzenen = new HashMap<>();
        }
        return this.benutzerSzenen;
    }
    
    // Getter und Setter
    public void setName(String name) {
        this.name = name.toLowerCase();
    }
    
    public String getName() {
        return name;
    }

    public void addRaum(Raum raum) {
        if (raum != null && raum.getName() != null) {
            String schluessel = raum.getName().toLowerCase();

            if (this.raeume.containsKey(schluessel)) {
                throw new RaumExistiertBereitsException(raum.getName());
            }
            this.raeume.put(schluessel, raum);
        }
    }

    public HashMap<String, Raum> getRaeume() {
        return raeume;
    }

    public Raum getRaum(String name) {
        // return Raum-Objekt mit dem übergebenen Namen
        if (name == null) {
            return null;
        }

        return this.raeume.get(name.toLowerCase());
    }
}