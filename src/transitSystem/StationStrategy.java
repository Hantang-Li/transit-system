package transitSystem;

import java.io.Serializable;
import java.util.HashSet;

/** A StationStrategy that deal with subway system. */
class StationStrategy implements FareStrategy, Serializable {
  /**
   * A method that calculate the money that the rider needs to pay for this one trip.
   *
   * @param trip The trip that the rider took.
   * @return A double that the rider needs to pay.
   */
  @Override
  public double calculateFare(Trip trip) {
    // set the farePerStation
    double farePerStation = 0.5;
    // set start and end station
    Station start = trip.getEnterStation();
    Station end = trip.getExitStation();
    // create a new HashSet to store all visited stations
    HashSet<Station> route = new HashSet<>();
    route.add(start);
    // set a int to count the stations the rider needs to travel
    int numStations = 0;
    // if the rider does not arrived the end station yet
    while (!containStation(route, end)) {
      // keep move to the next station
      HashSet<Station> nextStations = new HashSet<>();
      for (Station visitedStation : route) {
        nextStations.addAll(visitedStation.getNextStation());
      }
      route.addAll(nextStations);
      // add one to the numStations
      numStations++;
    }
    // calculate the fare for the trip
    return numStations * farePerStation;
  }

  /**
   * Check if the Station is in the set of stations.
   *
   * @param nextStations The set that contains all the stations.
   * @param end the station that need to checked.
   * @return True, if the stations is in the set, false if not.
   */
  private boolean containStation(HashSet<Station> nextStations, Station end){
    for (Station sta : nextStations){
      if(end.getLocation().equals(sta.getLocation()) && end.getStationType().equals(sta.getStationType())){
        return true;
      }
    }
    return false;
  }
}
