package transitSystem;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A WeeklyPass class that represents a transit WeeklyPass that can be used infinite times in a
 * week.
 */
public class WeeklyPass extends TransitPass implements Serializable, Observer {
  /** The start date of a WeeklyPass. */
  private Calendar startDate;

  /** The current date. */
  private Calendar currentDate;

  /** The CostPerDay of the WeeklyPass. */
  private HashMap<String, Double[]> costPerDay = new HashMap<>();

  /**
   * Creates a new WeeklyPass. Initialize its startDate of the WeeklyPass, and id. Set this new
   * WeeklyPass initially not suspended.
   */
  WeeklyPass(Calendar date) {
    super();
    startDate = date;
    currentDate = date;
  }

  /**
   * Get the type of this WeeklyPass.
   *
   * @return The type of this card.
   */
  public String getCardType() {
    return "Weekly Pass";
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
    // update the card cost information on the specific date.
    updateCardCostInformation(time, fare);
    // update account cost information in the specific date
    accountManager.updateAccountCostInformation(this.getOwnerEmail(), time, fare);
    return " days remaining " + this.calculateDaysRest();
  }

  /**
   * Show that is this WeeklyPass owing any money.
   *
   * @return If true means this card is owing money, if False means its not.
   */
  @Override
  boolean isOwingMoney() {
    return currentDate.getTimeInMillis() - startDate.getTimeInMillis() > 604800000;
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
   * Check the balance of the card.
   *
   * @return The balance of the card.
   */
  @Override
  public double viewBalance() {
    return calculateDaysRest();
  }

  /**
   * Return the string representation of the WeeklyPass.
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
    return "Weekly pass"
        + System.getProperty("line.separator")
        + "Start date     : "
        + ((startDate == null) ? "null" : startDate.getTime().toString())
        + System.getProperty("line.separator")
        + "Current date   : "
        + ((currentDate == null) ? "null" : currentDate.getTime().toString())
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
   * Calculate the valid days left for this WeeklyPass.
   *
   * @return A string that shows the left days, else the card is expired.
   */
  private double calculateDaysRest() {
    if (!isOwingMoney()) {
      return (604800000 - (currentDate.getTimeInMillis() - startDate.getTimeInMillis())) / 86400000;
    }
    return 0;
  }

  /**
   * Update the information of this WeeklyPass.
   *
   * @param o The Observable class.
   * @param arg The object.
   */
  @Override
  public void update(Observable o, Object arg) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date;
    Calendar cal = Calendar.getInstance();
    try {
      date = sdf.parse((String) arg);
      cal.setTime(date);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    this.currentDate = cal;
  }
}
