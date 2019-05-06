package transitSystem;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A Trip class that represents every transit activity. It stores each trip information, such as
 * enter station, exit station, enter time, exit time and the next station.
 */
public class Trip implements Serializable {

  /** The station that a customer enters. */
  private Station enterStation;
  /** The station that a customer exits. */
  private Station exitStation;
  /** The time a customer enters a station. */
  private Calendar enterTime;
  /** The time a customer exits a station. */
  private Calendar exitTime;
  /** All of the next stations. */
  private Trip next;
  /** A Strategy that calculate transit fare. */
  final FareStrategy STRATEGY;

  /**
   * Creates a new Trip and initializes a fare strategy.
   *
   * @param strategy A Strategy that calculate transit fare.
   */
  Trip(FareStrategy strategy) {
    STRATEGY = strategy;
  }

  /**
   * Sets the station that a customer enters.
   *
   * @param station The station that a customer enters.
   * @param enterTime The time when the customer entering the station.
   */
  void setEnter(Station station, Calendar enterTime) {
    this.enterStation = station;
    this.enterTime = enterTime;
  }

  /**
   * Sets the station that a customer exits.
   *
   * @param station The station that a customer exits.
   * @param exitTime The time when the customer exiting the station.
   */
  void setExit(Station station, Calendar exitTime) {
    this.exitStation = station;
    this.exitTime = exitTime;
  }

  /**
   * Gets the time when a customer entering a station.
   *
   * @return The time when a customer entering a station.
   */
  Calendar getEnterTime() {
    return enterTime;
  }

  /**
   * Gets the station that a customer enters.
   *
   * @return The station that a customer enters.
   */
  Station getEnterStation() {
    return this.enterStation;
  }

  /**
   * Gets the station that a customer exits.
   *
   * @return The station that a customer exits.
   */
  Station getExitStation() {
    return this.exitStation;
  }

  /**
   * Gets all of the next stations.
   *
   * @return All of the next stations.
   */
  Trip getNext() {
    return this.next;
  }

  /**
   * Sets the next stations.
   *
   * @param next Next station.
   */
  void setNext_(Trip next) {
    this.next = next;
  }

  /**
   * Contextualizes all objects to readable information.
   *
   * @return A information we need.
   */
  @Override
  public String toString() {
    String exitStationLocation;
    String exitStationType;
    String enterStationLocation;
    String enterStationType;
    String enterTime;
    String exitTime;
    // check if someone enters a station
    if (this.enterStation == null) {
      enterStationLocation = "null";
      enterStationType = "";
    } else {
      enterStationLocation = this.enterStation.getLocation();
      enterStationType = this.enterStation.getStationType();
    }
    // check if someone exits a station
    if (this.exitStation == null) {
      exitStationLocation = "null";
      exitStationType = "";
    } else {
      exitStationLocation = this.exitStation.getLocation();
      exitStationType = this.exitStation.getStationType();
    }
    // check if the exit time is available
    if (this.exitTime == null) {
      exitTime = "null";
    } else {
      exitTime = this.exitTime.getTime().toString();
    }
    // check if the enter time is available
    if (this.enterTime == null) {
      enterTime = "null";
    } else {
      enterTime = this.enterTime.getTime().toString();
    }
    return "enterStation: "
        + enterStationLocation
        + " "
        + enterStationType
        + System.getProperty("line.separator")
        + "exitStation : "
        + exitStationLocation
        + " "
        + exitStationType
        + System.getProperty("line.separator")
        + "enterTime : "
        + enterTime
        + System.getProperty("line.separator")
        + "exitTime : "
        + exitTime;
  }
}
