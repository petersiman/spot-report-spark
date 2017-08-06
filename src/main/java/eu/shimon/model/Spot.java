package eu.shimon.model;

import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class Spot {
	private String id;
	private String name;
	private String description;
	private Location location;
}
