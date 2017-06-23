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

import com.google.gson.Gson;

import spark.Session;


public class Twitter {

	public static void main(String[] args) {
		// root is 'src/main/resources', so put files in
		// 'src/main/resources/public'
		staticFiles.location("public/"); // Static files

		port(3001);

		// database and tables created if they don't already exist
		FritterDB.createTables();

		// root directory
		get("/", (req, res) -> {
			User user = req.session().attribute("user");
			if (user == null) {
				res.redirect("/logIn");
				return "";
			}

			ArrayList<Tweet> timeline;
			JtwigTemplate template = JtwigTemplate
					.classpathTemplate("public/twitter.html");
			JtwigModel model = JtwigModel.newModel();

			timeline = FritterDB.getTweets(user);
			model.with("username", user.username);
			model.with("timeline", timeline);

			return template.render(model);
		});

		get("/logIn", (req, res) -> {
			
			JtwigTemplate template = JtwigTemplate
					.classpathTemplate("public/login.html");
			JtwigModel model = JtwigModel.newModel();
			return template.render(model);
		});

		get("/api/timeline", (req, res) -> {
			User user = req.session().attribute("user");
			if (user == null) {
				res.status(400);
				return "";
			}
			ArrayList<Tweet> timeline = FritterDB.getTweets(user);
			Gson gson = new Gson();
			return gson.toJson(timeline);
		});
		
		//show user feeds
		get("/user/:username", (req, res) -> {
			String userName = req.params(":username");
			// get user's feed from db
			// render feed template
			ArrayList<Tweet> timeline ;
			JtwigTemplate template = JtwigTemplate
					.classpathTemplate("public/feed.html");
			JtwigModel model = JtwigModel.newModel();

			timeline = FritterDB.getFeed(userName);
			model.with("username", userName);
			model.with("feed", timeline);

			return template.render(model);
	
		});
		


		post("/api/newTweet", (req, res) -> {
			User user = req.session().attribute("user");
			if (user == null) {
				res.status(400);
				return "";
			}
			String content = req.queryParams("newTweetContent");
			boolean tweetInserted = FritterDB.insertTweet(user, content);
			if (!tweetInserted) {
				res.status(400);
				return "";
			}
			System.out.println(content);
			System.out.println("User content saved");
			return user.username + " " + content;
		});
		
		get("/retweet/:tweetId", (req, res) -> {
			User user = req.session().attribute("user");
			String tweetId = req.params(":tweetId");
			Integer tweetIdInt = Integer.parseInt(tweetId);
			if (user == null) {
				res.status(400);
				return "";
			}
			boolean retweetingUser = FritterDB.reTweet(user.username, tweetIdInt);
			if (!retweetingUser) {
				res.status(400);
				return "";
			}
			System.out.println("tweetId is " + tweetId + " and retweeting user is " + user.username);
			System.out.println("Retweet successful");
			res.redirect("/");
			return "";
		});
		
//		liked tweets
		get("/like/:tweetId/:tweetAuthorId", (req, res) -> {
			User user = req.session().attribute("user");
			String tweetId = req.params(":tweetId");
			Integer tweetIdInt = Integer.parseInt(tweetId);
			String tweetAuthorId = req.params(":tweetAuthorId");
			Integer tweetAuthor = Integer.parseInt(tweetAuthorId); //update this
			if (user == null) {
				res.status(400);
				return "";
			}
			boolean likedUser = FritterDB.insertLike(user, tweetIdInt, tweetAuthor);
			if (!likedUser) {
				res.status(400);
				return "";
			}
			System.out.println(user.username + " has liked tweetId " + tweetId + " from tweet author " + tweetAuthor);
			System.out.println("Like successful");
			res.redirect("/");
			return "";
		});


		post("/api/newUser", (req, res) -> {
			User usr = new User(req.queryParams("userName"),
					req.queryParams("password"), req.queryParams("email"));

			int status = FritterDB.insertUser(usr);
			if (status == -2) {
				return "Sorry, username " + usr.username + " already exists.";
			} else {
				return "Login created";
			}
		});

		// for authentication
		post("/returnUser", (req, res) -> {
			System.out.println(req.queryParams("returnUserName"));
			System.out.println(req.queryParams("returnUserPassword"));

			User usr = new User(req.queryParams("returnUserName"),
					req.queryParams("returnUserPassword"));

			if (!FritterDB.checkUser(usr)) {
				return "Invalid User, password combination";

			} else {
//				String sessionId = req.cookie("JSESSIONID");
//				res.cookie("JSESSIONID", sessionId, 3600);
				req.session().attribute("user", usr);
				return "logged in";
			}
		});
		
		get("/popular", (req, res) -> {
			System.out.print("/popular");
			User user = req.session().attribute("user");
			if (user == null) {
				res.status(400);
				return "";
			}
			ArrayList<User> userList ;
			JtwigTemplate template = JtwigTemplate
					.classpathTemplate("public/popular.html");
			JtwigModel model = JtwigModel.newModel();

			userList = FritterDB.getPopular(user);
			model.with("username", user.username);
			model.with("timeline", userList);

			return template.render(model);
			

		});
		
		post("/follow", (req, res) -> {
			System.out.print("/follow");
			User user = req.session().attribute("user");
			if (user == null) {
				res.status(400);
				return "";
			}
			String followedUser = req.queryParams("followedUser");
			System.out.println("this is followedUser in req " + followedUser);
			Integer followed = Integer.parseInt(followedUser);
			boolean followingInserted = FritterDB.insertFollowing(user, followed);
			if (!followingInserted) {
				res.status(400);
				return "";
			}
			System.out.println(followed);
			System.out.println("User " + user.username + " now following " + followed);
			return user.username + " " + followed;

		});
		
		get("/logOff", (req, res) -> {
			System.out.print("/logOff");
			Session session = req.session();
			session.invalidate();
			res.redirect("/");
			return "Logged off";

		});

		
	}

}
