package ir.webutils;

import java.net.*;

/**
 * URLChecker tries to clean up some URLs that do not conform to the standard and cause confusion.  Valid URLs
 * are returned unchanged.  The idea behind this class is to fix some common problems (like leaving spaces in URLs)
 * with simple heuristics.
 *
 * @since June 1, 2001
 * @author Ted Wild
 */
public final class URLChecker {

  /**
   * Returns a URL for the given string after correcting simple errors.
   */
  public static URL getURL(String urlString) throws MalformedURLException {

    String checkedUrl = urlString.replace(' ', '+');

    return new URL(checkedUrl);
  }

  private URLChecker() {
    // Enforce non-instantiability
  }
}

















