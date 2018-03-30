package IRsystemStart;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class TweetQueryParser {
	
	private String query_path;
	
	public TweetQueryParser(String test_query_path) {
		this.query_path = test_query_path;
	}

	
	/*
	 * Retrive string lines from tweet data path,
	 * return: List<String> contains query strings of format "MBxxx,query";
	 */
	public List<Query> parseTestQueries()throws IOException{

		List<Query> result = new ArrayList<Query>(49);
		String queryID = "";
		String tweet_query = "";
		int id = -1;
		
		//read lines from file
		String [] lines = this.getLines(this.query_path);
		System.out.println("Now parsing 49 quries...");
		for(String query : lines) {
			if(query.startsWith("<num>")){
				
				int i = query.indexOf("MB");
				String num = query.substring(i+2, i+5);
				//queryID = "MB"+num; //MBxxx
				queryID = String.valueOf(Integer.parseInt(num));
				
			}else if(query.startsWith("<title>")){
				
				int i = query.indexOf("</title>");        
				tweet_query = query.substring( 7, i);
				
			}else if(query.startsWith("</top>")){
				Query q = new Query(queryID, tweet_query);
				result.add(q);
				queryID = "";
				tweet_query = "";
				id = -1;
			}else{
			}	
		}
		System.out.println("Finish parsing 49 quries.");
		return result;
	}
	
	
	/*
	 * @param: file: file path of input document;
	 * Take a file path as input;
	 * return a String[] contains each line in the file;
	 */
	public String [] getLines (String file)throws IOException {
	    String [] result = null;
	    ArrayList<String> list = new ArrayList<String>((int)file.length());
	    	BufferedReader input = null;
	    	FileInputStream _f = new FileInputStream( file );
			InputStreamReader _is = new InputStreamReader( _f );
			input = new BufferedReader( _is );

		    String temp;
		    while((temp =input.readLine())!=null){
		       	//get string array of line
		    	list.add(temp);
		    }

		    input.close();
			Object [] tmpArr = list.toArray();
			result = new String[tmpArr.length];
			for(int i=0;i<result.length;i++){
				result[i]=(String)tmpArr[i];
			}
	        return result;
	  }
	
	public static void main(String[] args) {
		TweetQueryParser parser = new TweetQueryParser("/Users/junhanliu/Desktop/topics_MB1-49.txt");
		try{
			for(Query query: parser.parseTestQueries()) {
				System.out.println(query.getID()+", "+query.getQuery());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
