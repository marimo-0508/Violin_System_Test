import themidibus.*; //Import the library
import javax.sound.midi.MidiMessage; //Import the MidiMessage classes http://java.sun.com/j2se/1.5.0/docs/api/javax/sound/midi/MidiMessage.html
import javax.sound.midi.SysexMessage;
import javax.sound.midi.ShortMessage;
import processing.video.*;  //ビデオライブラリをインポート
import processing.opengl.*;

//楽譜
PImage score, part_score, left_grad, right_grad;

//主に楽譜の音を管理する用
ScoreNote[][]note = new ScoreNote[4][8];
int note_y, note_x = 0;
boolean move = false;
float score_top = 90.0;
float moving = 0.0;

//時刻
boolean flag = false;

//txtファイル出力に必要な配列
ArrayList<String> note_number = new ArrayList<String>();
ArrayList<String> now_number = new ArrayList<String>();
ArrayList<String> count = new ArrayList<String>();
ArrayList<String> note_velocity = new ArrayList<String>();
ArrayList<String> result = new ArrayList<String>();
ArrayList<String> pitche_bend = new ArrayList<String>();
float mill;//時間用
int note_num;//弾くべき音番号
int now_num = 0;//今現在弾いている音
int note_vel = 0;//ベロシティ

//色を管理する用
Color []col = new Color[22];//色を22色の配列で管理
int []r = { 0,  38,  65, 112,  38, 131, 160,  82,   9,  29,  36,
           87, 111, 211, 248, 245, 244, 243, 246, 238, 234, 255
           };
int []g = {  0,  92, 131, 160, 187, 206, 213, 186, 127, 117, 155, 
	       175, 189, 227, 229, 211, 161, 162, 189, 129,  93, 0
           };
int []b = {255, 170, 197, 214, 238, 237, 205, 155, 93, 57, 58, 
           79, 105, 142, 141,  60,  55, 134, 187, 127, 87, 0
	       };

//webカメラ用
Capture video;  //Capture型の変数videoを宣言

//midi用
MidiBus myBus; //The MidiBus
int pitchbend, notebus_different=0;//note_yは段落数、note_xで段落内の何番目を弾いているか管理

int channel = 0;
int pitch = 64;
int velocity = 127;
int status_byte = 0xA0; // For instance let us send aftertouch
int channel_byte = 0; // On channel 0 again
int first_byte = 64; // The same note;
int second_byte = 80; // But with less velocity

ArrayList<ScoreNote> played_note;//pitchbendで得たどの程度ずれているかを入れるための配列を用意

void setup() {
  size(displayWidth, displayHeight);

  //midibus用
  MidiBus.list(); // List all available Midi devices on STDOUT. This will show each device's index and name.
  myBus = new MidiBus(this, 0, 0); // Create a new MidiBus object

 //カメラの準備
  video = new Capture(this, 640, 540,"USB_Camera");  //カメラからのキャプチャーをおこなうための変数を設定
  video.start();  //Processing ver.2.0以上はこのコードが必要

 //画像を用意
  score = loadImage("star.png");
  part_score = loadImage("part_star.png");
  left_grad = loadImage("left_grad.png"); //左用グラデを用意
  right_grad = loadImage("right_grad.png"); //右用グラデを用意

  //col[number] = new Color(R, G, B)
  for(int i = 0; i < 22 ; i++){
  	col[i] = new Color(r[i], g[i], b[i]);
  }

//note[note_y][note_x] = new Note(all_score_PositionX, ×の初期設定, NoteName);
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

  //midibusを管理
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

void draw() {
  	background(0);

  	//カメラの調整と表示
    video.read();

    //カメラ映像を回転させて、演奏者の見ているものと同じ映像にする
    pushMatrix(); 
    translate(100, 900);
    rotate(radians(-90));
    image(video, 10, 10, 640, 540);
    popMatrix();

    note[note_y][note_x].move_score();//楽譜の一段落のうち弾いている箇所のみ切り抜き
    image(score, 800, 100, 1142, 681);

    //楽譜の水色▼を表示
    note[note_y][note_x].blue_triangle(); 

    //ずれ別の色の見本を表示
    note[note_y][note_x].color_example();

    //その場で弾いた音のずれを表示
    note[note_y][note_x].real_time_color();

    //音の記録（色と×の表示）
    note[note_y][note_x].note_recorder();//音のずれ
    note[note_y][note_x].judgement();//×をつける

    //ミスの回数
    note[note_y][note_x].sum_false();//ミスのカウントとテキストを表示
  }

//midibusを管理している
void rawMidi(byte[] data) { // You can also use rawMidi(byte[] data, String bus_name) 
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
 
//webカメラを更新
void captureEvent(Capture video) {
  video.read();
}
  void mouseClicked(){
    println("mouseX:" + mouseX + "," + "mouseY:" + mouseY);
  }