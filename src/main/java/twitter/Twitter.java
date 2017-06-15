package twitter;

import static spark.Spark.*;

import java.util.ArrayList;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.google.gson.Gson;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public class Twitter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// root is 'src/main/resources', so put files in
		// 'src/main/resources/public'
		staticFiles.location("public/"); // Static files
		port(3001);

		Connection conn = connect();
		
//		ArrayList<Timeline> tweetList = new ArrayList<Timeline>();
//		tweetList.get(0);
		
		createTables();

		// root directory
		get("/", (req, res) -> {

			Timeline timeline = Timeline.getTimeline("david");
			JtwigTemplate template = JtwigTemplate.classpathTemplate("public/twitter.html");
			JtwigModel model = JtwigModel.newModel().with("timeline", timeline.tweets);

			return template.render(model);
			// String htmlBody = "";
			//
			// for (int i = 0; i < AlbumCatalog.size(); i++) {
			//
			// htmlBody = htmlBody + "<div> Artist: " +
			// AlbumCatalog.get(i).artist + " Title:"
			// + AlbumCatalog.get(i).title + "</div>";
			// }
			//
			// String html = "<!DOCTYPE
			// html><html><head><h1>Albums</h1></head><body><h2>" + htmlBody
			// + "</h2></body></html>";
			// return html;

		});

		// // JSON return
		// get("/data", (req, res) -> {
		// Gson mygson = new Gson();
		// String json;
		// json = mygson.toJson(Tweets);
		// // System.out.println(json);
		// return json;
		// });
	}

	public static Connection connect() {
		Connection conn = null;
		try {
			String url = "jdbc:sqlite:fritter.db";
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to SQLite has been established.");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}
		return conn;
	}

	public static void createTables() {
		// SQLite connection string
		String url = "jdbc:sqlite:fritter.db";

		// SQL statement for creating a new table

		String usersSQL = "CREATE TABLE IF NOT EXISTS users (\n" + "	id integer PRIMARY KEY,\n"
				+ "	username text NOT NULL,\n" + "	email text ,\n" + "	password text \n" + ");";

		try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
			// create a new table
			stmt.execute(usersSQL);

			String tweetsSQL = "CREATE TABLE IF NOT EXISTS tweets (\n" + "	id integer PRIMARY KEY,\n"
					+ " content text,\n" + "	dt text DEFAULT CURRENT_TIMESTAMP);";
					

			stmt.execute(tweetsSQL);
			
			  	
			String userTweetsSQL = "CREATE TABLE IF NOT EXISTS userTweets (\n" + "	tweetId integer PRIMARY KEY,\n"
					+ " userId integer,\n"
					+ " dt text DEFAULT CURRENT_TIMESTAMP,\n" 
					+ " originalUserId integer,\n"
					+ " FOREIGN KEY (TweetId) REFERENCES tweets(id) \n,"
								+ " FOREIGN KEY (userId) REFERENCES user(id) ,\n" 
			  	    + " FOREIGN KEY (originalUserId) REFERENCES user(id) );";
					

			stmt.execute(userTweetsSQL);

			String followingSQL = "CREATE TABLE IF NOT EXISTS following (\n" + "	follower integer NOT NULL, \n"
					+ "	followed integer NOT NULL, \n" + " FOREIGN KEY (follower) REFERENCES user(id) \n,"
					+ " FOREIGN KEY (followed) REFERENCES user(id) \n" + ");";

			stmt.execute(followingSQL);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
