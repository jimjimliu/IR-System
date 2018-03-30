package ir.eval;

/**
 * A lightweight object for storing a pair of recall precision measures
 *
 * @author Ray Mooney
 */
public class RecallPrecisionPair {

  public double recall;
  public double precision;

  public RecallPrecisionPair(double recall, double precision) {
    this.recall = recall;
    this.precision = precision;
  }

  public String toString() {
    return "{" + recall + "," + precision + "}";
  }

}
