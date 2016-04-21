class ScoreNote {
  private int x;
  private int miss_count = 0;
  private int number;
  private ArrayList<Integer> played_note = new ArrayList();

  ScoreNote(int x, int number) {
    this.x = x;
    this.number = number;
  }

  public int getX() {
    return this.x;
  }
  public int get_miss_count() {
    return this.miss_count;
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
  
  public int get_played_note_size(){
    return this.played_note.size();
  }

  void blue_triangle() {//水色▼の位置と形を管理  
    noStroke();
    fill(186, 233, 255);
    textSize(25);
    text("▼", x-20, 250+173*note_y, 40, 40);
    text("▼", 210, 38, 40, 40);
  }

  void real_time_color() {//リアルタイムで変化する音の色を表示
    if (this.played_note.size()>=1) {
      col[this.played_note.get(this.played_note.size()-1)].color_rect();
      rect(200, 160, 30, 30);
    }
  }

  void judgement() {
    if ((note_x>=0) && (note_y>=0)) {//音が入力されていることが前提
      for (int i=0; i<note_x; i++) {//現在演奏している段落のみの色表示
        if (note[note_y][i].miss_count>=1) {
          fill(255);
          textSize(25);
          text("×", note[note_y][i].getX(), 250+212*note_y, 40, 40);
        }
      }
      for (int j = 0; j <= note_y-1; j++) {
        for (int i = 0; i < 14; i++) {//現在演奏しているよりも前の色表示
          if (note[j][i].miss_count>=1) {
            fill(255);
            textSize(25);
            text("×", note[note_y][i].getX(), 250+212*j, 40, 40);
          }
        }
      }
    }
  }

  void sum_false() {
    int sum = 0;
    for (int i = 0; i < note.length; i++) {
      for (int j=0; j < note[i].length-1; j++) {
        if (note[i][j].miss_count == 1) {
          sum++;
        }
      }
    }
    fill(255);
    textSize(25);
    text(sum+"/32", 1000, 955);
  }

  void move_score() {
    if ((note_x>=0) &&(note_y>=0)) {
      if ((move == true)) {
        moving+=0.9;
      }
      if (moving >= 12.58) {
        moving = 0.0;
        move = false;
      }
    }
    score_top = score_top - moving;

    image(part_score, score_top, 50, 3778, 148);//移動する楽譜の第1連
    noStroke();
    fill(0);
    rect(0, 40, 70, 218);
    rect(700, 40, displayWidth-700, 218);
    image(left_grad, 70, 40, 88, 178); //グラデーション左を配置
    image(right_grad, 700, 40, 88, 178);//グラデーション右を配置
  }
}