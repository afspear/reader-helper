package com.spear;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by aspear on 6/6/17.
 */
public class Graphics {
    Graphics2D graphics;
    BufferedImage bImageFromConvert;

    public Graphics(byte [] image) throws IOException {
        InputStream in = new ByteArrayInputStream(image);

            bImageFromConvert = ImageIO.read(in);

            graphics = bImageFromConvert.createGraphics();


        float alpha = 0.6f;
        Color color = new Color(1, 0, 0, alpha); //Red
        graphics.setPaint(color);
            graphics.setStroke(new BasicStroke(5));

    }

    public int getHeight(){
        return bImageFromConvert.getHeight();
    }

    public int getWidth() {
        return bImageFromConvert.getWidth();
    }

    public void drawPolygon(Polygon polygon) {
        graphics.drawPolygon(polygon);
    }

    public void dispose() {
        graphics.dispose();
    }

    public BufferedImage getbImageFromConvert() {
        return bImageFromConvert;
    }
}
