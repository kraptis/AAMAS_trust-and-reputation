
import java.rmi.RemoteException;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 * Write a description of interface SuperAgentBehaviorInterface here.
 * 
 * @author n0ru 
 * @version (a version number or a date)
 */

public interface SuperAgentBehaviorInterface {
	public boolean getTransactionOutcome() throws RemoteException;

    public boolean wouldSellTo(double agent_weight_percentage, boolean newcomer) throws RemoteException;

    public boolean wouldBuyFrom(double agent_weight_percentage, boolean newcomer)  throws RemoteException;

    public Vector<SuperAgentInterface> filterAgents(Vector<SuperAgentInterface> agentScores)  throws RemoteException;

    public double getFairness() throws RemoteException;
}
