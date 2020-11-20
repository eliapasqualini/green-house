package server;

public class MonitoringAgent extends Thread{
		
	static final String MSG_AUTO =  "A";
	static final String MSG_MANUAL = "M";
	static final String MSG_CLOSE = "C";
	static final String MSG_OPEN = "O";
	static final String MSG_SEGNALATION = "W";
	
	private SerialCommChannel channel;
	private ArduinoFSM task;
	private OpenEvent oEvent = new OpenEvent();
	private CloseEvent cEvent = new CloseEvent();
	private ManualEvent mEvent = new  ManualEvent();
	private AutoEvent aEvent = new AutoEvent();
	private SegnalationEvent sEvent = new SegnalationEvent();
	
	public MonitoringAgent(SerialCommChannel channel, ArduinoFSM task) throws Exception {
		this.channel = channel;
		this.task = task;
	}
	
	public void run(){
		while (true){
			try {
				if(channel.isMsgAvailable()) {
					String msg = channel.receiveMsg();
					if (msg.startsWith(MSG_AUTO)){
						
						task.notifyEvent(aEvent);
					} else if (msg.startsWith(MSG_MANUAL)) {
	
						task.notifyEvent(mEvent);
					} else if (msg.startsWith(MSG_CLOSE)) {
						
						task.notifyEvent(cEvent);
					} else if (msg.startsWith(MSG_OPEN)) {
						
						task.notifyEvent(oEvent);
					}  else if (msg.startsWith(MSG_SEGNALATION)) {
						task.notifyEvent(sEvent);
					}
				}
			} catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

}
