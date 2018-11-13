#include <FirebaseArduino.h>
#include <ESP8266WiFi.h>
#include <time.h>

#define FIREBASE_HOST "medvol-4803c.firebaseio.com"
#define FIREBASE_AUTH "oRxBtvusapJRi4AwnFyuv92w2R0w5cHivMuaD2pw"

#define WIFI_SSID "NET dos outros"
#define WIFI_PASSWORD "usasuanet"

//#define WIFI_SSID "NETVIRTUA 107"
//#define WIFI_PASSWORD "3817097950"

#define USER_ID "04aR0o2AqCV36A0ElWP8wHxj3N22"
#define ITEM_ID "-LR-4unjWyatHb2sZI98"

#include <HX711.h>  //You must have this library in your arduino library folder
#define D_OUT 5 //D1
#define Hard_CLOCK 4 // D2

//Change this calibration factor as per your load cell once it is found you many need to vary it in thousands
HX711 scale;

float interval = 10000;
float interval_config = 2000;
unsigned long previousMillis = 0;
float previousmass = 0;
String config_path = (String) "itens/" + USER_ID + "/" + ITEM_ID + "/";
String db_path = (String) "/logs/" + USER_ID + "/" + ITEM_ID + "/";
String data = "no";


//=============================================================================================
//                         SETUP
//=============================================================================================
void setup() {
  Serial.begin(9600);

  // connect to wifi.
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wifi\n");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
  Firebase.stream((String) config_path + "/update");
  if (Firebase.success()) {
    Serial.println("streaming ok");
  }else{
    Serial.println("Streaming Fail.");
  }
  
  while (!set_scale());
  
  configTime(-3 * 3600, 0, "pool.ntp.org", "time.nist.gov");
  Serial.println("\nWaiting for time");
  while (!time(nullptr)) {
    Serial.print(".");
    delay(1000);
  }
  Serial.println("");

  Serial.println("All done. :D");
  Serial.println();
}

bool set_scale(){

  float calibration_factor = Firebase.getFloat((String) config_path + "calibration_factor");
  if (Firebase.success()) {
    Serial.println("Nothing wrong until getting the calibration factor.");
  }else if (Firebase.failed()){
    Serial.print("Getting Firebase calibration_factor failed:");
    Serial.println(Firebase.error());
    delay(2000);
    return false;
  }

  scale.begin(D_OUT, Hard_CLOCK);
//  scale.set_scale(calibration_factor);  //Calibration Factor obtained from first sketch
  scale.set_scale(-250);
  scale.tare(10);             //Reset the scale to 0

  float tare_factor = scale.get_offset();

  Firebase.setFloat((String) config_path + "tare", tare_factor);
  if (Firebase.failed()) {
    Serial.print("getting Firebase tare_factor failed:");
    Serial.println(Firebase.error());
    delay(2000);
    return false;
  } else if (Firebase.success()) {
    Serial.println("Nothing wrong until returning the offset factor (tare).");
  }

  Serial.println("Scale set.");

  return true;
}

//=============================================================================================
//                         LOOP
//=============================================================================================

void loop() {
  if ( data != "yes") {
    unsigned long currentMillis = millis();

    if (currentMillis - previousMillis >= interval) {
      previousMillis = currentMillis;
      float mass = scale.get_units(10);
      float rate = ((mass - previousmass) / (interval / 1000)) * 60; // grams/min
      previousmass = mass;

      // append a new value to /log
      //    StaticJsonDocument<200> doc;
      //    JsonObject& root = doc.to<JsonObject>();
      
      StaticJsonBuffer<200> jsonBuffer;
      JsonObject& root = jsonBuffer.createObject();

      
      Serial.print("Mass: ");
      Serial.print(mass);
      Serial.println("g");

      Serial.print("Rate: ");
      Serial.print(rate);
      Serial.println("g/min");
      root["mass"] = mass;
      root["rate"] = rate;

      time_t now = time(nullptr);
      Serial.print("Time: ");
      Serial.println(now);
      Serial.print("Date: ");
      Serial.println(ctime(&now));
      root["date"] = ctime(&now);
      root["timestamp"] = now;
      
      String name = Firebase.push(db_path, root);

      // handle error
      if (Firebase.failed()) {
        Serial.print((String)"Firebase failed Pushing " + db_path + " failed:");
        Serial.println(Firebase.error());
        return;
      }
      Serial.print((String)"Firebase Pushed " + db_path );
      Serial.println(name);
    }

    if (Firebase.available()) {
      FirebaseObject event = Firebase.readEvent();
      data = event.getString("data");
    }
  }
  else {

    unsigned long currentMillis = millis();

    if (currentMillis - previousMillis >= interval_config) {
      previousMillis = currentMillis;
      float raw = scale.read_average(10);
      float value = scale.get_units(10);
      Serial.print("raw: ");
      Serial.println(raw);
      Serial.print("value: ");
      Serial.println(value);
      Firebase.setFloat((String) config_path + "raw_value", raw);
      if (Firebase.failed()) {
        Serial.print("setting Firebase tare_factor failed:");
        Serial.println(Firebase.error());
        return;
      }
      Firebase.setFloat((String) config_path + "true_value", value);
      if (Firebase.failed()) {
        Serial.print("setting Firebase tare_factor failed:");
        Serial.println(Firebase.error());
        return;
      }


      if (Firebase.available()) {
        FirebaseObject event = Firebase.readEvent();
        float calib_fact = Firebase.getFloat((String) config_path + "calibration_factor");
        if (Firebase.failed()) {
          Serial.print("getting Firebase tare_factor failed:");
          Serial.println(Firebase.error());
          return;
        }
        scale.set_scale(calib_fact);  //Calibration Factor obtained from first sketch
        scale.tare(10);
        float tare_factor = scale.get_offset();

        Firebase.setFloat((String) config_path + "tare", tare_factor);
        if (Firebase.failed()) {
          Serial.print("setting Firebase tare_factor failed:");
          Serial.println(Firebase.error());
          return;
        }
        data = event.getString("data");
      }
    }
  }
}
//=============================================================================================
