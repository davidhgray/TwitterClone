package twitter;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class FritterDB {

	// class to contain all sql

	public static final String url = "jdbc:sqlite:fritter.db";

	public static final String createUsersTblSQL = "CREATE TABLE IF NOT EXISTS users (\n"
			+ "	id integer PRIMARY KEY,\n" + "	username text NOT NULL,\n"
			+ "	email text ,\n" + "	password text \n" + ");";

	public static final String createTweetsTblSQL = "CREATE TABLE IF NOT EXISTS tweets (\n"
			+ "	id integer PRIMARY KEY,\n" + " content text,\n"
			+ "	dt text DEFAULT CURRENT_TIMESTAMP);";

	public static final String userTweetsTblSQL = "CREATE TABLE IF NOT EXISTS userTweets (\n"
			+ "	tweetId integer ,\n" + " userId integer,\n"
			+ " dt text DEFAULT CURRENT_TIMESTAMP,\n"
			+ " originalUserId integer,\n"
			+ " FOREIGN KEY (TweetId) REFERENCES tweets(id) \n,"
			+ " FOREIGN KEY (userId) REFERENCES user(id) ,\n"
			+ " FOREIGN KEY (originalUserId) REFERENCES user(id) );";

	public static final String followingTblSQL = "CREATE TABLE IF NOT EXISTS following (\n"
			+ "	follower integer NOT NULL, \n"
			+ "	followed integer NOT NULL, \n"
			+ " FOREIGN KEY (follower) REFERENCES user(id) \n,"
			+ " FOREIGN KEY (followed) REFERENCES user(id) \n" + ");";

	public static void createTables() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to SQLite has been established.");
			Statement stmt = conn.createStatement();
			stmt.execute(createUsersTblSQL);
			stmt.execute(createTweetsTblSQL);
			stmt.execute(userTweetsTblSQL);
			stmt.execute(followingTblSQL);
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
	}

	public static int insertUser(User usr) throws NoSuchAlgorithmException {
		Connection conn = null;
		int status = 0;
		usr.hashPassword();

		try {
			conn = DriverManager.getConnection(url);

			PreparedStatement stmt = null;
			// check to see if user already exists
			String sql = "select count(1) cnt from  Users where username =?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, usr.username);
			ResultSet rs = stmt.executeQuery();
			int cnt = rs.getInt("cnt");
			if (cnt > 0) {
				return -2;// should never happen
			} else { // insert new user
				sql = "insert into Users (username,password, email) values(?,?,?)";
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, usr.username);
				stmt.setString(2, usr.hashedPassword);
				stmt.setString(3, usr.email);
				status = stmt.executeUpdate();
			}
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
		System.out.println(usr.hashedPassword);
		return status;

	}

	public static boolean checkUser(User usr) {
		Connection conn = null;
		usr.hashPassword();

		try {
			conn = DriverManager.getConnection(url);

			String sql = "select id, email, username, password from Users where userName = ? and password =?";
			PreparedStatement stmt = null;
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, usr.username);
			stmt.setString(2, usr.hashedPassword);

			ResultSet rs = stmt.executeQuery();
			boolean rowFound = false;

			while (rs.next()) {
				usr.id = rs.getInt("id");
				usr.email = rs.getString("email");
				System.out.println(rs.getString("password"));
				rowFound = true;
			}
			if (!rowFound) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				System.out.println(ex.getMessage());
			}
		}

	}

	public static ArrayList<Tweet> getTweets(User usr) {

		ArrayList<Tweet> timeline = new ArrayList<Tweet>();

		// return the tweets of the logged-in user + tweets of those followed
		String sql = "select ut.tweetId, b.id, b.username,content,ut.dt \n"
				+ "from users a, following,tweets ,userTweets ut , users b  \n"
				+ "where a.username=(?) \n" + "and a.id=following.follower \n"
				+ "and ut.userid=following.followed \n" + "and ut.userid=b.id\n"
				+ "and tweets.id=ut.tweetid \n" + "UNION\n"
				+ "select ut.tweetId, a.id, username,content,ut.dt \n"
				+ "from users a,tweets ,userTweets ut  \n"
				+ "where a.username=(?) and ut.userid=a.id\n"
				+ "and tweets.id=ut.tweetid \n" + "order by ut.dt desc;\n";

		try (Connection conn = DriverManager.getConnection(url)) {

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, usr.username);
			stmt.setString(2, usr.username);
			ResultSet rs = stmt.executeQuery();
			{
				while (rs.next()) {
					Tweet a = new Tweet(rs.getInt("tweetId"),
							rs.getInt("id"),
							rs.getString("username"),
							rs.getString("content"), 
							rs.getString("dt"));
					timeline.add(a);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return timeline;
	}

	public static ArrayList<User> getPopular(User usr) {

		ArrayList<User> popularUsers = new ArrayList<User>();

		// return active users for the purpose of following
		//- anyone who has tweeted EXCEPT the current user or anyone the current user is already following


		String sql = " select u.id, u.username"
				+ " from users u \n"
				+ " , (select count(1) tweetCnt, ut.userid from tweets , userTweets ut \n"
				+ " where tweets.id=ut.tweetid group by ut.userid order by tweetCnt desc) maxtweets \n"
				+ " where u.id=maxtweets.userid \n"
				+ " and u.username <> ? \n"
				+ " and u.id not in (select b.id \n"
				+ "from users u \n"
				+ ",following \n"
				+ ",users b \n"
				+ "where u.username =? \n"
				+ "and u.id=following.follower \n"
				+ "and b.id=following.followed);" ;

		try (Connection conn = DriverManager.getConnection(url)) {

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, usr.username);
			stmt.setString(2, usr.username);
			ResultSet rs = stmt.executeQuery();
			{
				while (rs.next()) {
					User u = new User(rs.getString("username"),
							rs.getInt("id"));

					popularUsers.add(u);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return popularUsers;
	}

	public static ArrayList<Tweet> getTweets() {

		ArrayList<Tweet> timeline = new ArrayList<Tweet>();

		// return top 10 recent tweets for homepage
		String sql = "select username,content,ut.dt \n"
				+ "from users a,tweets ,userTweets ut  \n"
				+ "where ut.userid=a.id\n" + "and tweets.id=ut.tweetid \n"
				+ "order by ut.dt desc limit 10;\n";

		try (Connection conn = DriverManager.getConnection(url)) {

			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			{
				while (rs.next()) {
					Tweet a = new Tweet(rs.getString("username"),
							rs.getString("content"), rs.getString("dt"));
					timeline.add(a);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return timeline;
	}

	public static ArrayList<Tweet> getFeed(String userName) {

		ArrayList<Tweet> feed = new ArrayList<Tweet>();

		// return the feed for the userName arg
		String sql = "select username,content,ut.dt \n"
				+ "from users a,tweets ,userTweets ut  \n"
				+ "where a.username=(?) and ut.userid=a.id\n"
				+ "and tweets.id=ut.tweetid \n" + "order by ut.dt desc;\n";

		try (Connection conn = DriverManager.getConnection(url)) {

			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userName);
			ResultSet rs = stmt.executeQuery();
			{
				while (rs.next()) {
					Tweet a = new Tweet(rs.getString("username"),
							rs.getString("content"), rs.getString("dt"));
					feed.add(a);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return feed;
	}

	public static boolean insertTweet(User usr, String content) {

		boolean tweetInserted = false;

		String sql = "insert into tweets (content) values (?);";

		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement stmt = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS);) {
			conn.setAutoCommit(false);
			stmt.setString(1, content);

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				conn.rollback();
				System.out.println("Tweet failed to insert");
				return false;
			}

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					long tweetId = generatedKeys.getLong(1);

					sql = "insert into userTweets (tweetId, userId) values (?, ?);";
					try (PreparedStatement stmt2 = conn.prepareStatement(sql,
							Statement.RETURN_GENERATED_KEYS);) {
						stmt2.setLong(1, tweetId);
						stmt2.setLong(2, usr.id); //
						affectedRows = stmt2.executeUpdate();
						if (affectedRows == 0) {
							conn.rollback();
							System.out.println("Tweet failed to insert");
							return false;
						}
					} catch (SQLException e) {
						conn.rollback();
						throw new RuntimeException(e);
					}

				} else {
					System.out.println("Tweet failed to insert");
					conn.rollback();
					return false;

				}
			}
			conn.commit();
			tweetInserted = true;
			return tweetInserted;
		} catch (SQLException e) {
			return false;

		}

	}

	
	public static boolean reTweet(String username, int tweetId) {

		boolean tweetInserted = false;
		
		String sql = "Insert into usertweets \n"
				+ "(tweetId,userId,dt,originalUserId) \n"
				+ "select tweetid, a.id ,CURRENT_TIMESTAMP,ut.userid\n"
				+ "from \n"
				+ "usertweets ut \n"
				+ ",users a \n" 
				+ "where ut.tweetid=? \n"
				+ "and originalUserId is null \n"
				+ "and a.username =?";

		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement stmt = conn.prepareStatement(sql);) {

			stmt.setInt(1, tweetId);
			stmt.setString(2, username);
			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				System.out.println("Following row failed to insert");
				return tweetInserted;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		tweetInserted = true;
		return tweetInserted;
	}

	
	
	public static boolean insertFollowing(User usr, int followed) {

		boolean followingInserted = false;

		String sql = "insert into following (follower,followed) values (?,?);";

		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement stmt = conn.prepareStatement(sql);) {
			
			stmt.setInt(1, usr.id);
			stmt.setInt(2, followed);

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				System.out.println("Following row failed to insert");
				return followingInserted;
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		followingInserted = true;
		return followingInserted;
	}

	public static int getUserIdByName(String usernm) {

		String sql = "select id from users where username = ?";
		int id = 0;
		try (Connection conn = DriverManager.getConnection(url);
				PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.setString(1, usernm);
			ResultSet rs = stmt.executeQuery();
			{
				while (rs.next()) {
					id = rs.getInt("id");
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return id;
	}
}
