#include "Arduino.h"
#ifndef USER
#define USER

class User{

public:

  User();

  bool checkManual();
  void changeManual();
  int getIntensity();
  void setIntensity(int value);
  void setConnected(bool value);
  void setApri();
  bool isAperto();
  bool isConnected();
  void receiveMsg();
  bool sendMsg(String msg);
  bool isBtAvailable();
  String getMessage();
  void resetMessage();
  int freeRam();
  String getHumidity();
  String getCmd();
  void receiveSerialMessage();
  void setCmd(String command);
  


  
private:
  int intensity;
  bool apri;
  bool manual;
  bool connession;
  String message;
  String tmp;
  int val;
  String last;
  String humidity;
  String cmd;
};

#endif
