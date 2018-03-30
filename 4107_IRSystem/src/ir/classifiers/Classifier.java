package ir.classifiers;

import java.util.*;

import ir.vsr.*;

/**
 * Abstract class specifying the functionality of a classifier. Provides methods for
 * training and testing a classifier
 *
 * @author Sugato Basu and Yuk Wah Wong
 */

public abstract class Classifier {

  /**
   * Used for breaking ties in argMax()
   */
  protected static Random random;

  /**
   * Array of categories (classes) in the data
   */
  protected String[] categories;

  static {
    random = new Random();
  }

  /**
   * The name of a classifier
   *
   * @return the name of a particular classifier
   */
  public abstract String getName();

  /**
   * Returns the categories (classes) in the data
   *
   * @return an array containing strings describing the categories
   */
  public String[] getCategories() {
    return categories;
  }

  /**
   * Trains the classifier on the training examples
   *
   * @param trainingExamples a list of Example objects that will be used
   *                         for training the classifier
   */
  public abstract void train(List<Example> trainingExamples);

  /**
   * Returns true if the predicted category of the test example matches the correct category,
   * false otherwise
   */
  public abstract boolean test(Example testExample);

  /**
   * Returns the array index with the maximum value
   *
   * @param results Array of doubles whose index with max value is to be found.
   *                Ties are broken randomly.
   */
  protected int argMax(double[] results) {
    ArrayList<Integer> maxIndices = new ArrayList<Integer>();
    maxIndices.add(0);
    double max = results[0];

    for (int i = 1; i < results.length; i++) {
      if (results[i] > max) {
        max = results[i];
        maxIndices.clear();
        maxIndices.add(i);
      } else if (results[i] == max) {
        maxIndices.add(i);
      }
    }
    int returnIndex;
    if (maxIndices.size() > 1) {
      // break ties randomly
      int winnerIdx = random.nextInt(maxIndices.size());
      returnIndex = maxIndices.get(winnerIdx);
    } else {
      returnIndex = maxIndices.get(0);
    }
    return (returnIndex);
  }
}





