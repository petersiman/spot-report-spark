package eu.shimon.dao;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Shape;

import eu.shimon.form.SpotForm;
import eu.shimon.model.Location;
import eu.shimon.model.Spot;

public class SpotDao {

	private Datastore datastore;

	public SpotDao(Datastore datastore) {
		this.datastore = datastore;
	}

	public List<Spot> findAllInBoudingBox(String neLat, String neLon, String swLat, String swLon) {
		return datastore.createQuery(Spot.class)
				.field("location")
				.within(Shape.box(
						new Shape.Point(Double.parseDouble(neLat), Double.parseDouble(neLon)),
						new Shape.Point(Double.parseDouble(swLat), Double.parseDouble(swLon))))
				.asList();
	}

	public Spot findOne(String id) {
		return datastore.createQuery(Spot.class)
				.field("_id")
				.equal(id)
				.get();
	}

	public Spot create(SpotForm spotForm) {
		Spot spot = new Spot();
		spot.setId(new ObjectId().toHexString());
		spot.setName(spotForm.getName());
		spot.setDescription(spotForm.getDescription());
		spot.setLocation(new Location(spotForm.getLat(), spotForm.getLon()));
		datastore.save(spot);
		return spot;
	}
}
