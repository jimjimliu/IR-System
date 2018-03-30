package ir.utilities;

import java.lang.StringBuffer;
import java.util.*;
import java.io.*;

/**
 * A place to put some additional string functions
 *
 * @author Ray Mooney
 */

public class MoreString {

  /**
   * Pad a string with a specific char on the right to make it the specified length
   */
  public static String padTo(String string, int length, char ch) {
    if (string.length() >= length)
      return string;
    StringBuffer stringBuf = new StringBuffer(string);
    stringBuf.setLength(length);
    for (int i = string.length(); i < length; i++)
      stringBuf.setCharAt(i, ch);
    return stringBuf.toString();
  }

  /**
   * Pad a string with blanks on the right to make it the specified length
   */
  public static String padTo(String string, int length) {
    return padTo(string, length, ' ');
  }

  /**
   * Pad a string with a specific char on the left to make it the specified length
   */
  public static String padToLeft(String string, int length, char ch) {
    if (string.length() >= length)
      return string;
    StringBuffer stringBuf = new StringBuffer(length);
    for (int i = 0; i < (length - string.length()); i++)
      stringBuf.append(ch);
    stringBuf.append(string);
    return stringBuf.toString();
  }

  /**
   * Pad a string with blanks on the left to make it the specified length
   */
  public static String padToLeft(String string, int length) {
    return padToLeft(string, length, ' ');
  }

  /**
   * Convert a double to a string and pad with blanks on the left
   * to make it the specified length
   */
  public static String padToLeft(double x, int length) {
    return padToLeft(Double.toString(x), length);
  }

  /**
   * Convert an int to a string and pad with blanks on the left
   * to make it the specified length
   */
  public static String padToLeft(int x, int length) {
    return padToLeft(Integer.toString(x), length);
  }

  public static String padWithZeros(int x, int length) {
    return padToLeft(Integer.toString(x), length, '0');
  }

  public static String padWithZeros(double x, int length) {
    return padToLeft(Double.toString(x), length, '0');
  }

  /**
   * Segment a string into substrings by breaking at occurences of the given
   * character and returning a list of segments
   */
  public static ArrayList<String> segment(String string, char ch) {
    ArrayList<String> result = new ArrayList<String>();
    String segment = null;
    int pos = 0; // The start position of the current segment
    for (int i = 0; i < string.length(); i++) {
      // If find breaking char at this point
      if (string.charAt(i) == ch) {
        // Create a segment from pos to this point
        segment = string.substring(pos, i);
        // Unless empty, add to list of segments
        if (!segment.equals(""))
          result.add(segment);
        // Update start position of next segment
        pos = i + 1;
      }
    }
    // Include last segment for pos to end of string
    if (pos != string.length())
      result.add(string.substring(pos, string.length()));
    return result;
  }

  public static int indexOfIgnoreCase(String string, String substring, int fromIndex) {
    for (int i = fromIndex; i < string.length(); i++) {
      if (startsWithIgnoreCase(string, substring, i))
        return i;
    }
    return -1;
  }

  public static int indexOfIgnoreCase(String string, String substring) {
    return indexOfIgnoreCase(string, substring, 0);
  }

  public static boolean startsWithIgnoreCase(String string, String substring, int fromIndex) {
    if ((fromIndex < 0) || ((fromIndex + substring.length()) > string.length()))
      return false;
    for (int i = 0; i < substring.length(); i++)
      if (Character.toUpperCase(string.charAt(fromIndex + i)) != Character.toUpperCase(substring.charAt(i)))
        return false;
    return true;
  }

  public static boolean startsWithIgnoreCase(String string, String substring) {
    return startsWithIgnoreCase(string, substring, 0);
  }

  public static String fileExtension(String fileName) {
    int pos = fileName.lastIndexOf(".");
    if (pos == -1)
      return "";
    else
      return fileName.substring(pos + 1);
  }

  /**
   * Load the stopwords from file to the hashtable where they are indexed.
   */
  public static String fileToString(String fileName) {
    StringBuffer stringBuf = new StringBuffer();
    String line;
    try {
      BufferedReader in = new BufferedReader(new FileReader(fileName));
      // Read in file, one per line, until file is empty
      while ((line = in.readLine()) != null) {
        stringBuf.append(line);
        stringBuf.append("\n");
      }
      in.close();
    }
    catch (IOException e) {
      System.out.println("\nCould not load file: " + fileName);
      System.exit(1);
    }
    return stringBuf.toString();
  }

  public static void main(String[] args) {
    System.out.println(fileToString(args[0]));
  }

}

