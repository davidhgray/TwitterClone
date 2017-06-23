package twitter;

import java.util.ArrayList;

public class Tweet {
	int ID;
    int user_id;
    String username;
    String content;
    String tweetDt;

    
	// this contstructor will be used when building tweets for Timeline
    public Tweet(String username, String content, String tweetDt) {
	 this.username = username;
     this.content=content;
     this.tweetDt=tweetDt;
	}
    
	// this contstructor will be used when building tweets for Timeline
    public Tweet(int ID, int user_id, String username, String content, String tweetDt) {
	 this.ID = ID;
	 this.user_id = user_id;
	 this.username = username;
     this.content=content;
     this.tweetDt=tweetDt;
	}
    
    //this constructor will be used for building new tweet
    public Tweet(String username, String content) {
   	    this.username = username;
        this.content=content;
      
   	}
    
    public Tweet(int id, String username, String content, String tweetDt) {
	 this.user_id = id;
	 this.username = username;
     this.content=content;
     this.tweetDt=tweetDt;
	}
    
	
	
}
