
import java.rmi.RemoteException;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author kostas
 */
public class Transaction {
    public static final int SELLER_DID_NOT_SELL = 0;
    public static final int DEAL_FAILURE_BUYER_FAULT = 1;
    public static final int DEAL_FAILURE_SELLER_FAULT = 2;
    public static final int DEAL_FAILURE_BOTH_FAULT = 3;
    public static final int DEAL_SUCCESS = 4;

    SuperAgentInterface buyer;
    SuperAgentInterface seller;
    Item item;

    public Transaction(SuperAgentInterface buyer, SuperAgentInterface seller, Item item)
    {
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
    }

    public int make() throws RemoteException
    {
        //this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] Trying to buy from " + seller.getAgentName().toString());
        //this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] Seller's credentials " + seller.getSellerCredentials().getPositives() + "(Pos)/" + seller.getSellerCredentials().getNegatives() + "(neg)");

        boolean transactionResult = this.seller.sellMeItem(this.buyer, this.item);
        if (transactionResult) {
            //this.loggingFacility.log(this.myRemoteObject, "[SUCCESS:] Bought item " + tobuy.getName() + " with price " + tobuy.getPrice() + " from agent " + seller.getAgentName().toString());
            boolean buyerOutcome = true;//buyer.informAboutTransactionOutcomeAsBuyer();
            boolean sellerOutcome = seller.informAboutTransactionOutcomeAsSeller(this.buyer,buyerOutcome);

            if (sellerOutcome == true && buyerOutcome == true) // if transaction from buyer is succesful e.g product delivered ok, etc
            {
                return Transaction.DEAL_SUCCESS;
            }
            else if (buyerOutcome == false && sellerOutcome == true) // case transaction failed or is not ok
            {
                return Transaction.DEAL_FAILURE_BUYER_FAULT;
            }
            else if (buyerOutcome == true && sellerOutcome == false) // case transaction failed or is not ok
            {
                return Transaction.DEAL_FAILURE_SELLER_FAULT;
            }
            else if (buyerOutcome == false && sellerOutcome == false) // case transaction failed or is not ok
            {
                return Transaction.DEAL_FAILURE_BOTH_FAULT;
            }
            else
            {
                return -1;
            }

            //this.loggingFacility.log(this.myRemoteObject, "[NOTICE:] Seller's credentials " + seller.getSellerCredentials().getPositives() + "(Pos)/" + seller.getSellerCredentials().getNegatives() + "(neg)");
            
        } else {
            return Transaction.SELLER_DID_NOT_SELL;
            //this.loggingFacility.log(this.myRemoteObject, "[WARNING:] Could not buy item from agent " + seller.getAgentName().toString() + "!");
        }
    }

}
