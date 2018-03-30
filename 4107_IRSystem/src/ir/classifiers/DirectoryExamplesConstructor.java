package ir.classifiers;

import java.io.*;
import java.util.*;

import ir.vsr.*;

/**
 * Creates a list of examples from a directory where file names contain the
 * category name as a substring.
 *
 * @author Ray Mooney
 */


public class DirectoryExamplesConstructor extends ExamplesConstructor {
  /**
   * Name of the directory where the example files are stored.
   */
  protected String dirName;

  /**
   * Type of document (text or HTML)
   */
  protected short docType;

  /**
   * Flag set to stem words to their root forms
   */
  protected boolean stem;

  /**
   * Array of categories (classes) in the data
   */
  protected String[] categories;

  /**
   * Construct an ExamplesConstructor for the given directory and category labels
   */
  public DirectoryExamplesConstructor(String dirName, String[] categories, short docType, boolean stem) {
    this.categories = categories;
    this.dirName = dirName;
    this.docType = docType;
    this.stem = stem;
  }

  /**
   * Construct an ExamplesConstructor for the given directory and category labels
   */
  public DirectoryExamplesConstructor(String dirName, String[] categories) {
    this(dirName, categories, DocumentIterator.TYPE_HTML, false);
  }

  /**
   * Get the examples from the directory, process them into HashMapVector's and
   * label them with the correct category label
   */
  public List<Example> getExamples() {
    ArrayList<Example> examples = new ArrayList<Example>();
    DocumentIterator docIter = new DocumentIterator(new File(dirName), docType, stem);
    while (docIter.hasMoreDocuments()) { //read in all documents
      FileDocument doc = docIter.nextDocument();
      int category = findClassID(doc.file.getName()); // find category of document
      Example example = new Example(doc.hashMapVector(), category, doc.file.getName(), doc);
      examples.add(example);
    }
    return examples;
  }

  /**
   * Finds the class ID from the name of the document file.
   * Assumes file name contains the category name as a substring
   */
  public int findClassID(String name) {
    for (int i = 0; i < categories.length; i++) {
      if (name.indexOf(categories[i]) != -1)
        return i;
    }
    return -1;
  }

  /**
   * Test loading a sample directory of examples
   */
  public static void main(String[] args) {
    String dirName = "/u/mooney/ir-code/corpora/yahoo-science/";
    String[] classes = {"bio", "chem", "phys"};
    DirectoryExamplesConstructor con = new DirectoryExamplesConstructor(dirName, classes);
    List<Example> examples = con.getExamples();
    System.out.println("Number Examples: " + examples.size() + "\n" + examples);
  }

}
