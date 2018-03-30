package ir.classifiers;

import java.io.*;
import java.util.*;

import ir.vsr.*;
import ir.utilities.*;

/**
 * Implements the NaiveBayes Classifier with Laplace smoothing. Stores probabilities
 * internally as logs to prevent underflow problems.
 *
 * @author Sugato Basu, Prem Melville, and Ray Mooney
 */
public class NaiveBayes extends Classifier {
  /**
   * Flag to set Laplace smoothing when estimating probabilities
   */
  boolean isLaplace = true;

  /**
   * Small value to be used instead of 0 in probabilities, if Laplace smoothing is not used
   */
  double EPSILON = 1e-6;

  /**
   * Stores the training result, set by the train function
   */
  BayesResult trainResult;

  /**
   * Name of classifier
   */
  public static final String name = "NaiveBayes";

  /**
   * Number of categories
   */
  int numCategories;

  /**
   * Number of features
   */
  int numFeatures;

  /**
   * Number of training examples, set by train function
   */
  int numExamples;

  /**
   * Flag for debug prints
   */
  boolean debug = false;

  /**
   * Create a naive Bayes classifier with these attributes
   *
   * @param categories The array of Strings containing the category names
   * @param debug      Flag to turn on detailed output
   */
  public NaiveBayes(String[] categories, boolean debug) {
    this.categories = categories;
    this.debug = debug;
    numCategories = categories.length;
  }

  /**
   * Sets the debug flag
   */
  public void setDebug(boolean bool) {
    debug = bool;
  }

  /**
   * Sets the Laplace smoothing flag
   */
  public void setLaplace(boolean bool) {
    isLaplace = bool;
  }

  /**
   * Sets the value of EPSILON (default 1e-6)
   */
  public void setEpsilon(double ep) {
    EPSILON = ep;
  }

  /**
   * Returns the name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns value of EPSILON
   */
  public double getEpsilon() {
    return EPSILON;
  }

  /**
   * Returns training result
   */
  public BayesResult getTrainResult() {
    return trainResult;
  }

  /**
   * Returns value of isLaplace
   */
  public boolean getIsLaplace() {
    return (isLaplace);
  }

  /**
   * Trains the Naive Bayes classifier - estimates the prior probs and calculates the
   * counts for each feature in different categories
   *
   * @param trainExamples The vector of training examples
   */
  public void train(List<Example> trainExamples) {
    trainResult = new BayesResult();
    numExamples = trainExamples.size();
    //calculate class priors
    trainResult.setClassPriors(calculatePriors(trainExamples));
    //calculate counts of feature for each class
    trainResult.setFeatureTable(conditionalProbs(trainExamples));
    if (debug) {
      displayProbs(trainResult.getClassPriors(), trainResult.getFeatureTable());
    }
  }

  /**
   * Categorizes the test example using the trained Naive Bayes classifier, returning true if
   * the predicted category is same as the actual category
   *
   * @param testExample The test example to be categorized
   */
  public boolean test(Example testExample) {
    // calculate posterior probs
    double[] posteriorProbs = calculateProbs(testExample);
    // predicted class
    int predictedClass = argMax(posteriorProbs);
    if (debug) {
      System.out.print("Document: " + testExample.name + "\nResults: ");
      for (int j = 0; j < numCategories; j++) {
        System.out.print(categories[j] + "(" + posteriorProbs[j] + ")\t");
      }
      System.out.println("\nCorrect class: " + testExample.getCategory() + ", Predicted class: " + predictedClass + "\n");
    }
    return (predictedClass == testExample.getCategory());
  }

  /**
   * Calculates the class priors
   *
   * @param trainExamples The training examples from which class priors will be estimated
   */
  protected double[] calculatePriors(List<Example> trainExamples) {
    double[] classCounts = new double[numCategories];

    //init class counts
    for (int i = 0; i < numCategories; i++)
      classCounts[i] = 0;

    //increment the count of the class that each example belongs to
    for (Example ex : trainExamples) {
      classCounts[ex.getCategory()]++;
    }

    // Get log probs from counts, with Laplace smoothing if specified
    for (int i = 0; i < numCategories; i++) {
      if (isLaplace)
        classCounts[i] = Math.log((classCounts[i] + 1) / (numExamples + numCategories));
      else
        classCounts[i] = Math.log(classCounts[i] / numExamples);
    }
    if (debug) {
      System.out.println("\nLog Class Priors:");
      for (int i = 0; i < numCategories; i++)
        System.out.print(classCounts[i] + " ");
      System.out.println();
    }
    return classCounts;
  }

