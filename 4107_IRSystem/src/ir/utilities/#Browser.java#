package ir.utilities;

import java.io.*;
import java.lang.*;

/**
 * Utilities for displaying a URL or local file in the current Browser
 * window using "browser -remote".  User must have browser running.
 *
 * @author Ray Mooney
 */

public class Browser {
  public final static String BROWSER_NAME = "firefox";

  /**
   * Make browser display a given URL
   */
  public static void display(String url) {
    String[] cmd = new String[2];
    cmd[0] = BROWSER_NAME;
    cmd[1] = url;
    try {
      Runtime.getRuntime().exec(cmd);
    }
    catch (IOException e) {
      System.out.println("Unable to run `" + BROWSER_NAME + "-remote' process.");
    }
  }

  /**
   * Make browser display a given file
   */
  public static void display(File file) {
    display("file:" + file.getAbsolutePath());
  }

  /**
   * Test interface
   */
  public static void main(String[] args) throws IOException {
    String name = args[0];
    File file = new File(name);
    System.out.println("\nDisplaying: " + file.getAbsolutePath());
    display(file);
  }

}
