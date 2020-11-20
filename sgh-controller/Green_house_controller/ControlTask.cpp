#include "ControlTask.h"
#include "SonarImpl.h"
#include "Led.h"
#include "Arduino.h"
#include "MsgService.h"

#define ECHO_PIN 8
#define TRIG_PIN 7
#define DIST1 0.3
#define LED1_PIN 13
#define LEDM_PIN 12

ControlTask::ControlTask(User* cUser){
  this->cUser = cUser;
  sonar = new SonarImpl(ECHO_PIN,TRIG_PIN);
  l1 = new Led(LED1_PIN);
  lm = new Led(LEDM_PIN);
    
}


void ControlTask::init(int period){
  Task::init(period);
  state = IDLE;
  //Serial.println("INIZIO CONTROL TASK");
  lm->switchOff();
  l1->switchOff();
}

void ControlTask::tick(){
  
  switch(state){
    case IDLE:{
      l1->switchOn();
      if(cUser->isBtAvailable() > 0){
        cUser->receiveMsg();
        String msg = cUser->getMessage();   
        if (msg.equals("b")){
          cUser->setConnected(true);
          delay(500);
        } else if(msg.equals("n")){
            cUser->setConnected(false);
            delay(500);
        }
      }
      
      //manca il controllo sulla connessione
      if(((sonar->getDistance())<DIST1) && (cUser->isConnected())){
        cUser->changeManual();
        cUser->setIntensity(170);
        state = MANUAL;
        cUser->resetMessage();
        MsgService.sendMsg("M");       
        l1->switchOff();
      }
      
      break;

    }
    case MANUAL:{   
      lm->switchOn();
      if(cUser->isConnected()){
        cUser->receiveMsg();
        String msg = cUser->getMessage();    
        if (msg.equals("n")){
          cUser->setConnected(false);
          delay(500);
        }
      }
      if(((sonar->getDistance())> DIST1) || (!cUser->isConnected()) ){
       
        cUser->changeManual();
        state = IDLE;
        MsgService.sendMsg("A"); 
        cUser->resetMessage();
        lm->switchOff();
      }
      break;
    }
    
  }
}
