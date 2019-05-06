package transitSystem;

import transitSystem.Exceptions.NullCardException;

import java.util.Calendar;

/** The class that produce the TransitPass. */
public class TransitPassFactory {

  /**
   * Create a Traffic Card or a Times Pass.
   *
   * @param cardType The type of the card that you want build.
   * @return A new TransitPass.
   * @throws NullCardException The card type required is not valid.
   */
  public TransitPass buildTransitPass(String cardType) throws NullCardException {
    if (cardType.equals("Traffic Card")) {
      return new TrafficCard();
    } else if (cardType.equals("Times Pass")) {
      return new TimesPass();
    }
    throw new NullCardException(
        "The card type you typed to TransitPassFactory does not exists" + cardType);
  }

  /**
   * Create a Weekly Pass based on the current time.
   *
   * @param cardType The type of the card that you want build.
   * @return A new TransitPass.
   * @throws NullCardException The card type required is not valid.
   */
  public TransitPass buildTransitPass(String cardType, Calendar currTime) throws NullCardException {
    if (cardType.equals("Weekly Pass")) {
      return new WeeklyPass(currTime);
    }
    throw new NullCardException(
        "The card type you typed to TransitPassFactory does not exists" + cardType);
  }
}
