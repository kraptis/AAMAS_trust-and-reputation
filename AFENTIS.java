
import java.rmi.RemoteException;
import java.util.Vector;


public class AFENTIS {//AFENTIS=master in greek ;)

    public static void main(String args[])
    {

        try
        {
            int numberOfAgents = 20;
            String directoryServiceName = "directoryService";
            String loggingFacilityName = "loggingFacility";

            LoggingFacility loggingFacility = new LoggingFacility(loggingFacilityName);

            DirectoryService ds = new DirectoryService(directoryServiceName,loggingFacilityName,4,80);
            Vector <SuperAgent> agents = new Vector<SuperAgent>();
            for(int i=0; i<numberOfAgents; i++)
                agents.add(new SuperAgent(directoryServiceName,loggingFacilityName));

        }
        catch(RemoteException e)
        {
            e.printStackTrace();
        }


    }

}
