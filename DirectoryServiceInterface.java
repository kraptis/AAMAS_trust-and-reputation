import java.rmi.*;
import java.rmi.server.*;
import java.util.Vector;
public interface DirectoryServiceInterface extends Remote
{
    /**
     * An example of a method header - replace this comment with your own
     * 
     * @param  y    a sample parameter for a method
     * @return        the result produced by sampleMethod 
     */
    public RegistrationData register(SuperAgentInterface agent) throws RemoteException;
    public Vector <SuperAgentInterface> getNeighbours(SuperAgentInterface agent) throws RemoteException;
    public Vector <SuperAgentInterface> getSellersOf(Item item) throws RemoteException;
    public int getNeighborhoodNumber(SuperAgentInterface agent) throws RemoteException;
    public long getSecondsAlive() throws RemoteException;
    
}
