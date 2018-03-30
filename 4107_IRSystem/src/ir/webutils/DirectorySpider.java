package ir.webutils;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * Spider that limits itself to the directory it started in.
 *
 * @author Ted Wild and Ray Mooney
 */

public class DirectorySpider extends Spider {
  static URL firstURL;

  /**
   * Gets links from the page that are in or below the starting
   * directory.
   *
   * @return The links on <code>page</code> that are in or below the
   *         directory of the first page.
   */
  public List<Link> getNewLinks(HTMLPage page) {
    List<Link> links = new LinkExtractor(page).extractLinks();
    URL url = page.getLink().getURL();

    ListIterator<Link> iterator = links.listIterator();
    while (iterator.hasNext()) {
      Link link = iterator.next();
      if (!url.getHost().equals(link.getURL().getHost()))
        iterator.remove();
      else if (!link.getURL().getPath().startsWith(getDirectory(firstURL)))
        iterator.remove();
    }
    return links;
  }

  /**
   * Sets the initial URL from the "-u" argument, then calls the
   * corresponding superclass method.
   *
   * @param value The value of the "-u" command line argument.
   */
  protected void handleUCommandLineOption(String value) {

    try {
      firstURL = new URL(value);
    }
    catch (MalformedURLException e) {
      System.out.println(e.toString());
      System.exit(-1);
    }
    super.handleUCommandLineOption(value);
  }

  private String getDirectory(URL u) {
    String directory = u.getPath();
    if (directory.indexOf(".") != -1)
      directory = directory.substring(0, directory.lastIndexOf("/"));
    return directory;
  }

  /**
   * Spider the web according to the following command options,
   * but only below the start URL directory.
   * <ul>
   * <li>-safe : Check for and obey robots.txt and robots META tag
   * directives.</li>
   * <li>-d &lt;directory&gt; : Store indexed files in &lt;directory&gt;.</li>
   * <li>-c &lt;count&gt; : Store at most &lt;count&gt; files.</li>
   * <li>-u &lt;url&gt; : Start at &lt;url&gt;.</li>
   * <li>-slow : Pause briefly before getting a page.  This can be
   * useful when debugging.
   * </ul>
   */
  public static void main(String args[]) {
    new DirectorySpider().go(args);
  }
}









