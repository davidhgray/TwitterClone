package twitter;

import java.util.ArrayList;

public class Tweet {
	int ID;
    int user_id;
    String username;
    String content;
    String dateTime;

    
	// this contstructor will be used when building tweets for Timeline
    public Tweet(String username, String content, String dateTime) {
	 this.username = username;
     this.content=content;
     this.dateTime=dateTime;
	}

    //this constructor will be used for building new tweet
    public Tweet(String username, String content) {
   	    this.username = username;
        this.content=content;
      
   	}
    
	
	
}
