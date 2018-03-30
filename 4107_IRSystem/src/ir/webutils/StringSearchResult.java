package ir.webutils;


/**
 * Lightweight object for storing both the number of DIFFERENT strings
 * in a set of search strings that are found in a text as well as the total number
 * of occurrences in the text of ANY of the strings in the set.
 *
 * @author Ray Mooney
 */

public class StringSearchResult extends Object {

  /**
   * Number of different strings found
   */
  public int numberFound;
  /**
   * Total number of occurrences of any of the strings
   */
  public int numberOccurrences;

  /**
   * Construct result with a given numberFound and numberOccurrences
   */
  public StringSearchResult(int numberFound, int numberOccurrences) {
    this.numberFound = numberFound;
    this.numberOccurrences = numberOccurrences;
  }

}
