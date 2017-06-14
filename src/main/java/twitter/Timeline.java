package twitter;

import java.sql.Connection;
import java.sql.DriverManager;
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

	public static Timeline getTimeline(String user) {

		String url = "jdbc:sqlite:fritter.db";

		// SQL statement for creating a new table

		String timeLineSQL = "select a.username,content,datetime from users, following,tweets, users a where users.username='"
				+ "david"
				+ "'and users.id=following.following and tweets.user_id=following.followed and tweets.user_id=a.id;";

		try (Connection conn = DriverManager.getConnection(url);
				Statement stmt = conn.prepareStatement(timeLineSQL);) {

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return new Timeline("", null); // TODO
	}
}