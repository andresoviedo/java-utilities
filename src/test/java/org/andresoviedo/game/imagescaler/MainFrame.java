package org.andresoviedo.game.imagescaler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainFrame
    extends JFrame
    implements WindowListener , Runnable{
  final public static int IMAGE_WIDTH = 100;
  final public static int IMAGE_HEIGHT = 100;
  final public static int CHECK_FPS = 25;

  private ImagePanel imagePanel;
  private JComboBox scalingComboBox = new JComboBox(new String[] {"1x", "2x",
      "4x", "8x","16x"});
  private BufferedImage animatedImage;
  private JComponent animatedComponent;

  private Thread feedRunnable;
  private PaintRunnable paintRunnable;
  private LocalPaintRunnable localPaintRunnable;

  public MainFrame() {
    super("Image Scaler");
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    start();
  }

  void jbInit() throws Exception {
    Container contentPane = this.getContentPane();
    contentPane.setLayout(new BorderLayout());

    animatedImage = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT,
                                        BufferedImage.TYPE_INT_RGB);
      imagePanel = new ImagePanel(animatedImage);
      contentPane.add(imagePanel, BorderLayout.CENTER);


    scalingComboBox.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
          // We directly set the index of the combo box: 0 is no scaling, 1 is 2x, 2 is 4x and 3 is 8x
          imagePanel.setScalingFactor(2<<(scalingComboBox.getSelectedIndex()-1));
          pack();
        }
      });

      JPanel scalingPanel = new JPanel();
      scalingPanel.setLayout(new FlowLayout());
      scalingPanel.add(new JLabel("ms:"));
      scalingPanel.add(imagePanel.getFPSLabel());
      scalingPanel.add(new JLabel("Scaling:"));
      scalingPanel.add(scalingComboBox);
      contentPane.add(scalingPanel, BorderLayout.NORTH);

      animatedComponent = new AnimatedImage();
      contentPane.add(animatedComponent,BorderLayout.WEST);

    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    addWindowListener(this);

    pack();
  }

  public void start() {
    feedRunnable = new Thread(this);
    paintRunnable = new PaintRunnable();
    localPaintRunnable = new LocalPaintRunnable();

    feedRunnable.start();
  }

   public void run() {
     // Test
     Graphics2D g = (Graphics2D) animatedImage.getGraphics();
     int currentWidth = IMAGE_WIDTH;
     int currentHeight = IMAGE_HEIGHT;
     int velX = -2;
     int velY = -5;

     int checkCounter = 0;
     long totalTime = 0;

     long currentTime;

     while (true) {
       currentTime = System.currentTimeMillis();

       currentWidth += velX;
       if (currentWidth < 10) {
         currentWidth = 10;
         velX = -velX;
       }
       else if (currentWidth > IMAGE_WIDTH) {
         currentWidth = IMAGE_WIDTH;
         velX = -velX;
       }

       currentHeight += velY;
       if (currentHeight < 10) {
         currentHeight = 10;
         velY = -velY;
       }
       else if (currentHeight > IMAGE_HEIGHT) {
         currentHeight = IMAGE_HEIGHT;
         velY = -velY;
       }

       g.setColor(Color.BLACK);
       g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);

       /*g.setColor(Color.GREEN);
                  g.fillRect(0, 0, IMAGE_WIDTH>>1, IMAGE_HEIGHT>>1);
                  g.setColor(Color.BLACK);
            g.fillRect((IMAGE_WIDTH - currentWidth) / 2, (IMAGE_HEIGHT - currentHeight) / 2,
                  1,1);*/

       g.setColor(Color.RED);
       g.fillArc( (IMAGE_WIDTH - currentWidth) / 2,
                 (IMAGE_HEIGHT - currentHeight) / 2,
                 currentWidth, currentHeight, 0, 360);

       /*Runnable paintRunnable = new Runnable() {
         public void run() {
           imagePanel.paintImmediately(0, 0, imagePanel.getWidth(), imagePanel.getHeight());
         }
                  };*/

       /*totalTime += (System.currentTimeMillis() - currentTime);
                  checkCounter++;*/

       currentTime = System.currentTimeMillis();

       try {
         //SwingUtilities.invokeLater(localPaintRunnable);
         SwingUtilities.invokeAndWait(paintRunnable);
       }
       catch (Exception ex) {
         ex.printStackTrace();
       }

       totalTime += (System.currentTimeMillis() - currentTime);
       checkCounter++;

       if (checkCounter > CHECK_FPS) {
         long average = totalTime / CHECK_FPS;
         imagePanel.getFPSLabel().setText("" + average);

         checkCounter = 0;
         totalTime = 0;
       }

       // Sleep a bit (note: this is to allow the user to see the drawing, this value CANNOT be touched to optimize the speed!!!!!)
       try {
         Thread.currentThread().sleep(40);
       }
       catch (InterruptedException iex) {
         iex.printStackTrace();
       }
     }
  }

  class PaintRunnable implements Runnable {
    //boolean goingDown = false;
    //boolean goindRight = false;

    public void run() {
      imagePanel.repaint();
    }
  };



  /*public void start() {
    Runnable feedRunnable = new Runnable() {
      public void run() {
        // Test
        Graphics2D g = (Graphics2D) animatedImage.getGraphics();
        int currentWidth = IMAGE_WIDTH;
        int currentHeight = IMAGE_HEIGHT;
        int velX = -2;
        int velY = -5;
        long lastCheckedTime = System.currentTimeMillis();
        int checkCounter = 0;

        while (true) {
          long currentTime = System.currentTimeMillis();

          currentWidth += velX;
          if (currentWidth < 10) {
            currentWidth = 10;
            velX = -velX;
          }
          else if (currentWidth > IMAGE_WIDTH) {
            currentWidth = IMAGE_WIDTH;
            velX = -velX;
          }

          currentHeight += velY;
          if (currentHeight < 10) {
            currentHeight = 10;
            velY = -velY;
          }
          else if (currentHeight > IMAGE_HEIGHT) {
            currentHeight = IMAGE_HEIGHT;
            velY = -velY;
          }

          g.setColor(Color.BLACK);
          g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
          g.setColor(Color.RED);
          g.fillArc((IMAGE_WIDTH - currentWidth) / 2, (IMAGE_HEIGHT - currentHeight) / 2,
                    currentWidth, currentHeight, 0, 360);

          Runnable paintRunnable = new Runnable() {
            public void run() {
              imagePanel.paintImmediately(0, 0, imagePanel.getWidth(), imagePanel.getHeight());
            }
          };
          try {
            SwingUtilities.invokeAndWait(paintRunnable);
          }
          catch (Exception ex) {
            ex.printStackTrace();
          }

          long diffTime = System.currentTimeMillis() - currentTime;

            // Sleep a bit (note: this is to allow the user to see the drawing, this value CANNOT be touched to optimize the speed!!!!!)
            try {
              Thread.currentThread().sleep(40);
            }
            catch (InterruptedException iex) {
              iex.printStackTrace();
            }

          checkCounter++;
          if (checkCounter > CHECK_FPS) {
            imagePanel.getFPSLabel().setText("" + diffTime);
            checkCounter = 0;
          }
        }
      }
    };

    new Thread(feedRunnable).start();
  }*/

  // Interface WindowListener
  public void windowIconified(WindowEvent e) {}

  public void windowDeiconified(WindowEvent e) {}

  public void windowDeactivated(WindowEvent e) {}

  public void windowActivated(WindowEvent e) {}

  public void windowClosing(WindowEvent e) {}

  public void windowOpened(WindowEvent e) {}

  public void windowClosed(WindowEvent e) {
    // We closed the main window, so we exit!
    System.exit(0);
  }

  class LocalPaintRunnable implements Runnable{
    public void run(){
      animatedComponent.repaint();
    }
  }

  class AnimatedImage
      extends JComponent {
    public Dimension getPreferredSize() {
      return new Dimension(animatedImage.getWidth(), animatedImage.getHeight());
    }

    public void paint(Graphics g) {
      super.paint(g);
      //BufferedImage scaledImage = scaleImage();
      g.drawImage(animatedImage, 0, 0, null);
   }
  }
}
