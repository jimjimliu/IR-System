package ir.vsr;

import java.io.*;
import java.util.*;
import java.lang.*;

/**
 * An object for iterating over a set of documents in a directory.
 * Produces DocumentFile objects that are either TextFileDocuments
 * or HTMFileDocuments depending on whether docType is TYPE_TEXT
 * or TYPE_HTML
 *
 * @author Ray Mooney
 */

public class DocumentIterator {

  /**
   * docType for ASCII text files
   */
  public static final short TYPE_TEXT = 0;
  /**
   * docType for HTML files
   */
  public static final short TYPE_HTML = 1;

  /**
   * An array of files in the directory
   */
  protected File[] files = null;
  /**
   * The current position of the iterator in this array
   */
  protected int position = 0;
  /**
   * The type of documents to be created
   */
  protected short docType = TYPE_TEXT;
  /**
   * Whether tokens should be stemmed with Porter stemmer
   */
  protected boolean stem = false;

  /**
   * Create an iterator with these attributes
   *
   * @param dirFile The directory to use as a source of documents.
   * @param docType The type of Document to create. e.g. TYPE_TEXT or TYPE_HTML
   * @param stem    Whether tokens should be stemmed with Porter stemmer.
   * @param filter  A filter to select a subset of the docs in the directory
   */
  public DocumentIterator(File dirFile, short docType, boolean stem, FilenameFilter filter) {
    // Get the files in this directory
    if (filter != null)
      files = dirFile.listFiles(filter);
    else
      files = dirFile.listFiles();
    // Initialize the position and docType
    position = 0;
    this.docType = docType;
    this.stem = stem;
  }

  /**
   * Create an iterator with these attributes
   *
   * @param dirFile The directory to use as a source of documents.
   * @param docType The type of Document to create. e.g. TYPE_TEXT or TYPE_HTML
   * @param stem    Whether tokens should be stemmed with Porter stemmer.
   */
  public DocumentIterator(File dirFile, short docType, boolean stem) {
    this(dirFile, docType, stem, null);
  }

  /**
   * Create an iterator for TexFileDocuments
   *
   * @param dirFile The directory to use as a source of documents.
   */
  public DocumentIterator(File dirFile) {
    this(dirFile, TYPE_TEXT, false);
  }

  /**
   * Get the next document
   */
  public FileDocument nextDocument() {
    if (position >= files.length)
      return null;
    FileDocument doc = null;
    // Create the correct type of FileDocument based on docType
    switch (docType) {
      case TYPE_TEXT:
        doc = new TextFileDocument(files[position], stem);
        break;
      case TYPE_HTML:
        doc = new HTMLFileDocument(files[position], stem);
        break;
    }
    // Reset position to the next file
    position++;
    return doc;
  }

  /**
   * Returns true iff there are more documents in this directory
   */
  public boolean hasMoreDocuments() {
    if (files != null && position < files.length)
      return true;
    else
      return false;
  }

  /**
   * Test by printing the bag-of-words for each file in the given directory
   */
  public static void main(String[] args) {
    String dirName = args[0];
    DocumentIterator docIter = new DocumentIterator(new File(dirName));
    while (docIter.hasMoreDocuments()) {
      FileDocument doc = docIter.nextDocument();
      System.out.println("\n" + doc.file);
      doc.printVector();
    }
  }

}

	
	
