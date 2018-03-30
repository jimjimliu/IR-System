package ir.vsr;

/**
 * A lightweight object for storing information about an occurrence of a token (a.k.a word, term)
 * in a Document.
 *
 * @author Ray Mooney
 */

public class TokenOccurrence {
  /**
   * A reference to the Document where it occurs
   */
  public DocumentReference docRef = null;
  /**
   * The number of times it occurs in the Document
   */
  public int count = 0;

  /**
   * Create an occurrence with these values
   */
  public TokenOccurrence(DocumentReference docRef, int count) {
    this.docRef = docRef;
    this.count = count;
  }

  @Override
  public String toString() {
    return "TokenOccurrence in " + docRef + ", count: " + count;
  }
}
