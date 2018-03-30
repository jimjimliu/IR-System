package ir.webutils;

/**
 * PathDisallowedException is thrown to indicate that a client program tried
 * to access a path that was disallowed by either a robots.txt file or a robots META tag.
 *
 * @author Ted Wild
 */
public class PathDisallowedException extends Exception {

  public PathDisallowedException() {
    super();
  }

  public PathDisallowedException(String msg) {
    super(msg);
  }
}
