package transitSystem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;

import static java.util.Objects.hash;

/**
 * A CardHolderAccount class that represents card owners' accounts. CardHolderAccount stores
 * information of each card owner's account, such as owner's name and email.
 */
public class CardHolderAccount implements Serializable {

  /** The card owner's name. */
  private String name;

  /** The card owner's email address. */
  private String email;

  /** The password of this card holder account. */
  private int password;

  /** A HashMap that stores all existing card in this account. */
  private HashSet<Integer> cards = new HashSet<>();

  /** A HashMap that stores cost and times to be deducted money of each day. */
  private HashMap<String, Double[]> costPerDay = new HashMap<>();

  /**
   * Creates a new CardHolderAccount with its owner's name and email address, initialize them to
   * this new CardHolderAccount.
   *
   * @param name The card owner's name.
   * @param email The card owner's email address.
   */
  CardHolderAccount(String email, String name, String password) {
    this.email = email;
    this.name = name;
    this.password = hash(password);
  }

  /**
   * Get the cost per day.
   *
   * @return The CostPerDay.
   */
  HashMap<String, Double[]> getCostPerDay() {
    return costPerDay;
  }


  /**
   * Get the hashed password of the account.
   *
   * @return The hashed password of the account.
   */
  public int getPassword() {
    return password;
  }

  /**
   * Set the password of the account.
   *
   * @param password The password that you want to set.
   */
  public void setPassword(String password) {
    this.password = hash(password);
  }

  /**
   * Gets this account owner's name.
   *
   * @return The name of this account owner.
   */
  public String getName() {
    return name;
  }

  /**
   * Sets this account owner's name.
   *
   * @param name The name of this account owner.
   */
  void setName(String name) {
    this.name = name;
  }

  /**
   * Gets this account owner's email address.
   *
   * @return The email address of this account owner.
   */
  String getEmail() {
    return email;
  }

  /**
   * Sets this account owner's email address.
   *
   * @param email The email address of this account owner.
   */
  void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets all existing cards in a single account.
   *
   * @return A HashMap that stores all existing cards.
   */
  public HashSet<Integer> getCards() {
    return cards;
  }

  /**
   * Adds a card to this account.
   *
   * @param card An existing card that belongs to this account owner.
   */
  void addCard(int card) {
    cards.add(card);
  }

  /**
   * Get the string representation of the Account.
   * @return A string the shows the details of the account.
   */
  @Override
  public String toString() {
    StringBuilder costs = new StringBuilder();
    for (String key : costPerDay.keySet()) {
      costs.append("Date: ");
      costs.append(key);
      costs.append(", total times: ");
      costs.append(costPerDay.get(key)[0]);
      costs.append(", costs: ");
      costs.append(costPerDay.get(key)[1]);
      costs.append(System.getProperty("line.separator"));
    }
    return "CardHolderAccount{"
        + "name='"
        + name
        + '\''
        + ", email='"
        + email
        + '\''
        + ", costPerDay="
        + costs
        + '}';
  }
}
