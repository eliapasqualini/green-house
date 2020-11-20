package server;

public class Manual {

	private static boolean manual;
	
	public Manual (boolean state) {
		Manual.manual = state;
	}
	
	public static void setManual(boolean state) {
		Manual.manual = state;
	}
	
	public static boolean getManual() {
		return Manual.manual;
	}
	
}
