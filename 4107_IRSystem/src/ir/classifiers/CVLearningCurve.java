package ir.classifiers;

import java.io.*;
import java.util.*;

import ir.utilities.*;

/**
 * Gives learning curves with K-fold cross validation for a classifier.
 *
 * @author Sugato Basu and Ray Mooney
 */
public class CVLearningCurve {
  /**
   * Stores all the examples for each class
   */
  protected Vector<Example>[] totalExamples;

  /**
   * foldBins[i][j] stores the examples for class i in fold j. This stores the training-test splits for all the folds
   */
  protected Vector<Example>[][] foldBins;

  /**
   * The classifier for which K-fold CV learning curve has to be generated
   */
  protected Classifier classifier;

  /**
   * Seed for random number generator
   */
  protected long randomSeed;

  /**
   * Number of classes in the data
   */
  protected int numClasses;

  /**
   * Total number of training examples per fold
   */
  protected int totalNumTrain;

  /**
   * Number of folds of cross validation to run
   */
  protected int numFolds;

  /**
   * Points on the X axis (percentage of train data) to plot
   */
  protected double[] points;

  /**
   * Default points
   */
  protected static double[] DEFAULT_POINTS = {0.0, 0.01, 0.05, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1};

  /**
   * Flag for debug display
   */
  protected boolean debug = false;

  /**
   * Total Training time
   */
  protected double trainTime;

  /**
   * Total Testing time
   */
  protected double testTime;

  /**
   * Total number of examples tested in test time
   */
  protected int testTimeNum;

  /**
   * Accuracy results for test data, one PointResults for each point on the curve
   */
  protected PointResults[] testResults;

  /**
   * Accuracy results for training data, one PointResults for each point on the curve
   */
  protected PointResults[] trainResults;

  /**
   * Creates a CVLearning curve object
   *
   * @param nfolds   Number of folds of CV to perform
   * @param c        Classifier on which to perform K-fold CV
   * @param examples List of examples.
   * @param points   Points (in percentage of full train set) to plot on learning curve
   * @param debug    Debugging flag to set verbose trace printing
   */
  @SuppressWarnings("unchecked")
  public CVLearningCurve(int nfolds, Classifier c, List<Example> examples, double[] points,
                         long randomSeed, boolean debug) {
    if (nfolds < 2) {
      throw new IllegalArgumentException("Cannot have less than 2 folds");
    }
    numFolds = nfolds;
    classifier = c;
    numClasses = c.getCategories().length;
    totalExamples = new Vector[numClasses];
    foldBins = new Vector[numClasses][numFolds];
    setTotalExamples(examples);
    this.points = points;
    // Initialize results for each point to be plotted on the curve
    testResults = new PointResults[points.length];
    trainResults = new PointResults[points.length];
    this.randomSeed = randomSeed;
    this.debug = debug;
    trainTime = testTime = 0;
  }

  /**
   * Creates a CVLearning curve object with 10 folds and default points
   *
   * @param c        Classifier on which to perform K-fold CV
   * @param examples List of examples.
   */
  public CVLearningCurve(Classifier c, List<Example> examples) {
    this(10, c, examples, DEFAULT_POINTS, 1, false);
  }

  /**
   * Return classifier
   */
  public Classifier getClassifier() {
    return classifier;
  }

  /**
   * Set the classifier
   */
  public void setClassifier(Classifier c) {
    classifier = c;
  }

  /**
   * Return all the examples
   */
  public Vector[] getTotalExamples() {
    return totalExamples;
  }

  /**
   * Set all the examples
   */
  public void setTotalExamples(Vector<Example>[] data) {
    totalExamples = data;
  }

  /**
   * Return the fold Bins
   */
  public Vector<Example>[][] getFoldBins() {
    return foldBins;
  }

  /**
   * Set the fold Bins
   */
  public void setFoldBins(Vector<Example>[][] bins) {
    foldBins = bins;
  }

  /**
   * Sets the totalExamples by partitioning examples into categories to
   * get a stratified sample
   */
  public void setTotalExamples(List<Example> examples) {
    totalNumTrain = (int) Math.round((1.0 - 1.0 / numFolds) * examples.size());
    for (Example example : examples) {
      int category = example.getCategory();
      if (totalExamples[category] == null)
        totalExamples[category] = new Vector<Example>();
      totalExamples[category].add(example);
    }
  }

