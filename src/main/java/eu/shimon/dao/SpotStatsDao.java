package eu.shimon.dao;

import org.joda.time.DateTime;

import eu.shimon.model.SpotStats;

public class SpotStatsDao {

	public static SpotStats getSpotStats(String spotId) {
		//TODO agregation throughout the spot report here
		return SpotStats.builder()
				.spotId(spotId)
				.lowerBoardRange(92)
				.upperBoardRange(112)
				.lowerSailRange(5.2)
				.upperSailRange(5.8)
				.currentRating(3.2)
				.lastReportDate(DateTime.now().minusHours(2))
				.build();
	}
}
