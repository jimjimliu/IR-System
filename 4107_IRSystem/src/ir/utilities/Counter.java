package ir.utilities;

/**
 * A simple wrapper data structure for storing an integer count
 * as an Object that can be put into lists, maps, etc. and then
 * incremented and decremented.
 *
 * @author Ray Mooney
 */

public class Counter {
  /**
   * The integer count
   */
  protected int count = 0;

  /**
   * Increment and return the new count
   */
  public int increment() {
    return ++count;
  }

  /**
   * Increment by n and return the new count
   */
  public int increment(int n) {
    count = count + n;
    return count;
  }

  /**
   * Decrement and return the new count
   */
  public int decrement() {
    return --count;
  }

  /**
   * Decrement by n and return the new count
   */
  public int decrement(int n) {
    count = count - n;
    return count;
  }

  /**
   * Get the current count
   */
  public int getValue() {
    return count;
  }

  /**
   * Set the current count
   */
  public int setValue(int value) {
    count = value;
    return count;
  }
}
