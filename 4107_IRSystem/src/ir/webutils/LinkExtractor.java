package ir.webutils;

import java.net.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.util.*;

import ir.utilities.*;

/**
 * LinkExtractor defines a callback that extracts the links from an
 * HTML document and provides functionality to parse a document.  The
 * extracted links are absolute.  Uses the HTML parser in Java Swing
 * to parse the document and find links and translate them to
 * absolute URL's (instead of relative ones).
 *
 * @author Ted Wild and Ray Mooney
 */
public class LinkExtractor extends HTMLEditorKit.ParserCallback {

  /**
   * The current list of extracted links
   */
  protected List<Link> links;

  /**
   * The page from which to extract links
   */
  protected HTMLPage page;

  /**
   * The URL for this page
   */
  protected URL url;

  /**
   * Create an link extractor for the given page
   */
  public LinkExtractor(HTMLPage page) {
    this.links = new LinkedList<Link>();
    this.page = page;
    this.url = HTMLPage.addEndSlash(page.getLink().getURL());
  }

  /**
   * Executed when a block of text is encountered. Just ignores text.
   *
   * @param text     A <code>char</code> array representation of the
   *                 text.
   * @param position The position of the text in the document.
   */
  public void handleText(char[] text, int position) {
  }

  /**
   * Executed when an opening HTML tag is found in the document.
   * Note that this method only handles tags that also have a
   * closing tag. Catches "a" tags and adds links for them
   * (after completing them)
   *
   * @param tag        The tag that caused this function to be executed.
   * @param attributes The attributes of <code>tag</code>.
   * @param position   The start of the tag in the document.  If the
   *                   tag is implied (filled in by the parser but not actually
   *                   present in the document) then <code>position</code> will
   *                   correspond to that of the next encountered tag.
   */
  public void handleStartTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {

    if (tag == HTML.Tag.A) {
      addLink(attributes, HTML.Attribute.HREF);
    }
  }

  /**
   * Executed when a closing HTML tag is found in the document.
   * Note that the parser may add "implied" closing tags.  For
   * example, the default parser adds closing &lt;p&gt; tags.
   * This version just ignores end tags.
   *
   * @param tag      The tag found.
   * @param position The position of the tag in the document.
   */
  public void handleEndTag(HTML.Tag tag, int position) {
  }

  /**
   * Executed when an HTML tag that has no closing tag is found in
   * the document. Adds link for FRAME's
   *
   * @param tag        The tag that caused this function to be executed.
   * @param attributes The attributes of <code>tag</code>.
   * @param position   The start of the tag in the document.  If the
   *                   tag is implied (filled in by the parser but not actually
   *                   present in the document) then <code>position</code> will
   *                   correspond to that of the next encountered tag.
   */
  public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {

    if (tag.equals(HTML.Tag.FRAME)) {
      addLink(attributes, HTML.Attribute.SRC);
    }
  }

  /**
   * Extracts links from the given page.  This method constructs a
   * parser and registers <code>this</code> as the callback.
   *
   * @return A list of <code>Link</code> objects containing the
   *         links found on this page.  The links will all be absolute
   *         links.
   */
  public List<Link> extractLinks() {
    HTMLParserMaker kit = new HTMLParserMaker();
    HTMLEditorKit.Parser parser = kit.getParser();
    StringReader reader = new StringReader(page.getText());
    // Swing HTML parser will execute callback routines and thereby
    // extract links
    try {
      parser.parse(reader, this, true);
    }
    catch (ChangedCharSetException e) {
      System.err.println("LinkExtractor.extractLinks(): " + e);
    }
    catch (IOException e) {
      System.err.println("LinkExtractor.extractLinks(): " + e);
    }
    // Set out-links for the page
    page.setOutLinks(this.links);
    return this.links;
  }

  /**
   * Retrieves a link from an attribute set and completes it against
   * the base URL.
   *
   * @param attributes The attribute set.
   * @param attr       The attribute that should be treated as a URL.  For
   *                   example, <code>attr</code> should be
   *                   <code>HTML.Attribute.HREF</code> if <code>attributes</code> is
   *                   from an anchor tag.
   */
  protected void addLink(MutableAttributeSet attributes, HTML.Attribute attr) {
    if (attributes.isDefined(attr)) {
      String link = (String) attributes.getAttribute(attr);
      try {
        URL completeURL = new URL(this.url, link);
        // Store extracted link unless it is an internal page link
        if (!link.startsWith("#"))
          this.links.add(new Link(completeURL));
      }
      catch (MalformedURLException e) {
        System.err.println("LinkExtractor: " + e);
        // e.printStackTrace(System.err);
      }
    }
  }


}// LinkExtractor
