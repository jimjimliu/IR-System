package IRsystemStart;

import java.io.*;
import java.util.List;
import ir.vsr.InvertedIndex;

public class WriteQueryResult {
	
	private InvertedIndex indexer; // InvertedIndex object, which contains indexed file;
	private List<Query> queries;  // tweet queries;
	private String file_path;

	public WriteQueryResult(List<Query> queries, InvertedIndex index, String file) {
		
		this.indexer = index;
		this.queries = queries;
		this.file_path = file;
	}
	
	/*
	 * process the input query
	 * 
	 */
//	private List<String> getQueryResult(String query){
//		
//		List<String> result = this.indexer.processQuery(query);
//		return result;
//	}
	
	/*
	 * process each query, format of each line in this.queries is: "queryID, query";
	 */
	public void writeToFile() throws IOException{
		
		System.out.println("Now retrive top 1000 results from each query.");
		System.out.println("Please wait...");
		
		String queryID = "";

		//testquery number	0		tweetid			rank
		String out = "";
		List<QueryResult> result;
		
		for(Query line: this.queries) {

			queryID = line.getID();	//"MBxxx"
			result = this.indexer.processQuery(line.getQuery());
		
			
			int i=1;
			String temp = "";
			//go through each tweet retreived
			for(QueryResult line2 : result){

				//build output line
				temp += queryID+"\tQ0\t"+line2.getID()+"\t"+i+"\t"+line2.getScore()+" ohlala\r\n";
				i++;
		
			}//end for one query;
			
			out += temp;
			//clear local variables;
			queryID = "";
		}
		
		this.write(out);
		
	}
	
	private void write(String input) throws IOException {
		
		System.out.println("\nNow start writing...");
		
		File file = new File(this.file_path);
		try {
			file.createNewFile();
			} catch(Exception e) {}
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		out.write(input);
		out.flush();
		out.close();
		
		System.out.println("Finish writing.");
		System.out.println("The result file is in: "+this.file_path);
	}
}
