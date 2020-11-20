#ifndef __BTSERVICE__
#define __BTSERVICE__

#include "SoftwareSerial.h"
#include "Arduino.h"
#include "User.h"
#include "Msg.h"


class BtService {
    
public: 
  BtService(int rxPin, int txPin);  
  void init();  
  bool isBtAvailable();
  Msg* receiveMsg();
  bool sendMsg(String msg);

private:
  String content;
  SoftwareSerial* channel;
  
};

#endif
