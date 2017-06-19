package twitter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class User {

	int id;
	String username;
	String password;
	String email;
	String hashedPassword;

	public User(String username, String password, String email) {
		int id;
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public User(int id, String username, String password, String email) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public User(String username) {
		this.username = username;
	}
	
	public void hashPassword() {
		final String SALT = "ELIZABETHDAVID";
		this.hashedPassword = this.password + SALT;
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException ex) {
			System.out.println(ex);
		}
		md.update(this.password.getBytes());
		String digest = new String(md.digest());
		this.hashedPassword=digest;
	}
  
}
