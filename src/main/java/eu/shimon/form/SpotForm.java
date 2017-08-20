package eu.shimon.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class SpotForm {
	private String name;
	private String description;
	private double lat;
	private double lon;

}
