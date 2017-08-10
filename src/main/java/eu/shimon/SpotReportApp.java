package eu.shimon;

import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

import java.util.HashMap;
import java.util.Map;

import eu.shimon.dao.SpotDao;
import eu.shimon.dao.SpotStatsDao;
import eu.shimon.model.Spot;
import eu.shimon.util.JsonUtil;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.velocity.VelocityTemplateEngine;

public class SpotReportApp {
	public static void main(String[] args) {
		exception(Exception.class, (e, req, res) -> e.printStackTrace()); // print all exceptions
		staticFiles.location("/public");

		port(getHerokuAssignedPort());

		get("/", (req, res) -> renderMap(req));
		get("/hello", (req, res) -> "Hello World");
		get("/spots", (req, res) -> SpotDao.findAllInBoudingBox(req.params("neLat"), req.params("neLon"), req.params("swLat"), req.params("swLon")), JsonUtil.json());
		get("/spots/:id", (req, res) -> SpotDao.findOne(req.queryParams("id")), JsonUtil.json());
		get("/spot-stats/:spotId", (req, res) -> SpotStatsDao.getSpotStats(req.queryParams("spotId")), JsonUtil.json());

		post("/spots", ((request, response) -> SpotDao.create(Spot.builder().name(request.queryParams("name")).build())));
	}

	private static String renderMap(Request req) {
		Map<String, Object> model = new HashMap<>();
		return renderTemplate("velocity/index.vm", model);
	}

	private static String renderTemplate(String template, Map model) {
		return new VelocityTemplateEngine().render(new ModelAndView(model, template));
	}

	static int getHerokuAssignedPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
	}

	@FunctionalInterface
	private interface ICRoute extends Route {
		default Object handle(Request request, Response response) throws Exception {
			handle(request);
			return "";
		}
		void handle(Request request) throws Exception;
	}

}
