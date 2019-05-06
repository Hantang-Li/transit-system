package transitSystem;

import java.io.Serializable;

/** Represent all the objects that are able to top up. */
public interface AbleTopUp extends Serializable {

  /**
   * Top up a mount of balance to the card.
   *
   * @param fare The fare that will be top up to the card.
   */
  void topUp(Double fare);
}
