package ir.classifiers;

import java.util.*;

/**
 * An object to hold the result of training a NaiveBayes classifier.
 * Stores the class priors and the counts of features in each class.
 *
 * @author Sugato Basu, Prem Melville and Ray Mooney
 */

public class BayesResult {
  /**
   * Stores the prior probabilities of each class
   */
  protected double[] classPriors;

  /**
   * Stores the counts for each feature: an entry in the hashTable stores
   * the array of class counts for a feature
   */
  protected Hashtable<String, double[]> featureTable;

  /**
   * Sets the class priors
   */
  public void setClassPriors(double[] priors) {
    classPriors = priors;
  }

  /**
   * Returns the class priors
   */
  public double[] getClassPriors() {
    return (classPriors);
  }

  /**
   * Sets the feature hash
   */
  public void setFeatureTable(Hashtable<String, double[]> table) {
    featureTable = table;
  }

  /**
   * Returns the feature hash
   */
  public Hashtable<String, double[]> getFeatureTable() {
    return (featureTable);
  }
}
