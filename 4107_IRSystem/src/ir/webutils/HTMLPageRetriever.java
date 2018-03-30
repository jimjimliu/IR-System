package ir.webutils;

import java.net.*;


/**
 * HTMLPageRetriever allows clients to download web pages from URLs.
 * This is the default implementation, which performs no processing
 * aside from downloading web pages from a URL.  This class does not
 * maintain any state, so subclasses do not need to worry about
 * fields.
 *
 * @author Ted Wild
 */
public class HTMLPageRetriever {

  /**
   * Constructs a HTMLPageRetriever object.  Subclasses wishing to
   * behave as singletons do not need to worry about overriding the
   * constructor.
   */
  public HTMLPageRetriever() {
  }

  /**
   * Downloads a web page from a given URL.
   *
   * @param link The <code>Link</code> with the URL to download the
   *             page from.
   * @return An <code>HTML</code> page representing the page
   *         downloaded from the <code>Link</code>.
   */
  public HTMLPage getHTMLPage(Link link) throws PathDisallowedException {
    return new HTMLPage(link, WebPage.getWebPage(link.getURL()));
  }
}// HTMLPageRetriever

