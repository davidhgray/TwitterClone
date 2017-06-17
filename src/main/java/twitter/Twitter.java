package twitter;

import static spark.Spark.*;

import java.util.ArrayList;
import java.io.InputStream;
import java.security.DigestException;
import java.security.MessageDigest;

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
	
	public final static String url = "jdbc:sqlite:fritter.db";

	public static void main(String[] args) {
		// root is 'src/main/resources', so put files in
		// 'src/main/resources/public'
		staticFiles.location("public/"); // Static files
		
		port(3001);
		
		// database and tables created if they don't already exist
		FritterDB.createTables();

		// root directory
		get("/", (req, res) -> {

			Timeline timeline = Timeline.getTimeline("david");
			JtwigTemplate template = JtwigTemplate.classpathTemplate("public/twitter.html");
			JtwigModel model = JtwigModel.newModel().with("timeline", timeline.tweets);

			return template.render(model);

		});

		post("/newUser", (req, res) -> {
			User usr= new User(req.queryParams("userName"),
					req.queryParams("password"),
					req.queryParams("email"));
			
			FritterDB.insertUser(usr);
			
			return "We created your login " + usr.username; //TODO what do we want to do after we create the account? Make them login?
		});
		
		post("/returnUser", (req, res) -> {
			// helper code
			System.out.print("/returnUser ");
			System.out.println(req.queryParams("returnUserName"));
			System.out.println(req.queryParams("returnUserPassword"));
			// real code starts here
			
			User usr= new User(req.queryParams("returnUserName"),
					req.queryParams("returnUserPassword"));
						
			boolean status=FritterDB.checkUser(usr);
			if(!status){
			 return "Invalid User, password combination";
			} else {
				return  "Welcome " + usr.username;
			}
			
			// TODO what do we want to do after they login?

		});
	}


	
	


}
