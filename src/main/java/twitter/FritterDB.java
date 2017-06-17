package twitter;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class FritterDB {

	public static final String url = "jdbc:sqlite:fritter.db";

	public static final String createUsersTblSQL = "CREATE TABLE IF NOT EXISTS users (\n"
			+ "	id integer PRIMARY KEY,\n" + "	username text NOT NULL,\n"
			+ "	email text ,\n" + "	password text \n" + ");";

	public static final String createTweetsTblSQL = "CREATE TABLE IF NOT EXISTS tweets (\n"
			+ "	id integer PRIMARY KEY,\n" + " content text,\n"
			+ "	dt text DEFAULT CURRENT_TIMESTAMP);";

	public static final String userTweetsTblSQL = "CREATE TABLE IF NOT EXISTS userTweets (\n"
			+ "	tweetId integer PRIMARY KEY,\n" + " userId integer,\n"
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
		int status=0;
		usr.hashPassword();
		
		try {
			conn = DriverManager.getConnection(url);
			String sql = "insert into Users (username,password, email) values(?,?,?)";
			PreparedStatement stmt = null;
			stmt = conn.prepareStatement(sql);

			stmt.setString(1, usr.username);
			stmt.setString(2, usr.hashedPassword);
			stmt.setString(3, usr.email);

			status = stmt.executeUpdate();
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
		
		try {conn = DriverManager.getConnection(url);
	
			String sql = "select id, email, username, password from Users where userName = ? and password =?";
			PreparedStatement stmt = null;
			stmt = conn.prepareStatement(sql);
	
			stmt.setString(1, usr.username);
			stmt.setString(2, usr.hashedPassword);
	
			ResultSet rs = stmt.executeQuery();
			boolean rowFound=false;
			
			while (rs.next()){
				    usr.id = rs.getInt("id");
				    usr.email=rs.getString("email");
					System.out.println(rs.getString("password"));	
					rowFound=true;
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

}
