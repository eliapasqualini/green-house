package server;

public class Humidity {
	
	private static int value;
	private static String place;
	
	public Humidity(int value, String place) {
		Humidity.value = value;
		Humidity.place = place;
	}
	
	public static void setValue(int value) {
		Humidity.value = value;
	}
	
	
	public static void setPlace(String place) {
		Humidity.place = place;
	}
	
	public static int getValue() {
		return Humidity.value;
	}
	
	
	public static String getPlace() {
		return place;
	}

}
