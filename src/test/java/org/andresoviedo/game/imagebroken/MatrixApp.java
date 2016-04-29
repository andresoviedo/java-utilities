package org.andresoviedo.game.imagebroken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class MatrixApp extends JFrame {
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabelFile = new JLabel();
  JTextField jTextFieldFile = new JTextField();
  JButton jButtonBrowseFile = new JButton();

  private MatrixImage matrixImage;
  //private MemoryImageSource image;

  private BufferedImage source_image;
  private int[][][] source_data;

  private BufferedImage target_image;
  private int[][][] target_data;
  private int[] target_order;

  private boolean move = false;
  private int x=0,y=0;
  private int x_=4,y_=4;

  private int[] selectedData = new int[20*20];

  public static void main(String[] args) {
    MatrixApp matrixApp = new MatrixApp();
    matrixApp.show();
    matrixApp.pack();
  }

  public MatrixApp(){
    try {
      loadDefaultImage();
      jbInit();
      initializeListeners();
      initializeData();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout2);
    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setResizable(true);
    jPanel1.setLayout(gridBagLayout1);
    jLabelFile.setPreferredSize(new Dimension(50, 15));
    jLabelFile.setHorizontalAlignment(SwingConstants.LEADING);
    jLabelFile.setLabelFor(jTextFieldFile);
    jLabelFile.setText("Image");
    jButtonBrowseFile.setPreferredSize(new Dimension(100, 25));
    jButtonBrowseFile.setText("Browse");
    jTextFieldFile.setPreferredSize(new Dimension(200, 21));
    jTextFieldFile.setText("");
    matrixImage = new MatrixImage(source_image);
    jPanel1.setBackground(Color.orange);
    jPanel1.add(jLabelFile,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(jTextFieldFile,    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
    jPanel1.add(jButtonBrowseFile,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    MatrixImage o = new MatrixImage(source_image);
    jPanel1.add(o,      new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    jPanel1.add(matrixImage,            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
    this.getContentPane().add(jPanel1, BorderLayout.NORTH);
  }

  private void loadDefaultImage(){
    source_image = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
    Graphics g = source_image.getGraphics();
    g.setColor(Color.BLACK);
    g.fillRect(0,0,100,100);
    g.setColor(Color.MAGENTA);
    g.fillArc(24,24,50,50,0,270);

    target_image = new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB);
  }

  private void initializeListeners(){
    /*jButtonBrowseFile.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent e){
        JFileChooser fc = new JFileChooser();
        File f = fc.getSelectedFile();
      }
    });*/
    jTextFieldFile.getInputMap().put(KeyStroke.getKeyStroke('w'),null);
    jTextFieldFile.getInputMap().put(KeyStroke.getKeyStroke('a'),null);
    jTextFieldFile.getInputMap().put(KeyStroke.getKeyStroke('s'),null);
    jTextFieldFile.getInputMap().put(KeyStroke.getKeyStroke('d'),null);

    jTextFieldFile.addKeyListener(new KeyAdapter() {
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'w') {
          System.out.println("Key w typed");
          moveBlock(x,y-1);
        }
        else if (e.getKeyChar() == 'a') {
          System.out.println("Key a typed");
          moveBlock(x-1,y);
        }
        else if (e.getKeyChar() == 's') {
          System.out.println("Key s typed");
          moveBlock(x,y+1);
        }
        else if (e.getKeyChar() == 'd') {
          System.out.println("Key d typed");
          moveBlock(x+1,y);
        }
        else if (e.getKeyChar() == 'x') {
          System.out.println("Key x typed");
            if (x != x_ && y != y_) {
              highLightBlock2(x, y, move ? Color.RED : Color.WHITE);
              move = !move;
              repaint();
            }
        }
      }
    });
  }

  private void initializeData(){
    source_data = new int[5][5][20*20];
    for (int i=0; i<5; i++){
      for (int j=0; j<5; j++){
        source_image.getRGB(i*20,j*20,20,20,source_data[i][j],0,20);
      }
    }

    Vector allPos = new Vector();
    for (int i=0; i<24; i++){ // 25 reserved for free block
      allPos.add(new Integer(i));
    }

    Random r = new Random();
    target_data = new int[5][5][20*20];
    target_order = new int[5*5];
    for (int i=0; i<5; i++){
      for (int j=0; j<5; j++){
        int pos = r.nextInt(allPos.size());
        pos = pos % allPos.size();
        Integer tpos = (Integer)allPos.remove(pos);

        System.out.println("pos["+tpos.intValue()+"]");

        target_order[i*5+j] = pos;
        target_data[(tpos.intValue()/5)][(tpos.intValue()%5)] = source_data[i][j];

        if (allPos.isEmpty()) break;
      }
      if (allPos.isEmpty()) break;
    }

    for (int i=0; i<5; i++){
      for (int j=0; j<5; j++){
        target_image.setRGB(i*20,j*20,20,20,target_data[i][j],0,20);
      }
    }

    target_image.getGraphics().setColor(Color.CYAN);
    for (int i=0; i<5; i++){
      for (int j=0; j<5; j++){
        target_image.getGraphics().drawString("["+i+","+j+"]",i*20+1,j*20+19);
      }
    }

    drawFree(x_,y_);

    highLightBlock(0,0);

    matrixImage.setImage(target_image);
    repaint();
  }

  class PaintRunnable implements Runnable{
    public void run(){
      repaint();
    }
  }

  private void moveBlock(int x2, int y2){
    if (x2 >=0 && x2 <5 && y2 >= 0 && y2 < 5){
      System.out.println("-------------");
      System.out.println("x:"+x+" y:"+y);
      System.out.println("x_:"+x_+" y_:"+y_);
      if (move){
        if (x2 == x_ && y2 == y_) {
          System.out.println("hello");
          // switch order
          target_order[x2 * 5 + y2] = target_order[x * 5 + y];
          target_order[x * 5 + y] = 24;

          // switch data
          int[] data = new int[400];
          target_image.setRGB(x2 * 20, y2 * 20, 20, 20, selectedData, 0, 20);
          drawFree(x, y);

          x_ = x;
          y_ = y;

          highLightBlock(x2, y2);
        }
        else{
          // invalid move
          return;
        }
      }
      else{
        highLightBlock(x, y, x2, y2);
      }
      x = x2;
      y = y2;

      System.out.println("x:" + x + " y:" + y);
      System.out.println("x_:" + x_ + " y_:" + y_);
      repaint();
    }
  }

  private void drawFree(int x,int y){
    System.out.println("Drawing x: "+x+",y: "+y);
    target_image.getGraphics().setColor(Color.red);
    target_image.getGraphics().fillRect(x*20,y*20,20,20);
    matrixImage.repaint();
  }

  private void highLightBlock(int x_old,int y_old,int x_new, int y_new){
    target_image.setRGB(20*x_old,20*y_old,20,20,selectedData,0,20);
    highLightBlock(x_new,y_new);
  }

  private void highLightBlock(int x_new, int y_new){
    highLightBlock(x_new,y_new,Color.RED);
  }

  private void highLightBlock(int x_new, int y_new, Color color){
    target_image.getRGB(20*x_new,20*y_new,20,20,selectedData,0,20);
    target_image.getGraphics().setColor(color);
    target_image.getGraphics().drawRect(x_new*20,y_new*20,19,19);
    matrixImage.repaint();
  }

  private void highLightBlock2(int x_new, int y_new, Color color){
    target_image.getGraphics().setPaintMode();
    target_image.getGraphics().setColor(color);
    target_image.getGraphics().fillArc(x_new*20,y_new*20,19,19,0,360);
    matrixImage.repaint();
  }

  private static boolean hasValue(int[] array, int value){
    for (int i=0; i<array.length; i++){
      if (array[i] == value){
        return true;
      }
    }
    return false;
  }
}
