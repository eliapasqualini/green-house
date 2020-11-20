#include "Arduino.h"
#ifndef __MSG__
#define __MSG__

class Msg {
  
public:
  Msg(const String& content);
  String getContent();

private:
  String content;  
};

#endif
