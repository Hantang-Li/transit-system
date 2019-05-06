package transitSystem;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A Station class that represents subway station and bus stop. Station stores information of each
 * station, such as the location of a station and the type of a station.
 */
public class Station implements Serializable {

  /** The location of a station. */
  private String location;
  /** The type of a station: either a subway station or a bus stop. */
  private String stationType;
  /** A list that stores nextStation in order to handle intersecting transport lines. */
  private ArrayList<Station> nextStation = new ArrayList<>();

  /**
   * Creates a new Station. Initialize its location and its type: either a subway station or a bus
   * stop.
   *
   * @param location The location of this Station.
   * @param stationType The type of this Station.
   */
  Station(String location, String stationType) {
    this.location = location;
    this.stationType = stationType;
  }

  /**
   * Stores the nextStation in order to handle intersecting transport lines.
   *
   * @param station The next Station.
   */
  void addNextStation(Station station) {
    this.nextStation.add(station);
  }


  /**
   * Gets location of this Station.
   *
   * @return This Station location.
   */
   String getLocation() {
    return location;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    } else if (obj == null || getClass() != obj.getClass()) {
      return false;
    } else {
      return (this.location.equals(((Station) obj).getLocation())
          && this.stationType.equals(((Station) obj).getStationType()));
    }
  }

  /**
   * Gets the type of this Station.
   *
   * @return The Station type: either a subway station or a bus stop.
   */
  public String getStationType() {
    return stationType;
  }


  /**
   * Gets all of the next Station.
   *
   * @return A ArrayList that stores all of next Stations.
   */
  ArrayList<Station> getNextStation() {
    return nextStation;
  }


  /**
   * Contextualizes all objects to readable information.
   *
   * @return A information we need.
   */
  @Override
  public String toString() {
    StringBuilder acc = new StringBuilder();
    for (Station s : this.nextStation) {
      acc.append(s.location);
    }
    return "Station{"
        + "location='"
        + location
        + '\''
        + ", stationType='"
        + stationType
        + '\''
        + "next stations: "
        + acc
        + '}'
        + System.getProperty("line.separator");
  }
}
