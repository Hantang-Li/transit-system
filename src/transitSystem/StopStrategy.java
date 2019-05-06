package transitSystem;

import java.io.Serializable;

/** A StopStrategy that deal with bus system. */
class StopStrategy implements FareStrategy, Serializable {
  /**
   * A method that calculate the money that the rider needs to pay for this one trip.
   *
   * @param trip The trip that the rider took.
   * @return A double that the rider needs to pay.
   */
  @Override
  public double calculateFare(Trip trip) {
    return 2;
  }
}
