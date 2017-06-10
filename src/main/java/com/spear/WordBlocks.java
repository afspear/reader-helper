package com.spear;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by aspear on 6/5/17.
 */
public class WordBlocks {

    private Map<Polygon, String> polygonStringMap = new HashMap<>();
    private Integer imageHeight;
    private Integer imageWidth;

    public WordBlocks(Integer imageHeight, Integer imageWidth) {
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
    }

    public void addWordBlock(Polygon polygon, String text) {
        polygonStringMap.put(polygon, text);
    }

    public String getTextForPoint(double xPercent, double yPercent) {

        AtomicReference<String> text = new AtomicReference<>("");

        Point point = findPoint(xPercent, yPercent);
        polygonStringMap
          .keySet()
          .stream()
          .filter(polygon1 -> polygon1.contains(point))
          .findAny()
          .ifPresent(polygon1 -> {
              text.set(polygonStringMap.get(polygon1));
          });

        return text.get();

    }

    private Point findPoint(double xPercent, double yPercent) {
        int x = (int)(xPercent * imageWidth);
        int y = (int)(yPercent * imageHeight);

        return new Point(x, y);
    }
}
