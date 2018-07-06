#include <ESP8266WebServer.h>

#include <Hash.h>

#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

MDNSResponder mdns;

// Replace with your network credentials
const char* ssid = "openlab1";
const char* password = "***";

ESP8266WebServer server(80);

String webPage = "";

int pin_led = 5;
int pin_button = 2;
boolean state_led, state_button;

void setup(void){
  webPage += "<h1>ESP8266 Web Server</h1><p>led  <a href=\"ledOn\"><button>ON</button></a>&nbsp;<a href=\"ledOff\"><button>OFF</button></a></p>";
  
  // preparing GPIOs
  pinMode(pin_led, OUTPUT);
  pinMode(pin_button,INPUT_PULLUP);
  digitalWrite(pin_led, LOW);
  digitalWrite(pin_button,HIGH);
  state_led = 0;
  
  delay(1000);
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  Serial.println("");

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  
  if (mdns.begin("esp8266", WiFi.localIP())) {
    Serial.println("MDNS responder started");
  }
  
  server.on("/", [](){
    server.send(200, "text/html", webPage);
  });
  server.on("/ledOn", [](){
    server.send(200, "text/html", webPage);
    digitalWrite(pin_led, HIGH);
    state_led=1;
    delay(1000);
  });
  server.on("/ledOff", [](){
    server.send(200, "text/html", webPage);
    digitalWrite(pin_led, LOW);
    state_led=0;
    delay(1000); 
  });
  server.begin();
  Serial.println("HTTP server started");
}
 
void loop(void){
  server.handleClient();

  // Reading the button state
  state_button=digitalRead(pin_button);

  // Testing conditions
  if (!state_button)          // if button pushed (=0 because pullup)
  {
    while(!digitalRead(pin_button))
      {
        delay(1);
      }
    if (!state_led)
    {
      digitalWrite(pin_led, HIGH);
      state_led=1;
    }
    else if (state_led)
    {
      digitalWrite(pin_led, LOW);
      state_led=0;
    }
  } 
}
