#include <ESP8266HTTPClient.h>
#include <ESP8266WiFi.h>

/* wifi network name */
char* ssidName = "2G_TNCAPA81F7F_EXT";
/* WPA2 PSK password */
char* pwd = "EB86E9FFFF";
/* service IP address */ 
char* address = "http://dcf65910.ngrok.io";

void setup() { 
  Serial.begin(115200);                                
  WiFi.begin(ssidName, pwd);
  Serial.print("Connecting...");
  while (WiFi.status() != WL_CONNECTED) {  
    delay(500);
    Serial.print(".");
  } 
}

int sendData(String address, float value, String data){  
   HTTPClient http;    
   http.begin(address + "/api/data");      
   http.addHeader("Content-Type", "application/json");     
   String msg = 
    String("{ \"value\": ") + String(value) + 
    ", \"data\": \"" + data +"\" }";
   int retCode = http.POST(msg);   
   http.end();  
      
   String payload = http.getString();  
   Serial.println(payload);      
   return retCode;
}
   
void loop() { 
  if (WiFi.status()== WL_CONNECTED){   

    /* read sensor */
    float value = map(analogRead(A0), 0, 1023, 0, 100);
    /* send data */
    Serial.print("sending "+String(value)+"...");    
    int code = sendData(address, value, "humidity");
  
    /* log result */
    if (code == 200){
      Serial.println("ok");   
    } else {
      Serial.println("error");
    }
  } else { 
    Serial.println("Error in WiFi connection");   
  }
 
  delay(2000);  
 
}
