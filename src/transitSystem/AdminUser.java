package transitSystem;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;

/** Represent the AdminUser that keep tracks of the entire system. */
public class AdminUser extends Observable {

  /** Store the transitManager. */
  private TransitManager transitManager;

  /** Store the cardManager. */
  private CardManager cardManager;

  /** Store the accountManager. */
  private AccountManager accountManager;

  /** Store a boolean to detect is the system opened. */
  private boolean isClosed = true;

  /** Store the current time. */
  private String lastDateOpen;

  /** Store the when the system is closed last time. */
  private String lastDateClose;

  /** Email of the admin user. */
  private static final String EMAIL = "admin";

  /** password of the admin user */
  private static final String PASSWORD = "admin";

  /** Create a AdminUser that controls all the transit system. */
  @SuppressWarnings("unchecked")
  public AdminUser() {
    try {
      File f = new File("data-Admin.out");
      if (f.exists()) {
        // Reading the object from a file
        FileInputStream file = new FileInputStream("data-Admin.out");
        ObjectInputStream in = new ObjectInputStream(file);
        // Method for deserialization of object
        Tuple<Tuple<String, String>, Boolean> tuple;
        tuple = (Tuple<Tuple<String, String>, Boolean>) in.readObject();
        lastDateClose = tuple.zero.one;
        lastDateOpen = tuple.zero.zero;
        isClosed = tuple.one;
        in.close();
        file.close();
      } else {
        // initialize last open date and close date
        lastDateOpen = "0000-00-00";
        lastDateClose = "0000-00-00";
        serializeDateInfo();
      }
    } catch (IOException ex) {
      Logging.getLogger()
          .log(
              Level.WARNING,
              "IOException is caught, appear in the first time initialize Admin.",
              ex);
    } catch (ClassNotFoundException ex) {
      Logging.getLogger().log(Level.WARNING, "ClassNotFoundException is caught.", ex);
    }
  }

  /**
   * To check if the system is closed or not.
   *
   * @return False if the system is opened and true if the the system is closed.
   */
  public boolean isClosed() {
    return isClosed;
  }

