package IRsystemStart;

public class QueryResult {
	
	private String tweetID;
	private String score;
	
	public QueryResult(String id, String score){
		
		this.tweetID = id;
		this.score = score;
		
	}
	
	public String getID() {
		return this.tweetID;
	}
	
	public String getScore() {
		return this.score;
	}

}
