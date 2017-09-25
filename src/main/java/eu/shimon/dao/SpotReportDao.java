package eu.shimon.dao;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;

import eu.shimon.model.SpotReport;

public class SpotReportDao {

	private Datastore datastore;

	public SpotReportDao(Datastore datastore) {
		this.datastore = datastore;
	}

	public SpotReport create(SpotReport spotReport) {
		Key<SpotReport> key = datastore.save(spotReport);
		spotReport.setId((String) key.getId());
		return spotReport;
	}
}
