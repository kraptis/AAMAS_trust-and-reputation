
/**
 * Write a description of interface ReputationMechanism here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class ReputationMechanism implements ReputationMechanismInterface
{
    private SuperAgentInterface myAgent;
    public HashMap <SuperAgentInterface,BuyerCredentials> historyBuyersCredentials;
    public HashMap <SuperAgentInterface,SellerCredentials> historySellersCredentials;
    public HashMap <SuperAgentInterface,InformerCredentials> historyInformersCredentials;


    protected HashMap<SuperAgentInterface, HashMap<SuperAgentInterface,Boolean>> latestInformers;
    private SuperAgentBehaviorInterface myBehaviour;

    protected boolean ASK_NEIGHBORHOOD = true;
    
    
    public ReputationMechanism(SuperAgentInterface myAgent, SuperAgentBehaviorInterface myBehaviour)
    { 
        this.myAgent = myAgent;
        this.myBehaviour = myBehaviour;
        this.historyBuyersCredentials = new HashMap<SuperAgentInterface, BuyerCredentials>();
        this.historySellersCredentials = new HashMap<SuperAgentInterface, SellerCredentials>();
        this.historyInformersCredentials = new HashMap<SuperAgentInterface, InformerCredentials>();
    }

    protected void askNeighborhood(Vector <SuperAgentInterface> agents, Vector <SuperAgentInterface> neighborhood) throws RemoteException
    {
        // ADK NEIGHBORHOOD FOR OPINIONS
        this.latestInformers = new HashMap<SuperAgentInterface, HashMap<SuperAgentInterface, Boolean>>();

        for(SuperAgentInterface agent : agents)
        {
            
            if(!this.latestInformers.keySet().contains(agent))
            {
                this.latestInformers.put(agent, new HashMap<SuperAgentInterface, Boolean>());
            }

            for(SuperAgentInterface neighbor : neighborhood)
            {
                if(!neighbor.equals(agent))
                {
                    int opinion = neighbor.wouldBuyFrom(agent);
                    this.myAgent.getLoggingFacility().log(this.myAgent, "[Notice:] Asked neighbor: "+neighbor.getAgentName() + " for "+ agent.getAgentName() +", opinion: " + opinion, this.myAgent.getRoundNumber(), this.myAgent.getTransactionId());

                    if(opinion == -1)
                        continue;


                    this.latestInformers.get(agent).put(neighbor, opinion==1);
                }
            }
        }
    }

    // Filter and sort a set of agents.
    public Vector <SuperAgentInterface> sortSellers(Vector <SuperAgentInterface> agents, Vector <SuperAgentInterface> neighborhood)  throws RemoteException
    {
        Vector <SuperAgentInterface> agentsLocal = (Vector <SuperAgentInterface>)agents.clone();
        
        // CALCULATE TABLE FROM MY KNOWLEDGE
        if(this.ASK_NEIGHBORHOOD)
        {
            this.myAgent.getLoggingFacility().log(this.myAgent, "[Notice:] Asking neighbors", this.myAgent.getRoundNumber(), this.myAgent.getTransactionId());
            this.askNeighborhood(agents, neighborhood);
        }
            

        // CALCULATE TABLE FROM SELLERS CREDENTIALS
        HashMap<SuperAgentInterface, Double> agentScores = new HashMap<SuperAgentInterface, Double>();
        for(SuperAgentInterface agent : agents)
        {
            this.myAgent.getLoggingFacility().log(this.myAgent, "[Notice:] Remembering history for: "+agent.getAgentName(), this.myAgent.getRoundNumber(), this.myAgent.getTransactionId());
                
            double agent_score = this.calculateScoreForSeller(agent, true);
            agentScores.put(agent, agent_score);
        }


        final HashMap<SuperAgentInterface, Double> agentScoresFinal = (HashMap<SuperAgentInterface, Double>)agentScores.clone();
        //this.myAgent.getLoggingFacility().log(this.myAgent, "[Notice:] Filtered agents by behavior: "+agentScoresFinal, this.myAgent.getRoundNumber(), this.myAgent.getTransactionId());
            
        
        Collections.sort(agentsLocal, new Comparator<SuperAgentInterface>() {
            public int compare(SuperAgentInterface o1, SuperAgentInterface o2) {
                if(agentScoresFinal.get(o1)  >  agentScoresFinal.get(o2))
                    return -1;
                else if (agentScoresFinal.get(o1)  <  agentScoresFinal.get(o2))
                    return 1;
                else
                    return 0;
            }
        });

        this.myBehaviour.filterAgents(agentsLocal);

        return agentsLocal;
    }

    // Decide if it will sell to a specific agent if it falls before a threshold
    public boolean willSellTo(SuperAgentInterface agent) throws RemoteException
    {
        // Sort this history
        // If this specific agent falls before a threshold return true
        // else return false
        double max_weight = 0.01;
        double agent_weight = 0;
        for(SuperAgentInterface buyer : this.historyBuyersCredentials.keySet())
        {
            double weight = this.calculateScoreForBuyer(agent);// Calculate something
            //System.out.println("CALCULATION:" + weight);

            if(weight > max_weight)
            {
                max_weight = weight;
            }

            if(buyer.equals(agent))
            {
                agent_weight = weight;
            }
            
        }

        //System.out.println(agent.getAgentName() + ": "+agent_weight);

        double agent_weight_percentage = (double)agent_weight/max_weight;
        return this.myBehaviour.wouldSellTo(agent_weight_percentage,agent_weight==0);
    }

    public int wouldBuyFrom(SuperAgentInterface agent) throws RemoteException
    {

        //this.myAgent.getLoggingFacility().log(this.myAgent, "[Notice:] saying my opinion for "+ agent.getAgentName());
                
        // Sort this history
        // If this specific agent falls before a threshold return true
        // else return false
//        double max_weight = 0.01;
//        double agent_weight = 0;
//        for(SuperAgentInterface buyer : this.historySellersCredentials.keySet())
//        {
//            double weight = this.calculateScoreForSeller(buyer,false);// Calculate something
//            if(weight > max_weight)
//            {
//                max_weight = weight;
//            }
//
//            if(buyer.equals(agent))
//            {
//                agent_weight = weight;
//            }
//
//        }

        double agent_weight = 0;
        SellerCredentials creds = this.historySellersCredentials.get(agent);
        if( creds != null)
        {
            agent_weight = creds.getSimpleRatio();
        }
        if( agent_weight == 0 ) return -1;
        //double agent_weight_percentage = agent_weight/max_weight;
        return this.myBehaviour.wouldBuyFrom(agent_weight, agent_weight==0) == true ? 1 : 0;
        
      
    }
    

    void updateCredentialsFor(int transactionOutcome, SuperAgentInterface seller) throws RemoteException {

        // Initialize credentials for unknown sellers
        if(!this.historySellersCredentials.containsKey(seller))
        {
            this.historySellersCredentials.put(seller, new SellerCredentials());
        }
        
        
        if(transactionOutcome == Transaction.DEAL_SUCCESS || transactionOutcome == Transaction.DEAL_FAILURE_BUYER_FAULT)
        {
                /* UPDATE HISTORY FOR SELLER IN HASHMAP (INCREASE POSITIVES) */
                this.historySellersCredentials.get(seller).increasePositives();

                /* INCREASE POSITIVE INFORMER CREDENTIALS (OLOUS OSOUS PROTINAN AUTON) */
                for(SuperAgentInterface informer : this.latestInformers.get(seller).keySet())
                {
                    if(!this.historyInformersCredentials.keySet().contains(informer))
                            this.historyInformersCredentials.put(informer, new InformerCredentials());
                    
                    if(this.latestInformers.get(seller).get(informer))
                    {
                        this.historyInformersCredentials.get(informer).increasePositives();
                    }
                    else
                    {
                        this.historyInformersCredentials.get(informer).increaseNegatives();
                    }
                }

                /* SEND REFERENCE */
                seller.addSellerReference(true);
        }
        else if(transactionOutcome == Transaction.DEAL_FAILURE_SELLER_FAULT || transactionOutcome == Transaction.DEAL_FAILURE_BOTH_FAULT)
        {
                /* UPDATE HISTORY FOR SELLER IN HASHMAP (INCREASE NEGATIVES) */
                this.historySellersCredentials.get(seller).increaseNegatives();

                /* INCREASE NEGATIVE INFORMER CREDENTIALS (OLOUS OSOUS PROTINAN AUTON) */
                for(SuperAgentInterface informer : this.latestInformers.get(seller).keySet())
                {
                    if(!this.historyInformersCredentials.keySet().contains(informer))
                            this.historyInformersCredentials.put(informer, new InformerCredentials());

                    if(!this.latestInformers.get(seller).get(informer))
                    {
                        this.historyInformersCredentials.get(informer).increasePositives();
                    }
                    else
                    {
                        this.historyInformersCredentials.get(informer).increaseNegatives();
                    }
                }
                
                /* SEND REFERENCE */
                 seller.addSellerReference(false);

        }
        else if(transactionOutcome == Transaction.SELLER_DID_NOT_SELL)
        {}
    }

    protected double calculateScoreForSeller(SuperAgentInterface agent, boolean considerNeighbors)   throws RemoteException {
        // IMPLEMENTED IN SUBCLASSES!
        throw new UnsupportedOperationException("Not yet implemented");
    }
    protected double calculateScoreForBuyer(SuperAgentInterface agent)  throws RemoteException {
        // IMPLEMENTED IN SUBCLASSES!
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
