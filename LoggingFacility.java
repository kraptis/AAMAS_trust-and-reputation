
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kostas
 */
public class LoggingFacility implements LoggingFacilityInterface {
    private JTextArea textArea = null;
    private JTable table = null;

     public LoggingFacility(String loggingFacilityName) throws RemoteException
    {
        super();
        System.out.println("Logging facility started.");
        try{

            Naming.rebind("//localhost/"+loggingFacilityName, UnicastRemoteObject.exportObject(this));

        } catch(Exception e) {
           e.printStackTrace();
        }
        
    }

    public LoggingFacility(String loggingFacilityName, JTextArea textArea) throws RemoteException
    {
        this(loggingFacilityName);
        
        this.textArea = textArea;

    }

    public LoggingFacility(String loggingFacilityName, JTextArea textArea, JTable table) throws RemoteException
    {
        this(loggingFacilityName,textArea);

        this.table = table;

    }

    public LoggingFacility(String loggingFacilityName,  JTable table) throws RemoteException
    {
        this(loggingFacilityName);

        this.table = table;

    }

    private void log(String entity, String logMessage)
    {
        long time = System.currentTimeMillis();
        String message = "\n"+time+" \t " + entity + "\t" + logMessage;
        if(this.textArea != null)
            this.textArea.append(message);
        else
            System.out.println(message);

        if(this.table != null)
        {
            Vector data = new Vector();
            data.add(entity);
            data.add(logMessage);
            DefaultTableModel tableModel = (DefaultTableModel) this.table.getModel();
            tableModel.insertRow(0, data);

        }
    }

    public void log(SuperAgentInterface entity, String logMessage, int roundNumber, int transactionId) throws RemoteException
    {
        this.log(entity, logMessage + " (TRANSACTION: " + entity.getAgentName() + ":" + roundNumber + ":" + transactionId +")");
    }
    

    public void log(SuperAgentInterface agent, String logMessage) throws RemoteException {
        this.log(agent.getAgentName(), logMessage);
    }

    public void log(DirectoryServiceInterface ds, String logMessage) throws RemoteException {
        this.log("Directory service", logMessage);
    }

    

}