  /**
   * Set the system to closed state or open state.
   *
   * @param closed False if the system is opened and true if the the system is closed.
   */
  public void setClosed(boolean closed) {
    isClosed = closed;
    serializeDateInfo();
    if (closed) {
      try {
        String lastDateOpen = getLastDateOpen();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(sdf.parse(lastDateOpen));
        transitManager.deleteOldTripRecord(cal1);
        transitManager.deleteCountStationsPerDay(cal1);
        cardManager.deleteCardRevenueRecord(cal1);
        accountManager.deleteAccountRevenueRecord(cal1);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  }

  /** Check if any person were inside the station that prevents system close */
  public boolean isAbleClose() {
    return transitManager.inStationPopulation() == 0;
  }

  /** Get last date open */
  public String getLastDateOpen() {
    return lastDateOpen;
  }

  /** Set last date open */
  public void setLastDateOpen(String lastDateOpen) {
    this.lastDateOpen = lastDateOpen;
    serializeDateInfo();
    this.setChanged();
    this.notifyObservers(lastDateOpen);
  }

  /** Get last date open */
  public String getLastDateClose() {
    return lastDateClose;
  }

  /** Set last date close */
  public void setLastDateClose(String lastDateClose) {
    this.lastDateClose = lastDateClose;
    serializeDateInfo();
  }

  /**
   * View the 3 most recent trips of one card.
   *
   * @param cardId The cardId of the card.
   */
  public String viewTrips(Integer cardId) {
    // get the TripRecord
    StringBuilder recentTrip = new StringBuilder();
    HashMap<Integer, ArrayList<ContinuousTrip>> allFiles = this.transitManager.getTripRecord();
    if (this.transitManager.getTripRecord().containsKey(cardId)) {
      ArrayList<ContinuousTrip> allCardsTrips = allFiles.get(cardId);
      getLastThreeTrips(recentTrip, allCardsTrips);
    } else {
      recentTrip = new StringBuilder("no recent trips");
    }
    // get the 3 recent trip
    return recentTrip.toString();
  }

  /**
   * View the 3 most recent trips of one card.
   *
   * @param accountEmail The cardId of the card.
   */
  public String accountViewTrips(String accountEmail) {
    CardHolderAccount account = accountManager.findAccount(accountEmail);
    // get the TripRecord
    StringBuilder recentTrip = new StringBuilder();
    HashMap<Integer, ArrayList<ContinuousTrip>> allFiles = this.transitManager.getTripRecord();

    ArrayList<ContinuousTrip> AllCardsThreeTrips = new ArrayList<>();
    for (Integer cardId : account.getCards()) {
      if (this.transitManager.getTripRecord().containsKey(cardId)) {
        ArrayList<ContinuousTrip> cardsTrips = allFiles.get(cardId);
        for (int i = cardsTrips.size() - 1; i >= 0; i--) {
          AllCardsThreeTrips.add(cardsTrips.get(i));
        }
      }
    }
    AllCardsThreeTrips.sort(new TripTimeComparator());
    if (AllCardsThreeTrips.size() == 0) {
      recentTrip.append("no recent trips");
    } else {
      getLastThreeTrips(recentTrip, AllCardsThreeTrips);
    }
    // get the 3 recent trip
    return recentTrip.toString();
  }

  /**
   * Get the last three trips by the list of continuous trips.
   *
   * @param recentTrip A string that combine information of recent trip.
   * @param list A list of all the continuous trips.
   */
  private void getLastThreeTrips(StringBuilder recentTrip, ArrayList<ContinuousTrip> list) {
    int counter = 1;
    for (int i = list.size() - 1; i >= 0; i--) {
      if (counter <= 3) {
        recentTrip.append(System.getProperty("line.separator"));
        recentTrip.append(counter);
        recentTrip.append(".");
        recentTrip.append(list.get(i).toString());
        recentTrip.append(System.getProperty("line.separator"));
        counter++;
      } else {
        break;
      }
    }
  }

  /**
   * Print all the number of stations traveled in all finished trips and all the fares collected.
   *
   * @param date The date that the user want to check.
   */
  public String generateReport(String date) {
    HashMap<String, Integer> transitRecord = this.transitManager.getCountStationsPerDay();
    int numOftStations = 0;
    if (transitRecord.containsKey(date)) {
      numOftStations += transitRecord.get(date);
    }
    Double totalCost = cardManager.trackTotalRevenuePerDay(date);
    Double accountTotalCost = accountManager.trackTotalCostPerDay(date);
    Double totalTimes = cardManager.trackTotalTimesPerDay(date);
    String format =
        String.format("The number of stations/stops that users passed: %d", numOftStations);
    StringBuilder speInfo = specifiedCardInfo(date);
    return "Current population inside station is: "
        + transitManager.inStationPopulation()
        + System.getProperty("line.separator")
        + "You are viewing system report on: "
        + date
        + System.getProperty("line.separator")
        + format
        + System.getProperty("line.separator")
        + "The total account cost is: $"
        + accountTotalCost
        + System.getProperty("line.separator")
        + "The card total cost is: $"
        + totalCost
        + System.getProperty("line.separator")
        + "The card total deduct times is: "
        + totalTimes.intValue()
        + speInfo;
  }

  /**
   * Lists out all the details of a card.
   *
   * @param date The card information will be print based on the date.
   * @return A StringBuilder that represents the card information.
   */
  private StringBuilder specifiedCardInfo(String date) {
    HashMap<String, Double[]> specifyInfo = cardManager.trackTotalInfo(date);
    StringBuilder speInfo = new StringBuilder();
    for (String cardType : specifyInfo.keySet()) {
      speInfo.append(System.getProperty("line.separator"));
      speInfo.append(cardType);
      speInfo.append(": ");
      speInfo.append(System.getProperty("line.separator"));
      speInfo.append("Total times: ");
      speInfo.append(specifyInfo.get(cardType)[0].intValue());
      speInfo.append(System.getProperty("line.separator"));
      speInfo.append("Total fare: $");
      speInfo.append(specifyInfo.get(cardType)[1]);
    }
    return speInfo;
  }

  /** Initialize the entire system. */
  public void initializeSystem() {
    // set the cardManager, accountManager, transitManager
    this.cardManager = new CardManager();
    this.accountManager = new AccountManager(this.cardManager);
    this.transitManager = new TransitManager(this.cardManager);
    this.cardManager.setAccountManager(accountManager);
  }

  /**
   * Get the transitManager of the system.
   *
   * @return The transitManager.
   */
  public TransitManager getTransitManager() {
    return transitManager;
  }

  /**
   * Get the CardManager of the system.
   *
   * @return The cardManager.
   */
  public CardManager getCardManager() {
    return cardManager;
  }

  /**
   * Get the AccountManager of the system.
   *
   * @return The AccountManager.
   */
  public AccountManager getAccountManager() {
    return accountManager;
  }

  /**
   * Check if the email is correct.
   *
   * @param email The input email.
   * @return true if the email is the admin user's email.
   */
  public boolean checkEmail(String email) {
    return (email.equals(EMAIL));
  }

  /**
   * Check if the password is correct.
   *
   * @param password The input password.
   * @return true if the email is the admin user's password.
   */
  public boolean checkPassword(String password) {
    return (password.equals(PASSWORD));
  }

  /**
   * Serializes information in AdminUser.
   */
  private void serializeDateInfo() {
    try {
      // Saving of object in a file
      FileOutputStream file = new FileOutputStream("data-Admin.out");
      ObjectOutputStream out = new ObjectOutputStream(file);

      // Method for serialization of object
      out.writeObject(new Tuple<>(new Tuple<>(lastDateOpen, lastDateClose), isClosed));

      out.close();
      file.close();

    } catch (IOException ex) {
      Logging.getLogger()
          .log(Level.WARNING, "IOException is caught when serialize DateInfo in admin", ex);
    }
  }
}
