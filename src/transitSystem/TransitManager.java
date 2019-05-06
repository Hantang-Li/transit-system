package transitSystem;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.*;

/** The TransitManager that keep track any transit activities. */
public class TransitManager extends Observable implements Serializable {
  /** Record all the trip information. */
  private HashMap<Integer, ArrayList<ContinuousTrip>> tripRecord = new HashMap<>();

  /** Store all the stations in the transit system. */
  private HashSet<Station> stations = new HashSet<>();

  /** Record the number of stations that has been visited per day. */
  private HashMap<String, Integer> countStationsPerDay = new HashMap<>();

  /** The CardManager that manage all the card in the transit system. */
  private CardManager cardManager;

  /** Record the number of people in each stations. */
  private HashMap<Integer, Tuple<Station, Calendar>> inStation = new HashMap<>();

  /** Record the most recent tap in time. */
  private Long mostRecentTime;

  /**
   * Create a TransitManager that track all the activities of a transit system. if there exist some
   * records of the previous trips, stations, and date then read from the files and reconstruct the
   * data. Else create the record file.
   *
   * @param cardManager The CardManager that manage all the card in the transit system.
   */
  @SuppressWarnings("unchecked")
  TransitManager(CardManager cardManager) {
    this.cardManager = cardManager;
    // somehow load the data back.
    // Deserialization
    // Reading the object from a file
    deserializeTripAndStationRecord();
    deserializeStations();
    deserializeDateToStations();
    Logging.getLogger().log(Level.FINE, "successfully deserialize TransitManager");
  }

  /** Deserialize tripRecord and inStation fields */
  @SuppressWarnings("unchecked")
  private void deserializeTripAndStationRecord() {
    try {
      File tripF = new File("data-Trips.out");
      if (tripF.exists()) {
        FileInputStream tripFile = new FileInputStream("data-Trips.out");
        ObjectInputStream tripIn = new ObjectInputStream(tripFile);
        Tuple<
                HashMap<Integer, ArrayList<ContinuousTrip>>,
                HashMap<Integer, Tuple<Station, Calendar>>>
            tuple;
        tuple =
            (Tuple<
                    HashMap<Integer, ArrayList<ContinuousTrip>>,
                    HashMap<Integer, Tuple<Station, Calendar>>>)
                tripIn.readObject();
        tripRecord = tuple.zero;
        inStation = tuple.one;
        tripIn.close();
        tripFile.close();
      } else {
        serializeTrips();
      }
    } catch (IOException ex) {
      Logging.getLogger()
          .log(Level.SEVERE, "IOException occurs in TransitManager.TransitManager()", ex);
      System.out.println(
          "IOException is caught, appear in the first time initialize TransitManager.");
    } catch (ClassNotFoundException ex) {
      Logging.getLogger()
          .log(Level.WARNING, "ClassNotFoundException is caught in TransitManager", ex);
      System.out.println("ClassNotFoundException is caught.");
    }
  }

  /** Deserialize stations*/
  @SuppressWarnings("unchecked")
  private void deserializeStations() {
    try {
      File stationF = new File("data-Stations.out");
      if (stationF.exists()) {
        FileInputStream stationFile = new FileInputStream("data-Stations.out");
        ObjectInputStream stationIn = new ObjectInputStream(stationFile);
        stations = (HashSet<Station>) stationIn.readObject();
        stationIn.close();
        stationFile.close();
      } else {
        readConfiguration();
        serializeStations();
      }
    } catch (IOException ex) {
      Logging.getLogger()
          .log(Level.SEVERE, "IOException occurs in TransitManager.TransitManager()", ex);
      System.out.println("IOException is caught when deserialize stations");
    } catch (ClassNotFoundException ex) {
      Logging.getLogger()
          .log(Level.WARNING, "ClassNotFoundException is caught in deserializeStations", ex);
      System.out.println("ClassNotFoundException is caught.");
    }
  }

