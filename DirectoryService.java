
/**
 * Write a description of class SuperAgent here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.rmi.*;
import java.rmi.server.*;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.HashMap;

public class DirectoryService extends Thread implements DirectoryServiceInterface
{
    private Vector<SuperAgentInterface> agents;
    private HashMap<String, Vector<SuperAgentInterface>> sellers;
    private AgentNeighborhoodManager neighborhoodManager;
    private int secondsAlive=0;
    private LoggingFacilityInterface loggingFacility = null;
    private DirectoryServiceInterface myRemoteObject = null;
    private int maxTime;
    
    public DirectoryService(String directoryServiceName, String loggingFacilityName,
                            int noOfNeighborhoods, int maxTime) throws RemoteException
    {
        super();
        this.maxTime = maxTime;
        this.neighborhoodManager = new AgentNeighborhoodManager(noOfNeighborhoods);
        this.sellers = new HashMap<String, Vector<SuperAgentInterface>>();
        this.agents = new Vector<SuperAgentInterface>();

        try{    
            this.loggingFacility = (LoggingFacilityInterface)Naming.lookup(loggingFacilityName);

            this.myRemoteObject = (DirectoryServiceInterface)UnicastRemoteObject.exportObject(this);
            Naming.rebind("//localhost/"+directoryServiceName, this.myRemoteObject);
            this.loggingFacility.log(this.myRemoteObject, "Directory service started.");
            
        } catch(Exception e) {
            System.out.println(e);
            System.exit(1);
        }   
        start();
    }
    
    public RegistrationData register(SuperAgentInterface agent) throws RemoteException
    {
        String newAgentName = "";
        RegistrationData registrationData = null;
        try{    
            newAgentName = "//localhost/agent_"+(this.agents.size()+1); 
            Naming.rebind(newAgentName, agent);
            registrationData = new RegistrationData(newAgentName,
                                   ItemInventory.getItemInventory(),
                                   ItemInventory.getItemInventory());
            
            this.agents.add(agent);
            this.addSeller(agent,registrationData);
            this.neighborhoodManager.addAgentToNeighborhood(agent);
            
        } catch(Exception e) {
            e.printStackTrace();
        } 


        return  registrationData;
        
    }
    
    
    public void run()                       
    {        
       while(true)
       {
           try{
               Thread.sleep(1000);
               this.secondsAlive++;
               if(this.endOfGame())
               {
                   this.loggingFacility.log(this.myRemoteObject,"END OF GAME!!!");
                   this.neighborhoodManager.killAllAgents();
                   this.calculateStats();
                   break;
               }
               else if(this.secondsAlive%4 == 0)
               {
                   this.loggingFacility.log(this.myRemoteObject, "Directory service scrambled neighborhoods...");
                   this.neighborhoodManager.scrumbleNeighborhoods();
               }
               //System.out.println(this.name + " talked to " + agentsName);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
       }
       
    }
    
    public Vector <SuperAgentInterface> getNeighbours(SuperAgentInterface agent)
    {
        Vector <SuperAgentInterface> allOthers = (Vector <SuperAgentInterface>)this.neighborhoodManager.getNeighboors(agent).clone();
        allOthers.remove(agent);
        return allOthers;
    }

    
    public Vector <SuperAgentInterface> getSellersOf(Item item)
    {
        if(!this.sellers.containsKey(item.getName()))
        {
            this.sellers.put(item.getName(), new Vector<SuperAgentInterface>());
        }
        return this.sellers.get(item.getName());
    }

    private void addSeller(SuperAgentInterface agent, RegistrationData registrationData) {
        for(Item item : registrationData.getItemsToSell())
        {
            if(!this.sellers.containsKey(item.getName()))
            {
               this.sellers.put(item.getName(), new Vector<SuperAgentInterface>());
            }
            this.sellers.get(item.getName()).add(agent);
        }
    }

    public int getNeighborhoodNumber(SuperAgentInterface agent)  throws RemoteException{
        return this.neighborhoodManager.getNeighborhood(agent);
    }

    private boolean endOfGame() {
        if(this.secondsAlive >= this.maxTime)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void calculateStats() throws RemoteException {
        for(SuperAgentInterface agent : this.agents)
        {
            DecimalFormat twoPlaces = new DecimalFormat("0.00");
            // + "\t Success Rate: " + twoPlaces.format(agent.getSuccessRate()) + "\t Mean opponent fairness: " + twoPlaces.format(agent.getMeanOpponentFairness())+
            this.loggingFacility.log(myRemoteObject, agent.getAgentName() + "\t fairness: " + twoPlaces.format(agent.getFairness()) + "\t total sellings: "+twoPlaces.format(agent.getTotalSellTransactions()));
        }
    }

    public long getSecondsAlive() throws RemoteException {
        return this.secondsAlive;
    }
    

}
