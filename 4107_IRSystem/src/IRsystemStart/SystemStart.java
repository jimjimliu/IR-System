package IRsystemStart;

import ir.utilities.*;
import java.io.File;
import java.io.IOException;
import ir.classifiers.*;
import ir.vsr.*;
import IRsystemStart.*;
import java.util.*;
import javax.swing.*;

public class SystemStart {
	
	public String TWEET_IN_PATH;
	public String TWEET_OUT_PATH;
	public String QUERY_PATH;
	public String RESULT_PATH;
	public String ROOT;
	private List<Query> tweet_query;
	private Boolean remove_link;
	private InvertedIndex index;
	
	public SystemStart() {
		this.ROOT = System.getProperty("user.dir");
		this.TWEET_IN_PATH = this.ROOT+"/files/twitter_blog.txt";
		this.TWEET_OUT_PATH = this.ROOT+"/tweets/";
		this.QUERY_PATH = this.ROOT+"/files/queries.txt";
		this.RESULT_PATH = this.ROOT+"/result.txt";
		this.tweet_query = null;
	}
	
	/**
	 * preprocess all twitter messages;
	 * re-locate each tweet in a .txt file
	 */
	public void preprocess() {

		 try{
	       Tokenize tokenizer = new Tokenize(this.TWEET_IN_PATH, this.TWEET_OUT_PATH);
	       tokenizer.tokenize();
	     }catch(Exception e) {e.printStackTrace();}
	}
	
	/**
	 * build inverted index;
	 * save index in variable 'this.index';
	 */
	public void indexer() {
		 short docType = DocumentIterator.TYPE_TEXT;
		 boolean stem = true, feedback = false;
		 this.index = new InvertedIndex(new File(this.TWEET_OUT_PATH), docType, stem, feedback);
	}
	
	/**
	 * fetch all queries from file and fetch query messages from query frames
	 */
	public void parse_query() {
		try {
			 System.out.println("");
			 TweetQueryParser parser = new TweetQueryParser(this.QUERY_PATH);
			 tweet_query = parser.parseTestQueries();
			}catch(Exception e) {e.printStackTrace();}
	}
	
	
	/**
	 * retrieve results of each queries and write to file;
	 */
	public void retrieve_result() {
		try{
			 System.out.println("");
		     WriteQueryResult result = new WriteQueryResult(tweet_query, this.index, this.RESULT_PATH);
		     result.writeToFile();
		    }catch(Exception e) {e.printStackTrace();}
	}
	
	/**
	 * retrieve result of one query message;
	 * @param query :String
	 * @return List of QueryResult
	 */
	public List<QueryResult> search_query(String query){
		List<QueryResult> result;
		result = this.index.processQuery(query);
		return result;
	}
	
	/**
	 * Print out an inverted index by listing each token and the documents it occurs in. 
	 * Include info on IDF factors, occurrence counts, and document vector lengths.
	 */
	private void get_index() {
		this.index.print();
	}
	
	/**
	 * Print out first number indicated tokens;
	 * @param number :Integer
	 */
	private void get_tokens(int number) {
		this.index.printTokens(number);
	}
	
 
 public static void main(String[] args) throws IOException {
	 
	 SystemStart starter = new SystemStart();
	 System.out.println(starter.ROOT);
//	 starter.preprocess();
//	 starter.indexer();
//	 starter.parse_query();
//	 starter.retrieve_result();
	 
//	 /**
//	  * fetch query input from cmd line, retrieve result;
//	  */
//	 System.out.println("Now Testing arbitrary query.\n");
//	 String query = JOptionPane.showInputDialog("请输入关键字进行搜索：");
//	 List<QueryResult> result = starter.search_query(query);
//	 
//	 /**
//	  * print ranked results
//	  */
//	 Iterator iter = result.iterator();
//	 for(int i=0; i<10; i++) {
//		 System.out.println(iter.next());
//		 }
 }

}
