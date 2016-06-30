package org.andresoviedo.game.imagescaler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.Arrays;

public class ImagePanel extends JComponent{

  Toolkit toolKit = Toolkit.getDefaultToolkit();

  private JLabel fpsLabel;
  private int panelWidth;
  private int panelHeight;

  private BufferedImage targetImage = null;
  private int[] source_data_bordered;
  private int realWidth;
  private int realHeight;

  private MemoryImageSource imageBuffer;
  private Image imageMemoryScaled;
  private ColorModel colorModel;

  private BufferedImage scaledImage;
  private int scaledImageWidth;
  private int scaledImageHeight;
  int[] scaledTempData;
  int[] scaledData;

  private int scalingFactor=0;

  public ImagePanel(BufferedImage targetImage) {
    this.targetImage = targetImage;

    realWidth = targetImage.getWidth();
    realHeight = targetImage.getHeight();

    this.panelWidth = realWidth;
    this.panelHeight = realHeight;

    fpsLabel = new JLabel("0");
    setScalingFactor(1);
  }

  public ImagePanel(int realWidth, int realHeight) {
    this(new BufferedImage(realWidth, realHeight, BufferedImage.TYPE_INT_RGB));
  }

  public synchronized void setScalingFactor(int scalingFactor) {
    if (scalingFactor != this.scalingFactor) {
      // Scaling has changed
      this.scalingFactor = scalingFactor;
      System.out.println("Changing scaling factor to: "+scalingFactor);

      if (scalingFactor > 0){
        System.out.println("Creating border buffer...");
        panelWidth = (realWidth*scalingFactor);
        panelHeight = (realHeight*scalingFactor);

        if (scalingFactor > 1){
          int newSize = (realWidth * (scalingFactor >> 1) + 2) *
              (realWidth * (scalingFactor >> 1) + 2);
          source_data_bordered = new int[newSize];
          Arrays.fill(source_data_bordered,0,newSize,0);
        }

        System.out.println("Creating image buffer...");
        scaledImageWidth = realWidth*scalingFactor;
        scaledImageHeight = realHeight*scalingFactor;
        scaledImage = new BufferedImage(scaledImageWidth,scaledImageHeight,
                                        BufferedImage.TYPE_INT_RGB);
        scaledTempData = new int[scaledImageWidth*scaledImageHeight];
        scaledData = new int[scaledImageWidth*scaledImageHeight];

        // Image buffer
        colorModel = targetImage.getColorModel();
        imageBuffer = new MemoryImageSource(scaledImageWidth,scaledImageHeight,
                                            colorModel,scaledData,0,scaledImageWidth);
        imageBuffer.setAnimated(true);
        imageBuffer.setFullBufferUpdates(true);
        imageMemoryScaled = toolKit.createImage(imageBuffer);
      }
    }
  }

  public int getScalingFactor() {
    return scalingFactor;
  }

  public JLabel getFPSLabel() {
    return fpsLabel;
  }

  public Dimension getPreferredSize() {
    return new Dimension(panelWidth + 1, panelHeight + 1);
  }

  public void paint(Graphics g) {
    /*g.drawImage(targetImage.getScaledInstance(realWidth*scalingFactor,
                                              realHeight*scalingFactor,
                                              BufferedImage.SCALE_FAST),0,0,null);*/

    //BufferedImage scaledImage = scaleImage();
    //BufferedImage scaledImage = scaleImageAndres();
    //Image scaledImage = getScaledImage();
    Image scaledImage = getScaledImage2();
    g.drawImage(scaledImage, 0, 0, null);
  }

  public BufferedImage scaleImage() {
    BufferedImage targetImage = this.targetImage;
    for (int c = 1; c < scalingFactor; c=c<<1) {
      targetImage = scaleImage2x(targetImage);
    }
    return targetImage;
  }

