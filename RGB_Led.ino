//you will need the Adafruit NeoPixel Library from github (https://github.com/adafruit/Adafruit_NeoPixel)
//press clone or download and download the ZIP folder
//in arduino go to sketch -> include library -> add .zip library

#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
  #include <avr/power.h>
#endif

#define PIN 6     // pin number for digital in
#define LEDS 50   // number of LEDs connected in series

//parameter 1 = number of LEDs in strip
//parameter 2 = Arduino pin number (most are valid)
//parameter 3 = LED type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     LEDs are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     LEDs are wired for RGB bitstream (v1 FLORA pixels, not v2)
//   NEO_RGBW    LEDs are wired for RGBW bitstream (NeoPixel RGBW products)
Adafruit_NeoPixel strip = Adafruit_NeoPixel(LEDS, PIN, NEO_RGB + NEO_KHZ800);

//if the red and green colors of the LEDs are swaped then set pixel type to NEO_GRB

uint16_t i=0;
uint16_t j=0;

//red, green and blue values for LEDs
uint16_t r = 255;
uint16_t g = 255;
uint16_t b = 255;

String data = "";     //received data stored in a string
char character;       //received charater
bool rainbow = false; //rainbow effecct set to false
uint16_t wait = 26;   //delay (in miliseconds) between each led color change

void setup()
{
  //this is for Trinket 5V 16MHz, you can remove these three lines if you are not using a Trinket
  #if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
  #endif

  Serial.begin(9600);
  strip.begin();
  strip.show();   //start with all LEDs off
}

void loop()
{
  while (Serial.available())  //while data is being received
  {
    character = Serial.read();
    data.concat(character);   //add the charater to the string
    delay(2);                 //wait until next charater is received
  }
  if (data != "")
  {
    Serial.print(data);
    Serial.print('\n');

    if (data == "on")
    {
      rainbow = false;
      colorWipe(strip.Color(r, g, b), wait);  //turn LEDs on to specific color
    }
    else if (data == "off")
    {
      rainbow = false;
      colorWipe(strip.Color(0, 0, 0), wait);  //turn LEDs off
    }
    else if (data == "rainbow")
    {
      rainbow = true;
    }
    else if (data.substring(0,5) == "delay")
    {
      wait = (data.substring(6).toInt()) * 2;
    }
    else if (data.substring(0,1) == "r")
    {
      rainbow = false;
      r = data.substring(1).toInt();
      colorWipe(strip.Color(r, g, b), wait);
    }
    else if (data.substring(0,1) == "g")
    {
      rainbow = false;
      g = data.substring(1).toInt();
      colorWipe(strip.Color(r, g, b), wait);
    }
    else if (data.substring(0,1) == "b")
    {
      rainbow = false;
      b = data.substring(1).toInt();
      colorWipe(strip.Color(r, g, b), wait);
    }
    
  }
  if (rainbow)
  {
    strip.setPixelColor(i, Wheel((2*i+j) & 255));
    i++;
    if (i == LEDS)
    {
      j++;
      i = 0;
      if (j >= 256)  {   j = 0;   }
      strip.show();
      delay(wait/2);
    }
  }
  data = "";
}
void colorWipe(uint32_t c, uint8_t Wait) {
  for(uint16_t k=0; k<strip.numPixels(); k++) {
    strip.setPixelColor(k, c);
    strip.show();
    delay(Wait);
  }
}
uint32_t Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if(WheelPos < 85) {
    return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  }
  if(WheelPos < 170) {
    WheelPos -= 85;
    return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
  WheelPos -= 170;
  return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
}
