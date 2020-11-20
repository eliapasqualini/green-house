#include "Scheduler.h"
#include "ControlTask.h"
#include "WaterTask.h"
#include "Arduino.h"
#include "MsgService.h"


Scheduler sched;

void setup() {
  sched.init(100);
  Serial.begin(9600);
  MsgService.init();
  User* cUser = new User();
  ControlTask* cControlTask = new ControlTask(cUser);
  WaterTask* wWaterTask = new WaterTask(cUser);
  
  cControlTask->init(100);
  wWaterTask->init(100);
  sched.addTask(cControlTask);
  sched.addTask(wWaterTask);
 
}

void loop() {
  sched.run();

}