  /*public synchronized BufferedImage scaleImageAndres() {
    if (scalingFactor > 1){
      long t1 = System.currentTimeMillis();
      //targetImage.getRaster().getDataElements(0,0,realWidth,realHeight,scaledTempData);
      targetImage.getRaster().getDataElements(0,0,realWidth,realHeight,scaledTempData);
      long t2 = System.currentTimeMillis();

      int currentWidth = realWidth;
      int currentHeight = realHeight;

      System.out.print("t1["+(t2-t1)+"]");

      boolean right = false;
      for (int currentFactor = 1; currentFactor < scalingFactor;
           (currentFactor=currentFactor<<1)) {
        long t3 = System.currentTimeMillis();
        if (right){
          scaleImage2xAndres(scaledData, currentWidth, currentHeight,
                             scaledTempData);
        }
        else{
          scaleImage2xAndres(scaledTempData, currentWidth, currentHeight,
                             scaledData);
        }
        long t4 = System.currentTimeMillis();
        currentWidth = currentWidth*2;
        currentHeight = currentHeight*2;
        right = !right;
        //long t5 = System.currentTimeMillis();
        //copyArray(scaledData,currentWidth,currentHeight,scaledTempData);
        //long t6 = System.currentTimeMillis();

        System.out.print(
          " t3["+(t4-t3)+"]");
      }
      long t7 = System.currentTimeMillis();
      if (right){
        scaledImage.getRaster().
            setDataElements(0,0,scaledImageWidth,scaledImageHeight,scaledData);
        /*scaledImage.setRGB(0, 0, scaledImageWidth, scaledImageHeight,
                           scaledData,
                           0, scaledImageWidth);*/
      /*}
      else{
        scaledImage.getRaster().
            setDataElements(0,0,scaledImageWidth,scaledImageHeight,scaledTempData);
        /*scaledImage.setRGB(0, 0, scaledImageWidth, scaledImageHeight,
                           scaledTempData,
                           0, scaledImageWidth);*/
      /*}
      long t8 = System.currentTimeMillis();
      System.out.print(
          " t7["+(t8-t7)+"]");
      System.out.print(
          " total["+(t8-t1)+"]\n");
      return scaledImage;
    }
    else{
      return targetImage;
    }
  }*/

 /* public synchronized Image getScaledImage() {
    if (scalingFactor > 1){
      //long t1 = System.currentTimeMillis();
      //targetImage.getRaster().getDataElements(0,0,realWidth,realHeight,scaledTempData);
      targetImage.getRaster().getDataElements(0,0,realWidth,realHeight,scaledTempData);
      //targetImage.getRaster().getDataElements(0,0,realWidth,realHeight,scaledTempData);
      //long t2 = System.currentTimeMillis();

      int currentWidth = realWidth;
      int currentHeight = realHeight;

      //System.out.print("t1["+(t2-t1)+"]");

      boolean right = false;
      for (int currentFactor = 1; currentFactor < scalingFactor;
           (currentFactor=currentFactor<<1)) {
        //long t3 = System.currentTimeMillis();
        if (right){
          scaleImage2xAndres(scaledData, currentWidth, currentHeight,
                             scaledTempData);
        }
        else{
          scaleImage2xAndres(scaledTempData, currentWidth, currentHeight,
                             scaledData);
        }
        //long t4 = System.currentTimeMillis();
        currentWidth = currentWidth*2;
        currentHeight = currentHeight*2;
        right = !right;
        //long t5 = System.currentTimeMillis();
        //copyArray(scaledData,currentWidth,currentHeight,scaledTempData);
        //long t6 = System.currentTimeMillis();

        //System.out.print(" t3["+(t4-t3)+"]");
      }
      //long t7 = System.currentTimeMillis();
      if (right){
        imageBuffer.newPixels(scaledData,colorModel,0,scaledImageWidth);
      }
      else{
        imageBuffer.newPixels(scaledTempData,colorModel,0,scaledImageWidth);
      }
      //imageBuffer.newPixels();
      //long t8 = System.currentTimeMillis();
      /*System.out.print(
          " t7["+(t8-t7)+"]");
      System.out.print(
          " total["+(t8-t1)+"]\n");*/
      /*return imageMemoryScaled;
    }
    else{
      return targetImage;
    }
  }*/

