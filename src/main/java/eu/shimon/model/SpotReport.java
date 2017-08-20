package eu.shimon.model;

import org.joda.time.DateTime;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity("spotReport")
public class SpotReport {
	@Id
	private String id;
	private String spotId;
	private double sailSize;
	private int boardMass;
	private DateTime createdAt;
	private short stars;
}