  /** Deserialize countStationsPerDay*/
  @SuppressWarnings("unchecked")
  private void deserializeDateToStations() {
    try {
      File dateF = new File("data-DateToStations.out");
      if (dateF.exists()) {
        // Method for deserialization of object
        FileInputStream stationDateFile = new FileInputStream("data-DateToStations.out");
        ObjectInputStream stationDateIn = new ObjectInputStream(stationDateFile);
        countStationsPerDay = (HashMap<String, Integer>) stationDateIn.readObject();
        stationDateIn.close();
        stationDateFile.close();
      } else {
        serializeDateToStations();
      }
    } catch (IOException ex) {
      Logging.getLogger()
          .log(
              Level.SEVERE,
              "IOException occurs in TransitManager.deserializeDateToStations()",
              ex);
    } catch (ClassNotFoundException ex) {
      Logging.getLogger()
          .log(Level.WARNING, "ClassNotFoundException is caught in TransitManager", ex);
    }
  }

  /**
   * Add a new line in to the transit system. the new line can be a bus or a subway line, and all
   * the stations are next to each other. A bus stop and a subway station in the same location will
   * be considered as two different stations.
   *
   * @param lineType The type of the new line, subway Stations or bus stops.
   * @param locations The location of each stations or stops.
   */
  private void addLine(String lineType, ArrayList<String> locations) {
    if (lineType.equals("Station") || lineType.equals("Stop")) {
      ArrayList<Station> tempStations = new ArrayList<>();
      for (String location : locations) {
        Station newStation = new Station(location, lineType);
        for (Station existStation : stations) {
          if (existStation.equals(newStation)) {
            newStation = existStation;
          }
        }
        tempStations.add(newStation);
      }
      int numStation = tempStations.size();
      tempStations.get(0).addNextStation(tempStations.get(1));
      tempStations.get(numStation - 1).addNextStation(tempStations.get(numStation - 2));
      for (int i = 1; i < (numStation - 1); i++) {
        tempStations.get(i).addNextStation(tempStations.get(i - 1));
        tempStations.get(i).addNextStation(tempStations.get(i + 1));
      }
      this.stations.addAll(tempStations);
      Logging.getLogger()
          .log(
              Level.FINE,
              "Stations are successfully read from file and recorded in TransitManager.stations");
    }
    serializeStations();
  }

  /**
   * Record all passed stops.
   *
   * @param trip The trip.
   * @param currTime The time that the card exits the station.
   */
  private void recordStations(Calendar currTime, ContinuousTrip trip) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String time = sdf.format(currTime.getTime());
    Integer stations;
    Integer amount = trip.countStations();
    if (countStationsPerDay.containsKey(time)) {
      stations = countStationsPerDay.get(time) + amount;
    } else {
      countStationsPerDay.put(time, amount);
      stations = amount;
    }

    this.countStationsPerDay.put(time, stations);
    Logging.getLogger()
        .log(
            Level.FINE,
            "Number of stations that a user passed are recorded in TransitManager.countStationsPerDay"
                + " date: "
                + time
                + " amount: "
                + amount);