  public synchronized Image getScaledImage2() {
    if (scalingFactor > 1){
      targetImage.getRaster().getDataElements(0, 0, realWidth, realHeight,
                                              scaledTempData);
      int currentWidth = realWidth;
      int currentHeight = realHeight;
      int currentFactor = 1;

      boolean right = false;
      do{
        if (right){
          scaleImage2xAndres(scaledData, currentWidth, currentHeight,
                             scaledTempData);
        }
        else{
          scaleImage2xAndres(scaledTempData, currentWidth, currentHeight,
                             scaledData);
        }
        currentFactor=currentFactor<<1;
        currentWidth = currentWidth<<1;
        currentHeight = currentHeight<<1;
        right = !right;
      }while(currentFactor < scalingFactor);

      if (right) {
        imageBuffer.newPixels(scaledData, colorModel, 0, scaledImageWidth);
      }
      else {
        imageBuffer.newPixels(scaledTempData, colorModel, 0, scaledImageWidth);
      }
      return imageMemoryScaled;
    }
    else{
      return targetImage;
    }
  }


  private BufferedImage scaleImage2x(BufferedImage targetImage) {
    long now = System.currentTimeMillis();
    int currentWidth = targetImage.getWidth(null);
    int currentHeight = targetImage.getHeight(null);
    int scaledWidth = currentWidth << 1;
    int scaledHeight = currentHeight << 1;
    int[] originalData = new int[currentWidth * currentHeight];
    int[] destinationData = new int[scaledWidth * scaledHeight];

    targetImage.getRGB(0, 0, currentWidth, currentHeight, originalData, 0, currentWidth);

    // Scale2x algorithm (based on AdvanceMAME Scale2x, http://scale2x.sourceforge.net)
    int i1, i2;
    int srcofs;
    int dstofs;
    int x, y, e;
    int finalRowOffset = currentWidth * (currentHeight - 1);
    int destinationFinalRowOffset = ((currentWidth * currentHeight) << 2) - (currentWidth << 2);
    for (int c=0; c<currentWidth; c++) {
      i1 = (c << 1);
      i2 = i1 + scaledWidth;
      e = originalData[c];
      destinationData[i1] = e;
      destinationData[i1 + 1] = e;
      destinationData[i2] = e;
      destinationData[i2 + 1] = e;

      i1 += destinationFinalRowOffset;
      i2 += destinationFinalRowOffset;
      e = originalData[c+finalRowOffset];
      destinationData[i1] = e;
      destinationData[i1 + 1] = e;
      destinationData[i2] = e;
      destinationData[i2 + 1] = e;
    }

    srcofs = currentWidth + 1;
    dstofs = (currentWidth << 2);

    for (y = 1; y < currentHeight - 1; y++) {
      i1 = dstofs;
      i2 = i1 + (currentWidth << 1);

      destinationData[i1] = originalData[srcofs - 1];
      destinationData[i1 + 1] = originalData[srcofs - 1];
      destinationData[i2] = originalData[srcofs - 1];
      destinationData[i2 + 1] = originalData[srcofs - 1];

      for (x = 1; x < currentWidth - 1; x++) {
        int E0, E1, E2, E3;
        int A, B, C, D, E, F, G, H, I;

        A = originalData[srcofs - currentWidth - 1];
        B = originalData[srcofs - currentWidth];
        C = originalData[srcofs - currentWidth + 1];
        D = originalData[srcofs - 1];
        E = originalData[srcofs];
        F = originalData[srcofs + 1];
        G = originalData[srcofs + currentWidth - 1];
        H = originalData[srcofs + currentWidth];
        I = originalData[srcofs + currentWidth + 1];

        //
        //	ABC
        //	DEF
        //	GHI

        //	E0E1
        //	E2E3
        //

        E0 = D == B && B != F && D != H ? D : E;
        E1 = B == F && B != D && F != H ? F : E;
        E2 = D == H && D != B && H != F ? D : E;
        E3 = H == F && D != H && B != F ? F : E;

        i1 = dstofs + (x << 1);
        i2 = i1 + scaledWidth;
        destinationData[i1] = E0;
        destinationData[i1 + 1] = E1;
        destinationData[i2] = E2;
        destinationData[i2 + 1] = E3;

        srcofs++;
      }

      // Row last pixel (just scale standard)
      i1 = dstofs + (currentWidth << 1) - 2;
      i2 = i1 + (currentWidth << 1);

      destinationData[i1] = originalData[srcofs];
      destinationData[i1 + 1] = originalData[srcofs];
      destinationData[i2] = originalData[srcofs];
      destinationData[i2 + 1] = originalData[srcofs];

      dstofs += (currentWidth << 2);


      srcofs += 2;
    }

    long first = System.currentTimeMillis();


    // Generate new image
    BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
    scaledImage.setRGB(0, 0, scaledWidth, scaledHeight, destinationData, 0, scaledWidth);

    long second = System.currentTimeMillis();

    /*System.out.print(", one  ["+(first-now)+"]");
    System.out.print(", third ["+(second-first)+"]\n\n");*/

    return scaledImage;
  }

