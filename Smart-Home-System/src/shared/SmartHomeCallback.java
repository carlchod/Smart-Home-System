package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SmartHomeCallback extends Remote {
    /**
     * vom Server aufgerufen, wenn sich im Haus etwas ändert
     */
    void empfangeUpdate(String nachricht) throws RemoteException;
}