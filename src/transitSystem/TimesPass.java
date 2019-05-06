package transitSystem;

import java.io.Serializable;
import java.util.HashMap;

/** A TimesPass class that represents a transit TimesPass that can be used 10 times initially. */
public class TimesPass extends TransitPass implements AbleTopUp, Serializable {

  /** The current available times of ride of a TimesPass. */
  private Integer times = 10;

  /** Records the total cost of this card each day. */
  private HashMap<String, Double[]> costPerDay = new HashMap<>();

  /**
   * Creates a new TimesPass. Initialize its times of ride, and id. Set this new TimesPass initially
   * not suspended.
   */
  TimesPass() {
    super();
  }

  /**
   * Get the cost per day of the WeeklyPass.
   *
   * @return The CostPerDay of the card.
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
    // deduct one time from the card
    int DEDUCT_VALUE = 1;
    this.setTimes(this.getTimes() - DEDUCT_VALUE);
    System.out.println("Remaining times is " + getTimes());
    // update the card cost information on the specific date.
    updateCardCostInformation(time, fare);
    // update account cost information in the specific date
    accountManager.updateAccountCostInformation(this.getOwnerEmail(), time, fare);
    return "Remaining times is " + getTimes();
  }

  /**
   * Get the type of this WeeklyPass.
   *
   * @return The type of this card.
   */
  public String getCardType() {
    return "Time Pass";
  }

  /**
   * Show that is this WeeklyPass owing any money.
   *
   * @return If true means this card is owing money, if False means its not.
   */
  @Override
  boolean isOwingMoney() {
    return this.times <= 0;
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
    return "Times Pass"
        + System.getProperty("line.separator")
        + "Times          : "
        + this.getTimes()
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

  /**
   * Gets the current available times of ride of this TimesPass.
   *
   * @return the TimesPass's available times of ride.
   */
  private int getTimes() {
    return times;
  }

  /**
   * Sets available times of ride of the TimesPass.
   *
   * @param times This TimesPass's available times of ride.
   */
  public void setTimes(int times) {
    this.times = times;
  }

  /**
   * Top up more times into the TimesPass.
   *
   * @param fare The fare that will be top up to the card.
   */
  @Override
  public void topUp(Double fare) {
    //check if fare is as required
    assert fare.equals(50.0) || fare.equals(20.0) || fare.equals(10.0);
    int TWENTY_FIVE_TIMES = 25;
    int TEN_TIMES = 10;
    int FIVE_TIMES = 5;
    if (fare.equals(50.0)) {
      this.setTimes(this.getTimes() + TWENTY_FIVE_TIMES);
    } else if (fare.equals(20.0)) {
      this.setTimes(this.getTimes() + TEN_TIMES);
    } else if (fare.equals(10.0)) {
      this.setTimes(this.getTimes() + FIVE_TIMES);
    }
    System.out.println("successfully topped up");
  }

  /**
   * Check the balance of the card.
   *
   * @return The balance of the card.
   */
  @Override
  public double viewBalance() {
    final int FARE = 2;
    return this.getTimes() * FARE;
  }
}
