
import java.rmi.RemoteException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kostas
 */
public class ReputationMechanism1 extends ReputationMechanism{
    private double NEIGHBORHOOD_OPINION_WEIGHT = 0.5;
    private double REFERENCES_WEIGHT = 0.0;
    private double CREDENTIALS_WEIGHT = 0.4;
            
    public ReputationMechanism1(SuperAgentInterface myAgent, SuperAgentBehaviorInterface myBehaviour)
    {
        super(myAgent,myBehaviour);
        this.ASK_NEIGHBORHOOD = true;
        
    }

   
    @Override
    protected double calculateScoreForSeller(SuperAgentInterface agent, boolean considerNeighbors) throws RemoteException {
        SellerCredentials creds = this.historySellersCredentials.get(agent);
        SellerCredentials references = agent.getSellerReferences();

        double score = 0;
        if(creds != null)
        {
            score = creds.getSimpleRatio();
        
        }

        double referencesScore = 0;

        if( references != null)
            referencesScore = references.getSimpleRatio();


        double informersScore = 0;

        if(considerNeighbors && this.latestInformers != null)
        {
           int positiveOpinions = 0;
           int negativeOpinions = 0;
           double positiveRatios = 0;
           double negativeRatios = 0;

           for(SuperAgentInterface informer : this.latestInformers.get(agent).keySet())
           {
               boolean posOpinion = this.latestInformers.get(agent).get(informer);
               InformerCredentials informerExperience = this.historyInformersCredentials.get(informer);
               double informerWeight = 1;

               if(informerExperience!=null)
                   informerWeight = informerExperience.getSimpleRatio();


               if(posOpinion)
               {
                    positiveRatios += informerWeight;
                    positiveOpinions++;
               }
               else
               {
                    negativeRatios += informerWeight;
                    negativeOpinions++;
               }
           }

           if( positiveOpinions + negativeOpinions > 0 )
            informersScore = (double) positiveRatios / (positiveRatios+negativeRatios);
        }

        if( considerNeighbors )
        {
            score = (this.CREDENTIALS_WEIGHT * score) + (this.NEIGHBORHOOD_OPINION_WEIGHT * informersScore) + this.REFERENCES_WEIGHT * referencesScore;
        }
                    //System.out.println(score);

        return score;
    }
    
    @Override
    protected double calculateScoreForBuyer(SuperAgentInterface agent) throws RemoteException {
        BuyerCredentials creds = this.historyBuyersCredentials.get(agent);
        BuyerCredentials references = agent.getBuyerReferences();

        if(creds!=null)
        {
            return creds.getSimpleRatio();
        }
        else
        {
            return 0;
        }
    }

}
