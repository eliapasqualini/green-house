#include "Msg.h"
#include "Arduino.h"

Msg::Msg(const String& content){
    this->content = content;
  }
  
String Msg::getContent(){
    return content;
};
