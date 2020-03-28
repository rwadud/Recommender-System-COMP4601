package edu.carleton.comp4601.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import edu.carleton.comp4601.database.DatabaseManager;
import edu.carleton.comp4601.model.Review;
import edu.carleton.comp4601.model.User;

//Main root path as per requirements of assignment
@Path("/rs")
public class ContextualAdvertisingSystem {

	private static final String PROJNAME = "Contextual Advertising System (CAS)";
	private static final String PARTNERS = "Alexander Nguyen & Redwan Wadud";
	private static boolean contextHit = false;

	// Display name as test that endpoints are working
	@GET
	public String nameOfSystem() {
		return PROJNAME;
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHtml() {
		String title = "<title>" + "COMP4601 - CAS" + "</title>";

		String projName = "<h1>" + PROJNAME + "</h1>";
		String partner = "<h2>" + PARTNERS + "</h2>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String bodyClose = "</h2></body>";
		String style = "<head><style>\r\n" + ".container { \r\n" + "  height: 100vh;\r\n" + "  position: relative;\r\n"
				+ "  text-align: center" + "}\r\n" + "\r\n" + ".center {\r\n" + "  margin: 0;\r\n"
				+ "  position: absolute;\r\n" + "  top: 50%;\r\n" + "  left: 50%;\r\n"
				+ "  -ms-transform: translate(-50%, -50%);\r\n" + "  transform: translate(-50%, -50%);\r\n"
				+ "}</style>\r\n" + "</head>";
		String divOpen = "<div class=\"container\">\r\n" + "  <div class=\"center\">";
		String divClose = "  </div>\r\n" + "</div>";

		return "<html> " + title + style + bodyOpen + divOpen + projName + partner + divClose + bodyClose + "</html> ";
	}

	@Path("context")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String contextProcessor() throws Exception {

		String title = "<head><title>" + "CAS - Context" + "</title>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String style = "<head><style>\r\n" + "table {\r\n" + "  border-collapse: collapse;\r\n"
				+ "border: 1px solid #ddd;\r\n" + "  width: 100%;\r\n" + "background-color: ivory;" + "}\r\n" + "\r\n"
				+ "th {\r\n" + "  padding: 8px;\r\n" + "color: white;\r\n" + "  text-align: center;\r\n"
				+ "  border: 1px solid black;\r\n" + "  background-color: #525d76;\r\n" + "}\r\n" + "\r\n" + "td {\r\n"
				+ "  padding: 8px;\r\n" + "  text-align: left;\r\n" + "  border: 1px solid #ddd;\r\n" + "}\r\n" + "\r\n"
				+ "tr:hover {background-color:#f5f5f5;}\r\n" + "</style></head>";
		String html = "<h1 style=\"padding: 15px;\" align=\"center\">Context</h1><table style= \"width:100%\"> <tr> <th>UserID</th> <th>Preferred Genre</th> <th>Movies Reviewed + User Score</th> <th>Community</th> </tr>";
		String ending = " </td>  </tr> </table></body></html>";

		try {
			DatabaseManager.getInstance();
			System.out.println("User collection loaded from database!");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(
					"Archive utility sequence incorrect - ensure archive is loaded & context is run first. Refer to readme.txt & try again.");
		}

		List<User> users = DatabaseManager.getInstance().getUsers();

		for (int i = 0; i < users.size(); i++) {
			List<Review> reviews = DatabaseManager.getInstance().getUserReviews(users.get(i).getUserId());

			html = html + "<tr><td>" + users.get(i).getUserId() + "</td><td>" + "users.get(i).getPreferredGenre()"
					+ "</td><td>";

			for (Review review : reviews)
				html = html + review.getPageId() + "(" + review.getScore() + ")" + ", ";

			html = html + "</td> <td> ";

			// if(users.get(i).getPreferredGenre().toString().equals("Action")) {
			html = html + "Action & Classics";
			// }
			// else if(users.get(i).getPreferredGenre().toString().equals("Comedy")) {
			// html = html + "Laugh Lovers";
			// }
			// else if(users.get(i).getPreferredGenre().toString().equals("Horror")) {
			// html = html + "Fear Fanatics";
			// }

		}

		// Put it all together
		html = title + style + bodyOpen + html + ending;

		contextHit = true;
		return html;
	}

	@Path("community")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String communityProcessor() {

		String title = "<head><title>" + "CAS - Community" + "</title>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String style = "<head><style>\r\n" + "table {\r\n" + "  border-collapse: collapse;\r\n"
				+ "border: 1px #ddd;\r\n" + "  width: 100%;\r\n" + "background-color: ivory;" + "}\r\n" + "\r\n"
				+ "th {\r\n" + "  padding: 8px;\r\n" + "color: white;\r\n" + "  text-align: center;\r\n"
				+ "  border: 1px solid black;\r\n" + "  background-color: #525d76;\r\n" + "}\r\n" + "\r\n" + "td {\r\n"
				+ "  padding: 8px;\r\n" + "  text-align: left;\r\n" + "  border: 1px solid #ddd;\r\n" + "}\r\n" + "\r\n"
				+ "tr:hover {background-color:#f5f5f5;}\r\n" + "</style></head>";

		// Ensure context is run first as per assignment requirement
		String html = "";
		if (contextHit == false) {
			html = "Archive utility sequence incorrect - ensure archive is loaded & context is run first. Refer to readme.txt & try again.";
		} else {
			// HTML output and table config

			html = "<h1 style=\"padding: 15px;\" align=\"center\">Community</h1><table style= \"width:100%\"> <tr> <th>Community</th> <th>Community Members</th></tr>";
			String ending = " </td>  </tr> </table></body></html>";

			List<User> users = DatabaseManager.getInstance().getUsers();

			// Now add each user profile to the table via the html string
			String actionCommunity = "";
			String comedyCommunity = "";
			String horrorCommunity = "";

			/**
			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getPreferredGenre().toString().equals("Action")) {
					actionCommunity = actionCommunity + users.get(i).getUserId().toString() + ", ";
				} else if (users.get(i).getPreferredGenre().toString().equals("Comedy")) {
					comedyCommunity = comedyCommunity + users.get(i).getUserId().toString() + ", ";
				} else if (users.get(i).getPreferredGenre().toString().equals("Horror")) {
					horrorCommunity = horrorCommunity + users.get(i).getUserId().toString() + ", ";
				}
			}
			**/

			html = html + "<tr><td>" + "Karate Kids" + "</td><td>" + "actionCommunity" + "</td></tr>";
			html = html + "<tr><td>" + "Funny ones.." + "</td><td>" + "comedyCommunity" + "</td></tr>";
			html = html + "<tr><td>" + "FearFactor" + "</td><td>" + "horrorCommunity" + "</td></tr>";

			html = title + style + bodyOpen + html + ending;

		}
		return html;
	}

	@Path("fetch/{user}/{page}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String fetchUserPageProcessor(@PathParam("user") String user, @PathParam("page") String page) {
		
		String title = "<head><title>" + "CAS - Fetch" + "</title>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String style = "<head><style>\r\n" + "table {\r\n" + "  border-collapse: collapse;\r\n"
				+ "border: 1px #ddd;\r\n" + "  width: 100%;\r\n" + "background-color: ivory;" + "}\r\n" + "\r\n"
				+ "th {\r\n" + "  padding: 8px;\r\n" + "color: white;\r\n" + "  text-align: center;\r\n"
				+ "  border: 1px solid black;\r\n" + "  background-color: #525d76;\r\n" + "}\r\n" + "\r\n" + "td {\r\n"
				+ "  padding: 8px;\r\n" + "  text-align: left;\r\n" + "  border: 1px solid #ddd;\r\n" + "}\r\n" + "\r\n"
				+ "tr:hover {background-color:#f5f5f5;}\r\n" + "</style></head>";

		// Ensure context is run first as per assignment requirement
		String html = "";
		if (contextHit == false) {
			html = "Archive utility sequence incorrect - ensure archive is loaded & context is run first. Refer to readme.txt & try again.";
		} else {
			
			html = "<h1 style=\"padding: 15px;\" align=\"center\">Fetch</h1><table style= \"width:100%\"><tr> <th> User: " + user + " - Page: " + page + "</th></tr>";
			String ending = " </td>  </tr> </table></body></html>";
			
			/**
									 * //Now add each user profile to the table via the html string
									 * ArrayList<UserProfile> users = LoadUserService.getInstance().getUsers();
									 * String prefGen = ""; for(int i = 0; i < users.size(); i++){
									 * 
									 * if(users.get(i).getUserId().toString().equals(user)) { prefGen =
									 * users.get(i).getPreferredGenre().toString(); Random rand = new Random(); int
									 * ad = rand.nextInt(3)+1;
									 * 
									 * if(prefGen.equals("Action")) { html = html + "
									 * <tr>
									 * <td>"+ "<img
									 * src=\"https://raw.githubusercontent.com/TedKachulis/RecommenderSystemImgPlaceholders/master/adverts/actionAd"
									 * + ad + ".jpg\" alt=\"Action 1 Ad\">" + "</td>
									 * </tr>
									 * ";
									 * 
									 * if(ad==1) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "6303212263" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; } else if(ad==2) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "0784010331" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; } else if(ad==3) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "0783226128" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; }
									 * 
									 * } else if(prefGen.equals("Horror")) { html = html + "
									 * <tr>
									 * <td>"+ "<img
									 * src=\"https://raw.githubusercontent.com/TedKachulis/RecommenderSystemImgPlaceholders/master/adverts/horrorAd"
									 * + ad + ".jpg\" alt=\"Horror 1 Ad\">" + "</td>
									 * </tr>
									 * ";
									 * 
									 * if(ad==1) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "6304240554" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; } else if(ad==2) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "B00004CJ2O" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; } else if(ad==3) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "B003EYVXUU" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; } } else if(prefGen.equals("Comedy")) { html = html + "
									 * <tr>
									 * <td>"+ "<img
									 * src=\"https://raw.githubusercontent.com/TedKachulis/RecommenderSystemImgPlaceholders/master/adverts/comedyAd"
									 * + ad + ".jpg\" alt=\"Comedy 1 Ad\">" + "</td>
									 * </tr>
									 * ";
									 * 
									 * if(ad==1) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "B001KEHAI0" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; } else if(ad==2) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "B00004RM0J" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; } else if(ad==3) { html = html + "
									 * <tr>
									 * <td>"+ " <a
									 * href=\"https://sikaman.dyndns.org:8443/WebSite/rest/site/courses/4601/assignments/training/pages/"
									 * + "B00004CX8I" + ".html\"> Click to check out the reviews (for the
									 * recommended movie)! </a>" + "</td>
									 * </tr>
									 * "; } } } }
									 * 
									 * ArrayList<MoviePage> movies = LoadMovieService.getInstance().getMovies();
									 * String genre = "", score = "", reviewString = ""; //Sort for movie we want
									 * for(int i = 0; i < movies.size(); i++){
									 * 
									 * //Find it if(movies.get(i).getMovieId().toString().equals(page)) {
									 * 
									 * //Get data about movie genre = movies.get(i).getGenre().toString(); score =
									 * movies.get(i).getScore().toString(); reviewString =
									 * movies.get(i).getReviews();
									 * 
									 * } }
									 **/
			html = html + "<tr><td>" + " Movie: " + "page" + " <br /> Average Rating: " + "score" + " <br /> Genre: "
					+ "genre" + " <br /> Reviews: " + "reviewString";
			
			html = title + style + bodyOpen + html + ending;
		}
		return html;
	}

	// Display advertisements for given category of user communities, refer to
	// /readme endpoint for help displaying desired content
	@Path("advertising/{category}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String advertisingCategoryProcessor(@PathParam("category") String category) {

		String title = "<head><title>" + "CAS - Advertising" + "</title>";
		String bodyOpen = "<body style=\"background-color:lightyellow;\">";
		String style = "<head><style>\r\n" + "table {\r\n" + "  border-collapse: collapse;\r\n"
				+ "border: 1px #ddd;\r\n" + "  width: 100%;\r\n" + "background-color: ivory;" + "}\r\n" + "\r\n"
				+ "th {\r\n" + "  padding: 8px;\r\n" + "color: white;\r\n" + "  text-align: center;\r\n"
				+ "  border: 1px solid black;\r\n" + "  background-color: #525d76;\r\n" + "}\r\n" + "\r\n" + "td {\r\n"
				+ "  padding: 8px;\r\n" + "  text-align: left;\r\n" + "  border: 1px solid #ddd;\r\n" + "}\r\n" + "\r\n"
				+ "tr:hover {background-color:#f5f5f5;}\r\n" + "</style></head>";
		
		// HTML output and table config
		String html = "<h1 style=\"padding: 15px;\" align=\"center\">Advertising</h1><table style= \"width:100%\"><tr> <th> Category </th> <th>Advertisement Samples</th> </tr>";
		String ending = "</table></body></html>";
		
		if (category.equals("actionclassics")) {
			html = html + "<tr><td>" + " Action & Classics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/actionAd1.png\" alt=\"Action 1 Ad\">"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Action & Classics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/actionAd2.png\" alt=\"Action 2 Ad\">"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Action & Classics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/actionAd3.png\" alt=\"Action 3 Ad\">"
					+ "</td></tr>";
		} else if (category.equals("fearfanatics")) {
			html = html + "<tr><td>" + " Fear Fanatics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/horrorAd1.png\" alt=\"Horror 1 Ad\">"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Fear Fanatics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/horrorAd2.png\" alt=\"Horror 2 Ad\">"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Fear Fanatics" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/horrorAd3.png\" alt=\"Horror 3 Ad\">"
					+ "</td></tr>";
		} else if (category.equals("laughlovers")) {
			html = html + "<tr><td>" + " Laugh Lovers" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/comedyAd1.png\" alt=\"Comedy 1 Ad\">"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Laugh Lovers" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/comedyAd2.png\" alt=\"Comedy 2 Ad\">"
					+ "</td></tr>";
			html = html + "<tr><td>" + " Laugh Lovers" + "</td><td>"
					+ "<img src=\"https://raw.githubusercontent.com/alex090nguyen/RecommenderSystemImgPlaceholders/master/adverts/comedyAd2.png\" alt=\"Comedy 3 Ad\">"
					+ "</td></tr>";
		}

		html = title + style + bodyOpen + html + ending;
		
		return html;
	}

}
