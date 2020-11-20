#ifndef __SONAR__
#define __SONAR__

#include "Sonar.h"

class SonarImpl: public Sonar {

public:  
  SonarImpl(int echoPin, int trigPin);
  float getDistance();
  
private:
    const float vs = 331.5 + 0.6*20;
    int echoPin, trigPin;
};

#endif 
