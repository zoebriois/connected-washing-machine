#include <ThingSpeak.h>
#include <ESP8266WiFi.h>
#include <AccelStepper.h>

// Network parameters
//const char* ssid     = "openlab1";
//const char* password = "phonglab1";
const char* ssid     = "Mobylette Hostel F5";
const char* password = "190mobylette190";

WiFiClient client;

// ThingSpeak information
char TSAddress[] = "api.thingspeak.com";
unsigned long channelID = 508003;
char* readAPIKey = "90C6G3IZQ5SPNPGJ";
char* writeAPIKey = "5CX2PMYHFI0BKZ6U";
unsigned int fieldMachineState = 1;   
unsigned int fieldButtonPushed = 2;
unsigned int fieldCycleState = 3;
unsigned int fieldTrapClosed = 4;
unsigned int fieldRemainingTime = 5;

// Distance sensor HC-SR04 parameters
#define pin_trigger D3
#define pin_echo D4
long duration;
int distance;

// Leds parameters
#define pin_led_1 D0         // green
#define pin_led_2 D2         // red

// Button parameter
#define pin_button D1

// Step motor parameters
int motorPin1 = D5;
int motorPin2 = D6;
int motorPin3 = D7;
int motorPin4 = D8;
#define HALFSTEP 8
AccelStepper stepper1(HALFSTEP, motorPin1, motorPin3, motorPin2, motorPin4);

// Constants
/* Reminder about how cycleState work
 *  0 when the machine is full but the user have not choose the cycle time for the moment,
 *  1 when a cycle is in progress (the user ask to start a cycle),
 *  2 when the cycle is finished but the user has not yet opened the machine since the end of the cycle
 *  3 when the machine is not full and the user didn’t ask to start a cycle.
 */
float MachineState;
float TrapClosed;
float CycleState;
float ButtonPushed;
float RemainingTime;

void setup() {
  // initializing serial port with 115200 bauds (to send debug messages to the IDE) 
  Serial.begin(115200);
  Serial.println("Setup begin");
  delay(10000);
  connectWifi();

  // initializing the leds
  pinMode(pin_led_1, OUTPUT);
  digitalWrite(pin_led_1, HIGH);
  pinMode(pin_led_2, OUTPUT);
  digitalWrite(pin_led_2, LOW);

  // initializing the pushing button
  pinMode(pin_button,INPUT_PULLUP);  
  digitalWrite(pin_button,HIGH);

  // initializing HC SR04
  pinMode(pin_trigger, OUTPUT);
  digitalWrite(pin_trigger, LOW);
  pinMode(pin_echo, INPUT);
  
  // initializing the stepper
  stepper1.setMaxSpeed(1000.0);
  stepper1.setAcceleration(100.0);
  stepper1.setSpeed(200);
  stepper1.moveTo(13000);
    
  firstInit();
  Serial.println("End of setup");
}

