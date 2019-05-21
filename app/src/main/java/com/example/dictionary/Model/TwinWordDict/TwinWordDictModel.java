package com.example.dictionary.Model.TwinWordDict;

import android.content.Context;

import com.example.dictionary.Model.AWSPolly.AWSPollyModel;
import com.example.dictionary.Model.RoomDB.Entity.Meaning;
import com.example.dictionary.Model.RoomDB.Entity.Pron;
import com.example.dictionary.Model.RoomDB.Entity.PronExtension;
import com.example.dictionary.Model.RoomDB.Entity.Usage;
import com.example.dictionary.Model.RoomDB.Entity.Word;
import com.example.dictionary.Model.RoomDB.Entity.WordType;
import com.example.dictionary.Model.RoomDB.TypeConverters.WordTypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwinWordDictModel {
    private String X_RapidAPI_Host = "twinword-word-graph-dictionary.p.rapidapi.com";
    private String X_RapidAPI_Key = "2422846cc4msh0ed578586b29b6ep1a5993jsn0e1d50ce67ca";

    private HashMap<String, String> ApiDef;
    private HashMap<Integer, String> WordTypeDef;
    private final String DEFINITION_KR = "definition_kr";
    private final String DIFFICULTY = "difficulty";
    private final String EXAMPLE = "example";

    private Context context;
    private AWSPollyModel pollyModel;

    public TwinWordDictModel(Context context) {
        ApiDef = new HashMap<>();
        ApiDef.put(DEFINITION_KR, "GET");
        ApiDef.put(DIFFICULTY, "GET");
        ApiDef.put(EXAMPLE, "GET");

        WordTypeDef = new HashMap<>();
        WordTypeDef.put(0, "noun");
        WordTypeDef.put(1, "verb");
        WordTypeDef.put(2, "adverb");
        WordTypeDef.put(3, "adjective");

        pollyModel = new AWSPollyModel(context);
    }

    public Word DefinitionKR(String entry) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("entry", entry);
        HttpURLConnection conn = getConnection(DEFINITION_KR, parameters);

        Word word = new Word();

        try {
            JSONObject response = new JSONObject(getResponseData(conn));
            word.Word_String = response.getString("entry");
            word.Meanings = new ArrayList<>();

            Pron pron = new Pron();
            pron.Pron_String = response.getString("ipa");
            pron.Pron_Extension = PronExtension.BASE64;
            pron.Pron_Data = pollyModel.GetTTSLink_IPA(pron.Pron_String, word.Word_String);

            JSONObject meanings = response.getJSONObject("meaning");
            for(int i = 0; i < WordTypeDef.entrySet().size(); i++) {
                String mean_kr = meanings.getString("korean");
                String mean_en = meanings.getString(WordTypeDef.get(i));
                WordType word_type = WordTypeConverter.toWordType(i);
                for(String en_item : mean_en.split("\\n")) {
                    if (en_item.length() <= 0)
                        continue;
                    Meaning meaning = new Meaning();
                    meaning.Meaning_Kor = mean_kr;
                    meaning.Meaning_Eng = en_item;
                    meaning.Meaning_Type = word_type;
                    meaning.PronUS = pron;
                    word.Meanings.add(meaning);
                }
            }

            word.Meanings.get(0).Usages = Example(entry);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return word;
    }

    public List<Usage> Example(String entry) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("entry", entry);
        HttpURLConnection conn = getConnection(EXAMPLE, parameters);

        List<Usage> result = new ArrayList<>();

        try {
            JSONObject response = new JSONObject(getResponseData(conn));
            JSONArray usages = response.getJSONArray("example");
            for(int i = 0; i < usages.length(); i++) {
                Usage usage = new Usage();
                usage.Usage_Seq = i;
                usage.Usage_String = usages.get(i).toString();
                result.add(usage);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private HttpURLConnection getConnection(String api, HashMap<String, String> params) {
        HttpURLConnection conn = null;
        try {
            String urlStr = "https://" + X_RapidAPI_Host + "/" + api + "/?";
            for(Map.Entry<String, String> entry : params.entrySet()) {
                urlStr = urlStr.concat(entry.getKey() + "=" + entry.getValue() + "&");
            }

            URL url = new URL(urlStr.substring(0, urlStr.length() - 1));
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(ApiDef.get(api));
            conn.setUseCaches(false);
            conn.setDefaultUseCaches(false);
            conn.setRequestProperty("X-RapidAPI-Host", X_RapidAPI_Host);
            conn.setRequestProperty("X-RapidAPI-Key", X_RapidAPI_Key);
            conn.setConnectTimeout(15000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conn;
    }

    private String getResponseData(HttpURLConnection conn) {
        String output =  "";
        BufferedReader br = null;
        OutputStream os = null;
        if (conn != null) {
            try {
                int code = conn.getResponseCode();
                if (code/400 >= 1)
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                else
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp;
                while ((temp = br.readLine()) != null) {
                    output += temp;
                }
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try { os.close(); } catch (IOException e) { e.printStackTrace(); }
                }
                if (br != null) {
                    try { br.close(); } catch (IOException e) { e.printStackTrace(); }
                }
            }
        }

        return output;
    }
}
