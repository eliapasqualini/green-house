#include "Task.h"
#include "Sonar.h"
#include "Led.h"
#include "User.h"
#include "BtService.h"

class ControlTask: public Task {
public:
  ControlTask(User* cUser);
  void init(int period);
  void tick();

private:
  User* cUser;
  Sonar* sonar;
  Led* l1;
  Led* lm;

  //variabili
  int manual;

  enum{IDLE, MANUAL} state;
};
