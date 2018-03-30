package ir.utilities;

import java.io.*;

/**
 * A place to put some helper functions for interacting with the user
 *
 * @author Ray Mooney
 */

public class UserInput {

  /**
   * Read a line of input from the user
   */
  public static String readLine() {
    // Get a reader for receiving input from the console
    BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
    String input = null;
    try {
      input = console.readLine().trim();
    }
    catch (IOException e) {
      System.out.println("\nCould not read from console");
      System.exit(1);
    }
    return input;
  }

  /**
   * Prompt the user with a string and then get a line of input
   */
  public static String prompt(String promptString) {
    System.out.print(promptString);
    return readLine();
  }

}

