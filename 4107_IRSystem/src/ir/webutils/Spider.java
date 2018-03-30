package ir.webutils;

import java.util.*;
import java.io.*;

import ir.utilities.*;

/**
 * Spider defines a framework for writing a web crawler.  Users can
 * change the behavior of the spider by overriding methods.
 * Default spider does a breadth first crawl starting from a
 * given URL up to a specified maximum number of pages, saving (caching)
 * the pages in a given directory.  Also adds a "BASE" HTML command to
 * cached pages so links can be followed from the cached version.
 *
 * @author Ted Wild and Ray Mooney
 */

public class Spider {

  /**
   * The queue of links maintained by the spider
   */
  protected List<Link> linksToVisit = new LinkedList<Link>();

  /**
   * Flag to purposely slow the crawl for debugging purposes
   */
  protected boolean slow = false;

  /**
   * The object to be used to retrieve pages
   */
  protected HTMLPageRetriever retriever = new HTMLPageRetriever();

  /**
   * The directory to save the downloaded files to.
   */
  protected File saveDir;

  /**
   * The number of pages indexed.  In the default implementation
   * a page is considered to be indexed only if it is written to a
   * file.
   */
  protected int count = 0;

  /**
   * The maximum number of pages to be indexed.
   */
  protected int maxCount = 10000;

  /**
   * The URLs that have already been visited.
   */
  protected HashSet<Link> visited;

  /**
   * Checks command line arguments and performs the crawl.  <p> This
   * implementation calls <code>processArgs</code> and
   * <code>doCrawl</code>.
   *
   * @param args Command line arguments.
   */
  public void go(String[] args) {
    processArgs(args);
    doCrawl();
  }

  /**
   * Processes command-line arguments.  <p> The following options are
   * handled by this function:
   * <ul>
   * <li>-safe : Check for and obey robots.txt and robots META tag
   * directives.</li>
   * <li>-d &lt;directory&gt; : Store indexed files in &lt;directory&gt.</li>
   * <li>-c &lt;count&gt; : Store at most &lt;count&gt; files.</li>
   * <li>-u &lt;url&gt; : Start at &lt;url&gt;.</li>
   * <li>-slow : Pause briefly before getting a page.  This can be
   * useful when debugging.
   * </ul>
   * <p/>
   * Each option has a corresponding
   * <code>handleXXXCommandLineOption</code> function that will be
   * called when the option is found.  Subclasses may find it
   * convenient to change how options are handled by overriding
   * those methods instead of this one.  Only the above options will
   * be dealt with by this function, and the input array will remain
   * unchanged.  Note that if the flag for an option appears in the
   * input array, any value associated with that option will be
   * assumed to follow.  Thus if a "-c" flag appears in
   * <code>args</code>, the next value in <code>args</code> will be
   * blindly treated as the count.
   *
   * @param args Array of arguments as passed in from the command
   *             line.
   */
  public void processArgs(String[] args) {
    int i = 0;

    while (i < args.length) {

      if (args[i].charAt(0) == '-') {

        if (args[i].equals("-safe"))
          handleSafeCommandLineOption();
        else if (args[i].equals("-d"))
          handleDCommandLineOption(args[++i]);
        else if (args[i].equals("-c"))
          handleCCommandLineOption(args[++i]);
        else if (args[i].equals("-u"))
          handleUCommandLineOption(args[++i]);
        else if (args[i].equals("-slow"))
          handleSlowCommandLineOption();
      }
      ++i;
    }
  }

  /**
   * Called when "-safe" is passed in on the command line.  <p> This
   * implementation sets <code>retriever</code> to a {@link
   * SafeHTMLPageRetriever SafeHTMLPageRetriever}.
   */
  protected void handleSafeCommandLineOption() {
    retriever = new SafeHTMLPageRetriever();
  }

  /**
   * Called when "-d" is passed in on the command line.  <p> This
   * implementation sets <code>saveDir</code> to <code>value</code>.
   *
   * @param value The value associated with the "-d" option.
   */
  protected void handleDCommandLineOption(String value) {
    saveDir = new File(value);
    if(!saveDir.exists()) {
      if (!saveDir.mkdir()) {
        throw new IllegalArgumentException("Failed to create directory " + saveDir.toString());
      } else {
        System.out.println("Created destination directory " + saveDir.toString());
      }
    }
  }

  /**
   * Called when "-c" is passed in on the command line.  <p> This
   * implementation sets <code>maxCount</code> to the integer
   * represented by <code>value</code>.
   *
   * @param value The value associated with the "-c" option.
   */
  protected void handleCCommandLineOption(String value) {
    maxCount = Integer.parseInt(value);
  }

