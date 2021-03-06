package edb.display;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class Image extends Display {

  private static Hashtable<String, BufferedImage> cache = new Hashtable<String, BufferedImage>();

  int                                             xOffset;
  int                                             yOffset;
  String                                          file;
  int                                             width;
  int                                             height;
  BufferedImage                                   image;

  public Image(int xOffset, int yOffset, int width, int height, String file) {
    super();
    this.xOffset = xOffset;
    this.yOffset = yOffset;
    this.file = file;
    this.width = width;
    this.height = height;
    loadImage();
  }

  private void loadImage() {
    try {
      if (cache.containsKey(file))
        image = cache.get(file);
      else {
        image = ImageIO.read(new File(file));
        cache.put(file, image);
      }
    } catch (IOException e) {
    }
  }

  public void draw(int x, int y, int width, int height, Graphics g) {
    g.drawImage(image, x + xOffset, y + yOffset, width, height, null);
  }

  public int minWidth(Graphics g) {
    return width + xOffset;
  }

  public int minHeight(Graphics g) {
    return height + yOffset;
  }

}
