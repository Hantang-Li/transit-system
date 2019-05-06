package transitSystem;

import transitSystem.Exceptions.AlreadyExistException;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.logging.*;

/** Represent the CardManager that keep tracks of all the cards. */
public class CardManager extends Observable implements Serializable {

  /** Stores all the cards in the system. */
  private HashMap<Integer, TransitPass> cards = new HashMap<>();

  /** Store the cards that can be top up. */
  private HashMap<Integer, AbleTopUp> ableTopUp = new HashMap<>();

  /** Store the accountManager. */
  private AccountManager accountManager;

  /** Store the number of card exist. */
  private int cardId = 1;

  /** Represents a card manager which can do some operations to all the cards in the system. */
  @SuppressWarnings("unchecked")
  CardManager() {
    // somehow load the data back.
    // Deserialization
    try {
      // setLog();
      File f = new File("data-Cards.out");
      if (f.exists()) {
        // Reading the object from a file
        FileInputStream file = new FileInputStream("data-Cards.out");
        ObjectInputStream in = new ObjectInputStream(file);

        // Method for deserialization of object
        Tuple<Tuple<HashMap<Integer, TransitPass>, HashMap<Integer, AbleTopUp>>, Integer> tuple;
        tuple =
            (Tuple<Tuple<HashMap<Integer, TransitPass>, HashMap<Integer, AbleTopUp>>, Integer>)
                in.readObject();
        cardId = tuple.one;
        cards = tuple.zero.zero;
        ableTopUp = tuple.zero.one;

        in.close();
        file.close();
      } else {
        serializeCards();
      }
      Logging.getLogger().log(Level.FINE, "Successfully serialize CardManager");
    } catch (IOException ex) {
      Logging.getLogger().log(Level.SEVERE, "IOException is caught in CardManager.", ex);
    } catch (ClassNotFoundException ex) {
      Logging.getLogger().log(Level.SEVERE, "ClassNotFoundException is caught.", ex);
    }
  }

  /**
   * Sets the account manager.
   *
   * @param accountManager Represents a new account manager.
   */
  void setAccountManager(AccountManager accountManager) {
    this.accountManager = accountManager;
  }

  public boolean checkCardIdMatch(int cardId) {
    return cards.containsKey(cardId);
  }

  /**
   * Suspends a card with card id.
   *
   * @param cardId The id of the card which is needed to be suspended.
   */
  public void suspendCard(Integer cardId) {
    TransitPass targetCard = cards.get(cardId); // Find the card from the HashMap.
    if (targetCard != null) {
      targetCard.setSuspended(true);
      Logging.getLogger().log(Level.INFO, String.format("Card %d has been suspended", cardId));
      serializeCards();
    } else {
      Logging.getLogger().log(Level.INFO, "Invalid card id when suspend card");
    }
  }

  /**
   * Activates a card with cardId.
   *
   * @param cardId The id of the card which is needed to be activated.
   */
  public void activateCard(Integer cardId) {
    TransitPass targetCard = cards.get(cardId); // Find the card from the HashMap.
    if (targetCard != null) {
      targetCard.setSuspended(false);
      Logging.getLogger().log(Level.INFO,
              "Card " + cardId + "has been activated");
      serializeCards();
    } else {
      Logging.getLogger().log(Level.INFO,
              "Card does not exist.");
    }
  }

  /**
   * Deducts money in card with cardId at a specific time. If the card is suspended, it can not
   * deduct money. If the balance in card is not enough, the balance becomes negative and the card
   * is suspended.
   *
   * @param cardId The id of the card that needs to be deducted money
   * @param fare The money that needs to be deducted in card.
   * @param calendar The specific time that the card needs to be deducted money.
   */
  String deductMoney(Integer cardId, double fare, Calendar calendar) {
    TransitPass targetCard = cards.get(cardId);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    String specificTime = sdf.format(calendar.getTime());
    String time = specificTime.substring(0, 10);
    String returnValue;
    if (isAbleDeduct(cardId)) {
      Logging.getLogger()
              .log(Level.INFO, "successfully deduct money from cardId:" + cardId + " fare: " + fare);
      returnValue = targetCard.tap(fare, time, accountManager);
      serializeCards();
    } else {
      Logging.getLogger()
          .log(Level.WARNING, "call tap function on a card which is unable to deduct");
      returnValue = "unable to deduct money";
    }

    return returnValue;
  }

  /**
   * return a tuple with message if the card is able to deduct money
   *
   * @param cardId The id of the card that needs to be deducted money
   */
  private Tuple<Boolean, String> checkAbleDeduct(Integer cardId) {
    Tuple<Boolean, String> returnValue;
    TransitPass targetCard = cards.get(cardId);
    if (targetCard == null) {
      returnValue = new Tuple<>(false, "Sorry, can't find this card.");
    } else {
      if (targetCard.isSuspended()) {
        returnValue = new Tuple<>(false, "Sorry, this card is suspended and can not deduct money.");
      } else {
        // update account cost information in the specific date
        // accountManager.updateAccountCostInformation(targetCard.getOwnerEmail(), time, fare);
        if (targetCard.isOwingMoney()) {
          returnValue = new Tuple<>(false, "The balance in card is not enough.");
        } else {
          returnValue = new Tuple<>(true, "");
        }
      }
    }
    return returnValue;
  }
  /**
   * Check if the card correspond to cardId is able to deduct.
   */
  public boolean isAbleDeduct(Integer cardId) {
    return checkAbleDeduct(cardId).zero;
  }

  /**
   * Get any message related to the card while tapping
   */
  String getDeductMessage(Integer cardId) {
    return checkAbleDeduct(cardId).one;
  }