void loop() {
  /*
   *    IF the machine was not full
   *      green led ON only
   *      IF the machine is now full
   *        publish 1 on TS field MachineState
   *        publish 0 on TS field CycleState
   *        red led ON only
   *    IF cycleState == 1
   *      close the trap
   *      publish 1 on TS field TrapClosed
   *      while Remaining Time > 0
   *        wait
   *      publish 2 on TS field CycleState
   *      when the user push the button
   *      or ask to open the trap with the application
   *        open the trap
   *        publish 0 on TS field TrapClosed
   *        wait for a push of 3s on the button
   *          publish 0 on TS field MachineState
   *          publish 3 on TS field CycleState
   *          green led ON only
   *          restart a the begining of the program
   */
  if (MachineState == 0)  {
    digitalWrite(pin_led_1, HIGH);
    digitalWrite(pin_led_2, LOW);

    int cpt_hcsr = 0;
    if(resultHCSR()!=5) {
      cpt_hcsr++;
      while(resultHCSR()!=5) {
        cpt_hcsr++;
        Serial.println("cpt hcsr04 value : " + String(cpt_hcsr));
        if (cpt_hcsr > 3){
          Serial.println("HCSR04: machine full");
          digitalWrite(pin_led_1, LOW);
          digitalWrite(pin_led_2, HIGH);
          Serial.println("led red ON");
          Serial.println("led green OFF");
          delay(500);
          MachineState = 1.0;
          CycleState = 0.0;
          writeTSData2(channelID, fieldMachineState, 1.0, fieldCycleState, 0.0);
          break;
        }
      }
    }
  }        
  
  CycleState = readTSData(channelID, fieldCycleState);
  if (CycleState==1){
    digitalWrite(pin_led_1, LOW);
    digitalWrite(pin_led_2, HIGH);
    Serial.println("led red ON");
    Serial.println("led green OFF");
    // actionning the motor to close the trap
    stepper1.setMaxSpeed(1000.0);
    stepper1.setAcceleration(100.0);
    stepper1.setSpeed(200);
    stepper1.moveTo(12000);
    Serial.println("...");
    closeTrap();
    while (readTSData(channelID, fieldRemainingTime) == 0) {
      delay(1000);
    }
    RemainingTime = readTSData(channelID, fieldRemainingTime);
    while (RemainingTime > 0) {
      Serial.println("Waiting to the end of the cycle: " + String(RemainingTime));
      delay(10000);
      RemainingTime = readTSData(channelID, fieldRemainingTime);
    }
    delay(1000);
    CycleState=2.0;
    writeTSData(channelID, fieldCycleState, CycleState);
    while(1)  {
      Serial.println("waiting for the order to open the trap");
      if (readTSData(channelID, fieldButtonPushed) == 1)  {
        Serial.println("  ask from the application");
        stepper1.setMaxSpeed(1000.0);
        stepper1.setAcceleration(100.0);
        stepper1.setSpeed(200);
        stepper1.moveTo(-10000);
        openTrap();
        break;
      }
      if (!digitalRead(pin_button)) {
        Serial.println("  ask with the button");
        stepper1.setMaxSpeed(1000.0);
        stepper1.setAcceleration(100.0);
        stepper1.setSpeed(200);
        stepper1.moveTo(-9000);
        openTrap();
        break;
      }
    }

    int cpt = 0;
    while(1)  {
      if (!digitalRead(pin_button)) {
        delay(1000);
        cpt++;
      }
      else {
        delay(500);
        cpt = 0;
      }
      Serial.println("cpt button value : " + String(cpt));
      if (cpt >= 3){
        Serial.println("button pushed durring 3s");
        MachineState = 0.0;
        CycleState = 3.0;
        writeTSData2(channelID, fieldMachineState, MachineState, fieldCycleState, CycleState);
        digitalWrite(pin_led_1, HIGH);
        digitalWrite(pin_led_2, LOW);  
        break;
      }  
    }        
  }
}

