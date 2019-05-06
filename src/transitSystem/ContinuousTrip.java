package transitSystem;

import java.io.Serializable;
import java.util.Calendar;

/**
 * A ContinuousTrip class that represents every transit activity. It stores every trip within this
 * continuous trip.
 */
public class ContinuousTrip implements Serializable {
  /** The first trip of this ContinuousTrip. */
  private Trip front;
  /** The last trip of this ContinuousTrip. */
  private Trip back;
  /** The number of trips of this ContinuousTrip. */
  private int size;
  /** The fare of this ContinuousTrip. */
  private float totalFare;
  /** The max fare of this ContinuousTrip. */
  private final int MAX_FAIR = 6;

  /**
   * To check if the tap in activity legal
   *
   * @param time the time that the trip begin.
   */
  boolean isLegalEnter(Calendar time) {
    if (this.back == null) {
      return true;
    } else {
      return !(back.getExitStation() == null)
          && !(back.getEnterStation() == null)
          && time.getTimeInMillis() > this.back.getEnterTime().getTimeInMillis();
    }
  }

  /**
   * To check if the tap out activity legal
   *
   * @param time the time that the trip begin.
   */
  boolean isLegalExit(Station station, Calendar time) {
    if (this.back == null) {
      return false;
    }
    return (back.getExitStation() == null)
        && !(back.getEnterStation() == null)
        && station.getStationType().equals(back.getEnterStation().getStationType())
        && time.getTimeInMillis() > this.back.getEnterTime().getTimeInMillis();
  }

  /**
   * To check if the trip is continuous by compare station and time
   *
   * @param station the station that user tap into
   * @param time the time that the trip begin.
   */
  boolean isContinuous(Station station, Calendar time) {
    if (this.back == null) {
      return true;
    }
    return (time.getTimeInMillis() - front.getEnterTime().getTimeInMillis()) / (60 * 1000) <= 120
        && station.getLocation().equals(this.back.getExitStation().getLocation());
  }

  /**
   * Add a new trip to the continuous trip.
   *
   * @param station The enter station that the trip begin.
   * @param time the time that the trip begin.
   */
  void addEnter(Station station, Calendar time) {
    // add and renew both front and back if there was no station in continuous trip,
    // else back.next_ becomes new node and renew back
    Trip trip = new Trip(getStrategy(station));
    trip.setEnter(station, time);
    if (size == 0) {
      front = back = trip;
      this.size++;
    } else {
      Trip curr = this.front;
      while (curr.getNext() != null) {
        curr = curr.getNext();
      }
      this.size++;
      this.back = trip;
      curr.setNext_(trip);
    }
  }

  /**
   * End the new trip of the continuous trip.
   *
   * @param station The exit station that the trip begin.
   * @param time the time that the trip end.
   */
  void addExit(Station station, Calendar time) {
    // add and renew both front and back if there was no station in continuous trip,
    // else set exit to back
    if (size == 0) {
      Trip trip = new Trip(getStrategy(station));
      trip.setExit(station, time);
      front = back = trip;
      this.size++;
    } else {
      Trip curr = this.front;
      while (curr.getNext() != null) {
        curr = curr.getNext();
      }
      curr.setExit(station, time);
    }
  }

  /**
   * Calculate the Fare of last subway trip of the continuous trip.
   *
   * @return The fare of the last trip.
   */
  double calculateStationFare() {
    // accumulate each trip's cost,
    Trip curr = back;
    double currFair = 0.0;
    float originalTotalFair = totalFare;
    while (curr != null
        && this.totalFare <= MAX_FAIR
        && curr.getExitStation() != null
        && curr.getEnterStation() != null) {
      // use strategy to calculate fair
      currFair = curr.STRATEGY.calculateFare(curr);
      totalFare += currFair;
      //if over 6 dollars adjust amount
      if (totalFare > MAX_FAIR) {
        totalFare = MAX_FAIR;
        currFair = MAX_FAIR - originalTotalFair;
      }
      curr = curr.getNext();
    }
    if (curr != null && (curr.getEnterStation() == null || curr.getExitStation() == null)) {
      currFair = MAX_FAIR - totalFare;
      totalFare = MAX_FAIR;
    }

    return currFair;
  }

  /**
   * Calculate the Fare of last bus trip of the continuous trip.
   *
   * @return The fare of the last trip.
   */
  double calculateStopFair() {
    Trip curr = back;
    double currFair = 0.0;
    float originalTotalFair;
    originalTotalFair = totalFare;

    //if enter station is null, deduct all the money
    if (curr != null && curr.getEnterStation() == null) {
      currFair = MAX_FAIR - totalFare;
      totalFare = MAX_FAIR;
    } else if (curr != null) {
      currFair = curr.STRATEGY.calculateFare(curr);
      totalFare += currFair;
      //if over 6 dollars adjust amount
      if (totalFare > MAX_FAIR) {
        currFair = MAX_FAIR - originalTotalFair;
        totalFare = MAX_FAIR;
      }
    }

    return currFair;
  }

  /**
   * Count all the number of stations on this continuous trip.
   *
   * @return The number of stations.
   */
  int countStations() {
    Trip curr = this.back;
    Double count;
    count = new StationStrategy().calculateFare(curr) * 2;
    return count.intValue();
  }

  /**
   * Get the strategy for subway or bus trips.
   *
   * @param station The station of subway or bus lines.
   * @return The strategy need for calculate fare.
   */
  private FareStrategy getStrategy(Station station) {
    if (station.getStationType().equals("Station")) {
      return new StationStrategy();
    } else if (station.getStationType().equals("Stop")) {
      return new StopStrategy();
    }
    throw new NullPointerException("Trip has no strategy, see ContinuousTrip.getStrategy()");
  }

  Trip getFront() {
    return front;
  }

  /**
   * Make the ContinuousTrip to a String.
   *
   * @return The string that represent the ContinuousTrip.
   */
  @Override
  public String toString() {
    Trip curr = this.front;
    StringBuilder acc = new StringBuilder("Continuous Trip: ");

    if (curr == null) {
      acc.append("front = null");
    }

    while (curr != null) {
      acc.append(System.getProperty("line.separator")).append(curr.toString());
      curr = curr.getNext();
    }

    return acc.toString();
  }
}
