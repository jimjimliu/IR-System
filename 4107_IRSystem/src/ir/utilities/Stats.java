package ir.utilities;

import java.util.*;
import java.io.*;

/**
 * A place to put statistical routines
 *
 * @author Ray Mooney
 */

public class Stats {

  /**
   * Return the arithmetic mean of the argument <code>values</code>.
   */
  public static double mean(double[] values) {
    double sum = 0;
    for (double value : values) {
      sum = sum + value;
    }
    return sum / values.length;
  }

  /**
   * Return the standard deviation of the argument <code>values</code>.
   * Standard deviation is the square root of the variance.
   */
  public static double standardDeviation(double[] values) {
    double sum = 0;
    double mean = mean(values);
    for (double value : values) {
      sum = sum + Math.pow(value - mean, 2);
    }
    return Math.sqrt(sum / values.length);
  }

  /**
   * Return the covariance between the vectors <code>x</code> and <code>y</code>.
   */
  public static double covariance(double[] x, double[] y) {
    if (x.length != y.length) {
      throw new IllegalArgumentException("Covariance error: Vectors not of the same length");
    }
    double sum = 0;
    double xMean = mean(x);
    double yMean = mean(y);
    for (int i = 0; i < x.length; i++) {
      sum = sum + (x[i] - xMean) * (y[i] - yMean);
    }
    return sum / x.length;
  }

  /**
   * Return the Pearson Correlation between the vectors <code>x</code> and <code>y</code>.
   */
  public static double pearsonCorrelation(double[] x, double[] y) {
    return covariance(x, y) / (standardDeviation(x) * standardDeviation(y));
  }
}