  /*private void scaleImage2xAndres(int[] originalData,
                                  int currentWidth, int currentHeight,
                                  int[] targetData) {

    int scaledWidth = currentWidth * 2 ;
    int scaledHeight = currentHeight * 2 ;

    long now = System.currentTimeMillis();

    addBorder(originalData,currentWidth,currentHeight,originalDataBordered);

    int h = currentHeight+2;
    int w = currentWidth+2;

    int src_index = w+1;

    int dest_left_top_index_1 = 0;
    int dest_left_top_index_2 = scaledWidth;

    int A, B, C, D, E, F, G, H, I;

    int E0, E1, E2, E3;

    // Scale2x algorithm (based on AdvanceMAME Scale2x, http://scale2x.sourceforge.net)
    for(int row = 0; row < currentHeight; row++){
      for (int col = 0; col < currentWidth; col++){

        //A = completeData[src_index - currentWidth - 1];
        B = originalDataBordered[src_index - w];
        C = originalDataBordered[src_index - w + 1];
        D = originalDataBordered[src_index - 1];
        E = originalDataBordered[src_index];
        F = originalDataBordered[src_index + 1];
        G = originalDataBordered[src_index + w - 1];
        H = originalDataBordered[src_index + w];
        I = originalDataBordered[src_index + w + 1];


        if (B != H && D != F) {
          E0 = D == B ? D : E;
          E1 = B == F ? F : E;
          E2 = D == H ? D : E;
          E3 = H == F ? F : E;
        }
        else {
          E0 = E;
          E1 = E;
          E2 = E;
          E3 = E;
        }


        targetData[dest_left_top_index_1++] = E0;
        targetData[dest_left_top_index_1++] = E1;
        targetData[dest_left_top_index_2++] = E2;
        targetData[dest_left_top_index_2++] = E3;

        src_index++;
      }
      src_index+=2;

      dest_left_top_index_1+=scaledWidth;
      dest_left_top_index_2+=scaledWidth;
    }
  }*/

