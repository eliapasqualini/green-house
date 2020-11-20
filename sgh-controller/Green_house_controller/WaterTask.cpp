#include "WaterTask.h"
#include "Led.h"
#include "servo_motor_impl.h"
#include "User.h"
#include "Arduino.h"
#include "MsgService.h"

#define LED2_PIN 6
#define MOTOR_PIN 9
#define TMAX 5000


WaterTask::WaterTask(User* cUser){
  this->cUser = cUser;
  l2 = new Led(LED2_PIN);
  pMotor = new ServoMotorImpl(MOTOR_PIN);
  pos = 0;
  delta = 1;
}

void WaterTask::init(int period){
  Task::init(period);
  state = STANDBY;
  l2->switchOff();
  Serial.println("INIZIO WATER TASK");
  
}

void WaterTask::tick(){
  switch(state){
    
    case STANDBY:{     
      if(cUser->checkManual()){
        if(cUser->isConnected()){    
          cUser->receiveMsg();
          cUser->receiveSerialMessage();
          String msg = cUser->getMessage();
          cUser->sendMsg(cUser->getHumidity());    
          if (msg.equals("o")){          
            state = OPEN;
            MsgService.sendMsg("O");        
          }
        }
      } else{
        //se sono in modalità auto controllo l'umidità per cambiare stato
        cUser->receiveSerialMessage();
        if (cUser->getCmd().equals("apri")){
          state = OPEN;
          MsgService.sendMsg("O"); 
        }
      }
      break;
    }
    
    case OPEN:{     
      //apro l'irrigatore 
      pMotor->on();
      for (int i = 0; i < 180; i++) {
        pMotor->setPosition(pos);         
        delay(5);            
        pos += delta;
      }
      pMotor->off();
      state = WATER;
      l2->setIntensity(cUser->getIntensity());
      T=0;
      break;
    }
    
    case WATER:{
      l2->setIntensity(cUser->getIntensity());
      T+= myPeriod;
      
      //scambio i dati di umidità ecc.
      //continuo in questo stato finche:
      if(cUser->checkManual()){
        //se sono in modalità manuale aspetto un comando per cambiare stato
        if(cUser->isConnected()){    
          cUser->receiveMsg();
          cUser->receiveSerialMessage();
          String msg = cUser->getMessage();
          cUser->sendMsg(cUser->getHumidity());   
            if (msg.equals("c")){
              state = CLOSE;
              MsgService.sendMsg("C");
              l2->setIntensity(0);          
            }
        }
       
      } else{
        //se sono in modalità auto controllo l'umidità se è maggiore del 35% o se ci sono già da T > di Tmax
        cUser->receiveSerialMessage();
        
          //controllo l'umidità se è maggiore del 35%
        if(cUser->getCmd().equals("chiudi")){
          state = CLOSE;
          MsgService.sendMsg("C"); 
          l2->setIntensity(0);
        }

          
      }
      if(T>TMAX){
        state = CLOSE;
        MsgService.sendMsg("W");
        l2->setIntensity(0);
      }     
      break;
    }
    
    case CLOSE:{

      //chiudo l'irrigatore
      pMotor->on();
      for (int i = 0; i < 180; i++) {
        pMotor->setPosition(pos);         
        delay(5);            
        pos -= delta;
      }
      pMotor->off();
      state = STANDBY;
      cUser->setCmd("chiudi");
      cUser->resetMessage();
      break;
    }
    
  }

}
