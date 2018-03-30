package ir.vsr;

/**
 * A lightweight object for storing information about a retrieved Document.
 *
 * @author Ray Mooney
 */
public class Retrieval implements Comparable {

  /**
   * A reference to the Document being retrieved
   */
  public DocumentReference docRef;
  /**
   * The score given to this document by a retrieval engine. Higher scores
   * mean it is more relevant to the query
   */
  public double score;

  /**
   * Create a retrieval with these values
   */
  public Retrieval(DocumentReference docRef, double score) {
    this.docRef = docRef;
    this.score = score;
  }

  /**
   * Compares this Retrieval to another for sorting from best to worst.
   *
   * @param obj The Retrieval to compare with.
   * @return -1 if better than obj, 0 if same, 1 if worse than obj
   *         since this will produce a descending sort from best to worst.
   * @see java.util.Arrays#sort
   */
  public int compareTo(Object obj) {
    Retrieval retrieval = (Retrieval) obj;
    if (score == retrieval.score)
      return 0;
    else if (score > retrieval.score)
      return -1;
    else return 1;
  }

}
