//package com.example.dictionary.Model;
//
//import com.example.dictionary.util;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.InputSource;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.StringReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.xml.namespace.QName;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.xpath.XPath;
//import javax.xml.xpath.XPathConstants;
//import javax.xml.xpath.XPathExpressionException;
//import javax.xml.xpath.XPathFactory;
//
//public class CambridgeDictModel {
//    private String baseUrl = "https://dictionary.cambridge.org/api/v1/dictionaries/";
//    private String accessKey = "xw4lCQJjCbE7mwjZduAF51Y8MFEBavXqWc35kgUOLV6AJGwoUGbckHgtB5GQzM3F";
//    private String Accept = "application/json";
//    private String dictCode = "english-korean";
//
//    public CambridgeDictModel() {
//
//    }
//
//    // Dictionary에서 검색어에 대한 단어를 가져옴.
//    public List<WordNote> search(String q, int pageindex, int pagesize) throws IOException, JSONException {
//        // GET 파라미터 구성
//        HashMap<String, String> parameters = new HashMap<>();
//        parameters.put("Accept", Accept);
//        parameters.put("dictCode", dictCode);
//        parameters.put("pageindex", String.valueOf(pageindex));
//        parameters.put("pagesize", String.valueOf(pagesize));
//        parameters.put("q", q);
//        // 커넥션 설정
//        HttpURLConnection conn = getConnection("search", parameters);
//
//        // 결과 파싱
//        JSONObject json = new JSONObject(getResponseData(conn));
//        List<String> queryList = new ArrayList<>();
//        JSONArray list = (JSONArray)json.get("results");
//        for (int i = 0; i < list.length(); i++) {
//            JSONObject item = list.getJSONObject(i);
//            queryList.add(item.getString("entryId"));
//        }
//
//        List<WordNote> results = new ArrayList<>();
//        for(String entryId : queryList) {
//            results.add(getEntry(entryId,false));
//        }
//        return results;
//    }
//
//    public WordNote getEntry(String entryId, boolean isLocal) {
//        // GET 파라미터 구성
//        HashMap<String, String> parameters = new HashMap<>();
//        parameters.put("format", "xml");
//        HttpURLConnection conn = getConnection("entries/" + entryId, parameters);
//
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder;
//        Document doc;
//
//        WordNote WordNote = new WordNote(isLocal, entryId, null, null);
//
//        ArrayList<WordInfo> wordInfos = new ArrayList<>();
//        try {
//            JSONObject js = new JSONObject(getResponseData(conn));
//            InputSource is = new InputSource(new StringReader(js.get("entryContent").toString()
//                    .replaceAll("\\\"", "\"")
//                    .replaceAll("\\n", "")
//                    .replaceAll("\\/", "/")
//                    .replaceAll("<(/?)cl>", "")
//            ));
//            util.sysout(js.get("entryContent").toString()
//                    .replaceAll("\\\"", "\"")
//                    .replaceAll("\\n", "")
//                    .replaceAll("\\/", "/")
//                    .replaceAll("<(/?)cl>", ""));
//            builder = factory.newDocumentBuilder();
//            doc = builder.parse(is);
//            XPathFactory xPathFactory = XPathFactory.newInstance();
//            XPath xPath = xPathFactory.newXPath();
//            NodeList PosBlocks = (NodeList) getEval(xPath, "//pos-block", doc, XPathConstants.NODESET);
//            util.sysout("posblock size: " + PosBlocks.getLength());
//            if (PosBlocks.getLength() <= 0)
//                return null;
//            for(int i = 0; i < PosBlocks.getLength(); i++) {
//                Node posBase = PosBlocks.item(i);
//                Node header = (Node)getEval(xPath, "//header/info", posBase, XPathConstants.NODE);
//                NodeList senseBlocks = (NodeList)getEval(xPath, "//sense-block", posBase, XPathConstants.NODESET);
//
//                String word_type = getEval(xPath, "//posgram/pos", header, XPathConstants.STRING).toString();
//                Pron word_pron_uk = new Pron(
//                                getEval(xPath, "(//info[1]/pron/ipa)[1]/text()", header, XPathConstants.STRING).toString(),
//                                getEval(xPath, "//info[1]/audio[@region = \"uk\"]/source[@type = \"audio/mpeg\"]/@src", header, XPathConstants.STRING).toString()
//                        );
//                Pron word_pron_us = new Pron(
//                                getEval(xPath, "(//info/pron/ipa)[2]/text()", header, XPathConstants.STRING).toString(),
//                                getEval(xPath, "//info/audio[@region = \"us\"]/source[@type = \"audio/mpeg\"]/@src", header, XPathConstants.STRING).toString()
//                        );
//                List<Meaning> word_meanings = new ArrayList<>();
//                util.sysout("senseblock size: " + senseBlocks.getLength());
//                for(int j = 0; j < senseBlocks.getLength(); j++) {
//                    Node senseBase = senseBlocks.item(j);
//                    String mean_ko = getEval(xPath, "//pos-block["+ (i + 1) +"]/sense-block["+ (j + 1) +"]/def-block/definition/trans/text()", doc, XPathConstants.STRING).toString();
//                    if (mean_ko.length() <= 0)
//                        continue;
//                    util.sysout("//pos-block["+ (i + 1) +"]/sense-block["+ (j + 1) +"]/def-block/definition/trans/text()");
//                    String mean_en = getEval(xPath, "//pos-block["+ (i + 1) +"]/sense-block["+ (j + 1) +"]/def-block/definition/def/text()", doc, XPathConstants.STRING).toString();
//                    if (mean_en.length() <= 0)
//                        continue;
//                    List<Usage> usages = new ArrayList<>();
//                    NodeList examps = (NodeList) xPath.evaluate("//def-block/examp", senseBase, XPathConstants.NODESET);
//                    util.sysout("examp size: "+ examps.getLength());
//                    for(int k = 0; k < examps.getLength(); k++) {
//                        String usage = getEval(xPath, "//pos-block["+ (i + 1) +"]/sense-block["+ (j + 1) +"]/def-block/examp["+ (k + 1) +"]/eg/text()", doc, XPathConstants.STRING).toString();
//                        if (usage.trim().length() > 0) {
//                            usages.add(new Usage(usage));
//                        }
//                        util.sysout("//pos-block["+ (i + 1) +"]/sense-block["+ (j + 1) +"]/def-block/examp["+ (k + 1) +"]/eg/text()");
//                    }
//                    word_meanings.add(new Meaning(mean_ko, mean_en, usages));
//                }
//
//                util.sysout(util.StringToWordType(word_type).toString());
//                wordInfos.add(new WordInfo(entryId, util.StringToWordType(word_type), word_pron_us, word_pron_uk, word_meanings));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        WordNote.WordInfos = wordInfos;
//        return  WordNote;
//    }
//
//    private HttpURLConnection getConnection(String api, HashMap<String, String> parameters) {
//        HttpURLConnection conn = null;
//        try {
//            String temp = baseUrl + dictCode + "/" + api + "?";
//            for(Map.Entry<String, String> entry : parameters.entrySet()) {
//                temp += entry.getKey() + "=" + entry.getValue() + "&";
//            }
//            URL url = new URL(temp.substring(0, temp.length() - 1));
//            conn = (HttpURLConnection)url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setUseCaches(false);
//            conn.setDefaultUseCaches(false);
//            conn.setRequestProperty("accessKey", accessKey);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return conn;
//    }
//
//    private String getResponseData(HttpURLConnection conn) {
//        String output =  "";
//        BufferedReader br = null;
//        OutputStream os = null;
//        if (conn != null) {
//            try {
//                int code = conn.getResponseCode();
//                if (code/400 >= 1)
//                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//                else
//                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String temp;
//                while ((temp = br.readLine()) != null) {
//                    output += temp;
//                }
//                conn.disconnect();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (os != null) {
//                    try { os.close(); } catch (IOException e) { e.printStackTrace(); }
//                }
//                if (br != null) {
//                    try { br.close(); } catch (IOException e) { e.printStackTrace(); }
//                }
//            }
//        }
//
//        return output;
//    }
//    private Object getEval(XPath xPath, String expression, Object item, QName returnType) {
//        try {
//            return xPath.evaluate(expression, item, returnType);
//        } catch (XPathExpressionException e) {
//            return "";
//        }
//    }
//}
