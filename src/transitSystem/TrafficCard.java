package transitSystem;

import java.io.Serializable;
import java.util.HashMap;

/**
 * A TrafficCard class that represents a transit card. TrafficCard stores information of a card,
 * such as card number, balance, status, and card owner's email address.
 */
public class TrafficCard extends TransitPass implements AbleTopUp, Serializable {
  /** The current balance of a card. */
  private double balance = 19;

  /**
   * Creates a new TimesPass. Initialize its times of ride, and id. Set this new TimesPass initially
   * not suspended.
   */
  TrafficCard() {
    super();
  }

  /**
   * Get the type of this WeeklyPass.
   *
   * @return The type of this card.
   */
  public String getCardType() {
    return "Traffic Card";
  }

  /** Records the total cost of this card each day. */
  private HashMap<String, Double[]> costPerDay = new HashMap<>();

  /**
   * Gets the current balance of this card.
   *
   * @return the card's balance.
   */
  private double getBalance() {
    return balance;
  }

  /**
   * Sets balance of the card.
   *
   * @param balance This card's new balance.
   */
  private void setBalance(double balance) {
    this.balance = balance;
  }

  /**
   * Top up more times into the TimesPass.
   *
   * @param fare The fare that will be top up to the card.
   */
  public void topUp(Double fare) {
    this.balance = this.balance + fare;
  }

  /**
   * Check the balance of the card.
   *
   * @return The balance of the card.
   */
  @Override
  public double viewBalance() {
    return this.balance;
  }

  /**
   * Gets the HashMap which record the total cost of this card each day.
   *
   * @return a HashMap which record the total cost of this card each day.
   */
  @Override
  HashMap<String, Double[]> getCostPerDay() {
    return costPerDay;
  }

  /**
   * Deduct a mount of money from the WeeklyPass.
   *
   * @param fare The amount of money that need to be deducted.
   * @param time The time of the deduction happened.
   * @param accountManager The AccountManager that keep tracks all the account.
   * @return The message that shows the remaining balance.
   */
  @Override
  String tap(double fare, String time, AccountManager accountManager) {
    setBalance(getBalance() - fare);
    System.out.println("$" + fare + " has been deducted from the balance of card " + cardId);
    // update the card cost information on the specific date.
    updateCardCostInformation(time, fare);
    // update account cost information in the specific date
    accountManager.updateAccountCostInformation(this.getOwnerEmail(), time, fare);
    return "deducted: $"
        + String.valueOf(fare)
        + System.getProperty("line.separator")
        + "remaining: $"
        + this.balance;
  }

  /**
   * Show that is this WeeklyPass owing any money.
   *
   * @return If true means this card is owing money, if False means its not.
   */
  @Override
  boolean isOwingMoney() {
    return getBalance() <= 0;
  }

  /**
   * Updates the cost information of a card every time the money is deducted.
   *
   * @param time The specific time that the money is deducted.
   * @param fare The fare which increase the total cost of this card.
   */
  @Override
  void updateCardCostInformation(String time, double fare) {
    if (this.costPerDay.containsKey(time)) {
      double totalCost = costPerDay.get(time)[1];
      totalCost += fare;
      costPerDay.get(time)[0]++;
      costPerDay.get(time)[1] = totalCost;
    } else {
      Double[] date = new Double[2];
      date[0] = 1.0;
      date[1] = fare;
      costPerDay.put(time, date);
    }
  }

  /**
   * Return the string representation of the TransitPass.
   *
   * @return The string message of the card.
   */
  @Override
  public String toString() {
    StringBuilder costs = new StringBuilder();
    StringBuilder times = new StringBuilder();
    if (costPerDay.isEmpty()) {
      costs.append(0);
      times.append(0);
    } else {
      for (String key : costPerDay.keySet()) {
        costs.append(costPerDay.get(key)[1]);
        times.append(costPerDay.get(key)[0].intValue());
        costs.append(" ");
      }
    }
    return "Traffic Card"
        + System.getProperty("line.separator")
        + "Balance        : "
        + balance
        + System.getProperty("line.separator")
        + "CardId         : "
        + cardId
        + System.getProperty("line.separator")
        + "Suspend status : "
        + ((super.isSuspended()) ? "suspend" : "activated")
        + System.getProperty("line.separator")
        + "Total costs    : "
        + costs
        + System.getProperty("line.separator")
        + "Total times    : "
        + times;
  }
}
