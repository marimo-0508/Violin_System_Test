import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import themidibus.*; 
import javax.sound.midi.MidiMessage; 
import javax.sound.midi.SysexMessage; 
import javax.sound.midi.ShortMessage; 
import processing.video.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Violin_System_Test extends PApplet {

 //Import the library
 //Import the MidiMessage classes http://java.sun.com/j2se/1.5.0/docs/api/javax/sound/midi/MidiMessage.html


  //\u30d3\u30c7\u30aa\u30e9\u30a4\u30d6\u30e9\u30ea\u3092\u30a4\u30f3\u30dd\u30fc\u30c8


//\u697d\u8b5c
PImage score, part_score, left_grad, right_grad;

//\u4e3b\u306b\u697d\u8b5c\u306e\u97f3\u3092\u7ba1\u7406\u3059\u308b\u7528
ScoreNote[][]note = new ScoreNote[4][8];
int note_y, note_x = 0;
boolean move = false;
float score_top = 90.0f;
float moving = 0.0f;

//\u6642\u523b
boolean flag = false;

//txt\u30d5\u30a1\u30a4\u30eb\u51fa\u529b\u306b\u5fc5\u8981\u306a\u914d\u5217
ArrayList<String> note_number = new ArrayList<String>();
ArrayList<String> now_number = new ArrayList<String>();
ArrayList<String> count = new ArrayList<String>();
ArrayList<String> note_velocity = new ArrayList<String>();
ArrayList<String> result = new ArrayList<String>();
ArrayList<String> pitche_bend = new ArrayList<String>();
float mill;//\u6642\u9593\u7528
int note_num;//\u5f3e\u304f\u3079\u304d\u97f3\u756a\u53f7
int now_num = 0;//\u4eca\u73fe\u5728\u5f3e\u3044\u3066\u3044\u308b\u97f3
int note_vel = 0;//\u30d9\u30ed\u30b7\u30c6\u30a3

//\u8272\u3092\u7ba1\u7406\u3059\u308b\u7528
Color []col = new Color[22];//\u8272\u309222\u8272\u306e\u914d\u5217\u3067\u7ba1\u7406
int []r = { 0,  38,  65, 112,  38, 131, 160,  82,   9,  29,  36,
           87, 111, 211, 248, 245, 244, 243, 246, 238, 234, 255
           };
int []g = {  0,  92, 131, 160, 187, 206, 213, 186, 127, 117, 155, 
	       175, 189, 227, 229, 211, 161, 162, 189, 129,  93, 0
           };
int []b = {255, 170, 197, 214, 238, 237, 205, 155, 93, 57, 58, 
           79, 105, 142, 141,  60,  55, 134, 187, 127, 87, 0
	       };

//web\u30ab\u30e1\u30e9\u7528
Capture video;  //Capture\u578b\u306e\u5909\u6570video\u3092\u5ba3\u8a00

//midi\u7528
MidiBus myBus; //The MidiBus
int pitchbend, notebus_different=0;//note_y\u306f\u6bb5\u843d\u6570\u3001note_x\u3067\u6bb5\u843d\u5185\u306e\u4f55\u756a\u76ee\u3092\u5f3e\u3044\u3066\u3044\u308b\u304b\u7ba1\u7406

int channel = 0;
int pitch = 64;
int velocity = 127;
int status_byte = 0xA0; // For instance let us send aftertouch
int channel_byte = 0; // On channel 0 again
int first_byte = 64; // The same note;
int second_byte = 80; // But with less velocity

ArrayList<ScoreNote> played_note;//pitchbend\u3067\u5f97\u305f\u3069\u306e\u7a0b\u5ea6\u305a\u308c\u3066\u3044\u308b\u304b\u3092\u5165\u308c\u308b\u305f\u3081\u306e\u914d\u5217\u3092\u7528\u610f

public void setup() {
  

  //midibus\u7528
  MidiBus.list(); // List all available Midi devices on STDOUT. This will show each device's index and name.
  myBus = new MidiBus(this, 0, 0); // Create a new MidiBus object

 //\u30ab\u30e1\u30e9\u306e\u6e96\u5099
  video = new Capture(this, 640, 540,"USB_Camera");  //\u30ab\u30e1\u30e9\u304b\u3089\u306e\u30ad\u30e3\u30d7\u30c1\u30e3\u30fc\u3092\u304a\u3053\u306a\u3046\u305f\u3081\u306e\u5909\u6570\u3092\u8a2d\u5b9a
  video.start();  //Processing ver.2.0\u4ee5\u4e0a\u306f\u3053\u306e\u30b3\u30fc\u30c9\u304c\u5fc5\u8981

 //\u753b\u50cf\u3092\u7528\u610f
  score = loadImage("star.png");
  part_score = loadImage("part_star.png");
  left_grad = loadImage("left_grad.png"); //\u5de6\u7528\u30b0\u30e9\u30c7\u3092\u7528\u610f
  right_grad = loadImage("right_grad.png"); //\u53f3\u7528\u30b0\u30e9\u30c7\u3092\u7528\u610f

  //col[number] = new Color(R, G, B)
  for(int i = 0; i < 22 ; i++){
  	col[i] = new Color(r[i], g[i], b[i]);
  }

//note[note_y][note_x] = new Note(all_score_PositionX, \u00d7\u306e\u521d\u671f\u8a2d\u5b9a, NoteName);
 note[0][0] = new ScoreNote(919, 0, 70);
  note[0][1] = new ScoreNote(1044, 0, 70);
  note[0][2] = new ScoreNote(1172, 0, 70);
  note[0][3] = new ScoreNote(1299, 0, 70);
  note[0][4] = new ScoreNote(1443, 0, 70);
  note[0][5] = new ScoreNote(1577, 0, 70);
  note[0][6] = new ScoreNote(1712, 0, 70);
  note[0][7] = new ScoreNote(1846, 0, 70);

  note[1][0] = new ScoreNote(919, 0, 70);
  note[1][1] = new ScoreNote(1044, 0, 70);
  note[1][2] = new ScoreNote(1172, 0, 70);
  note[1][3] = new ScoreNote(1299, 0, 70);
  note[1][4] = new ScoreNote(1443, 0, 70);
  note[1][5] = new ScoreNote(1577, 0, 70);
  note[1][6] = new ScoreNote(1712, 0, 70);
  note[1][7] = new ScoreNote(1846, 0, 70);

  note[2][0] = new ScoreNote(919, 0, 70);
  note[2][1] = new ScoreNote(1044, 0, 70);
  note[2][2] = new ScoreNote(1172, 0, 70);
  note[2][3] = new ScoreNote(1299, 0, 70);
  note[2][4] = new ScoreNote(1443, 0, 70);
  note[2][5] = new ScoreNote(1577, 0, 70);
  note[2][6] = new ScoreNote(1712, 0, 70);
  note[2][7] = new ScoreNote(1846, 0, 70);

  note[3][0] = new ScoreNote(919, 0, 70);
  note[3][1] = new ScoreNote(1044, 0, 70);
  note[3][2] = new ScoreNote(1172, 0, 70);
  note[3][3] = new ScoreNote(1299, 0, 70);
  note[3][4] = new ScoreNote(1443, 0, 70);
  note[3][5] = new ScoreNote(1577, 0, 70);
  note[3][6] = new ScoreNote(1712, 0, 70);
  note[3][7] = new ScoreNote(1846, 0, 70);

  //midibus\u3092\u7ba1\u7406
  myBus.sendNoteOn(channel, pitch, velocity); // Send a Midi noteOn
  myBus.sendNoteOff(channel, pitch, velocity); // Send a Midi nodeOff
  myBus.sendMessage(status_byte, channel_byte, first_byte, second_byte);
  myBus.sendMessage(
    new byte[] {
    (byte)0xF0, (byte)0x1, (byte)0x2, (byte)0x3, (byte)0x4, (byte)0xF7
    }
    );
  try { 
    SysexMessage message = new SysexMessage();
    message.setMessage(
      0xF0, 
      new byte[] {
      (byte)0x5, (byte)0x6, (byte)0x7, (byte)0x8, (byte)0xF7
      }, 
      5
      );
    myBus.sendMessage(message);
  } 
  catch(Exception e) {
  }
}

public void draw() {
  	background(0);

  	//\u30ab\u30e1\u30e9\u306e\u8abf\u6574\u3068\u8868\u793a
    video.read();

    //\u30ab\u30e1\u30e9\u6620\u50cf\u3092\u56de\u8ee2\u3055\u305b\u3066\u3001\u6f14\u594f\u8005\u306e\u898b\u3066\u3044\u308b\u3082\u306e\u3068\u540c\u3058\u6620\u50cf\u306b\u3059\u308b
    pushMatrix(); 
    translate(100, 900);
    rotate(radians(-90));
    image(video, 10, 10, 640, 540);
    popMatrix();

    note[note_y][note_x].move_score();//\u697d\u8b5c\u306e\u4e00\u6bb5\u843d\u306e\u3046\u3061\u5f3e\u3044\u3066\u3044\u308b\u7b87\u6240\u306e\u307f\u5207\u308a\u629c\u304d
    image(score, 800, 100, 1142, 681);

    //\u697d\u8b5c\u306e\u6c34\u8272\u25bc\u3092\u8868\u793a
    note[note_y][note_x].blue_triangle(); 

    //\u305a\u308c\u5225\u306e\u8272\u306e\u898b\u672c\u3092\u8868\u793a
    note[note_y][note_x].color_example();

    //\u305d\u306e\u5834\u3067\u5f3e\u3044\u305f\u97f3\u306e\u305a\u308c\u3092\u8868\u793a
    note[note_y][note_x].real_time_color();

    //\u97f3\u306e\u8a18\u9332\uff08\u8272\u3068\u00d7\u306e\u8868\u793a\uff09
    note[note_y][note_x].note_recorder();//\u97f3\u306e\u305a\u308c
    note[note_y][note_x].judgement();//\u00d7\u3092\u3064\u3051\u308b

    //\u30df\u30b9\u306e\u56de\u6570
    note[note_y][note_x].sum_false();//\u30df\u30b9\u306e\u30ab\u30a6\u30f3\u30c8\u3068\u30c6\u30ad\u30b9\u30c8\u3092\u8868\u793a
  }

//midibus\u3092\u7ba1\u7406\u3057\u3066\u3044\u308b
public void rawMidi(byte[] data) { // You can also use rawMidi(byte[] data, String bus_name) 
  println();
  print("Status Byte/MIDI Command:"+(int)(data[0] & 0xFF));
  if (((int)(data[0] & 0xFF) >= 224)&&((int)(data[0] & 0xFF) <= 227)) {
    pitchbend = (int)(data[2] & 0xFF) * 128 + (int)(data[1] & 0xFF);
  } 
 for (int i = 1; i < data.length; i++) {
    print(": "+(i+1)+": "+(int)(data[i] & 0xFF));
 }
 for (int i = 1; i < data.length; i++) {
 print(": "+(i+1)+": "+(int)(data[i] & 0xFF));
  }
if (((int)(data[0] & 0xFF) >= 144)&&((int)(data[0] & 0xFF) <= 171)) {
    notebus_different = (
    	(data[1] & 0xFF)-note[note_y][note_x].Number())*333+pitchbend-8192;
    note[note_y][note_x].addNote(notebus_different);
  }
if(((int)(data[0] & 0xFF) >= 143)&&((int)(data[0] & 0xFF) <= 150)) {
  //println("velocity:" +(int)(data[2] & 0xFF));
  note_vel = (int)(data[2] & 0xFF);
}
if (((int)(data[0] & 0xFF) >= 128)&&((int)(data[0] & 0xFF) <= 131)) {
    println();
    note_num = note[note_y][note_x].Number();
    now_num = (int)(data[1] & 0xFF);
 
 if ((int)(data[1] & 0xFF)!=(note[note_y][note_x].Number())) {
    note[note_y][note_x].judge = 1;      
    }
    if ((int)(data[1] & 0xFF)==(note[note_y][note_x].Number())) {
      note_x++;
      move = true;
      if (note_x!=0&&note_x==8) {
        note_y++;
        note_x=0;
        if (note_y>3) {
          note_y=0;
        }
      }
    }
  }
  if((int)(data[0] & 0xFF) >= 0){
    flag = true;
    if(flag == true){
    note_number.add(Integer.toString(note_num));
    now_number.add(Integer.toString(now_num));
    count.add(""+mill);
    note_velocity.add(Integer.toString(note_vel));
    pitche_bend.add(Integer.toString(notebus_different));
  }
    flag = false;
  }
}
 
//web\u30ab\u30e1\u30e9\u3092\u66f4\u65b0
public void captureEvent(Capture video) {
  video.read();
}
  public void mouseClicked(){
    println("mouseX:" + mouseX + "," + "mouseY:" + mouseY);
  }
class Color{ //\u97f3\u306e\u5909\u5316\u306e\u8272\u3092\u793a\u3059\u30af\u30e9\u30b9
  private int r;
  private int g;
  private int b;
  Color(int r, int g, int b){ 
 
    this.r=r;
    this.g=g;
    this.b=b;
  }
  public int getR() {
    return this.r;
  }
  public int getG() {
    return this.g;
  }
  public int getB() {
    return this.b;
  }

  public void color_rect() {
    noStroke();
    fill(r, g, b);
  }
}
class ScoreNote{
	private int x;
	private int judge;
	private int number;
	private ArrayList<Integer> played_note = new ArrayList();
	ScoreNote(int x, int judge, int number){
 	this.x = x;
 	this.judge = judge;
 	this.number = number;
 }

 public int getX() {
    return this.x;
  }
  public int Judge() {
    return this.judge;
  }
  public int Number() {
    return this.number;
  }
public void addNote(int n)
  {
    if (n<-300) {
      n=0;
    } else if ((-300<=n)&&(n<-270)) {
      n=1;
    } else if ((-270<=n)&&(n<-240)) {
      n=2;
    } else if ((-240<=n)&&(n<-210)) {
      n=3;
    } else if ((-210<=n)&&(n<-180)) {
      n=4;
    } else if ((-180<=n)&&(n<-150)) {
      n=5;
    } else if ((-150<=n)&&(n<-120)) {
      n=6;
    } else if ((-120<=n)&&(n<-90)) {
      n=7;
    } else if ((-90<=n)&&(n<-60)) {
      n=8;
    } else if ((-60<=n)&&(n<-30)) {
      n=9;
    } else if ((-30<=n)&&(n<0)) {
      n=10;
    } else if ((0<=n)&&(n<30)) {
      n=11;
    } else if ((30<=n)&&(n<60)) {
      n=12;
    } else if ((60<=n)&&(n<90)) {
      n=13;
    } else if ((90<=n)&&(n<120)) {
      n=14;
    } else if ((120<=n)&&(n<150)) {
      n=15;
    } else if ((150<=n)&&(n<180)) {
      n=16;
    } else if ((180<=n)&&(n<210)) {
      n=17;
    } else if ((210<=n)&&(n<240)) {
      n=18;
    } else if ((240<=n)&&(n<270)) {
      n=19;
    } else if ((270<=n)&&(n<300)) {
      n=20;
    } else if (300<=n) {
      n=21;
    }
    played_note.add(n);
  }

  public int getNote(int m) {
    return this.played_note.get(m);
  }

  public void blue_triangle() {//\u6c34\u8272\u25bc\u306e\u4f4d\u7f6e\u3068\u5f62\u3092\u7ba1\u7406  
    noStroke();
    fill(186, 233, 255);
    textSize(25);
    text("\u25bc", x-20, 67+212*note_y, 40, 40);
    text("\u25bc", 210, 38, 40, 40);
  }

public void real_time_color(){//\u30ea\u30a2\u30eb\u30bf\u30a4\u30e0\u3067\u5909\u5316\u3059\u308b\u97f3\u306e\u8272\u3092\u8868\u793a
  if (note[note_y][note_x].played_note.size()>=1) {
    col[note[note_y][note_x].getNote(note[note_y][note_x].played_note.size()-1)].color_rect();
    rect(200, 160, 30, 30);
  }
}

public void color_example(){//\u53f3\u4e0a\u306e\u8272\u306e\u898b\u672c\u3092\u8868\u793a
   for (int i = 0; i < col.length; i++) {
    col[i].color_rect();
    rect(1000+i*30, 20, 20, 20);
  }
  fill(255);
  textSize(20);
  text("low tone", 900, 20, 100, 40);//\u6587\u5b57\u8868\u793a
  text("high tone", 1670, 20, 100, 40);//\u6587\u5b57\u8868\u793a  
}

 public void note_recorder(){
  if ((note_x>=0) && (note_y>=0)) {//\u97f3\u304c\u5165\u529b\u3055\u308c\u3066\u3044\u308b\u3053\u3068\u304c\u524d\u63d0
    for (int i=0; i<note_x; i++) {//\u73fe\u5728\u6f14\u594f\u3057\u3066\u3044\u308b\u6bb5\u843d\u306e\u307f\u306e\u8272\u8868\u793a
      try{
      col[note[note_y][i].getNote(0)].color_rect();//\u6700\u521d\u306e\u97f3\u306e\u305a\u308c\u306e\u8272\u3092\u63a1\u7528
      }
      catch (NullPointerException e){
        fill(87, 175, 79);
      }
      
      rect(note[note_y][i].getX(), 250+212*note_y, 20, 20);//\u97f3\u306e\u305a\u308c\u3092\u8868\u793a
    }
    for (int j = 0; j <= note_y-1; j++) {
      for (int i = 0; i < 8; i++) {//\u73fe\u5728\u6f14\u594f\u3057\u3066\u3044\u308b\u3088\u308a\u3082\u524d\u306e\u8272\u8868\u793a
        try{
        col[note[j][i].getNote(0)].color_rect();//\u6700\u521d\u306e\u97f3\u306e\u305a\u308c\u306e\u8272\u3092\u63a1\u7528
      }catch (NullPointerException e){
        fill(87, 175, 79);
      }
        rect(note[note_y][i].getX(), 250+212*j, 20, 20);//\u97f3\u306e\u305a\u308c\u3092\u8868\u793a
      }
    }
  }
 }

  public void judgement(){
  if ((note_x>=0) && (note_y>=0)) {//\u97f3\u304c\u5165\u529b\u3055\u308c\u3066\u3044\u308b\u3053\u3068\u304c\u524d\u63d0
    for (int i=0; i<note_x; i++) {//\u73fe\u5728\u6f14\u594f\u3057\u3066\u3044\u308b\u6bb5\u843d\u306e\u307f\u306e\u8272\u8868\u793a
      if (note[note_y][i].judge>=1) {
        fill(255);
        textSize(25);
        text("\u00d7", note[note_y][i].getX(), 67+212*note_y, 40, 40);
      }
    }
    for (int j = 0; j <= note_y-1; j++) {
      for (int i = 0; i < 8; i++) {//\u73fe\u5728\u6f14\u594f\u3057\u3066\u3044\u308b\u3088\u308a\u3082\u524d\u306e\u8272\u8868\u793a
       if (note[j][i].judge>=1) {
          fill(255);
          textSize(25);
          text("\u00d7", note[note_y][i].getX(), 67+212*j, 40, 40);
        }
      }
    }
  }
}

public void sum_false(){
  int sum = 0;
  for (int i = 0; i < note.length; i++) {
    for (int j=0; j < note[i].length-1; j++) {
      if (note[i][j].judge == 1) {
        sum++;
      }
  }
    }
    fill(255);
    textSize(25);
    text(sum+"/32", 1000, 955);
}

public void move_score(){
   if ((note_x>=0) &&(note_y>=0)) {
    if ((move == true)) {
      moving+=0.9f;
    }
    if (moving >= 12.0f) {
      moving = 0.0f;
      move = false;
    }
  }
  score_top = score_top - moving;
  
  image(part_score, score_top, 50, 3990, 148);//\u79fb\u52d5\u3059\u308b\u697d\u8b5c\u306e\u7b2c1\u9023
  noStroke();
  fill(0);
  rect(0,40,70,218);
  rect(700,40,displayWidth-700,218);
  image(left_grad, 70, 40, 88, 178); //\u30b0\u30e9\u30c7\u30fc\u30b7\u30e7\u30f3\u5de6\u3092\u914d\u7f6e
 image(right_grad, 700, 40, 88, 178);//\u30b0\u30e9\u30c7\u30fc\u30b7\u30e7\u30f3\u53f3\u3092\u914d\u7f6e

}

}
  public void settings() {  size(displayWidth, displayHeight); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Violin_System_Test" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
