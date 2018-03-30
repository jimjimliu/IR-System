package ir.classifiers;

import java.util.*;

import ir.vsr.*;

/**
 * An object to hold training or test examples for categorization.
 * Stores the name, category and HashMapVector representation of
 * the example.
 *
 * @author Sugato Basu, Prem Melville, and Ray Mooney
 */

public class Example {

  /**
   * Name of the example
   */
  protected String name;

  /**
   * Category index of the example
   */
  protected int category;

  /**
   * Representation of the example as a vector of (feature -> weight) mappings
   */
  protected HashMapVector hashVector;

  /**
   * fileDocument object for the example
   */
  protected FileDocument document;

  public Example(HashMapVector input, int cat, String id, FileDocument doc) {
    hashVector = input;
    category = cat;
    name = id;
    document = doc;
  }

  /**
   * Sets the name of the example
   */
  public void setName(String id) {
    name = id;
  }

  /**
   * Returns the name of the example
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the category of the example
   */
  public void setCategory(int cat) {
    category = cat;
  }

  /**
   * Returns the category of the example
   */
  public int getCategory() {
    return category;
  }

  /**
   * Sets the hashVector of the example
   */
  public void setHashMapVector(HashMapVector v) {
    hashVector = v;
  }

  /**
   * Returns the hashVector of the example
   */
  public HashMapVector getHashMapVector() {
    return hashVector;
  }

  /**
   * Sets the document of the example
   */
  public void setDocument(FileDocument doc) {
    document = doc;
  }

  /**
   * Returns the document of the example
   */
  public FileDocument getDocument() {
    return document;
  }

  /**
   * Returns the String representation of the example object
   */
  public String toString() {
    String str;
    str = "Name:" + name + ", Category: " + category + "\n";
    return str;
  }
}
