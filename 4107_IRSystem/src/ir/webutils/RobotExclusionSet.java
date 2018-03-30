package ir.webutils;

import ir.utilities.*;

import java.util.*;
import java.util.regex.*;
import java.io.*;

/**
 * RobotExclusionSet provides support for the Robots Exclusion
 * Protocol.  This class provides the ability to parse a robots.txt
 * file and to check files to make sure that access to them has not
 * been disallowed by the robots.txt file.  This class can also be
 * used to exclude files linked to on a page that specifies NOFOLLOW
 * in its Robots META tag.
 *
 * @author Ted Wild & Ray Mooney
 */

public class RobotExclusionSet extends AbstractSet<String> {

  private LinkedList<String> set;

  /**
   * Constructs an empty set.
   */
  public RobotExclusionSet() {
    super();
    set = new LinkedList<String>();
  }

  /**
   * Constructs a set containing the paths in the robots.txt file
   * for this site. The  robots.txt
   * file should conform to the Robots Exclusion Protocol
   * specification, available at
   * http://www.robotstxt.org/wc/norobots.htmquerycount.
   *
   * @param site The name of the site
   */
  public RobotExclusionSet(String site) {
    super();
    set = new LinkedList<String>();
    String robotText = WebPage.getWebPage("http://" + site + "/robots.txt");
    if (robotText != null)
      this.parseRobotsFileString(robotText);
  }

  public int size() {
    return set.size();
  }

  public boolean add(String o) {
    if (set.contains(o))
      return false;

    set.add(o);
    return true;
  }

  public Iterator<String> iterator() {
    return set.iterator();
  }

  /**
   * Checks to see if a path is prohibited by this set.  A path is
   * prohibited if it starts with an entry in this set.
   *
   * @param path <code>String</code> object representing the path.
   * @return <code>true</code> iff. <code>o</code> is a
   *         <code>String</code> object, <code>o</code> is not
   *         <code>null</code>, and for each element e in this set
   *         <code>!o.startsWith(e)</code>.
   */
  public boolean contains(String path) {
    if (path.equals(""))
      path = "/";

    Iterator<String> i = set.iterator();
    boolean disallowed = false;

    while (!disallowed && i.hasNext()) {
      String disallowedPath = i.next();
      if (path.startsWith(disallowedPath))
        disallowed = true;
    }

    return disallowed;
  }

  /**
   * This method based on code in the WWW::RobotRules module in the
   * libwww-perl5 library, available from www.cpan.org.
   *
   * @param robotsFile The robots.txt file represented as a string.
   */
  private void parseRobotsFileString(String robotsFile) {
    int currentIndex = 0;
    // Regex Pattern matchers for finding user-agent, disallow, and blank lines in file
    Matcher userAgentLine = Pattern.compile("(?i)User-Agent:\\s*(.*)").matcher(robotsFile);
    Matcher disallowLine = Pattern.compile("(?i)Disallow:\\s*(.*)").matcher(robotsFile);
    Matcher blankLine = Pattern.compile("\n\\s*\n").matcher(robotsFile);
    // Find each user-agent portion of file
    while (userAgentLine.find()) {
      if (userAgentLine.group(1).indexOf('*') != -1) {
        // this User-Agent line applies to this robot
        // find next blank line after this user-agent line
        currentIndex = userAgentLine.end();
        blankLine.region(currentIndex, robotsFile.length());
        // Index of next blank line
        int blankLineIndex = robotsFile.length();
        if (blankLine.find())
          blankLineIndex = blankLine.start();
        // Find disallow lines before next blank line (or end of file)
        disallowLine.region(currentIndex, blankLineIndex);
        while (disallowLine.find()) {
          // For each disallow line, add its path to the disallowed set
          String disallowed = disallowLine.group(1).trim();
          if (disallowed.length() > 0) {
            if (disallowed.endsWith("/"))
              disallowed = disallowed.substring(0, disallowed.lastIndexOf('/'));
          }
          this.add(disallowed);
        }
      }
    }
  }

  /** The following methods are for test/diagnostic purposes */

  /**
   * Outputs list of disallowed rules.  Intended only for testing.
   *
   * @param w The writer the list should be output to.
   */
  void printDisallowed(Writer w) {
    Iterator<String> i = this.iterator();
    try {
      while (i.hasNext())
        w.write(i.next() + '\n');
      w.flush();
    }
    catch (IOException e) {
      System.err.println("RobotExclusionSet.printDisallowed(): error writing to writer.  " + e);
      System.exit(1);
    }
  }

  /**
   * For testing only.  Parses robosts.txt file for a particular site
   */
  public static void main(String[] args) {
    RobotExclusionSet r = new RobotExclusionSet(args[0]);
    r.printDisallowed(new PrintWriter(System.out));
  }

}
	
