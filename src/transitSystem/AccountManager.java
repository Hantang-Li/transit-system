package transitSystem;

import transitSystem.Exceptions.AlreadyExistException;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Observable;
import java.util.logging.Level;

import static java.util.Objects.hash;

/** Represents an account manager which can do some operations to all the accounts. */
public class AccountManager extends Observable {
  /** Stores all the CardHolderAccount. */
  private HashMap<String, CardHolderAccount> accounts = new HashMap<>();
  /** Represents cardManager which helps AccountManager find the card by card id. */
  private CardManager cardManager;

  /**
   * Initializes AccountManager with CardManager and serialize field accounts.
   *
   * @param cardManager Stores and manages all the cards.
   */
  @SuppressWarnings("unchecked")
  AccountManager(CardManager cardManager) {
    this.cardManager = cardManager;
    // somehow load the data back.
    // Deserialization
    try {
      File f = new File("data-Accounts.out");
      if (f.exists()) {
        // Reading the object from a file
        FileInputStream file = new FileInputStream("data-Accounts.out");
        ObjectInputStream in = new ObjectInputStream(file);
        // Method for deserialization of object
        accounts = (HashMap<String, CardHolderAccount>) in.readObject();
        in.close();
        file.close();
      } else {
        serializeAccounts();
      }
      Logging.getLogger().log(Level.FINE, "Object has been de-serialized.");
    } catch (IOException ex) {
      Logging.getLogger()
          .log(
              Level.SEVERE,
                  "IOException is caught, appear in the first time initialize AccountManager.",
                  ex);
      System.out.println(
          "IOException is caught, appear in the first time initialize AccountManager.");
    } catch (ClassNotFoundException ex) {
      Logging.getLogger().log(Level.SEVERE, "ClassNotFoundException is caught.", ex);
      System.out.println("ClassNotFoundException is caught.");
    }
  }

  /**
   * Checks whether the account exits by the email.
   *
   * @param email the email of the account.
   * @return true if the account exists.
   */
  public boolean checkAccountExist(String email) {
    return accounts.containsKey(email);
  }

  /**
   * Checks whether the email matches the password.
   *
   * @param email the email of the account.
   * @param password the password of the account.
   * @return true if email matches the password.
   */
  public boolean emailMatchPassword(String email, String password) {
    CardHolderAccount cardHolderAccount = accounts.get(email);
    return cardHolderAccount.getPassword() == (hash(password));
  }

  /**
   * Changes name of account to newName. Accesses the account by email and the operation will be
   * successful if and only if the account exists.
   *
   * @param email The email of account that it wants to find.
   * @param newName The new name the customer would like to change.
   */
  public void changeName(String email, String newName) {
    // Check if the account exist or not.
    CardHolderAccount account = accounts.get(email);
    if (account == null) {
      Logging.getLogger().log(Level.WARNING, "There is no account " + email + " been found.");
    } else {
      account.setName(newName);
      accounts.put(email, account);
      setChanged();
      notifyObservers(account);
      serializeAccounts();
      Logging.getLogger()
          .log(Level.INFO, "Account " + email + " has changed a new name:" + newName + " .");
    }
  }

  /**
   * Change the password of the account with the input email.
   *
   * @param email The email of the account.
   * @param newPassword The new password that you want change to.
   */
  public void changePassword(String email, String newPassword) {
    CardHolderAccount account = accounts.get(email);
    if (account == null) {
      Logging.getLogger().log(Level.WARNING, "There is no account " + email + " been found.");
    } else {
      account.setPassword(newPassword);
      accounts.put(email, account);
      serializeAccounts();
      Logging.getLogger().log(Level.INFO, "Account " + email + " has changed a new password.");
    }
  }

  /**
   * Creates a new account by email and name. Puts this new account into accounts which contain all
   * the account information.
   *
   * @param email The email of account that it wants to use.
   * @param name The new name that it wants account to use.
   */
  public void createAccount(String email, String name, String password)
          throws AlreadyExistException {
    if (this.checkAccountExist(email)) {
      throw new AlreadyExistException("This account is already exist!");
    } else {
      CardHolderAccount account = new CardHolderAccount(email, name, password);
      accounts.put(account.getEmail(), account);
      serializeAccounts();
      Logging.getLogger().log(Level.INFO, "Account " + email + ": " + name + " has been created.");
    }
  }

  /**
   * Binds a card with card id to a specific account with email.
   *
   * @param email The email of account that it wants to access.
   * @param cardId The id of card which is to be bound to account.
   */
  public void bindCardToAccount(String email, int cardId) {
    // Get the card and the account.
    CardHolderAccount account = accounts.get(email);
    HashSet<Integer> cards = account.getCards();
    TransitPass card = cardManager.findCard(cardId);
    // Deal with the different situations.
    if (card == null) {
      Logging.getLogger().log(Level.WARNING, "The card " + cardId + " is not found.");
    } else if (cards.contains(cardId)) {
      Logging.getLogger()
          .log(Level.WARNING, "The card " + cardId + " has already been bound to this account.");
    } else if (card.getOwnerEmail() != null) {
      Logging.getLogger().log(Level.WARNING, "The card " + cardId + " has another owner.");
    } else {
      account.addCard(cardId);
      card.setOwnerEmail(account.getEmail());
      Logging.getLogger()
          .log(Level.INFO, "The card " + cardId + " has successfully binds to account: " + email);
    }
    serializeAccounts();
  }

