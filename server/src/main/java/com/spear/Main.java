package com.spear;

import io.javalin.Javalin;
import io.javalin.core.util.FileUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(javalinConfig -> {
            javalinConfig.enableCorsForAllOrigins();
        }).start(7000);

        app.get("/", ctx -> ctx.result("Hello World"));

        app.get("/text-to-speech", ctx -> {
            var text = ctx.queryParam("text");
            var file = TextToSpeech.textToFile(text);
            ctx.result(new ByteArrayInputStream(file));
        });

        app.post("/ocr", ctx -> {
            var response = ctx.uploadedFiles("file")
               .stream()
               .findAny().map(uploadedFile -> {
                try {
                    var bytes = uploadedFile.getContent().readAllBytes();

                    return OCR.readImage(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                }
            });
            response.ifPresent(ctx::result);
        });
    }
}
