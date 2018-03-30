package ir.vsr;

import java.io.*;

/**
 * A Document stored as a file.
 *
 * @author Ray Mooney
 */
public abstract class FileDocument extends Document {

  /**
   * The name of the file
   */
  public File file = null;
  /**
   * The I/O reader for accessing the file
   */
  protected BufferedReader reader = null;

  /**
   * Creates a FileDocument and initializes its name and reader.
   */
  public FileDocument(File file, boolean stem) {
    super(stem);
    this.file = file;
    try {
      this.reader = new BufferedReader(new FileReader(file));
    }
    catch (IOException e) {
      System.out.println("\nCould not open FileDocument: " + file);
      System.exit(1);
    }
  }

}

