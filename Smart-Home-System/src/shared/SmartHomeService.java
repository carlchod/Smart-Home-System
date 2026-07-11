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

    /**
     * Löscht einen Raum aus dem Gebäude, wenn der Benutzer die Admin Rolle hat.
     * @param rolle
     * @param raumName
     * @return
     * @throws RemoteException
     */
    String raumLoeschen(Rolle rolle, String raumName) throws RemoteException;

    /**
     * Fügt ein neues Gerät zu einem Raum hinzu, wenn der Benutzer die Admin Rolle hat.
     * @param rolle
     * @param raumName
     * @param geraetTyp
     * @param geraetName
     * @return
     * @throws RemoteException
     */
    String geraetHinzufuegen(Rolle rolle, String raumName, String geraetTyp, String geraetName) throws RemoteException;

    /**
     * Löscht ein Gerät aus einem Raum, wenn der Benutzer die Admin Rolle hat.
     * @param rolle
     * @param raumName
     * @param geraetName
     * @return
     * @throws RemoteException
     */
    String geraetLoeschen(Rolle rolle, String raumName, String geraetName) throws RemoteException;

    /**
     * Führt eine Szene aus
     * @param rolle
     * @param szeneName
     * @return
     * @throws RemoteException
     */
    String szeneAusfuehren(Rolle rolle, String szeneName) throws RemoteException;

    /**
     * Erstellt eine neue Szene.
     * @param rolle
     * @param szeneName
     * @return
     * @throws RemoteException
     */
    String szeneErstellen(Rolle rolle, String szeneName) throws RemoteException;

    /**
     * Fügt eine Aktion zu einer Szene hinzu.
     * @param rolle
     * @param szeneName
     * @param raumName
     * @param geraetName
     * @param befehl
     * @param wert
     * @return
     * @throws RemoteException
     */
    String szeneAktionHinzufuegen(Rolle rolle, String szeneName, String raumName, String geraetName, String befehl, String wert) throws RemoteException;

    /**
     * Löscht eine Szene.
     * @param rolle
     * @param szeneName
     * @return
     * @throws RemoteException
     */
    String szeneLoeschen(Rolle rolle, String szeneName) throws RemoteException;
}