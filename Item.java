
import java.io.Serializable;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kostas
 */
public class Item implements Serializable{
    private String name;
    private String category;
    private float price;
    private int quantity;


    public Item(String name, float price, int quantity, String category)
    {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    public float getPrice()
    {
        return this.price;
    }

    public String getName()
    {
        return this.name;
    }

    public int getQuantity()
    {
        return this.quantity;
    }

    public String getCategory()
    {
        return this.category;
    }

    public void setQuantity(int newQuantity)
    {
        this.quantity = newQuantity;
    }

    @Override
    public String toString()
    {
        return this.name;
    }

}