  /**
   * Unbinds a card with card id to its owner account with email.
   *
   * @param email The email of account that it wants to access.
   * @param cardId The id of card which is to be unbound to account.
   */
  public void unbindCardToAccount(String email, int cardId) {
    // Get the card and the account.
    CardHolderAccount account = accounts.get(email);
    HashSet<Integer> cards = account.getCards();
    // Deal with the different situations.
    TransitPass card = cardManager.findCard(cardId);
    if (card != null) {
      cards.remove(cardId);
      card.setOwnerEmail(null);
      Logging.getLogger()
          .log(
              Level.INFO,
              "The card " + cardId + " has successfully unbinds from account: " + email);
      serializeAccounts();
    } else {
      Logging.getLogger().log(Level.WARNING, "The card " + cardId + " is not found.");
    }
    serializeAccounts();
  }

  /**
   * Tracks the average cost of account in a specific year and month.
   *
   * @param email The email of the account.
   * @param date The year and month in which we want to track the average cost.
   */
  public double trackAccountAverageCost(String email, String date) {
    double costPerMonth = 0;
    double totalTimes = 0;
    CardHolderAccount account = findAccount(email);
    if (account != null) {
      HashMap<String, Double[]> costPerDay = account.getCostPerDay();
      for (String time : costPerDay.keySet()) {
        if (time.substring(0, 7).equals(date)) {
          costPerMonth += costPerDay.get(time)[1];
          totalTimes += costPerDay.get(time)[0];
        }
      }
      System.out.println(
          "In "
              + date
              + ", "
              + "the "
              + "average cost of account "
              + email
              + "is "
              + costPerMonth / totalTimes);
    } else {
      Logging.getLogger().log(Level.WARNING, "There is no account " + email + " been found.");
    }
    return costPerMonth / totalTimes;
  }

  /**
   * Tracks total cost of all the account in a specific date.
   *
   * @param date The specific date that we want to track the total cost.
   * @return total cost of all the accounts in a specific date.
   */
  public double trackTotalCostPerDay(String date) {
    double totalCost = 0;
    // date format: YYYY-MM-DD
    for (CardHolderAccount account : accounts.values()) {
      HashMap<String, Double[]> costPerDay = account.getCostPerDay();
      for (String costTimes : costPerDay.keySet()) {
        if (costTimes.equals(date)) {
          totalCost += costPerDay.get(costTimes)[1];
        }
      }
    }
    return totalCost;
  }

  /**
   * Updates the cost information of an account with an email every time the money is deducted.
   *
   * @param ownerEmail The email which access to the card holder account.
   * @param time The specific time that the money is deducted and cost information needs to be
   *     updated.
   * @param fare The fare which increase the total cost of this account.
   */
  void updateAccountCostInformation(String ownerEmail, String time, double fare) {
    if (ownerEmail != null) {
      CardHolderAccount account = this.findAccount(ownerEmail);
      System.out.println(account.getName());
      HashMap<String, Double[]> accountCostPerDay = account.getCostPerDay();
      if (accountCostPerDay.containsKey(time)) {
        Double[] costTimes = accountCostPerDay.get(time);
        costTimes[0] = costTimes[0] + 1;
        costTimes[1] = costTimes[1] + fare;
      } else {
        Double[] newCostTimes = new Double[2];
        newCostTimes[0] = 1.0;
        newCostTimes[1] = fare;
        accountCostPerDay.put(time, newCostTimes);
      }
    }
    serializeAccounts();
  }

  /**
   * Finds the account by email.
   *
   * @param email The email of the account.
   * @return card holder account that is found by email.
   */
  public CardHolderAccount findAccount(String email) {
    return accounts.get(email);
  }

  /**
   * Delete the old revenue record of the account.
   *
   * @param currTime The current time.
   */
  void deleteAccountRevenueRecord(Calendar currTime) {
    boolean deleted = false;
    long teyYearsInMilli = 315_569_520_000L;
    for (CardHolderAccount user : accounts.values()) {
      HashMap<String, Double[]> costPerDay = user.getCostPerDay();
      for (String key : costPerDay.keySet()) {
        try {
          // change the date format to calendar
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          Calendar cal1 = Calendar.getInstance();
          cal1.setTime(sdf.parse(key));
          // set to zero
          if ((currTime.getTimeInMillis() - cal1.getTimeInMillis()) > teyYearsInMilli) {
            Double[] record = new Double[2];
            record[0] = 0.0;
            record[1] = 0.0;
            costPerDay.put(key, record);
            deleted = true;
          }
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }
    }
    if (deleted) {
      Logging.getLogger().log(Level.INFO, "Delete Account revenue record that over ten years ago");
    }
    serializeAccounts();
  }

  /** Serializes the accounts and catches the error. */
  @SuppressWarnings("unchecked")
  private void serializeAccounts() {
    try {
      // Saving of object in a file
      FileOutputStream file = new FileOutputStream("data-Accounts.out");
      ObjectOutputStream out = new ObjectOutputStream(file);

      // Method for serialization of object
      out.writeObject(accounts);

      out.close();
      file.close();

      // Reading the object from a file
      FileInputStream file2 = new FileInputStream("data-Accounts.out");
      ObjectInputStream in = new ObjectInputStream(file2);
      // Method for deserialization of object
      accounts = (HashMap<String, CardHolderAccount>) in.readObject();

      in.close();
      file.close();
      Logging.getLogger().log(Level.FINE, "serialization data-Accounts success");
    } catch (IOException ex) {
      System.out.println("IOException is caught.");
      Logging.getLogger().log(Level.WARNING, "serialization data-Accounts failed", ex);
    } catch (ClassNotFoundException e) {
      System.out.println("ClassNotFoundException is caught.");
      Logging.getLogger().log(Level.WARNING, "serialization data-Accounts failed", e);
    }
  }
}
