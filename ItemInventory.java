
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

public class ItemInventory {

    private static Vector<Item> allItems;
    private static final int itemsSize = 10;
    private static String[] categories = {"accessories", "computers", "clothes", "bikes", "food", "art"};
    static
    {
        Random rand = new Random(System.currentTimeMillis());
        allItems = new Vector<Item>();
        for(int i=0;i<itemsSize;i++)
        {
            allItems.add(new Item("Item"+i,rand.nextInt(500), rand.nextInt(20), categories[rand.nextInt(4)]));
        }
    }

    public static Vector<Item> getItemInventory()
    {
        
        // Maybe use a distribution instead of random.
        Vector <Item> itemInventory = new Vector<Item>();
        Random rand = new Random(System.currentTimeMillis());
        int itemInventorySize = rand.nextInt(itemsSize-1)+1;
        for(int i=0; i<itemInventorySize; i++)
        {
            Item item = allItems.get(rand.nextInt(itemsSize));
            if(!itemInventory.contains(item))
            {
                item.setQuantity(rand.nextInt(20));
                itemInventory.add(item);
            }
        }
        return itemInventory;
    }

}
