#include "Arduino.h"
#include "BtService.h"
#import "SoftwareSerial.h"


BtService::BtService(int rxPin, int txPin){
  channel = new SoftwareSerial(rxPin, txPin);
}

void BtService::init(){
  content.reserve(256);
  channel->begin(9600);
}

bool BtService::sendMsg(String msg){
  channel->println(msg);  
}

bool BtService::isBtAvailable(){
  return channel->available();
}

Msg* BtService::receiveMsg(){
  if (channel->available()){
    content="";
    while (channel->available()) {
      content += (char)channel->read();      
    }
    return new Msg(content);
  } else {
    return NULL;  
  }
}
