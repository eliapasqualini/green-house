package server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.json.JsonObject;

public class ArduinoFSM extends BasicEventLoopController {

	private String host;
	private int port;
	private Vertx vertx;
	private HttpClient client;
	private String state = "Auto";
		
	public ArduinoFSM(String host) throws Exception {
		this.port = 80;
		this.host = host;
		this.vertx = Vertx.vertx();
		this.client = vertx.createHttpClient();
	}
		
	protected void processEvent(Event ev) {
		
		if(ev instanceof AutoEvent) {
			Manual.setManual(false);
		} 
		if(ev instanceof ManualEvent) {
			
			Manual.setManual(true);
		}
		if(ev instanceof OpenEvent) {
			JsonObject item = new JsonObject().put("time", getDate()).put("irrigation", "start").put("state", getStatus());
			sendData(item);
		}
		if(ev instanceof CloseEvent) {

			JsonObject item = new JsonObject().put("time", getDate()).put("irrigation", "finish").put("state", getStatus());
			sendData(item);
		}
		if(ev instanceof SegnalationEvent) {

			JsonObject item = new JsonObject().put("time", getDate()).put("irrigation", "time out").put("state", getStatus());
			sendData(item);
		}
			
	}
		
	private void sendData(JsonObject item) {
		client.post(port, host, "/api/data", response -> {
			response.bodyHandler(bodyHandler -> {
				System.out.println(bodyHandler.toString());
			});
		}).putHeader("content-type", "application/json").end(item.encodePrettily());
	}
	
	private String getStatus() {
		if(Manual.getManual()) {
			state = "Manual";
		} else {
			state = "Auto";
		}
		return state;
	}
	
	private String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date); 
	}
}
