
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author kostas
 */
public class AgentNeighborhoodManager {

    private Hashtable<Integer, Vector<SuperAgentInterface>> neighborhoods;
    private Hashtable<SuperAgentInterface, Integer> agentNeighborhood;
    private int numberOfNeighborhoods;

    public AgentNeighborhoodManager(int numberOfNeighborhoods) {
        this.numberOfNeighborhoods = numberOfNeighborhoods;
        this.agentNeighborhood = new Hashtable<SuperAgentInterface, Integer>();
        this.neighborhoods = new Hashtable<Integer, Vector<SuperAgentInterface>>();

        for (int i = 0; i < this.numberOfNeighborhoods; i++) {
            this.neighborhoods.put(i, new Vector<SuperAgentInterface>());
        }

        this.neighborhoods.put(-1, new Vector<SuperAgentInterface>());
    }

    public int addAgentToNeighborhood(SuperAgentInterface agent) {
        // Put agent in a neighborhood in a way....
        Random rand = new Random();
        int randomNumber = rand.nextInt(this.numberOfNeighborhoods);
        this.neighborhoods.get(randomNumber).add(agent);
        this.agentNeighborhood.put(agent, randomNumber);

        return randomNumber;
    }

    public Vector<SuperAgentInterface> getNeighboors(SuperAgentInterface agent) {
        int neighborhood = this.agentNeighborhood.get(agent);
        return this.neighborhoods.get(neighborhood);
    }

    public void scrumbleNeighborhoods() {
        // Policy #1:
        // ----------------
        // Take a random agent from eagn neighborhood and move it to another.
        // ....
        Random rand = new Random();


        int scrubleSize = (int) Math.ceil(agentNeighborhood.keySet().size() * 0.1);

        for (Integer neighborhood : this.neighborhoods.keySet()) {

            Vector<SuperAgentInterface> agents = this.neighborhoods.get(neighborhood);

            for (int i = 0; i < scrubleSize; ++i) {
                if (agents.size() == 0) {
                    continue;
                }
                SuperAgentInterface agentToLeave = agents.get(rand.nextInt(agents.size()));
                if (agentToLeave == null) {
                    continue;
                }
                int newNeighborhood = rand.nextInt(this.numberOfNeighborhoods);

                // Remove from old neighborhood
                agents.remove(agentToLeave);
                this.agentNeighborhood.remove(agentToLeave);

                // Add to new neighborhood
                this.neighborhoods.get(newNeighborhood).add(agentToLeave);
                this.agentNeighborhood.put(agentToLeave, newNeighborhood);
            }

        }

    }

    int getNeighborhood(SuperAgentInterface agent) {
        return this.agentNeighborhood.get(agent);
    }

    void killAllAgents() {
        /*
        private Hashtable <Integer, Vector<SuperAgentInterface>> neighborhoods;
        private Hashtable <SuperAgentInterface, Integer> agentNeighborhood;
         */

        Vector<SuperAgentInterface> allAgents = new Vector(agentNeighborhood.keySet());

        agentNeighborhood.clear();
        for (Integer neighborhood : this.neighborhoods.keySet()) {

            this.neighborhoods.get(neighborhood).clear();
        }


        this.neighborhoods.get(-1).addAll(allAgents);
        for (SuperAgentInterface agent : allAgents) {

            this.agentNeighborhood.put(agent, -1);
        }

    }
}
