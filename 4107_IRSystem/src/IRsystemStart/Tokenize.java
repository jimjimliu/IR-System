package IRsystemStart;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/*
 * The interface takes an input data path, which should be a document file,
 * and takes a output path, which all tweets(strings) should locate in;
 * After the preprocessing, all output tweets should contain no special characters, and hyperlinks
 * as a user's wish;
 * The default action is to remove hyper links, but one can pass boolean input to indicate otherwise;
 * The further tokenizing process is separated from here,  tokenizing each tweet further
 * is done in .jar library(ir/vsr/InvertedIndex.java);
 */

public class Tokenize {

		private String file_path;
		private String outout_path;
		private String [] tweetData;
		private boolean remove_hyper_links = true;
		
		public Tokenize(String data_path, String output_path) throws IOException{
			
			this.file_path = data_path;
			this.outout_path = output_path;
			
			//read tweet;
			tweetData = this.getLines(file_path);
			System.out.println("\nSearching the file path;");
		}
		
		public Tokenize(String data_path, String output_path, boolean hyper_link_remove) throws IOException{
			
			this.file_path = data_path;
			this.outout_path = output_path;
			this.remove_hyper_links = hyper_link_remove;
			//read tweet;
			tweetData = this.getLines(file_path);
			System.out.println("Searching the file path;");
		}
		
		
		
		public void tokenize() throws IOException{
			
			//error checking
			if(tweetData == null || tweetData.length==0){	
				throw new IllegalStateException("A problem occured whil tokenizing. The data wasn't initiated properly."
						+ "Make sure the file "+this.file_path+" exists and has the tweet data");	
			}

			String [] lines = this.tweetData;
			
			System.out.println("Ready for parsing tweet in path: "+this.file_path);
			System.out.println("Hang on, I am parsing the tweet...\nThis may take up to 20 seconds.");
			
			for(String tweet : lines){
				//retrive tweet id;
				String id = tweet.substring(0, 17);
//				System.out.print(id+".txt");
				//retrive tweet string;
				String data = tweet.substring(18);

				/*before sparate each tweet into different files in /doc,filter the tweet. 
				 * Remove all special charaters and hyper links;
				 * Removing hyper links may affact the rank and score;
				 * This part of work can also be done by the .jar library;
				 */
				String filtered_data = this.removeSymboles(data);
				
				//write to file;
				String filename = this.outout_path+id+".txt";
				File file = new File(filename);
				try {
					file.createNewFile();
					} catch(Exception e) {}
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(filtered_data);
				out.flush();
				out.close();
				//end write to file;
			}
			
			System.out.println("Finish parsing the tweet file.");
			System.out.println("Now all tweets are located in: "+this.outout_path+"\n");

			
		}
		
		
		/*
		 * remove hyperlinks in the input string;
		 */
		private String removeHyperLink(String input){
	        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
	        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
	        Matcher m = p.matcher(input);
	        int i = 0;
	        while (m.find()) {
	            input = input.replaceAll(m.group(i),"").trim();
	            i++;
	        }
	        return input;
	    }
		
		public List<String> getHyperLink(String input){
	        
	        List<String> list = new ArrayList<String>();
	        Pattern pattern = Pattern.compile("\\(?\\b(http://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]");
	        Matcher matcher = pattern.matcher(input);   
	        int counter = 0;
	        while (matcher.find())
	        {
	            //System.out.println(matcher.group());
	            list.add(matcher.group());
	            counter++;
	        }
	        return list;
	    }
	    
		
		
		/* @param: String data;
		 * Takes a String contains special characters and hyperlinks;
		 * returns a String with special characters and links removed;
		 */
		public String removeSymboles(String _data){
			
			String lower_case = _data.toLowerCase();
			//Store links;
			List<String> links = this.getHyperLink(lower_case);
			String filter = lower_case.replaceAll("[\\[\\]{}<>^'\"“;!\\-\\+&#/%?,=~_|\\\\/:\\(\\)\\.\\*@]", "");
			String temp = removeHyperLink(filter);
			String result = temp.replaceAll("http.*", "");
			
			if(! this.remove_hyper_links) {
				//if false, put links back;
				for(String link: links) {
					result = result + link + " ";
				}
			}
//			System.out.println(result);
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
		
		public String getOutputPath() {
			return this.outout_path;
		}
		
		
		
}//end class
