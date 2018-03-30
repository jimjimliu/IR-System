package ir.webutils;

import java.util.*;
import java.net.*;

/**
 * Keeps track of Robot Exclusion information.  Clients can use this
 * class to ensure that they do not access pages prohibited either by
 * the Robots Exclusion Protocol or Robots META tags.
 *
 * @author Ted Wild & Ray Mooney
 */
public final class SafeHTMLPageRetriever extends HTMLPageRetriever {

  private Set<String> disallowed;
  private String currentSite;

  public SafeHTMLPageRetriever() {
    disallowed = new RobotExclusionSet();
    currentSite = "";
  }

  /**
   * Tries to download the given web page.  Throws
   * <code>PathDisallowedException</code> if access to the page is
   * prohibited.  Also updates Robots Exclusion information based on
   * the new page.
   *
   * @param link The Link to follow and download.
   * @return The web page specified by the URL.
   * @throws PathDisallowedException If <code>url</code> is
   *                                 disallowed by a robots.txt file or Robots META tag.
   */
  public HTMLPage getHTMLPage(Link link) throws PathDisallowedException {

    // check to make sure access to link is not disallowed
    // (e. g. because of a NOFOLLOW)
    if (disallowed.contains(link.getURL()))
      throw new PathDisallowedException("Robot access disallowed :" + link);

    // if URL is for a different site, update the robots.txt file
    if (!currentSite.equals(getSite(link.getURL()))) {
      currentSite = getSite(link.getURL());
      disallowed =
          new RobotExclusionSet(currentSite);
    }
    // currentSite and disallowed are updated for this URL

    // check to make sure this site is not already prohibited
    if (disallowed.contains(link.getURL().getPath()))
      throw new PathDisallowedException("Robot access disallowed: " + link);
    String page = WebPage.getWebPage(link.getURL());
    RobotsMetaTagParser metaInf = new RobotsMetaTagParser(link.getURL(), page);

    // check for Robots META tags and add new rules
    disallowed.addAll(getPaths(metaInf.parseMetaTags()));

    return new SafeHTMLPage(link, page, metaInf.index());
  }

  // The "site" is the host and port of the URL.  This
  // information can be found by stripping any user information
  // off the authority (the part of the URL between the protocol
  // and the path).

  private String getSite(URL url) {
    String site = url.getAuthority();

    if (site.indexOf("@") != -1)
      return site.substring(site.indexOf("@") + 1);
    else
      return site;
  }

  // Convert links into paths so that the RobotExclusionSet will
  // appropriately handle them.

  private List<String> getPaths(List<Link> links) {
    List<String> paths = new LinkedList<String>();
    for (Link link : links)
      paths.add(link.getURL().getPath());
    return paths;
  }
}
