package transitSystem;

import java.io.Serializable;

/** An Tuple that can store two objects. */
public class Tuple<K, V> implements Serializable {
  public final K zero;
  public final V one;
  /**
   * Constructs a new Tuple which stores two objects
   *
   * @param zero one object which can be used as key
   * @param one another object which indicates the object that key refers to
   */
  Tuple(K zero, V one) {
    this.zero = zero;
    this.one = one;
  }
}
