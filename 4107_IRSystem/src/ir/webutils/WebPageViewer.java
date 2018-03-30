package ir.webutils;

/*
 * WebPageViewer.java
 * downloads and displays web pages.
 * June 1, 2001
 * Ted Wild
 */

import java.net.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

/**
 * WebPageViewer contains utilities to download and display HTML
 * pages.
 *
 * @author Ted Wild
 */
public class WebPageViewer {

  private String url;
  private JEditorPane webPagePane;
  private JFrame frame;
  private JTextField locationField;

  public WebPageViewer() {

    // set up the JEditorPane that will display HTML
    initializeWebPagePane();

    // set up the JTextField that will allow user to control the
    // location
    initializeLocationField();

    // set up the layout in a JFrame
    initializeLayout();

    // incantation to display components
    frame.setVisible(true);
  }

  private void initializeWebPagePane() {
    webPagePane = new JEditorPane();

    // Calling setEditable with false causes clicking on links to
    // generate HyperlinkEvents.  Calling setEditable with true
    // allows you to "edit" the pages.
    webPagePane.setEditable(false);

    // Here we add a HyperlinkEventListener that will update the
    // page being viewed.
    webPagePane.addHyperlinkListener(new HyperlinkListener() {

      public void hyperlinkUpdate(HyperlinkEvent e) {

        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
          JEditorPane jep = (JEditorPane) e.getSource();

          try {
            jep.setPage(e.getURL());
            frame.setTitle("WebPageViewer: " + e.getURL());
          }
          catch (IOException ex) {
            System.err.println("Unable to view " +
                e.getURL() + ": " + ex);
          }
        }
      }
    });

    // sets up JEditorPane to handle HTML
    webPagePane.setContentType("text/html");
  }

  private void initializeLocationField() {

    // creates a new JTextField, single argument specifies number
    // of columns
    locationField = new JTextField(20);

    // sets the initial text
    locationField.setText("http://");

    // adds a listener that will be called when <Enter> is
    // pressed.  The listener uses the text in the JTextField as a
    // URL and tries to display that URL in the webPagePane.
    locationField.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        JTextField f = (JTextField) e.getSource();

        try {
          webPagePane.setPage(f.getText());
          frame.setTitle("WebPageViewer: " + f.getText());
        }
        catch (IOException ex) {
          System.err.println("Unable to open " +
              f.getText() + ": " + ex);
        }

        f.setText("http://");
      }
    });
  }

  private void initializeLayout() {
    frame = new JFrame("WebPageViewer");

    // To get a scrollbar, all we have to do is put the
    // webPagePane in a JScrollPane.
    JScrollPane scrollPane = new JScrollPane(webPagePane);

    // This causes the program to exit when the JFrame's window is
    // closed.
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    frame.getContentPane().add(locationField, BorderLayout.NORTH);
    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
    frame.setSize(640, 720);
  }

  public void displayURL(URL url) {

    try {
      webPagePane.setPage(url);
      frame.setTitle(frame.getTitle() + ": " + url);
    }
    catch (IOException e) {
      System.err.println("Unable to download " + url + ": " + e);
    }
  }

  public static void main(String args[]) {

    try {
      new WebPageViewer().displayURL(new URL("http://www.cs.utexas.edu/users/ml"));
    }
    catch (MalformedURLException e) {

      // should never occur, url is known not to be malformed.
    }
  }
}
