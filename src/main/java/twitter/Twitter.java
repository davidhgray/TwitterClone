package twitter;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.HashMap;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import spark.Session;

//import com.google.gson.Gson;

public class Twitter {

	static HashMap<String, User> userSessions = new HashMap<String, User>();

	public static void main(String[] args) {
		// root is 'src/main/resources', so put files in
		// 'src/main/resources/public'
		staticFiles.location("public/"); // Static files

		port(3001);

		// database and tables created if they don't already exist
		FritterDB.createTables();

		// root directory
		get("/", (req, res) -> {
			Session session = req.session();
			String usrName = session.attribute("userName");
			User usr = userSessions.get(usrName);
//			if (usr.username != null) {
//				System.out.println(usr.username);
//			}
//			if (usr.username == null){
//				System.out.println("User " + usrName + " is not logged in.");
//				res.redirect("/");
//				return "Please login or create account.";
//			}
//			else {
//				res.redirect("/loggedInUser");
//			}
			ArrayList<Tweet> timeline = FritterDB.getTweets();
			JtwigTemplate template = JtwigTemplate
					.classpathTemplate("public/twitter.html");
			JtwigModel model = JtwigModel.newModel().with("timeline", timeline);
			return template.render(model);

		});

		post("/newUser", (req, res) -> {
			User usr = new User(req.queryParams("userName"),
					req.queryParams("password"), req.queryParams("email"));

			int status = FritterDB.insertUser(usr);
			if (status == -2) {
				return "Sorry, username " + usr.username + " already exists.";
			} else {
				return "We created your login " + usr.username
						+ ".  Please login.";
			}
		});

		post("/returnUser", (req, res) -> {
			// helper code
			System.out.println(req.queryParams("returnUserName"));
			System.out.println(req.queryParams("returnUserPassword"));
			// real code starts here

			User usr = new User(req.queryParams("returnUserName"),
					req.queryParams("returnUserPassword"));

			boolean status = FritterDB.checkUser(usr);
			if (!status) {
				return "Invalid User, password combination";
			} else {

				User user = userSessions.get(usr.username);
				if (user == null) {
					userSessions.put(usr.username, usr);
				}

				Session session = req.session();
				session.attribute("userName", usr.username);

				res.redirect("/loggedInUser");
				return "Welcome back " + usr.username;
			}

		});

		get("/loggedInUser", (req, res) -> {
			// helper code
			System.out.print("/loggedInUser");
			Session session = req.session();
			String usrName = session.attribute("userName");
			User usr = userSessions.get(usrName);
			boolean status = FritterDB.checkUser(usr);
			if (!status) {
				System.out.println("User " + usrName + " is not logged in.");
				res.redirect("/");
				return "Please login.";
			} else {
				ArrayList<Tweet> timeline = FritterDB.getTweets();
				JtwigTemplate template = JtwigTemplate
						.classpathTemplate("public/loggedInUser.html");
				JtwigModel model = JtwigModel.newModel().with("timeline",
						timeline);
				return template.render(model);

			}

		});
		post("/logOff", (req, res) -> {
			// helper code
			System.out.print("/logOff");
			Session session = req.session();
			session.invalidate();
			res.redirect("/");
			return "Logged off";

		});
		
		post("/tweet", (req, res) -> {
			// helper code
			System.out.print("/tweet");
			Session session = req.session();
			session.invalidate();
			res.redirect("/");
			return "Logged off";

		});


	}

}
