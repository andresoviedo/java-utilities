package org.andresoviedo.game.imagebroken;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class MatrixImage extends JComponent {
  Image _img;
    MatrixImage(Image img){
      _img = img;
    }

    public void setImage(Image img){
      _img = img;
    }

    public Dimension getMaximumSize(){
      return getPreferredSize();
    }

    public Dimension getPreferredSize(){
      return new Dimension(100,100);
      /*if (_img != null){
        return new Dimension(119,120);
        //return new Dimension(_img.getWidth(null), _img.getHeight(null));
      }
      else{
        return new Dimension(119,120);
      }*/
    }

    public void paint(Graphics g){
      super.paint(g);
      if (_img != null){
        g.drawImage(_img, 0, 0, null);
      }
    }
}
