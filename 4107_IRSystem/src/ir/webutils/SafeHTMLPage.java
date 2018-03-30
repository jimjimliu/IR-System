package ir.webutils;

/**
 * SafeHTMLPage is an immutable representation of information about a
 * web page that includes information about whether or not this page
 * can be indexed.  This class is intended to be used in conjunction
 * with {@link SafeHTMLPageRetriever SafeHTMLPageRetriever} to allow
 * clients to facilitate writing spiders that obey both the Robots
 * Exclusion Protocol and the Robots META tags.
 *
 * @author Ted Wild
 */
public final class SafeHTMLPage extends HTMLPage {

  private final boolean indexAllowed;

  /**
   * Constructs an <code>SafeHTMLPage</code> with the given link,
   * text, and indication whether or not indexing is allowed.
   *
   * @param link  A <code>Link</code> object representing the given page.
   * @param text  The text of the page.
   * @param index Should be <code>true</code> iff. the page can be
   *              indexed.
   */
  public SafeHTMLPage(Link link, String text, boolean index) {
    super(link, text);
    indexAllowed = index;
  }

  /**
   * Indicates whether or not indexing has been disallowed by a
   * Robots META tag.  Clients should always call this method before
   * indexing an HTML page if they want to obey the "NOINDEX"
   * directive in the Robots META tag.  Clients should also make
   * sure to employ an <code>SafeHTMLPageRetriever</code> that
   * supports Robots META tags, such as {@link SafeHTMLPageRetriever
   * SafeHTMLPageRetriever}.
   *
   * @return <code>true</code> iff. the page can be indexed.
   */
  public boolean indexAllowed() {
    return indexAllowed;
  }
} 
