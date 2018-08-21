package com.spear;

import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Created by aspear on 6/5/17.
 */
public class WordBlocks {

    private Map<Polygon, String> polygonStringMap = new HashMap<>();
    private Map<Polygon, String> polygonFileMap = new HashMap<>();
    private Integer imageHeight;
    private Integer imageWidth;

    public WordBlocks(Integer imageHeight, Integer imageWidth) {
        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;
    }

    public void addWordBlock(Polygon polygon, String text) {
        polygonStringMap.put(polygon, text);
        UUID fileId = UUID.randomUUID();

        CompletableFuture.runAsync(() -> {
            TextToSpeech.textToFile(text, s -> {
                polygonFileMap.put(polygon, s);
            });
        });

    }

    public String getFileUrlForPoint(double xPercent, double yPercent ) {
        Point point = findPoint(xPercent, yPercent);
        return polygonFileMap
                .keySet()
                .stream()
                .filter(polygon1 -> polygon1.contains(point))
                .findAny()
                .map(polygon -> {
                    return polygonFileMap.get(polygon);
                })
                .orElse("");

    }

    public String getTextForPoint(double xPercent, double yPercent) {



        Point point = findPoint(xPercent, yPercent);
        return polygonStringMap
                .keySet()
                .stream()
                .filter(polygon1 -> polygon1.contains(point))
                .findAny()
                .map(polygon -> {
                    return polygonStringMap.get(polygon);
                })
                .orElse("");


    }



    private Point findPoint(double xPercent, double yPercent) {
        int x = (int)(xPercent * imageWidth);
        int y = (int)(yPercent * imageHeight);

        return new Point(x, y);
    }
}
