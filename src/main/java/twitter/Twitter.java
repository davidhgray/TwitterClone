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
		
		// root is 'src/main/resources', so put files in 'src/main/resources/public'
		staticFiles.location("/public"); // Static files
		
		port(3001);
		ArrayList<Tweets> TweetList = new ArrayList<Tweets>();
		
		connect();
		
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

	public static void connect(){
		Connection conn = null;
		try{
			String url = "jdbc:sqlite:fritter.db";
			conn = DriverManager.getConnection(url);
			System.out.println("Connection to SQLite has been established.");
		}
		catch (SQLException e){
			System.out.println(e.getMessage());
		} finally {
			try{
				if (conn != null){
					conn.close();
				}
			} catch (SQLException ex){
				System.out.println(ex.getMessage());
			}
		}

	}
}
