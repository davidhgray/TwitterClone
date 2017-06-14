package twitter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Feed {
	public Feed() {
		String username;
		String content;
		String DateTime;
	}

	public void getFeed(String user, Connection conn) {
		String url = "jdbc:sqlite:fritter.db";

		// SQL statement for creating a new table
		
		String feedSQL = "select a.username,content,datetime from users, following,tweets, users a where users.username='" + 
		"david" + "'and users.id=following.following and tweets.user_id=following.followed and tweets.user_id=a.id;";

		try (Statement stmt = conn.) {
			// create a new table
			stmt.execute(usersSQL);

			String tweetsSQL = "CREATE TABLE IF NOT EXISTS tweets (\n" + "	id integer PRIMARY KEY,\n"
					+ " content text,\n" + "	dateTime text, \n"
					+ "	user_id integer NOT NULL, FOREIGN KEY (user_id) REFERENCES user(id)\n" + ");";

			stmt.execute(tweetsSQL);

			String followingSQL = "CREATE TABLE IF NOT EXISTS following (\n" + "	following integer NOT NULL, \n"
					+ "	followed integer NOT NULL, \n" + " FOREIGN KEY (following) REFERENCES user(id) \n,"
					+ " FOREIGN KEY (followed) REFERENCES user(id) \n" + ");";

			stmt.execute(followingSQL);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}