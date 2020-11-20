package server;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;

public class TestServerFsm {
	
	public static void main(String[] args) throws Exception {
		Vertx vertx = Vertx.vertx();
		Manual manual = new Manual(false);
		SerialCommChannel channel = new SerialCommChannel("COM3", 9600);
		DataServiceFSM dataFSM = new DataServiceFSM(channel);
		ArduinoFSM arduinoFSM = new ArduinoFSM("dcf65910.ngrok.io");
		DataService service = new DataService(80, dataFSM);
		vertx.deployVerticle((Verticle)service);
		dataFSM.start();
		arduinoFSM.start();
		new MonitoringAgent(channel, arduinoFSM).start();
	}

}
