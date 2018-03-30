package ir.webutils;

import java.util.*;
import java.io.*;

import ir.utilities.*;

import java.net.*;

/**
 * HTMLPage is a representation of information about a web
 * page.
 *
 * @author Ted Wild and Ray Mooney
 */

public class HTMLPage {

  /**
   * The original link to this page
   */
  protected final Link link;

  /**
   * The text of the page
   */
  protected final String text;

  /**
   * The links on this page
   */
  protected List<Link> outLinks;

  /**
   * Constructs an <code>HTMLPage</code> with the given link and text.
   *
   * @param link <code>Link</code> object to the given page.
   * @param text The text of the page.
   */
  public HTMLPage(Link link, String text) {
    this.link = link;
    this.text = text;
  }

  /**
   * Returns the full text of this page.  None of the HTML is
   * stripped out.
   *
   * @return The text of the this page.
   */
  public String getText() {
    return text;
  }

  /**
   * Returns the <code>Link</code> object that was used to access
   * this page.
   *
   * @return The <code>Link</code> object that was used to access
   *         this page.
   */
  public Link getLink() {
    return link;
  }

  /**
   * Set of the outLinks for this page to given list
   */
  public void setOutLinks(List<Link> links) {
    outLinks = links;
  }

  /**
   * Get the list of out links from this page.
   */
  public List<Link> getOutLinks() {
    return outLinks;
  }

  /**
   * Clients should always call this method before indexing an HTML
   * page if they want to obey the "NOINDEX" directive in the Robots
   * META tag.  Always returns <code>true</code> in default implementation.
   *
   * @return <code>true</code> iff. the page can be indexed.  Always
   *         returns <code>true</code> in the default implementation.
   */
  public boolean indexAllowed() {
    return true;
  }

  /**
   * Returns true if the page is empty or a 404 error.
   */
  public boolean empty() {
    if (text.equals("") ||
        MoreString.indexOfIgnoreCase(text, "<title>404 Not Found") >= 0)
      return true;
    return false;
  }

  /**
   * Writes web page to a file with a BASE HTML element with the
   * original URL.
   *
   * @param dir  The directory to store the file in.
   * @param name The name of the file.
   */
  public void write(File dir, String name) {
    try {
      PrintWriter out = new PrintWriter(new FileWriter(new File(dir, name + ".html")));
      // Add an HTML "BASE" element with the original URL so that
      // image and link references in the page will be properly "based" and work correctly.
      // Ideally, this command should be added to the <head> part of the document; however,
      // many documents don't have explicit <head>'s and putting it at the from of the
      // document seems to work since browsers are robust to "ungrammatical" HTML
      out.println("<base href=\"" + addEndSlash(link.getURL()) + "\">");
      out.print(text);
      out.close();
    }
    catch (IOException e) {
      System.err.println("HTMLPage.write(): " + e);
    }
  }

  /**
   * If URL looks like a directory rather than a file, then
   * add a "/" at the end so that it acts as a proper base URL
   * for completing URLs in this page
   */
  protected static URL addEndSlash(URL url) {
    String fileName = url.getPath();
    if (MoreString.fileExtension(fileName).equals(""))
      try {
        return new URL(url.toString() + "/");
      }
      catch (MalformedURLException e) {
        System.err.println("HTMLPage: " + e);
      }
    return url;
  }


}
