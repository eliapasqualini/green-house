package server;

public class DataServiceFSM extends BasicEventLoopController{

	
	private SerialCommChannel channel;
	
	public DataServiceFSM(SerialCommChannel channel) throws Exception {
		this.channel = channel;
	}
	
	@Override
	protected void processEvent(Event ev) {
		
		//unico stato che invia ad arduino quando il dataservice riceve un cambiamento di umidità
		if(ev instanceof EspMessageEvent) {
			if(Manual.getManual()) {

				channel.sendMsg(Humidity.getValue()+":");
			} else {
				if(Humidity.getValue() < 30) {	
					if(Humidity.getValue() < 10) {
						//intensità massima
						channel.sendMsg("3");
					} else if(Humidity.getValue() <= 20 && Humidity.getValue() >= 10) {
						//intensità media
						channel.sendMsg("2");
					} else {
						//intensità minima
						channel.sendMsg("1");
					}
				} else if(Humidity.getValue() > 35){
					channel.sendMsg("0");
				}
			}
		}
	}

}
