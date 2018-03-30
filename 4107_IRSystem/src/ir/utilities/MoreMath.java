package ir.utilities;

import java.util.*;
import java.io.*;

/**
 * A place to put some additional math functions
 *
 * @author Ray Mooney
 */

public class MoreMath {
  /**
   * Round a double to the given number of decimalPlaces
   */
  public static double roundTo(double num, int decimalPlaces) {
    double factor = Math.pow(10, decimalPlaces);
    return Math.round(num * factor) / factor;
  }

  /**
   * Return logarithm of a given base
   */
  public static double log(double x, double base) {
    return Math.log(x) / Math.log(base);
  }

  public static double log(int x, int base) {
    return log((double) x, (double) base);
  }

  public static double log(double x, int base) {
    return log(x, (double) base);
  }

  public static double log(int x, double base) {
    return log((double) x, base);
  }

  /**
   * Add two vectors and return the vector sum
   */
  public static double[] addVectors(double[] x, double[] y) {
    if (x.length != y.length) {
      throw new IllegalArgumentException("Tried to add vectors of unequal length");
    }
    double[] sum = new double[x.length];
    for (int i = 0; i < x.length; i++)
      sum[i] = x[i] + y[i];
    return sum;
  }

  /**
   * Divide a vector by a scalar and return the resulting vector
   */
  public static double[] vectorDivide(double[] x, double c) {
    double[] result = new double[x.length];
    for (int i = 0; i < x.length; i++)
      result[i] = x[i] / c;
    return result;
  }

  /**
   * Average all of the vectors in a list of vectors and return
   * the average vector.
   */
  public static double[] averageVectors(ArrayList<double[]> list) {
    double[] result = new double[list.get(0).length];
    for (double[] vector : list)
      result = addVectors(result, vector);
    return vectorDivide(result, list.size());
  }

  /**
   * Print a vector in the form [x,y,...z] to standard out
   */
  public static void printVector(double[] x) {
    printVector(x, System.out);
  }

  /**
   * Print a vector in the form [x,y,...z] to the print stream
   */
  public static void printVector(double[] x, PrintStream out) {
    out.print("[");
    for (int i = 0; i < x.length; i++) {
      out.print(x[i]);
      if (i != (x.length - 1))
        out.print(", ");
    }
    out.print("]");
  }

  public static double vectorLength(double[] values) {
    double sumSquares = 0;
    for (double value: values) {
      sumSquares = sumSquares + Math.pow(value, 2);
    }
    return Math.sqrt(sumSquares);
  }

  public static double vectorOneNorm(double[] values) {
    double sum = 0;
    for (double value : values) {
      sum = sum + Math.abs(value);
    }
    return sum;
  }

}