/* FUNCTION to connect to the Wifi network */
int connectWifi() {
  Serial.println("Beginning of connectWifi()");
  while (WiFi.status() != WL_CONNECTED) {
      WiFi.begin(ssid, password);
      delay(2500);
      Serial.print(".");
  }
  Serial.print("\nConnected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  ThingSpeak.begin(client);
}

/* FUNCTION to read data from thingSpeak */
float readTSData (long TSChannel, unsigned int TSField) {
  float data = ThingSpeak.readFloatField (TSChannel, TSField, readAPIKey);
  Serial.println("Data read from field " + fieldName(TSField) + " (" + TSField + ") = " + data);
  if (!(data>=0)) {
    data = 0;
  }
  return data;
}

/* FUNCTION to write a single data to ThingSpeak */
int writeTSData (long TSChannel, unsigned int TSField, float data)  {
  int WriteSuccess = ThingSpeak.writeField(TSChannel, TSField, data, writeAPIKey);
  if (WriteSuccess)  {
    Serial.println(String(data) + " written to field " + fieldName(TSField));
  }
  else  {
    Serial.println("Can't write data " + String(data) + " to field " + fieldName(TSField));
  }
  return WriteSuccess;
}

/* FUNCTION to write 2 datas to differents fields on ThingSpeak */
int writeTSData2 (long TSChannel, unsigned int TSField1, float data1, unsigned int TSField2, float data2) {
  ThingSpeak.setField(TSField1, data1);
  ThingSpeak.setField(TSField2, data2);
  int WriteSuccess = ThingSpeak.writeFields(TSChannel, writeAPIKey);
  if (WriteSuccess)  {
    Serial.println(String(data1) + " written to field " + fieldName(TSField1));
    Serial.println(String(data2) + " written to field " + fieldName(TSField2));
  }
  else  {
    Serial.println("Can't write data " + String(data1) + " to field " + fieldName(TSField1));
    Serial.println("and " + String(data2) + " to field " + fieldName(TSField2));
  }
  return WriteSuccess;
}

/* FUNCTION to write 3 datas to differents fields on ThingSpeak */
int writeTSData3 (long TSChannel, unsigned int TSField1, float data1, unsigned int TSField2, float data2, unsigned int TSField3, float data3) {
  ThingSpeak.setField(TSField1, data1);
  ThingSpeak.setField(TSField2, data2);
  ThingSpeak.setField(TSField3, data3);
  int WriteSuccess = ThingSpeak.writeFields(TSChannel, writeAPIKey);
  if (WriteSuccess)  {
    Serial.println(String(data1) + " written to field " + fieldName(TSField1));
    Serial.println(String(data2) + " written to field " + fieldName(TSField2));
    Serial.println(String(data3) + " written to field " + fieldName(TSField3));
  }
  else  {
    Serial.println("Can't write data " + String(data1) + " to field " + fieldName(TSField1));
    Serial.println("and " + String(data2) + " to field " + fieldName(TSField2));
    Serial.println("and " + String(data3) + " to field " + fieldName(TSField3));
  }
  return WriteSuccess;
}

/* FUNCTION only to print more easily on the serial monitor */
String fieldName(int fieldNumber) {
  String fieldName;
  switch(fieldNumber) {
    case 1:
      fieldName = "MachineState";
      break;
    case 2:   
      fieldName = "ButtonPushed";
      break;
    case 3: 
      fieldName = "CycleState";
      break;
    case 4: 
      fieldName = "TrapClosed";
      break;
    case 5: 
      fieldName = "RemainingTime";
      break; 
  }
  return fieldName;
}

/* FUNCTION to initialize the values on ThingSpeak at the begining */
void firstInit() {
  MachineState = 0;
  CycleState = 3;
  TrapClosed = 0;
  ButtonPushed = 0;
  RemainingTime = 0;
  writeTSData3 (channelID, fieldMachineState, MachineState, fieldCycleState, CycleState, fieldTrapClosed, TrapClosed);
  writeTSData2 (channelID, fieldButtonPushed, ButtonPushed, fieldRemainingTime, RemainingTime);
}

/* FUNCTION to read the value on the HCSR04 */
float resultHCSR(){
  // Clears the pin_trigger
  digitalWrite(pin_trigger, LOW);
  delayMicroseconds(2);
  // Sets the pin_trigger on HIGH state for 100 micro seconds
  digitalWrite(pin_trigger, HIGH);
  delayMicroseconds(100);
  digitalWrite(pin_trigger, LOW);
  // Reads the pin_echo, returns the sound wave travel time in microseconds
  duration = pulseIn(pin_echo, HIGH, 50000);
  // Calculating the distance
  distance= duration*0.034/2;
  // Prints the distance on the Serial Monitor
  Serial.print("Distance: ");
  Serial.println(distance);
  return distance;
}

/* FUNCTION to close the hatch */
void closeTrap() {
  stepper1.run();
  while(stepper1.distanceToGo() >0) {
    stepper1.run();
    yield();
  }
  Serial.println("Hatch closed");
  TrapClosed=1;
  writeTSData(channelID, fieldTrapClosed, TrapClosed);
}

/* FUNCTION to open the hatch */
void openTrap() {
  stepper1.run();
  while(stepper1.distanceToGo() <0) {
    stepper1.run();
    yield();
  }
  Serial.println("Hatch open");
  TrapClosed=0;
  writeTSData(channelID, fieldTrapClosed, TrapClosed);
}
