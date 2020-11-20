#include "User.h"
#include "BtService.h"
#include "MsgService.h"
BtService btService(2,3);

User::User(){

  btService.init();
  manual = false;
  apri = false;
  intensity = 0;
  connession = false;
  message = "";
  tmp = "";
  last = "";
  val = "";
  humidity = "";
  cmd = "";
}

bool User::checkManual(){
  return manual;
}

void User::changeManual(){
  manual = !manual;
}

void User::setApri(){
  if(apri){
    apri = false;  
  } else{
    apri = true;
  }
}

void User::setIntensity(int value){
  intensity = value;  
}

void User::setConnected(bool value){
  connession = value;  
}

int User::getIntensity(){
  return intensity;  
}

bool User::isAperto(){
  return apri;  
}

bool User::isConnected(){
  return connession;  
}

bool User::sendMsg(String msg){
  btService.sendMsg(msg);
}

void User::receiveMsg(){
  tmp = btService.receiveMsg()->getContent();
  
  last = tmp.substring(tmp.length()-1);
  if (last.equals(":")){
    val = tmp.substring(tmp.length()-3,tmp.length()-1).toInt() -10;
    int prop = val*255/99;
    if(prop <= 85){
      setIntensity(85);
    } else if(prop > 85 && prop < 170){
        setIntensity(170);
    } else {
          setIntensity(255);
    }
    
  }
  if(!tmp.equals("")){
      message = tmp;
   }
   last = "";
   tmp = "";
   val = 0;
}

bool User::isBtAvailable(){
  return btService.isBtAvailable();
}


String User::getMessage(){
  return message;
}

void User::resetMessage(){
  message = "";
}

int User::freeRam () {
  extern int __heap_start, *__brkval; 
  int v; 
  return (int) &v - (__brkval == 0 ? (int) &__heap_start : (int) __brkval); 
}

void User::receiveSerialMessage(){
  String temp = MsgService.receiveMsg()->getContent();
  String str = temp.substring(temp.length()-1,temp.length());

  if (str.equals(":")){
    humidity = temp.substring(0,temp.length()-1);
  } else{
    temp = temp.substring(temp.length()-2,temp.length()-1);
    if(temp.equals("1")){
        setIntensity(85);
        setCmd("apri");
    } else if(temp.equals("2")){
        setIntensity(170);
        setCmd("apri");
    } else if(temp.equals("3")){
        setIntensity(255);
        setCmd("apri");
    } else if(temp.equals("0")){
        setCmd("chiudi");
    }
  }
  str = "";
  temp = "";
   /*Serial.println("Comando:"+cmd);
   Serial.println("umidità:"+humidity);
   Serial.println(freeRam());*/
}

String User::getCmd(){
  return cmd;  
}

 void User::setCmd(String command){
  cmd = command;  
}

String User::getHumidity(){
  return humidity;  
};
