
/**
 * Write a description of class SuperAgent here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class SuperAgent extends Thread implements SuperAgentInterface {
    /* BASIC STRUCTURES */
    private DirectoryServiceInterface directoryService;
    private SuperAgentInterface myRemoteObject;
    private RegistrationData registrationData;
    private LoggingFacilityInterface loggingFacility;

    private int transactionId = 0;
    private int roundNumber = 0;

    private int totalSuccessfullTransactions = 0;
    private int totalTransactions = 0;
    private int totalSuccessfullSellTransactions = 0;
    private int totalSellTransactions = 0;
    private double totalFairnessOfOpponents = 0.0;
    /* END BASIC STRUCTURES */

    /* CAPABILITIES */
    private SuperAgentBehavior myBehavior;
    private ItemChoiceUtility myItemChoiceUtility;
    private ReputationMechanism myReputationMechanism;
    /* END CAPABILITIES */

    /* CREDENTIALS */
    private BuyerCredentials myReferencesAsBuyer;
    private SellerCredentials myReferencesAsSeller;
    //private InformerCredentials myInformerCredentials;
//    /* END CREDENTIALS */

    public SuperAgent(String directoryServiceName,
            String loggingFacilityName) throws RemoteException {
        super();
        try {
            this.directoryService = (DirectoryServiceInterface) Naming.lookup(directoryServiceName);
            this.loggingFacility = (LoggingFacilityInterface) Naming.lookup(loggingFacilityName);

            this.myRemoteObject = (SuperAgentInterface) UnicastRemoteObject.exportObject(this);
            RegistrationData registrationData = this.directoryService.register(this.myRemoteObject);
            this.registrationData = registrationData;

            this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] I am alive.");
            this.myBehavior = new SuperAgentBehavior(this.myRemoteObject); //to be replaced with random selection of behavior (default: Neutral)
            //this.loggingFacility.log(this.myRemoteObject, "[INFO:] Selling behavior: " + this.myBehavior.sellBehavior + ", Buying behavior: " + this.myBehavior.buyBehavior);
            this.myItemChoiceUtility = new ItemChoiceUtility(this.registrationData.getItemsToBuy(), 1);  //to be replaced with random strategy selection (default: Random)
            //this.loggingFacility.log(this.myRemoteObject, "[INFO:] Buying strategy: " + this.myItemChoiceUtility.getStrategy());
            this.myReputationMechanism = new ReputationMechanism1(this.myRemoteObject, this.myBehavior); //to be replaced with random selection of behavior (default: Neutral)
//            this.loggingFacility.log(this.myRemoteObject, "[INFO:] Reputation mechanism: " + this.myReputationMechanism.getBehavior());

            //this.loggingFacility.log(this.myRemoteObject, "[INFO:] Initializing credentials...");
            this.myReferencesAsBuyer = new BuyerCredentials();
            this.myReferencesAsSeller = new SellerCredentials();
            //this.myInformerCredentials = new InformerCredentials();
/*
            for (Item item : this.registrationData.getItemsToSell()) {
                this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] I will sell " + item.toString());
            }

            for (Item item : this.registrationData.getItemsToBuy()) {
                this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] I will buy " + item.toString());
            }
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        start();
    }


    private HashMap<SuperAgentInterface, Vector<Item>> getItemsPerSeller() throws RemoteException
    {
        List<Item> tobuyList = this.myItemChoiceUtility.chooseItem();
        Vector<SuperAgentInterface> allSellers = new Vector<SuperAgentInterface>();
        HashMap<SuperAgentInterface, Vector<Item>> itemsPerSeller = new HashMap<SuperAgentInterface, Vector<Item>>();

        for (Item tobuy : tobuyList) {
            if (tobuy != null) {
                //this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] I want to buy " + tobuy.getName());
                Vector<SuperAgentInterface> sellers = this.directoryService.getSellersOf(tobuy);
                //Remove me
                sellers.remove(this.myRemoteObject);
                //this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] Got list of sellers (" + sellers.size() + ") from DS for product " + tobuy.getName());
                /* SORT SELLERS IN TRUST-REPUTATION MECHANISM */
                allSellers.addAll(sellers);
                for (SuperAgentInterface seller : sellers) {
                    if (!itemsPerSeller.keySet().contains(seller)) {
                        itemsPerSeller.put(seller, new Vector<Item>());
                    }
                    itemsPerSeller.get(seller).add(tobuy);
                }
            }
        }

        return itemsPerSeller;

    }


    @Override
    public void run() {

        while (true) {
            try {
                // Every 4 seconds
                Thread.sleep(1000);
                this.roundNumber++;
                this.transactionId=0;
                
                // Ask directory service for neighborhood
                int neighborhoodNumber = this.directoryService.getNeighborhoodNumber(this.myRemoteObject);
                Vector<SuperAgentInterface> neighborhood = this.directoryService.getNeighbours(this.myRemoteObject);
                //this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] I am in neighborhood: " + neighborhoodNumber);


                // If neigh is -1 then the game has ended.
                if(neighborhoodNumber==-1)
                    break;

                // Decide which items to buy and who sells it
                HashMap <SuperAgentInterface, Vector<Item>> itemsPerSeller = this.getItemsPerSeller();
                this.getLoggingFacility().log(this.myRemoteObject, "[NOTICE:] I am looking to buy ", getRoundNumber(), getTransactionId());

                /* SORTING SELLERS VIA MECHANISM */
                //this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] Sellers are sorted based on the ReputationMechanism..");
                Vector<SuperAgentInterface> sellers = this.myReputationMechanism.sortSellers(new Vector(itemsPerSeller.keySet()), neighborhood);
                this.getLoggingFacility().log(this.myRemoteObject, "[NOTICE:] Sorted sellers" , getRoundNumber(), getTransactionId());


                if (sellers.size() > 0)
                {
                    /* START NEGOTIATING WITH SELLERS */
                    for (SuperAgentInterface seller : sellers) {

                        Item tobuy = itemsPerSeller.get(seller).firstElement();

                        /* DO TRANSACTION */
                        Transaction transaction = new Transaction(this.myRemoteObject, seller, tobuy);
                        int transactionOutcome = transaction.make();
                        if(transactionOutcome != Transaction.SELLER_DID_NOT_SELL)
                        {
                            this.totalFairnessOfOpponents += seller.getFairness();
                            this.totalTransactions++;
                        }
                        this.transactionId++;

                        /* UPDATE CREDENTIALS */
                        this.myReputationMechanism.updateCredentialsFor(transactionOutcome,seller);
                        if(transactionOutcome == Transaction.DEAL_SUCCESS)
                        {
                            this.getLoggingFacility().log(this.myRemoteObject, "Success!", getRoundNumber(), getTransactionId());
                            this.totalSuccessfullTransactions++;
                            break;
                        }
                        else if(transactionOutcome != Transaction.SELLER_DID_NOT_SELL)
                        {
                            this.getLoggingFacility().log(this.myRemoteObject, "Failure!", getRoundNumber(), getTransactionId());

                        }
                    }
                    /* END NEGOTIATING WITH SELLERS */
                } 
                else
                {
                    
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    public String getAgentName() throws RemoteException {
        return this.registrationData.getName();
    }

    
    public boolean sellMeItem(SuperAgentInterface buyer, Object Item) throws RemoteException {
        return this.myReputationMechanism.willSellTo(buyer);
    }

    public boolean informAboutTransactionOutcomeAsBuyer() throws RemoteException {
        return this.myBehavior.getTransactionOutcome();
    }

    public boolean informAboutTransactionOutcomeAsSeller(SuperAgentInterface agent, boolean foreignOutcome) throws RemoteException {
        this.totalSellTransactions++;
        //this.totalFairnessOfOpponents += agent.getFairness();
        boolean myOutcome = this.myBehavior.getTransactionOutcome();
        /* check agent involved in transaction about the outcome */
        if(!this.myReputationMechanism.historyBuyersCredentials.containsKey(agent))
        {
           this.myReputationMechanism.historyBuyersCredentials.put(agent, new BuyerCredentials());
        }
        if (foreignOutcome == true) {
            this.myReputationMechanism.historyBuyersCredentials.get(agent).increasePositives();
            agent.addBuyerReference(true);
            this.totalSuccessfullSellTransactions++;
            
        } else {
            this.myReputationMechanism.historyBuyersCredentials.get(agent).increaseNegatives();
           agent.addBuyerReference(false);
            
        }
        return myOutcome;
    }

    public int wouldBuyFrom(SuperAgentInterface agent)  throws RemoteException {
        return myReputationMechanism.wouldBuyFrom(agent);
    }

    public double getSuccessRate() throws RemoteException
    {
        return (double)this.totalSuccessfullTransactions/this.getTotalTransactions();
    }

    /**
     * @return the loggingFacility
     */
    public LoggingFacilityInterface getLoggingFacility() throws RemoteException {
        return loggingFacility;
    }

    public void addBuyerReference(boolean reference) throws RemoteException
    {
        if(reference)
            this.myReferencesAsBuyer.increasePositives();
        else
            this.myReferencesAsBuyer.increaseNegatives();
    }

    public void addSellerReference(boolean reference) throws RemoteException
    {
        if(reference)
            this.myReferencesAsSeller.increasePositives();
        else
            this.myReferencesAsSeller.increaseNegatives();
    }

    public BuyerCredentials getBuyerReferences() throws RemoteException
    {
        return this.myReferencesAsBuyer;
    }

     public SellerCredentials getSellerReferences() throws RemoteException
    {
        return this.myReferencesAsSeller;
    }

    /**
     * @return the transactionId
     */
    public int getTransactionId() throws RemoteException{
        return transactionId;
    }

    /**
     * @return the roundNumber
     */
    public int getRoundNumber() throws RemoteException{
        return roundNumber;
    }

    public double getFairness() throws RemoteException {
        return this.myBehavior.getFairness();
    }

    public double getMeanOpponentFairness() throws RemoteException {
        return this.totalFairnessOfOpponents / (double)this.getTotalTransactions();
    }

    /**
     * @return the totalTransactions
     */
    public int getTotalTransactions() {
        return totalTransactions;
    }

    public int getTotalSellTransactions() {
        return totalSellTransactions;
    }

    

   
}
