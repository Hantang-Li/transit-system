package transitSystem;

/** A FareStrategy interface represent different strategies. */
interface FareStrategy {

   static final int a  =1;
  /**
   * A method that calculate the money that the rider needs to pay for this one trip.
   *
   * @param trip The trip that the rider took.
   * @return A double that the rider needs to pay.
   */
  double calculateFare(Trip trip);
}
