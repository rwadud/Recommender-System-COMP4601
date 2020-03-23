package edu.carleton.comp4601.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rs")
public class ContextualAdvertisingSystem {
	
	private static final String NAME = "tony white";
	
	@GET
	public String nameOfSystem() {
		return NAME;
	}
	
	
	@Path("context")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String contextProcessor() {
		return "Return a table as per requirement 7 of assignment 2";
	}
	
	@Path("community")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String communityProcessor() {
		return "Return a table as per requirement 8 of assignment 2";
	}
	
	@Path("fetch/{user}/{page}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String fetchUserPageProcessor(@PathParam("user") String user, @PathParam("page") String page) {
		return "Return a table as per requirement 9 of assignment 2";
	}
	
	@Path("advertising/{category}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String advertisingCategoryProcessor(@PathParam("category") String category) {
		return "Return a table as per requirement 11 of assignment 2";
	}
}
