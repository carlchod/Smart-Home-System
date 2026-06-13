package server;

import shared.Gebaeude;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SmartHomeService extends Remote {
    // Attribute
    // Konstruktor
    // Methoden
    Gebaeude getGebaeude() throws RemoteException;
    String befehlAusfuehren(String raumName, String geraetName, String befehl, String wert) throws RemoteException;
    // Getter und Setter
}