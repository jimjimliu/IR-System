package ir.webutils;

import java.net.*;
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;
import java.util.*;

import ir.utilities.*;

/**
 * YahooSiteLinkExtractor defines a callback that extracts site links from a 
 * Yahoo directory page and provides functionality to parse a document.  The
 * extracted links are absolute.  Uses the HTML parser in Java Swing
 * to parse the document and find links and translate them to
 * absolute URL's (instead of relative ones).
 *
 * @author Ted Wild and Ray Mooney
 */
public class YahooSiteLinkExtractor extends HTMLEditorKit.ParserCallback {

  /**
   * The current list of extracted site links
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
   * Flag that is true during parsing while HTML parser is
   * in the section of the webpage that lists site links
   */
  protected boolean inSiteSection = false;

  /**
   * Flag that is true during parser while the HTML parser
   * in inside an anchor link text for a Yahoo link that
   * refers to more sites not listed on the current page
   * Stores the URL for this link while in its anchor text.
   */
  protected String moreURL = null;

  /**
   * Create an link extractor for the given page
   */
  public YahooSiteLinkExtractor(HTMLPage page) {
    this.links = new LinkedList<Link>();
    this.page = page;
    this.url = HTMLPage.addEndSlash(page.getLink().getURL());
  }

  /**
   * Executed when a block of text is encountered. 
   * If text indicates entering the site listing part of a Yahoo
   * directory page, then sets the inSiteSection flag to true.
   * If in the anchor text of a link indicating more site links
   * that explicitly refers to the "Next" set of results, then
   * recursively extract the site links on this page of additional
   * results by creating a YahooSiteLinkExtractor for that page
   * and adding the extracted links to the links for this category
   *
   * @param text     A <code>char</code> array representation of the
   *                 text.
   * @param position The position of the text in the document.
   */
  public void handleText(char[] text, int position) {
      String string = new String(text);
      if (string.indexOf("SITE LISTINGS") >= 0)
	  inSiteSection = true;
      // Check if in link to more site results
      else if (moreURL != null && string.indexOf("Next") >=0) {
	  // If so, extract and add these additional site links
	  Link link = new Link(moreURL);
	  HTMLPageRetriever retriever = new HTMLPageRetriever();
	  try {
	      HTMLPage page = retriever.getHTMLPage(link);
  	      List<Link> moreLinks = new YahooSiteLinkExtractor(page).extractLinks();
	      links.addAll(moreLinks);
	  }
	  catch (PathDisallowedException e) {
	      System.out.println(e);
	  }
      }
  }

  /**
   * Executed when an opening HTML tag is found in the document.
   * Note that this method only handles tags that also have a
   * closing tag. 
   * If currently in the site listing section, then save any link in the
   * set of extracted links.
   * If an anchor link to more Yahoo site results, then save the URL
   * in the moreURL flag.
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
	if (inSiteSection) {
	    addLink(attributes, HTML.Attribute.HREF);
	}
	else {
	    // Check if this is a Yahoo link to more site results
	    String url = (String)attributes.getAttribute(HTML.Attribute.HREF);
	    if (url.indexOf("dir.yahoo") >=0 && url.indexOf("?b=") >=0) {
		// If so, store the URL
		moreURL = url;
	    }
	}
    }
  }

  /**
   * Executed when a closing HTML tag is found in the document.
   * Note that the parser may add "implied" closing tags.  For
   * example, the default parser adds closing &lt;p&gt; tags.
   * If encounters end of TABLE tag while in the site listing section
   * of Yahoo page, indicates the end of this section and 
   * sets the inSiteSection flag to false.
   * If ending an anchor text section of a link to more results
   * then set moreURL flag to null to indicate no longer in such a link
   *
   * @param tag      The tag found.
   * @param position The position of the tag in the document.
   */
  public void handleEndTag(HTML.Tag tag, int position) {
      if (inSiteSection && tag == HTML.Tag.TABLE)
	  inSiteSection = false;
      if (tag == HTML.Tag.A)
	  moreURL = null;
  }

  /**
   * Executed when an HTML tag that has no closing tag is found in
   * the document. Nothing to do here.
   *
   * @param tag        The tag that caused this function to be executed.
   * @param attributes The attributes of <code>tag</code>.
   * @param position   The start of the tag in the document.  If the
   *                   tag is implied (filled in by the parser but not actually
   *                   present in the document) then <code>position</code> will
   *                   correspond to that of the next encountered tag.
   */
  public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attributes, int position) {

  }

  /**
   * Extracts site links from the given Yahoo page.  This method constructs a
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
	// or link to a Yahoo specific help or directory page
        if (!link.startsWith("#") && link.indexOf("help.yahoo") == -1
	    && link.indexOf("picks.yahoo") == -1
	    && completeURL.getHost().indexOf("dir.yahoo") == -1)
          this.links.add(new Link(completeURL));
      }
      catch (MalformedURLException e) {
        System.err.println("LinkExtractor: " + e);
        // e.printStackTrace(System.err);
      }
    }
  }

   /**
    * Given Yahoo directory URL as a single arg, test extraction of
    * site links from this page.
    */

  public static void main(String args[]) {
      Link link = new Link(args[0]);
      HTMLPageRetriever retriever = new HTMLPageRetriever();
       try {
	   HTMLPage page = retriever.getHTMLPage(link);
	   System.out.println(new YahooSiteLinkExtractor(page).extractLinks());
      }
      catch (PathDisallowedException e) {
        System.out.println(e);
      }
  }


}// LinkExtractor