  private void scaleImage2xAndres(int[] sourceData,
                                  int currentWidth, int currentHeight,
                                  int[] targetData) {

    long t1 = System.currentTimeMillis();

    addBorder(sourceData,currentWidth,currentHeight,source_data_bordered);

    long t2 = System.currentTimeMillis();

    // source
    int source_scansize = currentWidth+2;
    int source_offset = source_scansize+1;
    int source_offset_temp;

    // target
    int target_scansize = currentWidth <<1 ;
    int offset_target_1 = 0;
    int offset_target_2 = target_scansize;

    int A, B, C, D, E, F, G, H, I;

    int E0, E1, E2, E3;

    long t3 = System.currentTimeMillis();

    // Scale2x algorithm (based on AdvanceMAME Scale2x, http://scale2x.sourceforge.net)
    for(int row = 0; row < currentHeight; row++,source_offset+=2){
      for (int col = 0; col < currentWidth; col++,source_offset++){
        B = source_data_bordered[source_offset - source_scansize];
        D = source_data_bordered[source_offset - 1];
        E = source_data_bordered[source_offset];
        F = source_data_bordered[source_offset + 1];
        H = source_data_bordered[source_offset + source_scansize];

        if (B != H && D != F) {
          E0 = D == B ? D : E;
          E1 = B == F ? F : E;
          E2 = D == H ? D : E;
          E3 = H == F ? F : E;
        }
        else {
          E0 = E;
          E1 = E;
          E2 = E;
          E3 = E;
        }

        //long t3_1_3 = System.currentTimeMillis();

        targetData[offset_target_1++] = E0;
        targetData[offset_target_1++] = E1;
        targetData[offset_target_2++] = E2;
        targetData[offset_target_2++] = E3;

        //long t3_1_4 = System.currentTimeMillis();

        //t3_1_1_total+=t3_1_2-t3_1_1;
        //t3_1_2_total+=t3_1_3-t3_1_2;
        //t3_1_3_total+=t3_1_4-t3_1_3;
      }
      offset_target_1+=target_scansize;
      offset_target_2+=target_scansize;
    }

    long t4 = System.currentTimeMillis();
    /*System.out.print(
        "-- t1["+(t2-t1)+"]"+
        " t2["+(t3-t2)+"]"+
        " t3["+(t4-t3)+"]"+
        " t3_1["+(t3_1-t3)+"]"+
        " t3_1_1["+(t3_1_1_total)+"]"+
        " t3_1_2["+(t3_1_2_total)+"]"+
        " t3_1_3["+(t3_1_3_total)+"]"+
        " t3_2["+(t3_2-t3_1)+"]"+
        " t3_2["+(t3_3-t3_2)+"]--"
        );*/
    /*System.out.println(
        " t3_1_1["+(t3_1_1_total)+"]"+
        " t3_1_2["+(t3_1_2_total)+"]"+
        " t3_1_3["+(t3_1_3_total)+"]");*/

  }



  private static void addBorder(int[] array, int width, int height,int target[]){
    int source_offset = 0;
    int stride = width+2;
    int target_offset = stride;

    // Fill first row
    for (; target_offset < stride; target_offset++){
      target[target_offset] = 0;
    }

    // Fill area
    for (int row = 0; row < height; row++) {
      target[target_offset++] = 0;
      for (int col = 0; col < width; col++) { // primera linea
        target[target_offset++] = array[source_offset++];
      }
      target[target_offset++] = 0;
    }

    // Fill last row
    for (; target_offset < (stride<<1); target_offset++){
      target[target_offset] = 0;
    }
  }

  /*private static void copyArray(int[] src, int width, int height, int[] target){
    int index = 0;
    for (int i=0; i<height; i++){
      for (int j=0; j<width; j++){
        target[index] = src[index];
        index++;
      }
    }
  }*/

  /*private static void copyArray(int[] src, int size, int[] target){
    for (int i=0; i<size; i++){
      target[i] = src[i];
    }
  }*/

  /*private static int[] addBorder(int[] array, int width, int height){
    int newWidth = width+2;
    int newHeight = height+2;
    int newSize = newWidth*newHeight;

    int[] ret = new int[newSize];

    for (int index = 0; index < newSize ; index++){
      ret[index] = 0;
    }

    int src_index = 0;
    int index = newWidth+1;
    for (int row = 0; row < height; row++) {
      for (int col = 0; col < width; col++) { // primera linea
        ret[index++] = array[src_index++];
      }
      index+=2;
    }
    return ret;
  }*/