  /**
   * Called when "-u" is passed in on the command line.  <p> This
   * implementation adds <code>value</code> to the list of links to
   * visit.
   *
   * @param value The value associated with the "-u" option.
   */
  protected void handleUCommandLineOption(String value) {
    linksToVisit.add(new Link(value));
  }

  /**
   * Called when "-slow" is passed in on the command line.  <p> This
   * implementation sets a flag that will be used in <code>go</code>
   * to pause briefly before downloading each page.
   */
  protected void handleSlowCommandLineOption() {
    slow = true;
  }

  /**
   * Performs the crawl.  Should be called after
   * <code>processArgs</code> has been called.  Assumes that
   * starting url has been set.  <p> This implementation iterates
   * through a list of links to visit.  For each link a check is
   * performed using {@link #visited visited} to make sure the link
   * has not already been visited.  If it has not, the link is added
   * to <code>visited</code>, and the page is retrieved.  If access
   * to the page has been disallowed by a robots.txt file or a
   * robots META tag, or if there is some other problem retrieving
   * the page, then the page is skipped.  If the page is downloaded
   * successfully {@link #indexPage indexPage} and {@link
   * #getNewLinks getNewLinks} are called if allowed.
   * <code>go</code> terminates when there are no more links to visit
   * or <code>count &gt;= maxCount</code>
   */
  public void doCrawl() {
    if (linksToVisit.size() == 0) {
      System.err.println("Exiting: No pages to visit.");
      System.exit(0);
    }
    visited = new HashSet<Link>();
    while (linksToVisit.size() > 0 && count < maxCount) {
      // Pause if in slow mode
      if (slow) {
        synchronized (this) {
          try {
            wait(1000);
          }
          catch (InterruptedException e) {
          }
        }
      }
      // Take the top link off the queue
      Link link = linksToVisit.remove(0);
      System.out.println("Trying: " + link);
      // Skip if already visited this page
      if (!visited.add(link)) {
        System.out.println("Already visited");
        continue;
      }
      if (!linkToHTMLPage(link)) {
        System.out.println("Not HTML Page");
        continue;
      }
      HTMLPage currentPage = null;
      // Use the page retriever to get the page
      try {
        currentPage = retriever.getHTMLPage(link);
      }
      catch (PathDisallowedException e) {
        System.out.println(e);
        continue;
      }
      if (currentPage.empty()) {
        System.out.println("No Page Found");
        continue;
      }
      if (currentPage.indexAllowed()) {
        count++;
        System.out.println("Indexing" + "(" + count + "): " + link);
        indexPage(currentPage);
      }
      if (count < maxCount) {
        List<Link> newLinks = getNewLinks(currentPage);
        // System.out.println("Adding the following links" + newLinks);
        // Add new links to end of queue
        linksToVisit.addAll(newLinks);
      }
    }
  }

  /**
   * Check if this is a link to an HTML page.
   *
   * @return true if a directory or clearly an HTML page
   */
  protected boolean linkToHTMLPage(Link link) {
    String extension = MoreString.fileExtension(link.getURL().getPath());
    if (extension.equals("") || extension.equalsIgnoreCase("html") ||
        extension.equalsIgnoreCase("htm") || extension.equalsIgnoreCase("shtml"))
      return true;
    return false;
  }

  /**
   * Returns a list of links to follow from a given page.
   * Subclasses can use this method to direct the spider's path over
   * the web by returning a subset of the links on the page.
   *
   * @param page The current page.
   * @return Links to be visited from this page
   */
  protected List<Link> getNewLinks(HTMLPage page) {
    return new LinkExtractor(page).extractLinks();
  }

  /**
   * "Indexes" a <code>HTMLpage</code>.  This version just writes it
   * out to a file in the specified directory with a "P<count>.html" file name.
   *
   * @param page An <code>HTMLPage</code> that contains the page to
   *             index.
   */
  protected void indexPage(HTMLPage page) {
    page.write(saveDir,
        "P" + MoreString.padWithZeros(count, (int) Math.floor(MoreMath.log(maxCount, 10)) + 1));
  }

  /**
   * Spider the web according to the following command options:
   * <ul>
   * <li>-safe : Check for and obey robots.txt and robots META tag
   * directives.</li>
   * <li>-d &lt;directory&gt; : Store indexed files in &lt;directory&gt;.</li>
   * <li>-c &lt;maxCount&gt; : Store at most &lt;maxCount&gt; files (default is 10,000).</li>
   * <li>-u &lt;url&gt; : Start at &lt;url&gt;.</li>
   * <li>-slow : Pause briefly before getting a page.  This can be
   * useful when debugging.
   * </ul>
   */
  public static void main(String args[]) {
    new Spider().go(args);
  }
}






