package ir.utilities;

import java.io.*;
import java.util.*;
import java.lang.*;

/**
 * Prefix all files in a directory with a particular prefix.
 *
 * @author Ray Mooney
 */

public class FilePrefixer {

  /**
   * An array of files in the directory
   */
  private File[] files = null;

  public FilePrefixer(File dirFile, FilenameFilter filter) {
    // Get the files in this directory
    if (filter != null)
      files = dirFile.listFiles(filter);
    else
      files = dirFile.listFiles();
    // Initialize the position and docType
  }

  public FilePrefixer(File dirFile) {
    this(dirFile, null);
  }

  public void prefix(String prefix) throws Exception {
    for (int i = 0; i < files.length; i++) {
      File newFile = new File(files[i].getParent(), prefix + files[i].getName());
      files[i].renameTo(newFile);
    }
  }

  public static void main(String[] args) throws Exception {
    String dirName = args[0];
    FilePrefixer prefixer = new FilePrefixer(new File(dirName));
    prefixer.prefix(args[1]);
  }

}

	
	
