package com.data.search;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebSearch {

    private ArrayList<SearchResult> listOfSearchResults;
    private String directoryPath;
    private String directoryPathData;
    private String cookie;
    private String query;
    private int datanumber = 0;
    private JsonUtil js;
    private Multimap<String, String> resultsMultiMap;

    public void setQuery(String query) {
        this.query = query;
    }

    public void setDirectoryPathData(String directoryPathData) {
        this.directoryPathData = directoryPathData;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }

    private void getCookies(HttpURLConnection urlconnect) {

        String hdr, cookieKey, cookieValue;
        cookie = "";
        for (int i = 1; (hdr = urlconnect.getHeaderFieldKey(i)) != null; i++) {
            String testvalue = urlconnect.getHeaderField(i);
            int test = 0;
            if (hdr.equals("Set-Cookie")) {
                String field = urlconnect.getHeaderField(i);
                int k = field.indexOf("=");
                int k2 = field.indexOf(";");
                cookieKey = field.substring(0, k).trim();
                cookieValue = field.substring(k + 1, k2).trim();
                field = cookieKey + "=" + cookieValue;
                cookie += "; " + field;
            }
        }
        if (cookie.length() > 2) {
            cookie = cookie.substring(2);
        }
        int here = 0;

//        java.util.Map<String, java.util.List<String>> headers = urlconnect.getHeaderFields();
//        java.util.List<String> values = headers.get("Set-Cookie");
//        cookie = null;
//        for (java.util.Iterator iter = values.iterator(); iter.hasNext();) {
//            String v = iter.next().toString();
//            if (cookie == null) {
//                cookie = v;
//            } else {
//                cookie = cookie + ";" + v;
//            }
//        }
//        if (cookie.length() > 2) {
//            cookie = cookie.substring(2);
//        }
    }

    public void setDirectoryPath(String directoryPath) {
        this.directoryPath = directoryPath;
    }

    String readURL(String urlAddress) throws MalformedURLException, IOException {
        StringBuilder urldata = new StringBuilder();
        String newLine = System.lineSeparator();

        URL url = new URL(urlAddress);
        InputStream inputStream = url.openStream();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                urldata.append(line).append(newLine);
            }
        }

        return urldata.toString();
    }

    private void saveDocument(String webPage, String pageTitle, boolean append) {

        File newFile = new File(directoryPath + pageTitle + ".html");
        try {
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(newFile, append), "UTF-8"));
            pw.write(webPage);

            pw.flush();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

    }

    // Create an intermediate service that gets data from Google, manipulate data and return it to a user
    // Connect to a search engine. Send user query as parameters.
    // ArrayList<SearchResult> 
    AllResults getSearchPage() {//String query

        js = new JsonUtil();
        js.setPath(directoryPathData);

        XmlUtil xml = new XmlUtil();
        xml.setPath(directoryPathData);

        // listOfSearchResults = new ArrayList<>();
        //listOfSearchResults = js.deserializeFromJSON("data.json", "SearchResult");
        listOfSearchResults = xml.deserializeFromXML("data.xml");
        query = query.replaceAll(" ", "+");
        String urlAddress = "http://go.mail.ru/search?q=" + query;

        String data = fetchURL(urlAddress, true);

        listOfSearchResults = parseData(data);
        //js.serialize2JSON(listOfSearchResults, "data.json");
        xml.serialize2XML(listOfSearchResults, "data.xml");

        //        return listOfSearchResults;
        AllResults allresults = new AllResults();
        allresults.setList(listOfSearchResults);
        return allresults;
    }

    private ArrayList<SearchResult> parseData(String data) {
        String[] searchBlocks = data.split("<div class=\"result__snp\">");
        for (int i = 1; i < searchBlocks.length; i++) {
//            SearchResult search = new SearchResult();
            String block = searchBlocks[i];
            block = block.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\t", "")
                    .replaceAll(" +", " ");

            String description = block.substring(0, block.indexOf("</div>"));
//            search.setDescription(description);

            String temp = "light-link\" target=\"_blank\" href=\"";
            int x = block.indexOf(temp);
            int y = block.indexOf("\"", x + temp.length());
            if (x > -1 && y > x) {
                String link = block.substring(x + temp.length(), y);
//                search.setLink(link);
                String title = getPageTitle(fetchURL(link, false));
                listOfSearchResults.add(new SearchResult(title, link, description));
                SearchResult sr = listOfSearchResults.get(listOfSearchResults.size() - 1);
                Map m = resultsMultiMap.asMap();
                sr.setResultsMap(m);
                datanumber++;
                js.serialize(sr, "test" + datanumber + ".json");
            }

//            listOfSearchResults.add(search);
        }

        return listOfSearchResults;
    }

    private String getPageTitle(String page) {
        String title = "Untitled";
        int begin = page.indexOf("<title>"),
                end = page.indexOf("</title>");
        if (begin == -1) {
            begin = page.indexOf("<TITLE>");
        }
        if (end == -1) {
            end = page.indexOf("</TITLE>");
        }
        if (begin > -1 && end > begin) {
            title = page.substring(begin + 7, end);
        }

//        parseDocument(page);
        saveDocument(parseDocument(page), title, false);

//        saveDocument(page, title, false);
        return title;
    }

    private String fetchURL(String urlAddress, boolean useCookie) {
        String newLine = System.lineSeparator(), line;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            URL url = new URL(urlAddress);
            HttpURLConnection urlconnect = (HttpURLConnection) url.openConnection();
            urlconnect.setRequestProperty("GET", "HTTP/1.1");
            urlconnect.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; PPC Mac OS X Mach-O;  en-US; rv:1.8.1.12) Gecko/20080201 Firefox/2.0.0.12");
            urlconnect.setRequestProperty("Content-Encoding", "UTF-8");
