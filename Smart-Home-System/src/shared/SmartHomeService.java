package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SmartHomeService extends Remote {
    // Methoden
    /**
     * Gibt das gesamte Gebäude mit allen Räumen und Geräten zurück.
     * @return
     * @throws RemoteException
     */
    Gebaeude getGebaeude() throws RemoteException;

    /**
     * Gibt einen bestimmten Raum des Gebäudes zurück.
     * @param raumName
     * @return
     * @throws RemoteException
     */
    Raum getRaum(String raumName) throws RemoteException;

    /**
     * Führt einen Befehl für ein Gerät in einem bestimmten Raum aus.
     * @param raumName
     * @param geraetName
     * @param befehl
     * @param wert
     * @return
     * @throws RemoteException
     */
    String befehlAusfuehren(Rolle rolle, String raumName, String geraetName, String befehl, String wert) throws RemoteException;

    /**
     * Registriert einen Client für Updates vom Server.
     * @param client
     * @throws RemoteException
     */
    void registriereClient(SmartHomeCallback client) throws RemoteException;
    
    /**
     * Meldet einen Client vom Server ab.
     * @param client
     * @throws RemoteException
     */
    void meldeClientAb(SmartHomeCallback client) throws RemoteException;

    /**
     * Überprüft das Passwort und gibt die entsprechende Rolle zurück.
     * @param passwort
     * @return
     * @throws RemoteException
     */
    Rolle login(String passwort) throws RemoteException;

    /**
     * Fügt einen neuen Raum zum Gebäude hinzu, wenn der Benutzer die Admin Rolle hat.
     * @param rolle
     * @param raumName
     * @return
     * @throws RemoteException
     */
    String raumHinzufuegen(Rolle rolle, String raumName) throws RemoteException;
}