package com.spear;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import net.minidev.json.JSONArray;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;


import java.awt.Polygon;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/**
 * Created by aspear on 6/4/17.
 */
public class OCR {
    static OkHttpClient client = new OkHttpClient();
    static Gson gson = new Gson();
    public static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
    private static final String key = System.getenv("google_cloud_vision_key");

    public static String readImage(byte[] bytes) {
        String encodedImage = Base64.getEncoder().encodeToString(bytes);

        Map bodyMap = ImmutableMap.of(
          "requests", ImmutableList.of(
            ImmutableMap.of(
              "image", ImmutableMap.of(
                "content", encodedImage
              ),
              "features", ImmutableList.of(
                ImmutableMap.of(
                  "type", "DOCUMENT_TEXT_DETECTION"
                )
              )
            )
          )
        );

        String json = gson.toJson(bodyMap);
        RequestBody body = RequestBody.create(JSON, json);

        Request request = new Request.Builder()
          .url("https://vision.googleapis.com/v1/images:annotate?key="+key)
          .post(body)
          .build();


        Response response = null;
        String bodyString = "";
        try {
            response = client.newCall(request).execute();
            bodyString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bodyString;
    }

    public static void consumeAllBlockPolygons(String json, Consumer<Pair<Polygon, String>> polygonConsumer) {
        ReadContext ctx;
        String findBlocks = "$.responses[0].fullTextAnnotation.pages[0].blocks";
        try {
            ctx = JsonPath.parse(json);
        }
        catch (Exception e) {
            throw e;
        }
        Integer numberOfBlocks = ctx.read(findBlocks +".length()");
        IntStream.range(0, numberOfBlocks)
          .forEach(block -> {
              JSONArray jsonArray = ctx.read(findBlocks + "[" + block + "].boundingBox.vertices");
              List<Integer> xPoints = new ArrayList();
              List<Integer> yPoints = new ArrayList();
              jsonArray.forEach(o -> {
                  Integer x = (Integer)((Map)o).get("x");
                  Integer y = (Integer)((Map)o).get("y");
                  if(x == null || y == null)
                      return;
                  xPoints.add(x);
                  yPoints.add(y);
              });

              int[] xArray = xPoints.stream()

                      .mapToInt(i -> i)
                      .toArray();
              int[] yArray = yPoints.stream()

                      .mapToInt(i -> i)
                      .toArray();

              Polygon polygon = new Polygon(xArray, yArray, xArray.length);

              StringJoiner paragraphJoiner = new StringJoiner(" ");

              JSONArray paragraphs = ctx.read(findBlocks + "[" + block + "].paragraphs");
              paragraphs.forEach(paragraph -> {
                  JSONArray words = (JSONArray)((Map)paragraph).get("words");
                  words.forEach(word -> {
                      StringBuilder wordBuilder = new StringBuilder();
                      JSONArray symbols = (JSONArray)((Map)word).get("symbols");
                      symbols.forEach(symbol -> {
                          String text = (String)((Map)symbol).get("text");
                          wordBuilder.append(text);

                      });
                      paragraphJoiner.add(wordBuilder.toString());
                  });
                  //paragraphJoiner.add("\n");
              });

              String wordBlock = paragraphJoiner.toString();

              polygonConsumer.accept(Pair.of(polygon, wordBlock));



          });
    }

}