  /**
   * Tops up the card with card id.
   *
   * @param cardId The id of the card that needs to be topped up.
   * @param fare The money that needs to be topped up in card.
   */
  public void topUp(Integer cardId, Integer fare) {
    // assert 10 20 50
    assert fare == 50 || fare == 20 || fare == 10;
    AbleTopUp targetCard = ableTopUp.get(cardId);
    if (targetCard != null) {
      if (cards.get(cardId).isSuspended()) {
        Logging.getLogger().log(Level.INFO,
                "Card " + cardId + " has been top up successfully.");
      } else {
        targetCard.topUp(fare.doubleValue());
        Logging.getLogger().log(Level.INFO,
                "Card " + cardId + " is unable to top up.");
      }
      serializeCards();
    } else {
      Logging.getLogger().log(Level.INFO,
              "Card does not exist.");
    }
  }

  /**
   * Finds card with id in the cardManager.
   *
   * @param cardId The id of card.
   * @return the card that is found in cardManager.
   */
  public TransitPass findCard(Integer cardId) {
    return cards.get(cardId);
  }

  /** Applies for a new card. */
  public void applyForCard(TransitPass newCard) throws AlreadyExistException {
    if (cards.containsKey(newCard.cardId)) {
      throw new AlreadyExistException("this card is already exist");
    } else {
      newCard.setId(cardId);
      cardId++;
      cards.put(newCard.getCardId(), newCard);

      if (newCard instanceof AbleTopUp) {
        ableTopUp.put(newCard.getCardId(), (AbleTopUp) newCard);
      }
      serializeCards();
    }
  }

  /**
   * Tracks total revenue collected in the specific day.
   *
   * @param date The specific day.
   * @return the total revenue collected in the specific day.
   */
  double trackTotalRevenuePerDay(String date) {
    double revenue = 0;
    for (TransitPass card : cards.values()) {
      HashMap<String, Double[]> cardCostPerDay = card.getCostPerDay();
      if (cardCostPerDay.containsKey(date)) {
        revenue += cardCostPerDay.get(date)[1];
      }
    }
    return revenue;
  }

  /**
   * Tracks total times collected in the specific day.
   *
   * @param date The specific day.
   * @return the total revenue collected in the specific day.
   */
  double trackTotalTimesPerDay(String date) {
    double times = 0;
    for (TransitPass card : cards.values()) {
      HashMap<String, Double[]> cardCostPerDay = card.getCostPerDay();
      if (cardCostPerDay.containsKey(date)) {
        times += cardCostPerDay.get(date)[0];
      }
    }
    return times;
  }

  /**
   * Tracks total times and fares that cards spend in the specific day.
   *
   * @param date The specific day.
   * @return the total revenue collected in the specific day.
   */
  HashMap<String, Double[]> trackTotalInfo(String date) {
    HashMap<String, Double[]> info = new HashMap<>();
    for (TransitPass card : cards.values()) {
      if (info.keySet().contains(card.getCardType())) {
        addTimesHelper(info, card, date);
        addFareHelper(info, card, date);
      } else {
        info.put(card.getCardType(), new Double[2]);
        info.get(card.getCardType())[0] = 0.0;
        info.get(card.getCardType())[1] = 0.0;
        addTimesHelper(info, card, date);
        addFareHelper(info, card, date);
      }
    }
    return info;
  }

  /**
   * Helper function for trackTotalInfo, collect a card's fare and save it to info.
   *
   * @param date The specific day.
   */
  private void addFareHelper(HashMap<String, Double[]> info, TransitPass card, String date) {
    HashMap<String, Double[]> costPerDay = card.getCostPerDay();
    double fare = 0;
    if (costPerDay != null && costPerDay.containsKey(date)) {
      fare = card.getCostPerDay().get(date)[1];
    }
    info.get(card.getCardType())[1] = info.get(card.getCardType())[1] + fare;
  }

  /**
   * Helper function for trackTotalInfo, collect a card's tap times and save it to info.
   *
   * @param date The specific day.
   */
  private void addTimesHelper(HashMap<String, Double[]> info, TransitPass card, String date) {
    HashMap<String, Double[]> costPerDay = card.getCostPerDay();
    double times = 0;
    if (costPerDay != null && costPerDay.containsKey(date)) {
      times = card.getCostPerDay().get(date)[0];
    }
    info.get(card.getCardType())[0] = info.get(card.getCardType())[0] + times;
  }

  /** Delete all the card's fare and tap times information that are older than 10 years. */
  void deleteCardRevenueRecord(Calendar currTime) {
    long teyYearsInMilli = 315_569_520_000L;
    // get all cards
    for (TransitPass card : cards.values()) {
      HashMap<String, Double[]> costPerDay = card.getCostPerDay();
      for (String key : costPerDay.keySet()) {
        try {
          // translate date's format from string to Calendar.
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
          Calendar cal1 = Calendar.getInstance();
          cal1.setTime(sdf.parse(key));
          // check
          if ((currTime.getTimeInMillis() - cal1.getTimeInMillis()) > teyYearsInMilli) {
            Double[] record = new Double[2];
            record[0] = 0.0;
            record[1] = 0.0;
            costPerDay.put(key, record);
          }
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }
    }
    serializeCards();
  }

  /** Serializes all the cards in the system. */
  private void serializeCards() {
    try {
      // Saving of object in a file
      FileOutputStream file = new FileOutputStream("data-Cards.out");
      ObjectOutputStream out = new ObjectOutputStream(file);

      // Method for serialization of object
      out.writeObject(new Tuple<>(new Tuple<>(cards, ableTopUp), cardId));

      out.close();
      file.close();

    } catch (IOException ex) {
      Logging.getLogger().log(Level.SEVERE, "IOException is caught in card manager.", ex);
    }
  }
}
