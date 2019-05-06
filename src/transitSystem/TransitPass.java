package transitSystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/** The TransitPass that can be used in the transit system. */
public abstract class TransitPass implements Serializable {

    /** The id of a card. */
    int cardId;
    /** The status of a card, whether it is suspended. */
    private boolean suspended;
    /** The card owner's email address. */
    private String ownerEmail;

    /**
     * Creates a new TrafficCard. Initialize its initial balance, and id.
     * Set this new card initially not suspended.
     */
    TransitPass() {
        this.suspended = false;
    }

    /**
     * Set the id of the TransitPass.
     *
     * @param cardId The id of the card.
     */
    public void setId(int cardId){
        this.cardId = cardId;
    }

    /**
     * Get the cost per day of the TransitPass.
     *
     * @return The CostPerDay of the card.
     */
    abstract HashMap<String, Double[]> getCostPerDay();

    /**
     * Deduct a mount of money from the TransitPass.
     *
     * @param fare The amount of money that need to be deducted.
     * @param time The time of the deduction happened.
     * @param accountManager The AccountManager that keep tracks all the account.
     * @return The message that shows the remaining balance.
     */
    abstract String tap(double fare, String time, AccountManager accountManager);

    /**
     * Get the type of this TransitPass.
     *
     * @return The type of this card.
     */
    public abstract String getCardType();

    /**
     * Gets the id of this TransitPass.
     *
     * @return This card's id.
     */
   public int getCardId() {
        return cardId;
    }

    /**
     * Gets the status of this card, see if this card is suspended.
     *
     * @return The card' status.
     */
    public boolean isSuspended() {
        return suspended;
    }

    /**
     * Sets status of the card. Sometimes we need to set a card to suspended or not suspended.
     *
     * @param suspended The card's status. If true means this card is set as suspended,
     *                  if false means this card is set as not suspended.
     */
    void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }

    /**
     * Show that is this TransitPass owing any money.
     *
     * @return If true means this card is owing money, if False means its not.
     */
    abstract boolean isOwingMoney();

    /**
     * Gets this email address of this card owner.
     *
     * @return This card owner's email address.
     */
    String getOwnerEmail() {
        return ownerEmail;
    }

    /**
     * Sets this card owner's email address.
     *
     * @param ownerEmail The email address belongs to this card owner.
     */
    void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    /**
     * Updates the cost information of a card every time the money is deducted.
     *
     * @param time The specific time that the money is deducted.
     * @param fare The fare which increase the total cost of this card.
     */
    abstract void updateCardCostInformation(String time, double fare);

    /**
     * Check the balance of the card.
     *
     * @return The balance of the card.
     */
    public abstract double viewBalance();

    /**
     * Return the string representation of the TransitPass.
     *
     * @return The string message of the card.
     */
    @Override
    abstract public String toString();

  public static void main(String[] args) {
    List<Station> a = new ArrayList<>();
    int i = 0;
    System.out.println(i++);
    System.out.println(++i);
  }
}
