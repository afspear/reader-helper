package com.spear;

import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.texttospeech.v1.*;
import com.google.protobuf.ByteString;
import okhttp3.*;
import org.apache.http.client.CredentialsProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;


public class TextToSpeech {

    private static final OkHttpClient client = new OkHttpClient();

    public static void textToFile(String text, Consumer<String> fileUrlConsumer) {


        // Instantiates a client
        try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {


            // Set the text input to be synthesized
            SynthesisInput input = SynthesisInput.newBuilder()
                    .setText(text)
                    .build();

            // Build the voice request, select the language code ("en-US") and the ssml voice gender
            // ("neutral")
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode("en-US")
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();

            // Select the type of audio file you want returned
            AudioConfig audioConfig = AudioConfig.newBuilder()
                    .setAudioEncoding(AudioEncoding.MP3)
                    .setSpeakingRate(.6)
                    .build();

            // Perform the text-to-speech request on the text input with the selected voice parameters and
            // audio file type
            SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice,
                    audioConfig);

            // Get the audio contents from the response
            ByteString audioContents = response.getAudioContent();

            // Write the response to the output file.
            String fileName = UUID.randomUUID().toString() + ".mp3";

            postResponse(audioContents.toByteArray(), fileName).ifPresent(fileUrlConsumer);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static Optional<String> postResponse(byte[] b, String fileName) {

        MediaType MEDIA_TYPE_MP3 = MediaType
                .parse("audio/mpeg");

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName,  RequestBody.create(MEDIA_TYPE_MP3, b))
                .build();



        Request request = new Request.Builder()
                .url("https://uguu.se/api.php?d=upload-tool")
                .post(requestBody)
                .build();

        try {

            Response response = client.newCall(request).execute();
            String url = response.body().string();
                    System.out.println(url);
            return Optional.of(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

}
