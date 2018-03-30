package ir.eval;

import java.io.*;
import java.util.*;
import java.lang.*;

import ir.utilities.*;
import ir.vsr.*;

/**
 * Version of Experiment for queries that have continuously rated 
 * gold-standard document relevance judgements and includes evaluation 
 * with NDCG. Computes and reports NDCG values at all ranks up to a
 * specified limit (NDCGlimit)
 * 
 * Assumes the format of the results for a query in the queries file is a 
 * list of pairs of document names followed by a relevance score between 0 and 1
 * 
 * @author Ray Mooney
 */

public class ExperimentRated extends Experiment {

  /**
   * HashMap that stores the mapping of document names to their gold-standard relevance ratings
   */
  public Map<String, Double> ratingsMap = new HashMap<String, Double>();

  /**
   * The maximum N for computing NDCG @ N
   */
    public static int NDCGlimit = 10;

  /**
   * Current sum of NDCG values @ all levels up to NDCGlimit
   * Updated when processing each query.
   * Eventually used to compute average NDCG across all queries
   */
  public double[] NDCGvalues = new double[NDCGlimit];

  /**
   * Constructor that just calls the Experiment constructor
   */
  public ExperimentRated(File corpusDir, File queryFile, File outFile, short docType, boolean stem)
      throws IOException {
      super(corpusDir, queryFile, outFile, docType, stem);
  }

  /**
   * Process the next query read from the query file reader and evaluate
   * results compared to known relevant docs also read from the query file.
   * This version computes NDCG results for each query, storing summed 
   * results in NDCGvalues
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

    // Get the correct retrievals
    ArrayList<String> correctRetrievals = new ArrayList<String>();
    getCorrectRatedRetrievals(in, correctRetrievals);

    // Generate Recall/Precision points and save in rpResults
    rpResults.add(evalRetrievals(retrievals, correctRetrievals));

    // Update the NDCG values for this query
    UpdateNDCG(retrievals, correctRetrievals);    

    // Read the blank line delimiter between queries in the query file
    String line = in.readLine();
    if (!(line == null || line.trim().equals(""))) {
      System.out.println("\nCould not find blank line after query, bad queryFile format");
      System.exit(1);
    }
    return true;
  }

    /**
     *  Read the known relevant docs with gold-standard relevance scores from query file and parse them
     *  into an ArrayList of String's of relevant file names which ratingsMap maps to these relevance scores
     *  Assume the format is a list of pairs of document names followed by a relevance score between 0 and 1
     */
    void getCorrectRatedRetrievals(BufferedReader in, ArrayList<String> correctRetrievals) throws IOException {
	String line = in.readLine();
	ArrayList<String> ratedRetrievals = MoreString.segment(line, ' ');
	// Process input 2 items at a time (filename followed by score)
	for (int i = 0; i < ratedRetrievals.size(); i=i+2) {    
	    correctRetrievals.add(ratedRetrievals.get(i));
	    ratingsMap.put(ratedRetrievals.get(i), Double.valueOf(ratedRetrievals.get(i+1)));
	}
	System.out.println(correctRetrievals.size() + " truly relevant documents.");
    }

