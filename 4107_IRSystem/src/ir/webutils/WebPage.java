package ir.webutils;

/*
 * WebPage.java
 * utility class to handle downloading web pages.
 * May 23, 2001
 * Ted Wild
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


/**
 * WebPage is a static utility class that provides operations for
 * downloading web pages.
 *
 * @author Ted Wild
 */
public class WebPage {

  /**
   * Downloads the web page specified by the URL represented by a
   * given string.
   *
   * @param urlString <code>String</code> representation of the URL
   *                  to get the page from.  The URL must be absolute.
   * @return A <code>String</code> containing the contents of the
   *         page.  No extra parsing work is done on the page.
   */
  public static String getWebPage(String urlString) {
    String page = null;
    try {
      URL url = URLChecker.getURL(urlString);
      page = getWebPage(url);
    }
    catch (MalformedURLException e) {
      System.out.println("WebPage.getWebPage(): " + e.toString());
    }
    return page;
  }

  /**
   * Downloads the web page specified by the given <code>URL</code>
   * object.
   *
   * @param url The <code>URL</code> object that the page will be
   *            downloaded from.
   * @return A <code>String</code> containing the contents of the
   *         page.  No extra parsing work is done on the page.
   */
  public static String getWebPage(URL url) {

    // using a StringBuffer instead of a String has huge
    // performance benefits.
    StringBuffer page = new StringBuffer();

    try {
      URLConnection connection = url.openConnection();
      String line;
      BufferedReader in;
      if (connection.getContentEncoding() == null)
        in = new BufferedReader(new InputStreamReader(url.openStream()));
      else
        in = new BufferedReader(new InputStreamReader(url.openStream(),
            connection.getContentEncoding()));
      while ((line = in.readLine()) != null)
        page.append(line).append('\n');

      in.close();
    }
    catch (UnsupportedEncodingException e) {
      System.err.println("WebPage.getWebPage(): " + e);
    }
    catch (IOException e) {
      System.err.println("WebPage.getWebPage(): " + e);
    }

    return page.toString();
  }

  /**
   * Retrieve the page on the URL given and output its contents to STDOUT.
   */
  public static void main(String[] args) {
    System.out.println("Fetching page: " + args[0] + "\n");
    System.out.println(getWebPage(args[0]));
  }
}
