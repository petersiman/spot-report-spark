package eu.shimon.dao;

import java.util.Collections;
import java.util.List;

import eu.shimon.model.Location;
import eu.shimon.model.Spot;

public class SpotDao {

	public static List<Spot> findAllInBoudingBox(String neLat, String neLon, String swLat, String swLon) {
		return Collections.singletonList(findOne("2"));
	}

	public static Spot findOne(String id) {
		Location location = Location.builder()
				.lat(48.8555998)
				.lon(16.0552128)
				.build();
		return Spot.builder()
				.id(id)
				.name("spot name 1")
				.description("spot description")
				.location(location)
				.build();
	}

}