  /*public static void main(String[] args){
    test2();
  }

  private static void test1(){
    System.out.println("hola");
    int[] test = new int[16];
    for (int i=0; i<16; i++){
      test[i] = 1;
    }
    printTable(test,4,4);
    int[] borderedArray = addBorder(test,4,4);
    printTable(borderedArray,6,6);
  }

  private static void test2(){
    System.out.println("hola");
    int[] test = new int[(100*4)*(100*4)];  // simulated scaled 4X

    //printTable(test,200,200);

    long now = System.currentTimeMillis();
    int[] borderedArray = addBorder(test,100*4,100*4);
    //printTable(borderedArray,6,6);
    System.out.println("took["+(System.currentTimeMillis()-now)+"]");
  }

  private static void printTable(int[] array, int width, int height){
    for (int i=0; i<width; i++){
      for (int j=0; j<width; j++){
        System.out.print("["+array[(i*width+j)]+"] ");
      }
      System.out.print("\n");
    }
  }*/

/*
  public synchronized Image getScaledImage() {
    if (scalingFactor > 1){
      long t1 = System.currentTimeMillis();
      // get source data bordered
      Arrays.fill(scaledTempData,0,scaledTempData.length,0);
      targetImage.getRGB(0,0,realWidth,realHeight,scaledTempData,realWidth+2+1,realWidth+2);
      long t2 = System.currentTimeMillis();

      int currentWidth = realWidth;
      int currentHeight = realHeight;

      System.out.print("t1["+(t2-t1)+"]");

      boolean right = false;
      for (int currentFactor = 1; currentFactor < scalingFactor;
           (currentFactor=currentFactor<<1)) {
        long t3 = System.currentTimeMillis();
        if (right){
          //Arrays.fill(scaledTempData,0,scaledTempData.length,0);
          scaleImage2xAndres(scaledData, currentWidth, currentHeight,
                             scaledTempData);
        }
        else{
          //Arrays.fill(scaledData,0,scaledData.length,0);
          scaleImage2xAndres(scaledTempData, currentWidth, currentHeight,
                             scaledData);
        }
        long t4 = System.currentTimeMillis();
        currentWidth = currentWidth*2;
        currentHeight = currentHeight*2;
        right = !right;
        //long t5 = System.currentTimeMillis();
        //copyArray(scaledData,currentWidth,currentHeight,scaledTempData);
        //long t6 = System.currentTimeMillis();

        System.out.print(
          " t3["+(t4-t3)+"]");
      }
      long t7 = System.currentTimeMillis();
      if (right){
        imageBuffer.newPixels(scaledData,colorModel,scaledImageWidth+3,
                              scaledImageWidth+2);
      }
      else{
        imageBuffer.newPixels(scaledTempData,colorModel,scaledImageWidth+3,
                              scaledImageWidth+2);
      }
      long t8 = System.currentTimeMillis();
      System.out.print(
          " t7["+(t8-t7)+"]");
      System.out.print(
          " total["+(t8-t1)+"]\n");
      return imageMemoryScaled;
    }
    else{
      return targetImage;
    }
  }

  private void scaleImage2xAndres(int[] originalDataBordered,
                                  int currentWidth, int currentHeight,
                                  int[] targetDataBordered) {
    long now = System.currentTimeMillis();

    // source
    int scansize = currentWidth+2;
    int offset_source = scansize+1;

    // target
    int scansize_target = (currentWidth * 2)+2;
    int offset_target_1 = scansize_target + 1;
    int offset_target_2 = offset_target_1 + scansize_target;

    int A, B, C, D, E, F, G, H, I;

    int E0, E1, E2, E3;

    // Scale2x algorithm (based on AdvanceMAME Scale2x, http://scale2x.sourceforge.net)
    for(int row = 0; row < currentHeight; row++){
      for (int col = 0; col < currentWidth; col++){

        //A = completeData[src_index - currentWidth - 1];
        B = originalDataBordered[offset_source - scansize];
        C = originalDataBordered[offset_source - scansize + 1];
        D = originalDataBordered[offset_source - 1];
        E = originalDataBordered[offset_source];
        F = originalDataBordered[offset_source + 1];
        G = originalDataBordered[offset_source + scansize - 1];
        H = originalDataBordered[offset_source + scansize];
        I = originalDataBordered[offset_source + scansize + 1];


        if (B != H && D != F) {
          E0 = D == B ? D : E;
          E1 = B == F ? F : E;
          E2 = D == H ? D : E;
          E3 = H == F ? F : E;
        }
        else {
          E0 = E;
          E1 = E;
          E2 = E;
          E3 = E;
        }


        targetDataBordered[offset_target_1++] = E0;
        targetDataBordered[offset_target_1++] = E1;
        targetDataBordered[offset_target_2++] = E2;
        targetDataBordered[offset_target_2++] = E3;

        offset_source++;
      }
      offset_source+=2;

      offset_target_1+=scansize_target+2;
      offset_target_2+=scansize_target+2;
    }
  }*/
}
