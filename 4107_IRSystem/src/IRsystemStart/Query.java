package IRsystemStart;

public class Query {
	
	private String queryID;
	private String query;
	
	public Query(String id, String query) {
		this.queryID = id;
		this.query = query;
	}
	
	public String getID() {
		return this.queryID;
	}
	
	public String getQuery() {
		return this.query;
	}

}
