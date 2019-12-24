package common;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import business.loginLogic.User;

public class DBLogger {
	private final static Logger LOGGER = Logger.getLogger("common");
	static Handler fileHandler;
	SimpleFormatter simpleFormatter = new SimpleFormatter();

	public static void iniciarSesion() {
		String date = String.valueOf((new Date()).getTime());
		String type = User.getInstance("", "").getType();
		String user = User.getInstance("", "").getUsername();
		try {
			fileHandler = new FileHandler("logs/" + date + "-" + type + "-" + user + ".log", false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		SimpleFormatter simpleFormatter = new SimpleFormatter();
		fileHandler.setFormatter(simpleFormatter);
		LOGGER.addHandler(fileHandler);
		fileHandler.setLevel(Level.ALL);

		LOGGER.log(Level.INFO,
				User.getInstance("", "").toStringEs() + " inicio sesion a fecha - " + (new Date()).toString());
	}

	public static Logger getLogger() {
		return LOGGER;
	}
}