  /**
   * Run a CV learning curve test and print total training and test time
   * and generate an averge learning curve plot output files suitable
   * for gunuplot
   */
  public void run() throws Exception {
    System.out.println("Generating 10 fold CV learning curves...");
    trainAndTest();
    System.out.println();
    System.out.println("Total Training time in seconds: " + trainTime / 1000.0);
    System.out.println("Testing time per example in milliseconds: " +
        MoreMath.roundTo(testTime / testTimeNum, 2));
    // Create Gnuplot of learning curve
    makeGnuplotFile(trainResults, classifier.getName() + "Train");
    System.out.println("GNUPLOT train accuracy file is " + classifier.getName() + "Train.gplot");
    makeGnuplotFile(testResults, classifier.getName());
    System.out.println("GNUPLOT test accuracy file is " + classifier.getName() + ".gplot");
  }

  /**
   * Run training and test for each point to be plotted, gathering a result for
   * each fold.
   */
  public void trainAndTest() {
    // randomly mix the training examples in each category
    randomizeOrder();
    // create foldBins from totalExamples -- effectively creates the
    // training-test splits for each fold
    binExamples();
    // Gather results for each point (number of examples) to be plotted 
    // on the learning curve
    for (int i = 0; i < points.length; i++) {
      double percent = points[i];
      System.out.println("Train Percentage: " + 100 * percent + "%");
      // Initialize PointResults for training and test accuracy for
      // this point
      testResults[i] = new PointResults(numFolds);
      trainResults[i] = new PointResults(numFolds);
      // Train and test for each fold for this point
      for (int fold = 0; fold < numFolds; fold++) {
        System.out.println("  Calculating results for fold " + fold);
        // Creates training data for this fold, from the first
        // percent data in each of the training folds
        Vector<Example> train = getTrainCV(fold, percent);
        // Creates testing data for this fold
        Vector<Example> test = getTestCV(fold);
        // Get testing results for this fold and percent setting
        trainAndTestFold(train, test, fold, testResults[i], trainResults[i]);
        if (debug) {
          System.out.println("Training on:\n" + train);
          System.out.println("Testing on:\n" + test);
        }
      }
    }
  }

  /**
   * Train and test on given example sets for the given fold:
   *
   * @param train             The training dataset vector
   * @param test              The testing dataset vector
   * @param fold              The current fold number
   * @param testPointResults  train accuracy PointResults for this point
   * @param trainPointResults test accuracy PointResults for this point
   */
  public void trainAndTestFold(Vector<Example> train, Vector<Example> test, int fold,
                               PointResults testPointResults, PointResults trainPointResults) {
    long startTime = System.currentTimeMillis();
    // train the classifier on train data
    classifier.train(train);
    double timeTaken = System.currentTimeMillis() - startTime;
    trainTime += timeTaken;

    // Test on test data and measure time and accuracy
    int testCorrect = 0;
    startTime = System.currentTimeMillis();
    for (Example example : test) {
      // classify the test example
      if (classifier.test(example))
        testCorrect++;
    }
    timeTaken = System.currentTimeMillis() - startTime;
    testTime += timeTaken;
    testTimeNum += test.size();

    testPointResults.setPoint(train.size());
    double testAccuracy = 1.0 * testCorrect / test.size();
    testPointResults.addResult(fold, testAccuracy);

    // Test on training data and measure accuracy
    int trainCorrect = 0;
    for (Example example : train) {
      // classify the test example
      if (classifier.test(example))
        trainCorrect++;
    }
    trainPointResults.setPoint(train.size());
    double trainAccuracy = 1.0 * trainCorrect / train.size();
    if (train.size() == 0) trainAccuracy = 1.0;
    trainPointResults.addResult(fold, trainAccuracy);

    System.out.println("    Train Accuracy = " + MoreMath.roundTo(100 * trainAccuracy, 3) +
        "%; Test Accuracy = " + MoreMath.roundTo(100 * testAccuracy, 3) + "%");
  }

  /**
   * Set the fold Bins from the total Examples -- this effectively
   * stores the training-test split
   */
  public void binExamples() {
    for (int classNum = 0; classNum < numClasses; classNum++) {
      for (int j = 0; j < numFolds; j++) {
        foldBins[classNum][j] = new Vector<Example>();
      }
      for (int j = 0; j < totalExamples[classNum].size(); j++) {
        int foldNum = j % numFolds;
        foldBins[classNum][foldNum].add(totalExamples[classNum].get(j));
      }
    }
  }

