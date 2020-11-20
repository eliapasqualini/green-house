#include "Task.h"
#include "Led.h"
#include "User.h"
#include "Sonar.h"
#include "Timer.h"
#include "servo_motor.h"

class WaterTask: public Task {

public:

  WaterTask(User* cUser);
  void init(int period);
  void tick();

private:
  User* cUser;
  Led* l2;
  ServoMotor* pMotor;
  int arrivalTime;
  int pos;   
  int delta;
  int T;
  

  enum{STANDBY, OPEN, WATER, CLOSE} state;
};
