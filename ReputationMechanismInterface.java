
import java.rmi.RemoteException;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Write a description of interface ReputationMechanismInterface here.
 * 
 * @author n0ru 
 * @version (a version number or a date)
 */

public interface ReputationMechanismInterface {
    public Vector <SuperAgentInterface> sortSellers(Vector <SuperAgentInterface> agents, Vector <SuperAgentInterface> neighbors)  throws RemoteException;
    public boolean willSellTo(SuperAgentInterface agent)  throws RemoteException;
}
