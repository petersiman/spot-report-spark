package eu.shimon.model;

import org.joda.time.DateTime;

import lombok.Builder;

@Builder
public class SpotReport {
	private String spotId;
	private double sailSize;
	private int boardMass;
	private DateTime createdAt;
	private short stars;
}
