package twitter;

import static spark.Spark.get;
import static spark.Spark.port;

import java.util.ArrayList;

import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import com.google.gson.Gson;

public class Twitter {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		port(3001);
		ArrayList<Tweets> TweetList = new ArrayList<Tweets>();
		// root directory
		get("/", (req, res) -> {

			JtwigTemplate template = JtwigTemplate.classpathTemplate("twitter.html");
			JtwigModel model = JtwigModel.newModel().with("Tweets", TweetList);

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

//		// JSON return
//		get("/data", (req, res) -> {
//			Gson mygson = new Gson();
//			String json;
//			json = mygson.toJson(Tweets);
//			// System.out.println(json);
//			return json;
//		});
	}

}
