package twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Timeline {
	public String username;
	public ArrayList<Tweet> tweets;

	public Timeline(String username, ArrayList<Tweet> tweets) {
		this.username = username;
		this.tweets = tweets;
	}

	public static Timeline getTimeline(String username) throws Exception {

		String url = "jdbc:sqlite:fritter.db";

		ArrayList<Tweet> holddata = new ArrayList<Tweet>();
		
		// SQL statement for creating a new table

		String timeLineSQL = "select a.username,content,ut.dateTime from users, following,tweets ,userTweets ut , users a  where users.username='" + "david" + "'and users.id=following.follower and ut.userid=following.followed and ut.userid=a.id and tweets.id=ut.tweetid;";		
		
		try (Connection conn = DriverManager.getConnection(url)){
				   
			try (PreparedStatement stmt = conn.prepareStatement(timeLineSQL);
					         ResultSet rs = stmt.executeQuery()) {
//                           ArrayList<Tweet> holddata;
				
					       while (rs.next()) {
					               Tweet a =new Tweet(rs.getString("username"),
					                           rs.getString("content"),
					                           rs.getString("dateTime"));
					            holddata.add(a);
					        }
					    }
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return new Timeline(username, holddata);
	}
}

