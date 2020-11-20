package server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;


/*
 * Data Service as a vertx event-loop 
 */
public class DataService extends AbstractVerticle {

	private int port;
	private DataServiceFSM task;
	private EspMessageEvent event = new EspMessageEvent();
	
	public DataService(int port, DataServiceFSM task) {
		this.port = port;
		this.task = task;
	}

	@Override
	public void start() {		
		Router router = Router.router(vertx);
		router.route().handler(BodyHandler.create());
		router.post("/api/data").handler(this::handleAddNewData);	
		vertx
			.createHttpServer()
			.requestHandler(router::accept)
			.listen(port);

		log("Service ready.");
	}
	
	private void handleAddNewData(RoutingContext routingContext) {
		HttpServerResponse response = routingContext.response();
		log("new msg "+routingContext.getBodyAsString());
		JsonObject res = routingContext.getBodyAsJson();
		if (res == null) {
			sendError(400, response);
		} else {
			if(res.containsKey("value")) {

				Humidity.setValue(res.getInteger("value"));
				Humidity.setPlace(res.getString("data"));
				
				log(Humidity.getValue()+"");
				task.notifyEvent(event);
		}
		response.setStatusCode(200).end();
			

		}
	}
	
	
	private void sendError(int statusCode, HttpServerResponse response) {
		response.setStatusCode(statusCode).end();
	}

	private void log(String msg) {
		System.out.println("[DATA SERVICE] "+msg);
	}

}