package ir.webutils;

import java.util.*;
import java.io.*;

/**
 * Graph data structure.
 *
 * @author Ray Mooney
 */
public class Graph {

  private Map<String, Node> nodeMap = new HashMap<String, Node>();
  Iterator<Map.Entry<String, Node>> iterator;

  /**
   * Basic constructor.
   */
  public Graph() {
  }

  /**
   * Adds an edge from xName to yName.
   */
  public void addEdge(String xName, String yName) {
    Node xNode = getNode(xName);
    Node yNode = getNode(yName);
    xNode.addEdge(yNode);
  }

  /**
   * Adds a node if it is not already present.
   */
  public boolean addNode(String name) {
    Node node = getExistingNode(name);
    if (node == null) {
      node = new Node(name);
      nodeMap.put(name, node);
      return true;
    } else return false;
  }

  /**
   * Returns the node with that name, creates one if not
   * already present.
   */
  public Node getNode(String name) {
    Node node = getExistingNode(name);
    if (node == null) {
      node = new Node(name);
      nodeMap.put(name, node);
    }
    return node;
  }

  /**
   * Reads graph from file where each line consists of a node-name followed by a
   * list of the names of nodes to which it points
   */
  public void readFromFile(String fileName) throws IOException {
    String line;
    BufferedReader in = new BufferedReader(new FileReader(fileName));
    while ((line = in.readLine()) != null) {
      StringTokenizer tokenizer = new StringTokenizer(line);
      String source = tokenizer.nextToken();
      while (tokenizer.hasMoreTokens()) {
        addEdge(source, tokenizer.nextToken());
      }
    }
    in.close();
  }

  /**
   * Returns the node with that name
   */
  public Node getExistingNode(String name) {
    return nodeMap.get(name);
  }

  /**
   * Resets the iterator.
   */
  public void resetIterator() {
    iterator = nodeMap.entrySet().iterator();
  }

  /**
   * Returns the next node in an iterator over the nodes of the graph
   */
  public Node nextNode() {
    if (iterator == null) {
      throw new IllegalStateException("Graph: Error: Iterator not set.");
    }
    if (iterator.hasNext())
      return iterator.next().getValue();
    else
      return null;
  }

  /**
   * Prints the entire graph on stdout.
   */
  public void print() {
    Node node;
    resetIterator();
    while ((node = nextNode()) != null) {
      System.out.println(node + "->" + node.getEdgesOut());
    }
  }

  /**
   * Returns all the nodes of the graph.
   */
  public Node[] nodeArray() {
    Node[] nodes = new Node[nodeMap.size()];
    Node node;
    int i = 0;
    resetIterator();
    while ((node = nextNode()) != null) {
      nodes[i++] = node;
    }
    return nodes;
  }


  public static void main(String[] args) throws IOException {
    Graph graph = new Graph();
    graph.readFromFile(args[0]);
    graph.print();
    System.out.println("\n" + graph.nodeArray().toString());
  }
}