// urlconnect.setRequestProperty("Content-type", "text/html; charset=UTF-8");
            urlconnect.setRequestProperty("Cookie", cookie); // step 2: send request to server
            urlconnect.setConnectTimeout(5000);
            urlconnect.setReadTimeout(5000);

            urlconnect.setInstanceFollowRedirects(false);

            int status = urlconnect.getResponseCode();
            String message = urlconnect.getResponseMessage();
            if (status == 200) {

                if (useCookie) {
                    getCookies(urlconnect); // step 3: response from server
                }
                String contentType = urlconnect.getHeaderField("Content-Type");
                String charset = null;
                for (String param : contentType.replace(" ", "").split(";")) {
                    if (param.startsWith("charset=")) {
                        charset = param.split("=", 2)[1];
                        break;
                    }
                }
                if (charset == null) {
                    charset = "UTF-8";
                }

                InputStream is = urlconnect.getInputStream();

                BufferedReader rdr = new BufferedReader(new InputStreamReader(is, charset));
                while ((line = rdr.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(newLine);
                }
                urlconnect.disconnect();
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(WebSearch.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WebSearch.class.getName()).log(Level.SEVERE, null, ex);
        }

        return stringBuilder.toString();
    }

    // Parse, Extract and Order sentences relevant to each search query terms.
    // Perform ordering inside Multi Map
    private String parseDocument(String txt) {
        int start = txt.indexOf("<body");
        int end = txt.indexOf("</body>");
        if (start > -1 && end > start) {
            txt = txt.substring(start, end);
        }
        start = 0;
        int indexfrom = 0;
        while (start > -1) {
            start = txt.indexOf("<script", indexfrom);
            indexfrom = start + 1;
            if (start > -1) {
                end = txt.indexOf("</script>", start);
                if (end > start) {
                    txt = txt.substring(0, start) + txt.substring(end); //concatenate text inside <script
                }
            }

        }

        txt = txt.replaceAll("[.]<", ". <");
        txt = txt.replaceAll("<[^>]*>", " ").replaceAll(" +", " ");

        String txt1 = txt.toLowerCase();
        String search = query.toLowerCase();
        String[] wordSearchArray = search.split("[+]");
        //TreeMultimap<String, String> map 
        resultsMultiMap = TreeMultimap.create(Collections.reverseOrder(), Ordering.natural());

        for (int i = 0; i < wordSearchArray.length; i++) {

            int x = 0, from = 0;
            while (x > -1) {
                x = txt1.indexOf(wordSearchArray[i], from);
                from = x + 1;

                if (x > -1) {
                    int y1 = txt1.lastIndexOf(". ", x);
                    if (y1 == -1) {
                        y1 = 0;
                    } else {
                        y1 += 2;
                    }

                    int y2 = txt1.indexOf(". ", x);
                    if (y2 == -1) {
                        y2 = txt1.length();
                    } else {
                        y2 += 1;
                    }
                    if (y2 > y1) {

                        String sentence = txt.substring(y1, y2);
                        if (sentence.length() < 300) {
                            resultsMultiMap.put(wordSearchArray[i], highLight(wordSearchArray, sentence));
                        }
                    }

                }
            }
        }

//        datanumber++;
//        js.serializeMap2JSON(map, "test" + datanumber
//                + ".json");
        TreeMultimap<Integer, String> map2 = TreeMultimap.create(Collections.reverseOrder(), Ordering.natural());
        Iterator<String> itermapkey1 = resultsMultiMap.keySet().iterator();
        while (itermapkey1.hasNext()) {
            String key = itermapkey1.next();
            map2.put(resultsMultiMap.get(key).size(), key);
        }

        StringBuilder sb = new StringBuilder("<table style=\" text-align: left; width: 66%; "
                + "background-color:rgb(255, 255, 204); margin-left: auto; "
                + "margin-right: auto;\" border=\"1\" cellpadding=\"2\" cellspacing=\"2\">");// Создаем таблицу

        Iterator<Integer> iter = map2.keySet().iterator();
        while (iter.hasNext()) {
            int key = iter.next();
            SortedSet set = map2.get(key);
            Iterator<String> iter2 = set.iterator();
            while (iter2.hasNext()) {
                String wordofset = iter2.next();
                Collection set2 = resultsMultiMap.get(wordofset);
                Iterator<String> iter3 = set2.iterator();
                String sentenceofset2 = "";
                while (iter3.hasNext()) {
                    sentenceofset2 += iter3.next() + "<br><br>";
                }
                sb.append("<tr><td>").append(wordofset + " (" + key + ")").append("</td><td>").append(sentenceofset2).append("</td></tr>");
            }
        }

        sb.append("</table>");

        return sb.toString();

    }

    private String highLight(String[] array, String sent) {
        String sent2 = sent.toLowerCase();
        TreeMap<Integer, Integer> map = new TreeMap();
        for (int i = 0; i < array.length; i++) {
            int begin = 0, from = 0;
            while (begin > -1) {
                begin = sent2.indexOf(array[i], from);
                from = begin + array[i].length();
                if (begin > -1) {
                    map.put(begin, from);
                }
            }
        }

        String before = "<b><font color=\"#2554C7\">", after = "</font></b>";
        int k = after.length() + before.length();
        int z = 0;
        Iterator<Integer> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            int begin = iter.next();
            int end = map.get(begin) + z;
            begin += z;

            sent = sent.substring(0, begin) + before + sent.substring(begin, end) + after + sent.substring(end);

            z += k;
        }

        return sent;
    }

} // end class