    /**
     * Compute NDCGs for this query and update the total NDCGvalues to eventually compute an average
     */
    void UpdateNDCG(Retrieval[] retrievals,  ArrayList<String> correctRetrievals) {
        // Vector of gains at each ranked retrieval
	double[] gains = new double[NDCGlimit];
	System.out.println("\nComputing NDCG for this query");
	// Examine each ranked retrieval in order to compute gain at each rank
	for (int i = 0; i < NDCGlimit; i++) {
	    String fileName = retrievals[i].docRef.file.getName();
	    // Check if the ith retrieval is in the set of gold-standard relevant docs
	    if (correctRetrievals.contains(fileName)) 
		// If so, set the ith gain to the relevance value of this document
		gains[i] = ratingsMap.get(fileName).doubleValue();
	}
	System.out.println("Ranked Retrieval Gains:" + Arrays.toString(gains));
	// Compute the discounted cummulative gains from the gains array
	computeDCGs(gains);
	System.out.println("DCGs:" + Arrays.toString(gains));
	// Vector of gains for all of the gold-standard retrievals
	double[] allGains = new double[correctRetrievals.size()];
	for (int i = 0; i < allGains.length; i++) 
	    allGains[i] = ratingsMap.get(correctRetrievals.get(i)).doubleValue();
	// Sort gains in ascending order
	Arrays.sort(allGains);
	// Set ideal gains to the highest N of allGains by effectively reversing the 
	// end of this array
	double[] idealGains = new double[NDCGlimit];
	for (int i = 0; i < NDCGlimit; i++) 
	    // check that index is not larger than the number of gold-standard gains
	    if (i < allGains.length)
		// Set the ith ideal gain to the ith highest value in allGains
		idealGains[i] = allGains[allGains.length - i - 1];
	System.out.println("Ideal Retrieval Gains:" + Arrays.toString(idealGains));
	// Compute the DCG values for the ideal set of ranked retrievals
	computeDCGs(idealGains);
	System.out.println("Ideal DCGs:" + Arrays.toString(idealGains));
	// Normalize the gains to set them to final NDCG values
	for (int i = 0; i < NDCGlimit; i++) 
	    // Divide actual gain by the ideal gain at this rank to get NDCG
	    gains[i] = gains[i]/idealGains[i];
	System.out.println("NDCGs:" + Arrays.toString(gains));
	// Update NDCG totals to maintain running results for averging
	for (int i = 0; i < NDCGlimit; i++) 
	    NDCGvalues[i] += gains[i];
    }

    /**
     * From a ranked array of gains, compute the discounted cummlative gains at each rank
     */
    void computeDCGs(double[] gains) {
	// dcg maintains running sum of discounted cummalative gain
	// initialized to gain of top ranked retrieval
	double dcg = gains[0];
	// Compute dcg for rank 2 and above
	for (int i = 1; i < NDCGlimit; i++) {
	    // compute discounted cummlative gain as defined by NDCG
	    dcg = dcg + gains[i]/MoreMath.log(i+1,2);
	    // Set value in the gains array to the discounted cummlative gain
	    gains[i] = dcg;
	}
    }

    /**
     * Print out the final NDCG values for all ranked positions up to NDCGlimit
     * and write them out to an .ndccg file
     */
    void makeNDCGtable() throws IOException {
	File ndcgFile = new File(outFile.getPath() + ".ndcg");
	PrintWriter out = new PrintWriter(new FileWriter(ndcgFile));
	System.out.println("\nAverage NDCG results:");
	for (int i = 0; i < NDCGlimit; i++) {
	    double avgNDCG = NDCGvalues[i]/rpResults.size();
	    System.out.println("Rank " + (i+1) + ": " + avgNDCG);
	    out.println((i+1) + " " + avgNDCG);
	}
	out.close();

    /**
     * Create gnuplot file for graph
     */
	File ndcgPlotFile = new File(outFile.getPath() + ".ndcg.gplot");
	PrintWriter out2 = new PrintWriter(new FileWriter(ndcgPlotFile));
    out2.print("set xlabel \"Rank\"\nset ylabel \"NDCG\"\n\nset terminal postscript color\nset size 0.75,0.75\n\nset style data linespoints\nset key top right\n\nset xrange [1:" + NDCGlimit + "]\nset yrange [0:1]\n\nplot \'" + outFile.getName()+ ".ndcg" + "\' title \"VSR\"");
    out2.close();
    }

  /**
   * Evaluate retrieval performance on a given query test corpus and
   * generate a recall/precision graph and table of NDCG results.
   * Command format: "Experiment [OPTION]* [DIR] [QUERIES] [OUTFILE]" where:
   * DIR is the name of the directory whose files should be indexed.
   * QUERIES is a file of queries paired with relevant docs 
   * and continuous gold-standard relevance ratings (see queryFile).
   * OUTFILE is the name of the file to put the output. The plot
   * data for the recall precision curve is stored in this file and a
   * gnuplot file for the graph is the same name with a ".gplot" extension
   * and a NDCG result file is the same name with a ".ndcg" extension
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
    ExperimentRated exper = new ExperimentRated(new File(corpusDir), new File(queryFile),
        new File(outFile), docType, stem);
    // Generate a recall precision curve and NDCG results for this dataset
    // makeRpCurve must be first since it calculates the statistics for both
    exper.makeRpCurve();
    exper.makeNDCGtable();
  }
}

