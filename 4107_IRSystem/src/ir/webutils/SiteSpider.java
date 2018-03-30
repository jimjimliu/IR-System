package ir.webutils;

import java.util.*;
import java.net.*;
import java.io.*;

/**
 * A spider that limits itself to a given site.
 *
 * @author Ray Mooney
 */
public class SiteSpider extends Spider {

  /**
   * Gets links from the given page that are on the same host as the
   * page.
   *
   * @return A list of links on <code>page</code> that have the same
   *         host as <code>url</code>.
   */
  public List<Link> getNewLinks(HTMLPage page) {
    List<Link> links = new LinkExtractor(page).extractLinks();
    URL url = page.getLink().getURL();
    ListIterator<Link> iterator = links.listIterator();
    while (iterator.hasNext()) {
      Link link = iterator.next();
      if (!url.getHost().equals(link.getURL().getHost()))
        iterator.remove();
    }
    return links;
  }

  /**
   * Spider the web according to the following command options,
   * but stay within the given site (same URL host).
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
    new SiteSpider().go(args);
  }
}









