
/**
 * Write a description of class AgentStarter here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.rmi.*;
import java.util.*;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

public class AgentStarter extends SwingWorker<Void, Long> {

    JProgressBar progress = null;
    private int agents;
    private int neighborhoods;
    private int timeToLive;

    public AgentStarter(JProgressBar progress, int agents, int neighborhoods, int timeToLive) {
        this.progress = progress;
        this.agents = agents;
        this.neighborhoods = neighborhoods;
        this.timeToLive = timeToLive;

        progress.setMaximum(timeToLive);
        progress.setMinimum(0);

    }

    @Override
    protected Void doInBackground() throws Exception {
        try {
            
            String directoryServiceName = "directoryService";
            String loggingFacilityName = "loggingFacility";

            LoggingFacility loggingFacility = new LoggingFacility(loggingFacilityName);

            DirectoryService ds = new DirectoryService(directoryServiceName, loggingFacilityName, this.neighborhoods, this.timeToLive);
            Vector<SuperAgent> agentsVector = new Vector<SuperAgent>();
            for (int i = 0; i < agents; i++) {
                agentsVector.add(new SuperAgent(directoryServiceName, loggingFacilityName));
            }
            while( true )
            {
                publish(ds.getSecondsAlive());
            }

        } 
                
        catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    protected void process( List<Long> seconds ) {
        if( progress == null ) return;

        progress.setValue( seconds.get( seconds.size()-1 ).intValue() );
    }

    
    
}
