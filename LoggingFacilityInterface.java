/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kostas
 */
import java.rmi.*;

public interface LoggingFacilityInterface extends Remote {
    public void log(SuperAgentInterface agent, String logMessage) throws RemoteException;
    public void log(DirectoryServiceInterface ds, String logMessage) throws RemoteException;
    public void log(SuperAgentInterface entity, String logMessage, int roundNumber, int transactionId) throws RemoteException;


}
