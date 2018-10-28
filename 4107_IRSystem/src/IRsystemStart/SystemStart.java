package IRsystemStart;

import ir.utilities.*;
import java.io.File;
import ir.classifiers.*;
import ir.vsr.*;
import IRsystemStart.*;
import java.util.*;

public class SystemStart {
 
 public static void main(String[] args) {

	  if(args.length < 4){
	   
	   System.out.println("Invalid arguments... please indicate your original tweet file path,"
	     + " the path you want to store all tweets' file, your query document path, and your result.txt path."
	     + "\n i.e. The path of your original tweet document should be similar like this: /xxx/xxx/Desktop/Trec_microblog11.txt"
	     + "\n      The path of your preprocessed documents should be similar like: /xxx/xxx/Desktop/doc/");
	   System.exit(1);
	  }
	
	 String tweet_data_path = args[0]; //tweet document path;
	 String tweet_output_path = args[1]; //tweet output path;
	 
	 if(! tweet_output_path.substring(tweet_output_path.length()-1).equals("/")) {
	   tweet_output_path = tweet_output_path + "/";
	   System.out.println("\nIllegal path format: \nYour second input ("+args[1]+") is missing a '/' at the end.\n"
	     + "Add '/' at the end, indicates all tweet txt file will be store in the folder.\n"
	     + "If you are running in windows, please using slash '/' to indicate path instead of back slash '\\'.\n"
	     + "Suggestion: create a new foler, and pass the folder's path as input.\n"
	     + "i.e. The correct format of the path should be /xxxx/xxxx/your folder/ \n");
	   System.exit(1);
	 }
	 
	 String tweet_query_path = args[2]; //tweet query document path;
	 String query_output_path = args[3]; //query result document path;
	 String output_path = null; //tweet output path;
	 List<Query> tweet_query = null; //stores all the query strings;
	 boolean remove_link = false; //remove hyper link or not;
	 

	 /**
	  * tweet preprocessing
	  */
	 try{
	   /*default to remove hyper link*/
	   //Tokenize tokenizer = new Tokenize(tweet_path, tweet_output_path, remove_link);
       Tokenize tokenizer = new Tokenize(tweet_data_path, tweet_output_path);
       tokenizer.tokenize();
       output_path = tokenizer.getOutputPath();
     }catch(Exception e) {e.printStackTrace();}
	   
	 /**
	  * indexing
	  */
	 short docType = DocumentIterator.TYPE_TEXT;
	 boolean stem = true, feedback = false;
	 InvertedIndex index = new InvertedIndex(new File(output_path), docType, stem, feedback);
	     
	     
	/**
	 * parsing queries
	 */
	try {
	 System.out.println("");
	 TweetQueryParser parser = new TweetQueryParser(tweet_query_path);
	 tweet_query = parser.parseTestQueries();
	}catch(Exception e) {e.printStackTrace();}


	/**
	 * retrieve result
	 */
	try{
	 System.out.println("");
     WriteQueryResult result = new WriteQueryResult(tweet_query, index, query_output_path);
     result.writeToFile();
    }catch(Exception e) {e.printStackTrace();}
	   
	    
	     
//	 /**
//	  * list each token in all documents, and the document id it occurs in;
//	  */
//	 index.print();
//	 
//	 /**
//	  * print out first 100 tokens;
//	  */
//	 index.print100Tokens();
//	 
//	 /**
//	  *  Interactively process queries to this index.
//	  */
//	 index.processQueries();
//	     
	
	 }

}
