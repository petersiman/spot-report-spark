package eu.shimon.model;

import org.joda.time.DateTime;

import lombok.Builder;

@Builder
public class SpotStats {
	String spotId;
	double lowerSailRange;
	double upperSailRange;
	int lowerBoardRange;
	int upperBoardRange;
	double currentRating;
	DateTime lastReportDate;
}
