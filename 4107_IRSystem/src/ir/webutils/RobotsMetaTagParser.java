package ir.webutils;

import java.util.*;
import java.net.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.io.*;

/**
 * Parser callback that extracts robots META tag information.
 *
 * @author Ted Wild
 */
public final class RobotsMetaTagParser extends HTMLEditorKit.ParserCallback {

  private String page;
  private HTMLEditorKit.Parser parser;
  private String robotRules = null;
  private URL url;
  private boolean index = true;

  public RobotsMetaTagParser() {
    HTMLParserMaker kit = new HTMLParserMaker();
    this.parser = kit.getParser();
  }

  public RobotsMetaTagParser(URL url) {
    this();
    this.url = url;
  }

  public RobotsMetaTagParser(URL url, String page) {
    this();
    this.url = url;
    this.page = page;
  }

  public void setPage(String page) {
    this.page = page;
  }

  public void setUrl(URL url) {
    this.url = url;
  }

  /**
   * Checks for robots META tags.  If a robots META tag is found,
   * then the content (if any) is extracted and stored.  Note that
   * only the last robots META tag will be considered.
   *
   * @param tag        Indicates the type of tag that caused this method to
   *                   be called.  Only META tags are handled, any other kind of tag
   *                   causes this method to do nothing.
   * @param attributes The attributes of this tag.  If the tag
   *                   defines the "name" attribute with value "robots" (not case
   *                   sensitive) then the "content" attribute will be checked, and
   *                   stored if it exists.
   * @param position   The position of the tag in the document.  Not
   *                   used.
   */
  public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {

    if (tag == HTML.Tag.META) {

      if (attributes.isDefined(HTML.Attribute.NAME)) {
        String name = (String) attributes.getAttribute(HTML.Attribute.NAME);

        if (name.compareToIgnoreCase("robots") == 0) {

          if (attributes.isDefined(HTML.Attribute.CONTENT))
            robotRules = ((String) attributes.getAttribute(HTML.Attribute.CONTENT)).toLowerCase();
        }
      }
    }
  }

  /**
   * Parses the document and returns a list of links that can not be
   * followed.  This method also sets a flag that indicates whether
   * or not this page can be indexed.  Clients can then use
   * <code>index</code> to check the value of this flag.
   *
   * @return A <code>List</code> of <code>Link</code>s that should
   *         not be followed from this page.
   */
  public List<Link> parseMetaTags() {
    StringReader r = new StringReader(this.page);

    try {
      parser.parse(r, this, true);
    }
    catch (ChangedCharSetException e) {

      // should not occur
    }
    catch (IOException e) {
      System.err.println("RobotsMetaTagParser.parseMetaTags(): " + e);
    }

    if (robotRules != null) {

      if (robotRules.indexOf("no") != -1) {

        if (robotRules.indexOf("noindex") != -1)
          index = false;

        if (robotRules.indexOf("nofollow") != -1 || robotRules.indexOf("none") != -1)
          return new LinkExtractor(new HTMLPage(new Link(this.url), this.page)).extractLinks();

      }
    }
    return new LinkedList<Link>();
  }

  /**
   * Indicates whether the page can be indexed.  Call this method
   * only after <code>parseMetaTags</code> has been called.
   *
   * @return <code>true</code> iff. the page can be indexed.
   */
  public boolean index() {
    return index;
  }
}// RobotsMetaTagParser
