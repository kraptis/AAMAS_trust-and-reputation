
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.Random;   
/**
 * Write a description of interface ItemChoiceUtility here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

public class ItemChoiceUtility extends Thread implements ItemChoiceUtilityInterface
{
    private Vector<Item> buyItems;
    private ArrayList<Double> weights = new ArrayList();
    private String strategy;
    public static int BIG = 10000000;
    private int windowSize;
    private int currentWindowPlace = 0;
    /* constructors */
     public ItemChoiceUtility(Vector<Item> buyItems, int windowSize)
    {
        this.windowSize = windowSize;
        this.buyItems = buyItems;
        this.strategy = "Random";
        /* initialize weights */
        for(int i=0;i<buyItems.size();i++)
            weights.add(i,0.0);
        setBuyItemWeights();
        printWeights();
    }
     
    public ItemChoiceUtility(Vector<Item> buyItems, String strategy)
    {
        this.buyItems = buyItems;
        this.strategy = strategy;
        /* initialize weights */
        for(int i=0;i<buyItems.size();i++)
           weights.add(i,0.0);
        setBuyItemWeights();
        //printWeights();
    }
    
    public void printWeights()
    {
        //for(int i=0;i<this.buyItems.size(); i++)
        //      System.out.println("Weight "+i+": "+weights.get(i));
    }

    private void setBuyItemWeights()
    {
        if(false)
        {
        Random rand = new Random(101234234 + this.buyItems.size());
        if(this.strategy.equals("Random"))
            for(int i=0;i<this.buyItems.size(); i++)
                weights.set(i,(double)rand.nextInt(101));
        else if(this.strategy.equals("Poor"))
            for(int i=0;i<this.buyItems.size(); i++)
                weights.set(i,(double)BIG - (double)(this.buyItems.get(i).getPrice()));
        else if(this.strategy.equals("Rich"))
            for(int i=0;i<this.buyItems.size(); i++)
                weights.set(i,(double)(this.buyItems.get(i).getPrice()));
        }
    }

    public String getStrategy()
    {
        return this.strategy;
    }

    public int getMaxWeightPosition()
    {
         double maximum = this.weights.get(0);   // start with the first value
         int maxPosition = 0;
         for (int i=1; i<this.weights.size(); i++) {
             if (this.weights.get(i) > maximum) {
                  maximum = this.weights.get(i);   // new maximum
                  maxPosition = i;
             }
        }
        return maxPosition; 
    }

    public void removeItem(int position)
    {
        this.buyItems.remove(position);
        this.weights.remove(position);
    }

    public boolean decreaseQuantity(int position)
    {
        Item item = this.buyItems.get(position);
        int quantity = item.getQuantity();
        if(quantity == 1)
        {
            removeItem(position);
            return false;
        }
        else
        {
           item.setQuantity(quantity-1);
           return true;
        }
    }

    public List<Item> chooseItem()
    { 
        if(this.buyItems.isEmpty())
        {
            return null;
        }
        else
        {
            if(windowSize > this.buyItems.size())
            {
                windowSize = this.buyItems.size();
            }

            Vector <Item> toReturn = new Vector<Item>();//(this.buyItems.subList(this.currentWindowPlace, Math.min(this.buyItems.size(), this.windowSize+this.currentWindowPlace)));
            while(toReturn.size() < this.windowSize)
            {
                toReturn.add(this.buyItems.get(this.currentWindowPlace));
                ++this.currentWindowPlace;
                if(this.currentWindowPlace >= buyItems.size())
                    this.currentWindowPlace  = 0;
            }


            return toReturn;

        }
    }
    
}
