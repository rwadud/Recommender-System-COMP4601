package edu.carleton.comp4601.utility;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.carleton.comp4601.database.DatabaseManager;

public class ContextClassListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		DatabaseManager.getInstance();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
