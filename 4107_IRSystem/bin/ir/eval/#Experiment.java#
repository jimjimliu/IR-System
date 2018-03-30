package ir.eval;

import java.io.*;
import java.util.*;
import java.lang.*;

import ir.utilities.*;
import ir.vsr.*;

/**
 * Contains methods for running evaluation experiments for information
 * retrieval, specifically the generation of recall-precision curves
 * for a given test corpus of query/relevant-documents pairs.
 *
 * @author Ray Mooney, Misha Bilenko
 */

public class Experiment {

  /**
   * The standard recall levels for which we want to plot precision values
   */
  public static final double[] RECALL_LEVELS = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

  /**
   * The directory from which the indexed documents come.
   */
  public File corpusDir = null;

  /**
   * The file with the list of queries and results to be tested.
   * Assumes this file consists of 3 lines for each query:
   * 1) A line of text for the query.
   * 2) A line of filenames from corpusDir that are relevant to this
   * query, filenames must be separated by a space.
   * 3) A blank line as a separator from the next query.
   */
  public File queryFile = null;

  /**
   * The output file where final recall/precision result data is printed.
   */
  public File outFile = null;

  /**
   * The inverted index created for the corpus
   */
  InvertedIndex index = null;

  /**
   * List of recall-precision data for each query, where data for each
   * query is an ArrayList of RecallPrecisionPair's for each possible
   * recall point that can be determined from the ranked retrievals
   */
  ArrayList<ArrayList<RecallPrecisionPair>> rpResults =
      new ArrayList<ArrayList<RecallPrecisionPair>>();

  /**
   * List of interpolatedPrecision values for each query, where each element
   * is a double[] of interpolated precision values for each of the
   * standard recall levels in RECALL_LEVELS. See textbook for details.
   */
  ArrayList<double[]> interpolatedPrecisions = new ArrayList<double[]>();

  /**
   * Array of final average precision values for each of the
   * standard recall levels in RECALL_LEVELS
   */
  double[] averagePrecisions = null;

  /**
   * Create an Experiment object for generating Recall/Precision curves
   *
   * @param corpusDir The directory of files to index.
   * @param queryFile The file of query/relevant-docs pairs to evaluate.
   * @param outFile   File for output precision/recall data.
   * @param docType   The type of documents to index (See docType in DocumentIterator).
   * @param stem      Whether tokens should be stemmed with Porter stemmer.
   */
  public Experiment(File corpusDir, File queryFile, File outFile, short docType, boolean stem)
      throws IOException {
    this.corpusDir = corpusDir;
    this.index = new InvertedIndex(corpusDir, docType, stem, false);
    this.queryFile = queryFile;
    this.outFile = outFile;
  }

  /**
   * Create an Experiment object for generating Recall/Precision curves
   * using a provided InvertedIndex
   *
   * @param index     an InvertedIndex object that contains an indexed document collection
   * @param queryFile The file of query/relevant-docs pairs to evaluate.
   * @param outFile   File for output precision/recall data.
   */
  public Experiment(InvertedIndex index, File queryFile, File outFile)
      throws IOException {
    this.index = index;
    this.queryFile = queryFile;
    this.outFile = outFile;
  }

  /**
   * Process and evaluate all queries and generate recall-precision curve
   */
  public void makeRpCurve() throws IOException {
    processQueries();
    // Use rpResults generated to interpolate a precision values for
    // each standard recall level for each query and store results in
    // interpolatedPrecisions
    for (ArrayList<RecallPrecisionPair> rpPairs : rpResults) {
      interpolatedPrecisions.add(interpolatePrecision(rpPairs));
    }
    // Compute the average precision values
    averagePrecisions = MoreMath.averageVectors(interpolatedPrecisions);
    System.out.println("\nAverage Interpolated Precisions:");
    MoreMath.printVector(averagePrecisions);
    System.out.println("");
    // Write results to output file and Gnuplot file for graphing
    writeRpCurve();
    graphRpCurve();
  }

