
/**
 * Write a description of class RegistrationData here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.util.Vector;
import java.io.Serializable;
public class RegistrationData implements Serializable
{
   private String name;
   private Vector<Item> itemsToSell;
   private Vector<Item> itemsToBuy;
   public RegistrationData(String name, Vector itemsToSell, Vector itemsToBuy)
   {
       this.name = name;
       this.itemsToSell = itemsToSell;
       this.itemsToBuy = itemsToBuy;
   }


   public String getName()
   {
       return this.name;
   }

    /**
     * @return the itemsToSell
     */
    public Vector<Item> getItemsToSell() {
        return itemsToSell;
    }

    /**
     * @param itemsToSell the itemsToSell to set
     */
    public void setItemsToSell(Vector<Item> itemsToSell) {
        this.itemsToSell = itemsToSell;
    }

    /**
     * @return the itemsToBuy
     */
    public Vector<Item> getItemsToBuy() {
        return itemsToBuy;
    }

    /**
     * @param itemsToBuy the itemsToBuy to set
     */
    public void setItemsToBuy(Vector<Item> itemsToBuy) {
        this.itemsToBuy = itemsToBuy;
    }
}
