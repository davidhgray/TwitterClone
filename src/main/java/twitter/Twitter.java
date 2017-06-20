//requirements:
//	user accounts, login, sessions
//	a user can add tweets to their feed
//	a user can subscribe to the feeds of other users
//	a user can view feeds
//	a user can view their timeline (a chronological aggregate of the feeds they follow)
//OPTIONAL?
//	a user can 'like' the tweets of other users
//	a user can 'retweet': post tweets of other users into their own feed

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
			ArrayList<Tweet> timeline;
			if (usr == null) {
				timeline = FritterDB.getTweets();
			} else {
				timeline = FritterDB.getTweets(usr);
			}
			JtwigTemplate template = JtwigTemplate.classpathTemplate("public/twitter.html");
			JtwigModel model = JtwigModel.newModel().with("timeline", timeline);
			return template.render(model);
		});

		post("/newTweet", (req, res) -> {
			System.out.println("/newTweet");
			String usrnm = req.queryParams("returnUserName");

			Session session = req.session();

			String usrName = session.attribute("userName");
			User usr = userSessions.get(usrName);

			// long userId = req.session().attribute("userid");

			String content = req.queryParams("newTweetContent");
			boolean tweetInserted = FritterDB.insertTweet(usr, content);

			System.out.println(content);
			System.out.println("User content saved");
			return usrnm + content;
		});

		post("/newUser", (req, res) -> {
			User usr = new User(req.queryParams("userName"), req.queryParams("password"), req.queryParams("email"));

			int status = FritterDB.insertUser(usr);
			if (status == -2) {
				return "Sorry, username " + usr.username + " already exists.";
			} else {
				return "We created your login " + usr.username + ".  Please login.";
			}
		});
		// for authentication
		post("/returnUser", (req, res) -> {
			// helper code
			System.out.println(req.queryParams("returnUserName"));
			System.out.println(req.queryParams("returnUserPassword"));
			// real code starts here

			User usr = new User(req.queryParams("returnUserName"), req.queryParams("returnUserPassword"));

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
				ArrayList<Tweet> timeline = FritterDB.getTweets(usr);
				JtwigTemplate template = JtwigTemplate.classpathTemplate("public/twitter.html");
				JtwigModel model = JtwigModel.newModel().with("timeline", timeline);
				return template.render(model);
//				return "Welcome back " + usr.username;
			}

		});

		// for loading the logged-in user page, but not currently getting
		// invoked
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
				JtwigTemplate template = JtwigTemplate.classpathTemplate("public/loggedInUser.html");
				JtwigModel model = JtwigModel.newModel().with("timeline", timeline);
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

	}

}
