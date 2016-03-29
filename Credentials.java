
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/* B A Y E S I A N   A/(A+B)


/**
 * Write a description of class Credentials here.
 * 
 * @author 
 * @version (a version number or a date)
 */
public class Credentials implements Serializable{
    // instance variables - replace the example below with your own
    private Vector<Boolean> history;
    private int positives;
    private int negatives;
    
    /**
     * Constructor for objects of class Credentials
     */
    public Credentials()
    {
        this.history = new Vector<Boolean>();
        this.positives = 0;
        this.negatives = 0;
    }
    
    public Credentials(int positives, int negatives)
    {
        // initialise instance variables
        for(int i=0;i<positives;i++)
            this.history.add(true);
        for(int i=0;i<negatives;i++)
            this.history.add(false);

        this.positives = positives;
        this.negatives = negatives;
    }

    public Credentials(Vector<Boolean> history)
    {
        // initialise instance variables
        this.history = (Vector<Boolean>) history;
        for(boolean rec : this.history)
            if(rec)
                this.positives+=1;
            else
                this.negatives+=1;
    }

    public void increasePositives()
    {
        this.positives += 1;
        this.history.add(true);
        //System.out.println("INCREASING POSITIVES: " + this.positives);
    }

    public void increaseNegatives()
    {
        this.negatives += 1;
        this.history.add(false);
        //System.out.println("INCREASING NEGATIVES: " + this.negatives);
    }

    public int getPositives()
    {
        return this.positives;
    }

    public int getNegatives()
    {
        return this.negatives;
    }

    public double getSimpleRatio()
    {
        if( negatives + positives > 0 )
            return (double)this.positives / (this.negatives+this.positives);

        return 0;
    }
 
    public double getWeightedRatio()
    {
        double weightedRatio = 0;
        double ratio = this.positives / this.negatives;
        int total = this.positives + this.negatives;

        if(total > 0)
            if(ratio > 1)
              weightedRatio = ratio * total;
            else
              weightedRatio = ratio / total;
        else
            weightedRatio = 0;
        
        return weightedRatio;
    }

    
    public double getWeightedSum(int gamma)
    {
        double sum = 0;
        for(int i=0; i<this.history.size(); i++)
        {
            if(this.history.get(i))
            {
                sum += Math.pow(gamma, this.history.size()-i-1);
            }
            else
            {
                sum -= Math.pow(gamma, this.history.size()-i-1);
            }
        }

        return sum;
    }

    public Credentials filter(int window)
    {
      if(window>this.history.size())
      {
        return new Credentials(this.history);
      }
      else
      {
          return new Credentials(new Vector(this.history.subList(this.history.size()-window, window)));
      }

    }

    public Credentials weightFilter()
    {
        int wpositives=0;
        int wnegatives=0;

        for(int i=this.history.size()-1; i>=0; i--)
        {
            if(this.history.get(i))
            {
                wpositives += i;
            }
            else
            {
                wnegatives += i;
            }
        }
        return new Credentials(wpositives,wnegatives);
    }


}
