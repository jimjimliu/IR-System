package ir.webutils;

import java.util.*;
import java.io.*;

import ir.utilities.*;

/**
 * Specific spider for extracting and saving a particular number of random set of 
 * pages for a particular topic category in the Yahoo directory. Starts from
 * directory page and repeatedly randomly follows subcateogry and site links
 * to find a random site and save it.
 *
 * @author Ray Mooney
 */

public class YahooSpider {

  /**
   * Link for the main topic Yahoo category 
   */
  protected Link topCategoryLink;

  /**
   * Prefix to add to the name of all saved files for the current cateogry
   */
  protected String filePrefix = "P";

  /**
   * List of category links found for the current directory page
   */
  protected List<Link> categoryLinks = new LinkedList<Link>();

  /**
   * List of site links found for the current directory page   
   */
  protected List<Link> siteLinks = new LinkedList<Link>();


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
   * The number of pages to be found and indexed.
   */
  protected int maxCount = 10000;

  /**
   * The HashMap for storing categoryLinks for already downloaded Links
   */
  public Map<Link,List<Link>> categoryLinksMap = new HashMap<Link,List<Link>>();

  /**
   * The HashMap for storing siteLinks for already downloaded Links
   */
  public Map<Link,List<Link>> siteLinksMap = new HashMap<Link,List<Link>>();

   /**
   * The sites that have already been indexed.
   */
  protected HashSet<Link> visitedSites = new HashSet<Link>();


  /**
   * Random number generator to use
   */
  protected Random random = new Random();

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
   * <li>-d &lt;directory&gt; : Store indexed files in &lt;directory&gt.</li>
   * <li>-c &lt;maxCount&gt; : Find &lt;maxCount&gt; files (default is 10,000).</li>
   * <li>-u &lt;url&gt; : Start at Yahoo directory page given by &lt;url&gt;.</li>
   * <li>-p &lt;prefix &gt; : Prefix saved file names with &lt;prefix&gt;.</li>
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
        if (args[i].equals("-d"))
          handleDCommandLineOption(args[++i]);
        else if (args[i].equals("-c"))
          handleCCommandLineOption(args[++i]);
        else if (args[i].equals("-u"))
          handleUCommandLineOption(args[++i]);
        else if (args[i].equals("-p"))
          handlePCommandLineOption(args[++i]);
        else if (args[i].equals("-slow"))
          handleSlowCommandLineOption();
      }
      ++i;
    }
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
   * implementation sets the top level Yahoo directory category  
   * link to <code>value</code> 
   *
   * @param value The value associated with the "-u" option.
   */
  protected void handleUCommandLineOption(String value) {
    topCategoryLink = new Link(value);
  }

  /**
   * Called when "-p" is passed on the command line.
   * Sets file name prefix for saved files.
   */
  protected void handlePCommandLineOption(String value) {
    filePrefix = value;
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
   * until <code>count &gt;= maxCount</code>
   */
  public void doCrawl() {
    while (count < maxCount) {
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
      Link link = topCategoryLink;
      Link site = null;
      // Randomly follow links down the Yahoo taxonomy until we find and pick a 
      // random site
      while (site == null) {
	  System.out.println("Trying Category: " + link);
	  // See if subcategory links for this link already extracted and saved
	  categoryLinks = categoryLinksMap.get(link);
	  // If not, extract and save category and site links for this page
	  if (categoryLinks == null) {
	      // Use the page retriever to get the page
	      try {
		  HTMLPage page = retriever.getHTMLPage(link);
		  categoryLinks = new YahooCategoryLinkExtractor(page).extractLinks();
		  categoryLinksMap.put(link,categoryLinks);
		  siteLinks = new YahooSiteLinkExtractor(page).extractLinks();		  
		  siteLinksMap.put(link,siteLinks);
	      }
	      catch (PathDisallowedException e) {
		  System.out.println(e);
		  break;
	      }
	  }
	  else 
	      // Recover stored site links for already processed page
	      siteLinks = siteLinksMap.get(link);
	  // If no category or site links found for this page just 
	  // exit and try again
	  if ((categoryLinks == null || categoryLinks.isEmpty()) && 
	      (siteLinks == null || siteLinks.isEmpty())) {
	      System.out.println("No categories or sites");
	      break;
	  }
	  // If no cateogry links, then pick a random site
	  else if (categoryLinks == null || categoryLinks.isEmpty()) 
	      site = getRandomLink(siteLinks);
	  // If no site links, then pick a random subcateogry
	  else if (siteLinks == null || siteLinks.isEmpty())
	      link = getRandomLink(categoryLinks);
	  // Otherwise, randomly pick either a random site or a 
	  // a random subcategory to follow.
	  else if (random.nextBoolean()) 
	      link = getRandomLink(categoryLinks);
	  else 
	      site = getRandomLink(siteLinks);
      }
      // If no site found, try another random path 
      if (site == null) {
 	  System.out.println("Failed to find site");
	  continue;
      }
      // Otherwise try to download and save the chosen site
      System.out.println("Picking Site: " + site);
      // Skip if already visited this page
      if (!visitedSites.add(site)) {
	  System.out.println("Already picked site");
	  continue;
      }
      if (!linkToHTMLPage(site)) {
	  System.out.println("Not HTML Page");
	  continue;
      }
      // Use the page retriever to get the page      
      HTMLPage sitePage = null;
      try {
	  sitePage = retriever.getHTMLPage(site);
      }
      catch (PathDisallowedException e) {
	  System.out.println(e);
	  continue;
      }
      if (sitePage.empty()) {
	  System.out.println("No Page Found");
	  continue;
      }
      // Increment saved page count and save the site page
      count++;
      System.out.println("Indexing" + "(" + count + "): " + site);
      indexPage(sitePage);
    }
  }

  /**
   * Pick a random link from a list of links
   */
  protected Link getRandomLink(List<Link> links) {
      int pos = random.nextInt(links.size());
      return links.get(pos);
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
   * "Indexes" a <code>HTMLpage</code>.  This version just writes it
   * out to a file in the specified directory with a filePrefix&lt;count&gt;.html file name.
   *
   * @param page An <code>HTMLPage</code> that contains the page to
   *             index.
   */
  protected void indexPage(HTMLPage page) {
    page.write(saveDir,
	       filePrefix + MoreString.padWithZeros(count, (int) Math.floor(MoreMath.log(maxCount, 10)) + 1));
  }

  /**
   * Spider Yahoo category to randomly collect pages according to the following command options:
   * <ul>
   * <li>-d &lt;directory&gt; : Store indexed files in &lt;directory&gt;.</li>
   * <li>-c &lt;maxCount&gt; : Find &lt;maxCount&gt; files (default is 10,000).</li>
   * <li>-u &lt;url&gt; : Start at Yahoo directory page given by &lt;url&gt;.</li>
   * <li>-p &lt;prefix &gt; : Prefix saved file names with &lt;prefix&gt;.</li>
   * <li>-slow : Pause briefly before getting a page.  This can be
   * useful when debugging.
   * </ul>
   */
  public static void main(String args[]) {
    new YahooSpider().go(args);
  }

}