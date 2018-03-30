package ir.vsr;

import java.util.*;

/**
 * A lightweight object for storing information about a token (a.k.a word, term)
 * in an inverted index.
 *
 * @author Ray Mooney
 */

public class TokenInfo {
  /**
   * The IDF (inverse document frequency) factor for this token
   * which indicates how much to weight an occurence. Tokens that
   * appear in many documents are not very discriminative and therefore
   * weighted less.
   */
  public double idf;

  /**
   * A list of TokenOccurences giving documents where this
   * token occurs
   */
  public List<TokenOccurrence> occList;

  /**
   * Create an initially empty data structure
   */
  public TokenInfo() {
    occList = new ArrayList<TokenOccurrence>();
    idf = 0.0;
  }
}
