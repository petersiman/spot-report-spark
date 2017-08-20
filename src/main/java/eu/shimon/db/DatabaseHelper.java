package eu.shimon.db;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import eu.shimon.model.Spot;
import eu.shimon.model.SpotReport;

/**
 * @author Seun Matt
 * Date 13 Oct 2016
 * Year 2016
 * (c) SMATT Corporation
 */
public class DatabaseHelper {

	/**
	 * Constructor
	 * cloud contacts
	 */
	private static Morphia morphia = new Morphia();
	private static Datastore datastore = null;

	private static Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);

	public DatabaseHelper() {
		if(!morphia.isMapped(Spot.class)) {
			morphia.map(Spot.class);
			morphia.map(SpotReport.class);
			initDatastore();
		} else {
			logger.info("Database Class Mapped Already!");
		}
	}



	void initDatastore() {

		ProcessBuilder processBuilder = new ProcessBuilder();
		MongoClient mongoClient;

		//this will fetch the MONGODB_URI environment variable on heroku
		//that holds the connection string to our database created by the heroku mLab add on
		String HEROKU_MLAB_URI = processBuilder.environment().get("MONGODB_URI");

		if (HEROKU_MLAB_URI != null && !HEROKU_MLAB_URI.isEmpty()) {
			//heroku environ
			logger.error("Remote MLAB Database Detected");
			mongoClient = new MongoClient(new MongoClientURI(HEROKU_MLAB_URI));
			datastore = morphia.createDatastore(mongoClient, processBuilder.environment().get("MONGODB_NAME"));
		} else {
			//local environ
			logger.info("Local Database Detected");
			mongoClient = new MongoClient(new MongoClientURI("mongodb://psiman:test@127.0.0.1:27017/admin"));
			datastore = morphia.createDatastore(mongoClient, "admin");
		}

		datastore.ensureIndexes();
		logger.info("Database connection successful and Datastore initiated");
	}


	public Datastore getDataStore() {
		if(datastore == null) {
			initDatastore();
		}

		return datastore;
	}



}