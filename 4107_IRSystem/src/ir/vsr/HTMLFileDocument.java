package ir.vsr;

import java.io.*;
import java.util.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

/**
 * A thread that parses an HTML file document and extracts plain text
 * from the title and the document body.  It feeds the
 * HTMLFileDocument reader with a stream of text free of HTML
 * commands.  It makes use of the Swing HTML parser which converts
 * HTML character entities into Java characters automatically.
 *
 * @author Yuk Wah Wong
 */
class HTMLFileParserThread extends Thread {

  /**
   * The name of the HTML file
   */
  protected File file;
  /**
   * The I/O reader for accessing the HTML file
   */
  protected BufferedReader reader;
  /**
   * The I/O writer to which extracted plain text is written
   */
  protected PrintWriter writer;

  /**
   * Create an HTMLFileParserThread and initialize its reader and writer.
   */
  public HTMLFileParserThread(File file, BufferedReader reader, Writer writer) {
    this.file = file;
    this.reader = reader;
    this.writer = new PrintWriter(writer);
  }

  /**
   * Read the HTML file and parse it.  Extract plain text from the
   * document and print it to the I/O writer.
   */
  public void run() {
    try {
      // the HTML parser callback ignores everything but the
      // content of the document
      HTMLEditorKit.ParserCallback callback =
          new HTMLEditorKit.ParserCallback() {
            public void handleText(char[] data, int pos) {
              // print the text to the I/O writer
              writer.println(data);
            }
          };
      // entry point into the Swing HTML parser
      new ParserDelegator().parse(reader, callback, true);
      // close the I/O reader and writer when parsing is finished
      reader.close();
      writer.close();
    } catch (IOException e) {
      System.out.println("\nCould not read HTMLFileDocument: " + file);
      System.exit(1);
    }
  }

}

/**
 * An HTML file document where HTML commands are removed
 * from the token stream.  To include HTML tokens, just
 * create a TextFileDocument from the HTML file.
 *
 * @author Ray Mooney
 */

public class HTMLFileDocument extends FileDocument {

  /**
   * StringTokenizer delim for tokenizing only alphabetic strings.
   */
  public static final String tokenizerDelim = " \t\n\r\f\'\"\\1234567890!@#$%^&*()_+-={}|[]:;<,>.?/`~";

  /**
   * The tokenizer for lines read from this document.
   */
  protected StringTokenizer tokenizer = null;

  /**
   * The I/O reader for accessing the output of the HTML parser.
   */
  protected BufferedReader textReader = null;

  /**
   * Create a new text document for the given file.
   */
  public HTMLFileDocument(File file, boolean stem) {
    super(file, stem);  // Create a FileDocument
    try {
      // Create a new thread for parsing the HTML file.  The
      // output stream of the thread is connected to textReader.
      PipedWriter textWriter = new PipedWriter();
      textReader = new BufferedReader(new PipedReader(textWriter));
      HTMLFileParserThread thread = new HTMLFileParserThread(file, reader, textWriter);
      // The thread parses the HTML file at the same time when
      // its plain text output is read
      thread.start();

      // create a StringTokenizer for the first line of the
      // plain text
      String line = textReader.readLine();
      if (line != null) {
        this.tokenizer = new StringTokenizer(line, tokenizerDelim);
      }
      prepareNextToken();  // Prepare the first token
    }
    catch (IOException e) {
      System.out.println("\nCould not read HTMLFileDocument: " + file);
      System.exit(1);
    }
  }

  /**
   * Create a new text document for the given file name.
   */
  public HTMLFileDocument(String fileName, boolean stem) {
    this(new File(fileName), stem);
  }

  /**
   * Return the next purely alpha-character token in the document, or null if none left.
   */
  protected String getNextCandidateToken() {
    if (tokenizer == null)
      return null;
    String candidateToken = null;
    try {
      // Loop until you find a line in the file with a token
      while (!tokenizer.hasMoreTokens()) {
        // Read another line of plain text
        String line = textReader.readLine();
        if (line == null) {
          // End of file, no more tokens, return null
          textReader.close();
          return null;
        } else
          // Create a tokenizer for this file line
          tokenizer = new StringTokenizer(line, tokenizerDelim);
      }
      // Get the next token in the current line
      candidateToken = tokenizer.nextToken();
    }
    catch (IOException e) {
      System.out.println("\nCould not read from HTMLFileDocument: " + file);
      System.exit(1);
    }
    return candidateToken;
  }

  /**
   * For testing, print the bag-of-words vector for a given HTML file
   */
  public static void main(String[] args) throws IOException {
    String fileName = args[args.length - 1];
    Document doc = new HTMLFileDocument(fileName, args[0].equals("-stem"));
    doc.printVector();
    System.out.println("\nNumber of Tokens: " + doc.numberOfTokens());
  }

}

