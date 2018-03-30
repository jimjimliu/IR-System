package ir.webutils;

import javax.swing.text.html.HTMLEditorKit;

/**
 * HTMLParserMaker.java
 *
 *
 * Created: Fri Jun  8 13:19:14 2001
 *
 * @author <a href="mailto:wildt@cs.utexas.edu">Edward Wild</a>
 * @version
 */

/**
 * HTMLParserMaker allows clients to retrieve an
 * HTMLEditorKit&#046;Parser instance.  The factory method defined in
 * HTMLEditorKit is protected, and the default parser it returns (the
 * HotJava parser using an HTML 3.2 DTD) is fairly good at tolerating
 * malformed HTML.
 *
 * @author Ted Wild
 */
public class HTMLParserMaker extends HTMLEditorKit {


  /**
   * Returns a parser.  The parser can be used in conjunction with
   * an <code>HTMLEditorKit.ParserCallback</code> to (relatively
   * tolerantly) parse an HTML document.
   *
   * @return Instance of javax.swing.html.HTMLEditorKit.Parser
   */
  public HTMLEditorKit.Parser getParser() {
    return super.getParser();
  }


}// HTMLParser
