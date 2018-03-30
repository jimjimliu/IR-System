package ir.classifiers;

/**
 * Utility class for generating average result curves.
 * Gives vector of sample numerical results to be averaged for a given point on a curve.
 *
 * @author Ray Mooney
 */
public class PointResults {

  /**
   * Point on curve at which results are for
   */
  protected double point;

  /**
   * Sampled values of result at this point
   */
  protected double[] results;

  /**
   * Create a vector of results for a point
   */
  public PointResults(int numResults) {
    results = new double[numResults];
  }

  public double getPoint() {
    return point;
  }

  public void setPoint(double point) {
    this.point = point;
  }

  /**
   * Set the nth result
   */
  public void addResult(int numResult, double result) {
    results[numResult] = result;
  }

  public double[] getResults() {
    return results;
  }

  public String toString() {
    String result = "<" + point + ": " + results[0];
    for (int i = 1; i < results.length; i++) {
      result = result + "," + results[i];
    }
    return result + ">";
  }

}