    serializeDateToStations();
  }

  // new tap in
  /**
   * Tap into a station.
   *
   * @param station The taped in station.
   * @param cardId The cardId.
   * @param currTime The time that the card exits the station.
   */
  public String tapIn(int cardId, Calendar currTime, Station station) {
    // statement for log
    String printStatement =
        currTime.getTime()
            + ", card "
            + cardId
            + " enters "
            + station.getLocation()
            + " "
            + station.getStationType();
    // tap in activity
    ContinuousTrip lastTrip = getLastTrip(cardId);
    String returnValue = this.cardManager.getDeductMessage(cardId);
    //check legal enter
    if (checkLegalEnter(lastTrip, currTime, cardId)) {
      this.recordRecentTime(currTime);
      //check is in a continuous trip
      if (lastTrip.isContinuous(station, currTime)) {
        lastTrip.addEnter(station, currTime);
        // log
        Logging.getLogger()
            .log(Level.INFO, printStatement + " recorded in an exist continuousTrip");
      } else {
        lastTrip = new ContinuousTrip();
        lastTrip.addEnter(station, currTime);
        this.tripRecord.get(cardId).add(lastTrip);
        // log
        Logging.getLogger().log(Level.INFO, printStatement + " recorded in a new continuousTrip");
      }
      // first add station then deduct money
      if (station.getStationType().equals("Stop")) {
        // record return value and deduct money
        returnValue =
            System.getProperty("line.separator")
                + cardManager.deductMoney(cardId, lastTrip.calculateStopFair(), currTime);
      }
      inStation.put(cardId, new Tuple<>(station, currTime));
      returnValue = "successfully tapped in " + returnValue;
      setChanged();
      notifyObservers(inStation);
    } else {
      // log
      Logging.getLogger()
          .log(
              Level.INFO,
              "User has an illegal tapIn activity " + printStatement + ", " + lastTrip.toString());
      returnValue = returnValue + recordTapFailedReason(currTime, cardId);
    }
    serializeTrips();
    return returnValue;
  }
  /**
   * Check if the transit system is able to record this tap in as a trip,
   * checked the latest tap time is earlier than last tapping
   * checked if the card that user is using is able to deduct
   * checked if the enter time does not earlier than other user
   *
   * @param lastTrip The last trip that a person experienced.
   * @param cardId The cardId.
   * @param currTime The time that the card exits the station.
   */
  public boolean checkLegalEnter(ContinuousTrip lastTrip, Calendar currTime, int cardId) {
    return lastTrip.isLegalEnter(currTime)
        && cardManager.isAbleDeduct(cardId)
        && isTimeCorrect(currTime);
  }

  /**
   * Tap out of a station.
   *
   * @param station The taped in station.
   * @param cardId The cardId.
   * @param currTime The time that the card exits the station.
   */
  public String tapOut(int cardId, Calendar currTime, Station station) {
    String printStatement =
        currTime.getTime()
            + ", card "
            + cardId
            + " exits "
            + station.getLocation()
            + " "
            + station.getStationType();

    ContinuousTrip lastTrip = getLastTrip(cardId);
    String returnValue = this.cardManager.getDeductMessage(cardId);
    //check legal exit
    if (checkLegalExit(lastTrip, station, currTime, cardId)) {
      this.recordRecentTime(currTime);
      lastTrip.addExit(station, currTime);
      //only deduct money when in a station
      if (station.getStationType().equals("Station")) {
        returnValue =
            System.getProperty("line.separator")
                + cardManager.deductMoney(cardId, lastTrip.calculateStationFare(), currTime);
      }
      this.recordStations(currTime, lastTrip);
      Logging.getLogger().log(Level.INFO, printStatement);
      // remove from the record of population in stations
      if (inStation.remove(cardId) == null) throw new AssertionError();
      returnValue = "successfully tapped out" + returnValue;
      setChanged();
      notifyObservers(inStation);
    } else {
      // record return value
      Logging.getLogger()
          .log(
              Level.WARNING,
              printStatement + "This is an illegal exit" + ", " + lastTrip.toString());
      returnValue = returnValue + recordTapFailedReason(currTime, cardId);
    }

    serializeTrips();
    return returnValue;
  }

  /**
   * Check if the transit system is able to record this tap in as a trip,
   * checked the latest tap time is earlier than last tapping
   * checked if the card that user is using is able to deduct
   * checked if the exit time does not earlier than other user
   *
   * @param lastTrip The last trip that a person experienced.
   * @param cardId The cardId.
   * @param currTime The time that the card exits the station.
   */
  public boolean checkLegalExit(
      ContinuousTrip lastTrip, Station station, Calendar currTime, int cardId) {
    return lastTrip.isLegalExit(station, currTime)
        && this.cardManager.isAbleDeduct(cardId)
        && isTimeCorrect(currTime);
  }

  /**
   * Record the reasons that why the Tap has Failed.
   *
   * @param currTime The time of the tap.
   * @param cardId The taped card's id.
   * @return A string that shows the reason why the tap has failed.
   */
  private String recordTapFailedReason(Calendar currTime, int cardId) {
    StringBuilder reason = new StringBuilder();
    reason.append(System.getProperty("line.separator"));
    if (!isTimeCorrect(currTime)) {
      reason.append("Incorrect time! ");
      Logging.getLogger().log(Level.WARNING, "Incorrect time!, card id: " + cardId);
    } else if (!this.cardManager.isAbleDeduct(cardId)) {
      reason.append("This card is unable to deduct money, card id: ");
      reason.append(cardId);
    } else {
      reason.append("may caused by");
      reason.append(System.getProperty("line.separator"));
      reason.append("tap too soon or tap twice");
      reason.append(System.getProperty("line.separator"));
      reason.append("or enter station but exit bus stop");
      reason.append(System.getProperty("line.separator"));
      reason.append("vice versa");
    }
    return reason.toString();
  }

  /**
   * record time to the system.
   *
   * @param currentTime The current tap card time
   */
  private void recordRecentTime(Calendar currentTime) {
    this.mostRecentTime = currentTime.getTimeInMillis();
  }

  /**
   * check if a person tapped a card with in correct time(advance to the time that recorded in the
   * system).
   *
   * @param currentTime The current tap card time
   */
  private boolean isTimeCorrect(Calendar currentTime) {
    if (mostRecentTime == null) {
      return true;
    } else {
      return currentTime.getTimeInMillis() >= mostRecentTime - 10000;
    }
  }

  /**
   * Get the previous trip from system "tripRecord"
   *
   * @param cardId The cardId.
   */
  public ContinuousTrip getLastTrip(Integer cardId) {
    ContinuousTrip returnValue;
    if (tripRecord.containsKey(cardId)) {
      //if the record was deleted
      if (!(tripRecord.get(cardId).size() == 0)) {
        returnValue = tripRecord.get(cardId).get(tripRecord.get(cardId).size() - 1);
        //else create new
      } else {
        returnValue = addNewContinuousTrip(cardId);
      }
    } else {
      returnValue = addNewContinuousTrip(cardId);
    }
    return returnValue;
  }
  /**
   * Add new continuous trip which can be accessed by card id
   *
   * @param cardId The cardId.
   */
  private ContinuousTrip addNewContinuousTrip(int cardId) {
    tripRecord.put(cardId, new ArrayList<>());
    ContinuousTrip returnValue = new ContinuousTrip();
    tripRecord.get(cardId).add(returnValue);
    return returnValue;
  }

  /**
   * Get the record of all the trip.
   *
   * @return A HashMap that contains all the trip record.
   */
  HashMap<Integer, ArrayList<ContinuousTrip>> getTripRecord() {
    return tripRecord;
  }

  /**
   * Serialize all the Stations, so the system can use them next time, without go through all the
   * configuration lines again.
   */
  private void serializeStations() {
    try {
      // Saving of object in a file
      FileOutputStream stationFile = new FileOutputStream("data-Stations.out");
      ObjectOutputStream stationOut = new ObjectOutputStream(stationFile);
      // Method for serialization of object
      stationOut.writeObject(stations);
      stationOut.close();
      stationFile.close();
      Logging.getLogger().log(Level.FINE, "serialization station success");
    } catch (IOException ex) {
      System.out.println("IOException is caught.(TransitManager serialize stations)");
      Logging.getLogger().log(Level.WARNING, "serialization station failed", ex);
    }
  }

  /**
   * Serialize all the Trips, so the system can use them next time, without lost all the trip
   * record.
   */
  private void serializeTrips() {
    try {
      // Saving of object in a file
      FileOutputStream tripFile = new FileOutputStream("data-Trips.out");
      ObjectOutputStream tripOut = new ObjectOutputStream(tripFile);
      // Method for serialization of object
      tripOut.writeObject(new Tuple<>(tripRecord, inStation));
      tripOut.close();
      tripFile.close();
      Logging.getLogger().log(Level.FINE, "serialization trips success");
    } catch (IOException ex) {
      System.out.println("IOException is caught.(TransitManager serialize trips)");
      Logging.getLogger().log(Level.WARNING, "serialization Trips failed", ex);
    }
  }

  /**
   * Serialize all the DateToStations, so the system can use them next time, without lost all the
   * DateToStations record.
   */
  private void serializeDateToStations() {
    try {
      // Saving of object in a file
      FileOutputStream stationDateFile = new FileOutputStream("data-DateToStations.out");
      ObjectOutputStream stationDateOut = new ObjectOutputStream(stationDateFile);
      // Method for serialization of object
      stationDateOut.writeObject(countStationsPerDay);

      stationDateOut.close();
      stationDateFile.close();
      Logging.getLogger().log(Level.FINE, "serialization DateToStations success");
    } catch (IOException ex) {
      System.out.println("IOException is caught.(TransitManager serialize DateToStations)");
      Logging.getLogger().log(Level.WARNING, "serialization DateToStations failed", ex);
    }
  }

  /** Get the station object that was saved in manager */
  public Station getStation(String location, String type) {
    for (Station station : stations) {
      if (station.getLocation().equals(location) && station.getStationType().equals(type)) {
        return station;
      }
    }
    return null;
  }

  /**
   * Get the number of stations that has been visited per day.
   *
   * @return The HashMap that tracks the number of the times of the visiting of the station.
   */
  HashMap<String, Integer> getCountStationsPerDay() {
    return countStationsPerDay;
  }

  /**
   * Get the population of the station.
   *
   * @return The number of card in the station.
   */
  int inStationPopulation() {
    return inStation.size();
  }

  /**
   * Check if one card is in the station.
   *
   * @param cardId The card's id that you want check.
   * @return True if the card is in station, false if not.
   */
  public boolean isInStation(int cardId) {
    return inStation.containsKey(cardId);
  }

  /**
   * Check when card enters the station.
   *
   * @param cardId The card's id that you want check.
   * @return The Tuple that shows the station and time the card enters.
   */
  public Tuple<Station, Calendar> inStationInfo(int cardId) {
    return inStation.get(cardId);
  }

  /**
   * Delete the trip that exist for a long time.
   *
   * @param currTime The current time.
   */
  void deleteOldTripRecord(Calendar currTime) {
    long tenYearsInMilli = 315_569_520_000L;
    for (ArrayList<ContinuousTrip> record : this.tripRecord.values()) {
      //use an iterator to remove a old trip record
      Iterator<ContinuousTrip> iterator = record.iterator();
      ContinuousTrip curr;
      while (iterator.hasNext()) {
        curr = iterator.next();
        Calendar oldTime = (Calendar) curr.getFront().getEnterTime().clone();
        //only take the date to compare with current time
        oldTime.set(Calendar.MILLISECOND, 0);
        oldTime.set(Calendar.SECOND, 0);
        oldTime.set(Calendar.MINUTE, 0);
        oldTime.set(Calendar.HOUR, 0);
        if ((currTime.getTimeInMillis() - oldTime.getTimeInMillis()) > tenYearsInMilli) {
          iterator.remove();
        } else {
          break;
        }
      }
    }
    serializeTrips();
  }

  /**
   * Delete the record of number of stations user passed per day that exist for a long time.
   *
   * @param currTime The current time.
   */
  void deleteCountStationsPerDay(Calendar currTime) {
    long teyYearsInMilli = 315_569_520_000L;
    for (String key : this.countStationsPerDay.keySet()) {
      try {
        //change the date time from string to Calendar
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(sdf.parse(key));
        if ((currTime.getTimeInMillis() - cal1.getTimeInMillis()) > teyYearsInMilli) {
          this.countStationsPerDay.put(key, 0);
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    serializeTrips();
  }
  /**
   * Read configuration.txt to create a graph of stations then save it.
   */
  private void readConfiguration() {
    try (BufferedReader fileReader = new BufferedReader(new FileReader("configuration.txt"))) {
      String line = fileReader.readLine();
      // create the subway and bus lines
      while (line != null) {
        String lineType = line.substring(0, line.indexOf(" "));
        String[] locations = line.substring(line.indexOf(":") + 2).split("\\s");
        for (int i = 0; i < locations.length; i++) {
          locations[i] = locations[i].replace(",", "");
        }
        String type;
        // identify each station type
        switch (lineType) {
          case "Subway":
            type = "Station";
            break;
          case "Bus":
            type = "Stop";
            break;
          default:
            type = "Invalid type";
            break;
        }
        // when there is invalid type
        if (!type.equals("Invalid type")) {
          this.addLine(type, new ArrayList<>(Arrays.asList(locations)));
        } else {
          System.out.println("This is an Invalid input.");
        }
        line = fileReader.readLine();
      }
    } catch (Exception ex) {
      System.out.println("This is an Invalid input.");
    }
  }
}
