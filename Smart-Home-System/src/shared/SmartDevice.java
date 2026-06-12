package shared;

import java.io.Serializable;
import java.util.UUID;

public abstract class SmartDevice implements Serializable {
    // Attribute
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;

    // Konstruktor
    public SmartDevice(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    // Methoden
    public abstract String getStatusAsString();

    // Getter und Setter    
    public String getId() {
        return id;
    }

    // könnte zu problemen führen,
    // wenn der User kein Admin ist und weiß was er macht
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}