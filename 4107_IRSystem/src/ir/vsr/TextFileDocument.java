package ir.vsr;

import java.io.*;
import java.util.*;

/**
 * A normal ASCII text file Document
 *
 * @author Ray Mooney
 */

public class TextFileDocument extends FileDocument {

  /**
   * StringTokenizer delim for tokenizing only alphabetic strings.
   */
  public static final String tokenizerDelim = " \t\n\r\f\'\"\\1234567890!@#$%^&*()_+-={}|[]:;<,>.?/`~";

  /**
   * The tokenizer for lines read from this document.
   */
  protected StringTokenizer tokenizer = null;

  /**
   * Create a new text document for the given file.
   */
  public TextFileDocument(File file, boolean stem) {
    super(file, stem);  // Create a FileDocument
    try {
      // create a StringTokenizer for the first line in the file
      String line = reader.readLine();
      if (line != null) {
        this.tokenizer = new StringTokenizer(line, tokenizerDelim);
      }
      prepareNextToken();  // Prepare the first token
    }
    catch (IOException e) {
      System.out.println("\nCould not read TextFileDocument: " + file);
      System.exit(1);
    }
  }

  /**
   * Create a new text document for the given file name.
   */
  public TextFileDocument(String fileName, boolean stem) {
    this(new File(fileName), stem);
  }

  /**
   * Return the next purely alpha-character token in the document, or null if none left.
   */
  protected String getNextCandidateToken() {
    if (tokenizer == null)
      return null;
    String candidateToken = null;
    try {
      // Loop until you find a line in the file with a token
      while (!tokenizer.hasMoreTokens()) {
        // Read a line from the file
        String line = reader.readLine();
        if (line == null) {
          // End of file, no more tokens, return null
          reader.close();
          return null;
        } else
          // Create a tokenizer for this file line
          tokenizer = new StringTokenizer(line, tokenizerDelim);
      }
      // Get the next token in the current line
      candidateToken = tokenizer.nextToken();
    }
    catch (IOException e) {
      System.out.println("\nCould not read from TextFileDocument: " + file);
      System.exit(1);
    }
    return candidateToken;
  }

  /**
   * For testing, print the bag-of-words vector for a given file
   */
  public static void main(String[] args) throws IOException {
    String fileName = args[0];
    Document doc = new TextFileDocument(fileName, false);
    doc.printVector();
    System.out.println("\nNumber of Tokens: " + doc.numberOfTokens());
  }

}