  /**
   * Creates the training set for one fold of a cross-validation
   * on the dataset.
   *
   * @param foldnum The fold for which training set is to be constructed
   * @param percent Percentage of examples to use for training in this fold
   * @return The training data
   */
  public Vector<Example> getTrainCV(int foldnum, double percent) {
    Vector<Example> train = new Vector<Example>();
    // Compute number of train examples to use
    int numTrain = (int) Math.round(percent * totalNumTrain);
    // Collect enough from other fold bins to get this many training
    for (int j = 0; j < numFolds; j++) {
      // Avoid test fold for disjoint training
      if (j != foldnum) {
        int foldSize = sizeOfFold(j);
        // If adding this whole fold will not go over the number of
        // training examples still needed...
        if ((train.size() + foldSize) <= numTrain) {
          // Add all the examples in the fold to training data
          for (int i = 0; i < numClasses; i++) {
            train.addAll(foldBins[i][j]);
          }
        }
        // Otherwise need to add just a fraction of this fold to complete
        // train data
        else {
          double fractionNeeded = ((double) (numTrain - train.size())) / foldSize;
          // Add needed fraction of data in each class in this fold
          for (int i = 0; i < numClasses; i++) {
            // Number of examples needed from this fold and class
            int len = (int) Math.round(fractionNeeded * foldBins[i][j].size());
            for (int k = 0; k < len; k++) {
              train.add(foldBins[i][j].get(k));
            }
          }
          break;
        }
      }
    }
    System.out.println("    Number of training examples: " + train.size());
    return train;
  }

  /**
   * Computes the total number of examples in given fold
   */
  protected int sizeOfFold(int foldNum) {
    int size = 0;
    for (int i = 0; i < numClasses; i++) {
      size += foldBins[i][foldNum].size();
    }
    return size;
  }

  /**
   * Creates the testing set for one fold of a cross-validation
   * on the dataset.
   *
   * @param foldnum The fold which is to be used as testing data
   * @return The test data
   */
  public Vector<Example> getTestCV(int foldnum) {
    Vector<Example> test = new Vector<Example>();
    for (int i = 0; i < numClasses; i++)
      test.addAll(foldBins[i][foldnum]);

    return test;
  }

  /**
   * Shuffles the examples in totalExamples so that they are ordered randomly.
   */
  private void randomizeOrder() {
    Random random = new Random(randomSeed);
    for (int i = 0; i < numClasses; i++) {
      int maxSize = totalExamples[i].size();
      for (int j = maxSize - 1; j > 0; j--) {
        int next = random.nextInt(maxSize);
        Example temp = totalExamples[i].get(j);
        totalExamples[i].set(j, totalExamples[i].get(next));
        totalExamples[i].set(next, temp);
      }
    }
  }

  /**
   * Write out the final learning curve data.
   * One line for each value: [training set size, accuracy]
   * This is the format needed for GNUPLOT.
   *
   * @param allResults Array of results from which GNUPLOT data is generated
   * @param name       Name of classifier
   */
  void writeCurve(PointResults[] allResults, String name) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(name + ".data"));

    for (PointResults pointResults : allResults) {
      double accuracy = 0;
      double point = pointResults.getPoint();
      double[] results = pointResults.getResults();
      for (double result : results) {
        accuracy += result;
      }
      // find average accuracy across the K folds
      accuracy /= results.length;
      out.println(Math.round(point) + "\t" + accuracy);
    }
    out.close();
  }

  /**
   * Write out an appropriate input file for GNUPLOT for the final
   * learning curve  to the output file with a ".gplot" extension.
   * See GNUPLOT documentation.
   *
   * @param allResults Array of results from which GNUPLOT data is generated
   * @param name Name of classifier
   */
  void makeGnuplotFile(PointResults[] allResults, String name) throws IOException {
    writeCurve(allResults, name);
    File graphFile = new File(name + ".gplot");
    PrintWriter out = new PrintWriter(new FileWriter(graphFile));
    out.print("set xlabel \"Size of training set\"\nset ylabel \"Accuracy\"\n\nset terminal postscript color\nset size 0.75,0.75\n\nset style data linespoints\nset key bottom right\n\nplot \'" + name + ".data\' title \"" + name + "\"");
    out.close();
  }
}