  /**
   * Calculates the conditional probs of each feature in the different categories
   *
   * @param trainExamples The training examples from which counts will be estimated
   */
  protected Hashtable<String, double[]> conditionalProbs(List<Example> trainExamples) {
    // Initialize hashtable giving conditional prob of each class given a feature
    Hashtable<String, double[]> featureHash = new Hashtable<String, double[]>();
    double[] totalCounts = new double[numCategories]; // stores total count of all features in each category

    for (int i = 0; i < numCategories; i++)
      totalCounts[i] = 0;

    for (Example currentExample : trainExamples) {
      if (debug) {
        System.out.println("\nExample: " + currentExample);
        System.out.println("Number of tokens: " + currentExample.getHashMapVector().hashMap.size());
      }
      for (Map.Entry<String, Weight> entry : currentExample.getHashMapVector().entrySet()) {
        // An entry in the HashMap maps a token to a Weight
        String token = entry.getKey();
        // The count for the token is in the value of the Weight value
        int count = (int) entry.getValue().getValue();
        double[] countArray; // stores counts for current feature
        if (debug)
          System.out.println("Counts of token: " + token);
        if (!featureHash.containsKey(token)) {
          countArray = new double[numCategories]; //create a new array
          for (int m = 0; m < numCategories; m++)
            countArray[m] = 0.0; //init to 0
          featureHash.put(token, countArray); //add to hashtable
        } else {
          // retrieve existing array from hashtable
          countArray = featureHash.get(token);
        }
        countArray[currentExample.getCategory()] += count;
        totalCounts[currentExample.getCategory()] += count;
        if (debug) {
          for (int k = 0; k < countArray.length; k++)
            System.out.print(countArray[k] + " ");
          System.out.println();
        }
      }
    }
    numFeatures = featureHash.size();
    //We can now compute the log probabilities
    if (debug) {
      System.out.println("\nLog Probs before multiplying priors...\n");
    }
    for (Map.Entry<String, double[]> entry : featureHash.entrySet()) {
      String token = entry.getKey();
      double[] countArray = entry.getValue();
      for (int j = 0; j < numCategories; j++) {
        if (isLaplace) //Laplace smoothing
          countArray[j] = (countArray[j] + 1) / (totalCounts[j] + numFeatures);
        else {
          if (countArray[j] == 0)
            countArray[j] = EPSILON; // to avoid 0 counts when no Laplace smoothing
          else
            countArray[j] = countArray[j] / totalCounts[j];
        }
        countArray[j] = Math.log(countArray[j]); //take log of probability
      }
      if (debug) {
        System.out.println("Log probs of " + token);
        for (int k = 0; k < countArray.length; k++)
          System.out.print(countArray[k] + " ");
        System.out.println();
      }
    }
    return (featureHash);
  }

  /**
   * Calculates the prob of the testExample being generated by each category
   *
   * @param testExample The test example to be categorized
   */

  protected double[] calculateProbs(Example testExample) {
    //set initial probabilities to the prior probs
    double[] probs = trainResult.getClassPriors().clone();
    Hashtable<String, double[]> hashTable = trainResult.getFeatureTable();
    for (Map.Entry<String, Weight> entry : testExample.getHashMapVector().entrySet()) {
      // An entry in the HashMap maps a token to a Weight
      String token = entry.getKey();
      // The count for the token is in the value of the Weight
      int count = (int) entry.getValue().getValue();
      if (hashTable.containsKey(token)) {//ignore unknowns
        double[] countArray = hashTable.get(token); // stores the category array for one token
        for (int k = 0; k < numCategories; k++)
          probs[k] += count * countArray[k];//multiplying the probs == adding the logs
      }
    }
    return probs;
  }

  /**
   * Displays the probs for each feature in the different categories
   *
   * @param classPriors Prior probs
   * @param featureHash Feature hashtable after training
   */
  protected void displayProbs(double[] classPriors, Hashtable<String, double[]> featureHash) {
    System.out.println("\nAfter multiplying priors...");
    for (Map.Entry<String, double[]> entry : featureHash.entrySet()) {
      String token = entry.getKey();
      double[] probs = entry.getValue();
      System.out.print("\nFeature: " + token + ", Probs: ");
      for (int num = 0; num < probs.length; num++) {
        //double posterior = classPriors[num]+probs[num];
        double posterior = Math.pow(Math.E, classPriors[num] + probs[num]);
        System.out.print(" " + posterior);
      }
    }
    System.out.println();
  }
}