  /**
   * Process each query in the queryFile and store evaluated results
   * in rpResults
   */
  void processQueries() throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(queryFile));
    while (processQuery(in)) ;
    in.close();
    // System.out.println("\n" + rpResults);
  }

  /**
   * Process the next query read from the query file reader and evaluate
   * results compared to known relevant docs also read from the query file.
   *
   * @return true if query successfully read, else false if no more queries
   * in query file
   */
  boolean processQuery(BufferedReader in) throws IOException {
    String query = in.readLine();   // get the query
    if (query == null) return false;  // return false if end of file
    System.out.println("\nQuery " + (rpResults.size() + 1) + ": " + query);

    // Process the query and get the ranked retrievals
    Retrieval[] retrievals = index.retrieve(query);
    System.out.println("Returned " + retrievals.length + " documents.");

    // Read the known relevant docs from query file and parse them
    // into an ArrayList of String's of relevant file names.
    String line = in.readLine();
    ArrayList<String> correctRetrievals = MoreString.segment(line, ' ');
    System.out.println(correctRetrievals.size() + " truly relevant documents.");

    // Generate Recall/Precision points and save in rpResults
    rpResults.add(evalRetrievals(retrievals, correctRetrievals));

    // Read the blank line delimiter between queries in the query file
    line = in.readLine();
    if (!(line == null || line.trim().equals(""))) {
      System.out.println("\nCould not find blank line after query, bad queryFile format");
      System.exit(1);
    }
    return true;
  }

  /**
   * Compare retrieved docs to relevant docs and compute recall/precision
   * points.  Goes down ranked retrievals in order, stopping at each
   * relevant document and computing a RecallPrecisionPair for thresholding
   * at that point.
   *
   * @return An ArrayList of RecallPrecisionPair's
   */
  ArrayList<RecallPrecisionPair> evalRetrievals(Retrieval[] retrievals,
                                                ArrayList<String> correctRetrievals) {
    ArrayList<RecallPrecisionPair> rpList = new ArrayList<RecallPrecisionPair>();
    // Number of correctly retrieved docs at any given point
    double goodRetrievals = 0;
    // Examine each ranked retrieval in order to compute rp pairs
    for (int i = 0; i < retrievals.length; i++) {
      // Current number of retrievals considered
      int numRetrieved = i + 1;
      // Check if this retrieval is in the list of relevant docs
      if (correctRetrievals.contains(retrievals[i].docRef.file.getName())) {
        goodRetrievals++;  // This is a relevant retrieval
        // Compute recall and precision for first numRetrieved docs
        double recall = goodRetrievals / correctRetrievals.size();
        double precision = goodRetrievals / numRetrieved;
        System.out.println(MoreString.padToLeft(numRetrieved, 4) +
            " is relevant; Recall = " +
            MoreString.padToLeft(MoreMath.roundTo(100 * recall, 3) + "%", 7) +
            "; Precision = " +
            MoreString.padToLeft(MoreMath.roundTo(100 * precision, 3) + "%", 7));
        // Create a RecallPrecisionPair for this point and add to rpList
        rpList.add(new RecallPrecisionPair(recall, precision));
      }
    }
    return rpList;
  }

  /**
   * Interpolate precision values for each standard recall level
   * in RECALL_LEVELS from the list of rpPairs for a given query.
   * See textbook for details.
   */
  double[] interpolatePrecision(ArrayList<RecallPrecisionPair> rpPairs) {
    // Array of interpolated precisions
    double[] precisions = new double[RECALL_LEVELS.length];
    // Compute precision value for each recall level, starting
    // from the highest and working backwards
    for (int i = RECALL_LEVELS.length - 1; i >= 0; i--) {
      // Stores maximum precision for this recall level.
      // Interpolated precision at level i is the max
      // precision seen (or interpolated) at any recall
      // value between level i and level i+1, inclusive.
      double maxPrecision = 0.0;
      // Check each point in rpPairs to see if it is between
      // recall levels i and i+1, compute the max of these precision values.
      for (RecallPrecisionPair rpPair : rpPairs) {
        if (RECALL_LEVELS[i] <= rpPair.recall &&
            (i == RECALL_LEVELS.length - 1 ||  // no higher level i+1
                rpPair.recall <= RECALL_LEVELS[i + 1])) {
          // If recall in correct interval, update max precision
          if (rpPair.precision > maxPrecision)
            maxPrecision = rpPair.precision;
        }
      }
      // Also consider the previously computed precision level for
      // the next highest recall level i+1, to include in max computation
      if (i != RECALL_LEVELS.length - 1 && precisions[i + 1] > maxPrecision)
        maxPrecision = precisions[i + 1];
      // Set precision at level i to be the proper max interpolated value
      precisions[i] = maxPrecision;
    }
    //  	System.out.print("\nInterpolated Precisions: ");
    //	MoreMath.printVector(precisions);
    // Return vector of final interpolated precisions
    return precisions;
  }

  /**
   * Write out the final interpolated recall/precision graph data.
   * One line for each recall/precision point in the form: 'R-value P-value'.
   * This is the format needed for GNUPLOT.
   */
  void writeRpCurve() throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(outFile));
    for (int i = 0; i < RECALL_LEVELS.length; i++)
      out.println(RECALL_LEVELS[i] + " " + averagePrecisions[i]);
    out.close();
  }

  /**
   * Write out an appropriate input file for GNUPLOT for the final recall
   * precision graph to the output file with a ".gplot" extension.
   * See GNUPLOT documentation.
   */
  void graphRpCurve() throws IOException {
    File graphFile = new File(outFile.getPath() + ".gplot");
    PrintWriter out = new PrintWriter(new FileWriter(graphFile));
    out.print("set xlabel \"Recall\"\nset ylabel \"Precision\"\n\nset terminal postscript color\nset size 0.75,0.75\n\nset style data linespoints\nset key top right\n\nset xrange [0:1]\nset yrange [0:1]\n\nplot \'" + outFile.getName() + "\' title \"VSR\"");
    out.close();
  }

  /**
   * Evaluate retrieval performance on a given query test corpus and
   * generate a recall/precision graph.
   * Command format: "Experiment [OPTION]* [DIR] [QUERIES] [OUTFILE]" where:
   * DIR is the name of the directory whose files should be indexed.
   * QUERIES is a file of queries paired with relevant docs (see queryFile).
   * OUTFILE is the name of the file to put the output. The plot
   * data for the recall precision curve is stored in this file and a
   * gnuplot file for the graph is the same name with a ".gplot" extension.
   * OPTIONs can be
   * "-html" to specify HTML files whose HTML tags should be removed, and
   * "-stem" to specify tokens should be stemmed with Porter stemmer.
   */
  public static void main(String[] args) throws IOException {
    // Parse the arguments into a directory name and optional flag
    String corpusDir = args[args.length - 3];
    String queryFile = args[args.length - 2];
    String outFile = args[args.length - 1];
    short docType = DocumentIterator.TYPE_TEXT;
    boolean stem = false;
    for (int i = 0; i < args.length - 3; i++) {
      String flag = args[i];
      if (flag.equals("-html"))
        // Create HTMLFileDocuments to filter HTML tags
        docType = DocumentIterator.TYPE_HTML;
      else if (flag.equals("-stem"))
        // Stem tokens with Porter stemmer
        stem = true;
      else {
        throw new IllegalArgumentException("Unknown flag: " + flag);
      }
    }
    Experiment exper = new Experiment(new File(corpusDir), new File(queryFile),
        new File(outFile), docType, stem);
    exper.makeRpCurve();
  }
}

