
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

/**
 * Write a description of interface SuperAgentBehavior here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SuperAgentBehavior extends Thread implements SuperAgentBehaviorInterface {

    String buyBehavior;
    String sellBehavior;
    SuperAgentInterface myAgent;
    private double acceptanceThreshold = 0.7;
    private double newComerAcceptance = 0.5;
    private double fairness;
    private Random random;

    public SuperAgentBehavior(SuperAgentInterface myAgent) {
        this.buyBehavior = "Neutral";
        this.sellBehavior = "Neutral";
        this.myAgent = myAgent;

        random = new Random(System.currentTimeMillis());

        this.fairness = ((double)random.nextInt(9)+1)/10;
    }

    public SuperAgentBehavior(String buyBehavior, String sellBehavior) {
        this.buyBehavior = buyBehavior;
        this.sellBehavior = sellBehavior;
    }

    public boolean getTransactionOutcome() throws RemoteException {
        if (random.nextFloat() > getFairness()) {
            this.myAgent.getLoggingFacility().log(this.myAgent, "I am not fair");
            return false;
        } else {
            this.myAgent.getLoggingFacility().log(this.myAgent, "I am fair");
            return true;
        }
    }

    public boolean wouldSellTo(double agent_weight_percentage, boolean newcomer) {
        if (newcomer) {
            if (random.nextFloat() > newComerAcceptance ) {
                return false;
            } else {
                return true;
            }
        }

        if( agent_weight_percentage > acceptanceThreshold )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean wouldBuyFrom(double agent_weight_percentage, boolean newcomer) {

        if (newcomer) {
            
            if (random.nextFloat() > newComerAcceptance ) {
            
                return false;
            } else {


                return true;
            }
        }
        System.out.println(agent_weight_percentage);
        if( agent_weight_percentage > acceptanceThreshold )
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Vector<SuperAgentInterface> filterAgents(Vector<SuperAgentInterface> agentsSorted) {

        return new Vector<SuperAgentInterface>(agentsSorted.subList( 0, (int)Math.ceil((1-acceptanceThreshold)*agentsSorted.size() ) ) );

        
    }

    /**
     * @return the fairness
     */
    public double getFairness() throws RemoteException {
        return fairness;
    }
}
