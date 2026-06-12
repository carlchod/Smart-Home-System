@startuml
skinparam classAttributeIconSize 0
skinparam monochrome true
skinparam packageStyle rectangle

package "Shared / Model" {
    interface java.io.Serializable

    abstract class SmartDevice implements java.io.Serializable {
        - id: String
        - name: String
        + getName(): String
        + {abstract} getStatusText(): String
    }

    interface Schaltbar {
        + toggle(): void
        + istEingeschaltet(): boolean
    }

    class HeizungsThermostat extends SmartDevice {
        - zielTemperatur: double
        + setTemperatur(temp: double): void
        + getStatusText(): String
    }

    class Lichtschalter extends SmartDevice implements Schaltbar {
        - zustand: boolean
        + toggle(): void
        + istEingeschaltet(): boolean
        + getStatusText(): String
    }

    class Jalousie extends SmartDevice {
        - oeffnungsGrad: int
        + setOeffnungsGrad(grad: int): void
        + getStatusText(): String
    }

    class Raum implements java.io.Serializable {
        - name: String
        - geraete: ArrayList<SmartDevice>
        + getName(): String
        + getDevices(): ArrayList<SmartDevice>
        + addDevice(d: SmartDevice): void
    }

    Raum "1" *-- "*" SmartDevice : enthält >
}

package "Server-Seite (RMI)" {
    interface java.rmi.Remote

    interface SmartHomeService extends java.rmi.Remote {
        + getRaeume(): HashMap<String, Raum>
        + getRaum(name: String): Raum
    }

    class SmartHomeServer implements SmartHomeService {
        - raeume: HashMap<String, Raum>
        + startServer(): void
        + speichereZustand(): void
        + ladeZustand(): void
    }

    SmartHomeServer "1" *-- "*" Raum : verwaltet >
}

package "Client-Seite (CLI)" {
    class CLIClient {
        - currentRoomContext: String
        - scanner: java.util.Scanner
        + main(args: String[]): void
        + connectToServer(): void
        + runCommandLoop(): void
    }
}

CLIClient ..> SmartHomeService : nutzt via RMI >

@enduml