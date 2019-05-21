package com.example.dictionary.Model.AWSPolly;

import android.content.Context;
import android.util.Base64;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPolly;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.TextType;
import com.amazonaws.services.polly.model.Voice;
import com.amazonaws.services.polly.model.VoiceId;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class AWSPollyModel {
    private String COGNITO_POOL_ID = "ap-northeast-2_s8YeSiU3q";
    private Regions region = Regions.AP_NORTHEAST_2;
    private String Access_Key = "AKIASHBS5KBZSN2GOJXL";
    private String Access_Secret = "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";

    private AmazonPollyPresigningClient client;

    public AWSPollyModel(Context context) {
        BasicAWSCredentials credentials = new BasicAWSCredentials(
                Access_Key,
                Access_Secret
        );
        client = new AmazonPollyPresigningClient(new StaticCredentialsProvider(credentials));
    }

    private InputStream synthesize(String text, TextType textType, OutputFormat format) throws IOException {
        Voice voice = getVoice().get(0);
        SynthesizeSpeechRequest req =
                new SynthesizeSpeechRequest()
                        .withTextType(textType)
                        .withText(text)
                        .withVoiceId(voice.getId())
                        .withOutputFormat(format);
        SynthesizeSpeechResult res = this.client.synthesizeSpeech(req);
        return res.getAudioStream();
    }

    private URL synthesizeURL(String text, TextType textType, OutputFormat format) throws IOException {
        Voice voice = getVoice().get(0);
        SynthesizeSpeechPresignRequest req =
                new SynthesizeSpeechPresignRequest()
                        .withTextType(textType)
                        .withText(text)
                        .withVoiceId(voice.getId())
                        .withOutputFormat(format);
        //SynthesizeSpeechResult res = this.client.synthesizeSpeech(req);
        return client.getPresignedSynthesizeSpeechUrl(req);
    }

    public List<Voice> getVoice() {
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
        DescribeVoicesResult describeVoicesResult = this.client.describeVoices(describeVoicesRequest);
        return describeVoicesResult.getVoices();
    }

    public String GetTTSLink_IPA(String IPA, String Word_String) {
        ByteArrayOutputStream out = null;
        InputStream in = null;
        String output = null;
        try {
            String ssml = "<speak><phoneme alphabet='ipa' ph='" + IPA +  "'>" + Word_String + "</phoneme></speak>";
            in = synthesize(ssml, TextType.Ssml, OutputFormat.Mp3);
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int len;
            while((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
            out.close();
            output = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return output;
    }


}
