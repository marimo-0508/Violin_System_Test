class Layout{
  public void tone_color() {//右上の色の見本を表示
    for (int i = 0; i < col.length; i++) {
      col[i].color_rect();
      rect(1000+i*30, 20, 20, 20);
    }
    fill(255);
    textSize(20);
    text("low tone", 900, 20, 100, 40);//文字表示
    text("high tone", 1670, 20, 100, 40);//文字表示
  }
  
  public void note_recorder() {
    if ((note_x>=0) && (note_y>=0)) {//音が入力されていることが前提
      //弾いている段のrectを表示
      for (int i=0; i<note_x; i++) {//現在演奏している段落のみの色表示
        if(note[note_y][i].get_played_note_size()!=0) col[note[note_y][i].getNote(0)].color_rect();//最初の音のずれの色を採用
        rect(note[note_y][i].getX(), 360+212*note_y, 20, 20);//音のずれを表示
      }
      
      //弾いている段以前のrectを表示
      for (int j = 0; j <= note_y-1; j++) {
        for (int i = 0; i < 14; i++) {//現在演奏しているよりも前の色表示
          if(note[j][i].get_played_note_size()!=0) col[note[j][i].getNote(0)].color_rect();//最初の音のずれの色を採用
          rect(note[note_y][i].getX(), 360+212*j, 20, 20);//音のずれを表示
        }
      }
      
    }
  }
}