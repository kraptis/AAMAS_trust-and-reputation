
/**
 * Write a description of interface SuperAgentInterface here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.rmi.*;
import java.util.*;

public interface SuperAgentInterface extends Remote
{
    /**
     * An example of a method header - replace this comment with your own
     * 
     * @param  y    a sample parameter for a method
     * @return        the result produced by sampleMethod 
     */
    public String getAgentName() throws RemoteException;
    public boolean sellMeItem(SuperAgentInterface buyer, Object Item) throws RemoteException;
    /*
     * Step 1: Mechanism.willSellTo(buyer);
     */
    public boolean informAboutTransactionOutcomeAsBuyer() throws RemoteException;
    public boolean informAboutTransactionOutcomeAsSeller(SuperAgentInterface agent, boolean foreignOutcome) throws RemoteException;
    
    
    public int wouldBuyFrom(SuperAgentInterface agent)  throws RemoteException;

    public double getSuccessRate() throws RemoteException;

    public LoggingFacilityInterface getLoggingFacility() throws RemoteException;

    public void addBuyerReference(boolean b) throws RemoteException;
    public void addSellerReference(boolean b) throws RemoteException;
    public BuyerCredentials getBuyerReferences() throws RemoteException;
    public SellerCredentials getSellerReferences() throws RemoteException;

    public int getRoundNumber() throws RemoteException;
    public int getTransactionId() throws RemoteException;
    public double getFairness() throws RemoteException;
    public double getMeanOpponentFairness() throws RemoteException;
    public int getTotalTransactions() throws RemoteException;
    public int getTotalSellTransactions() throws RemoteException;

    
    

}