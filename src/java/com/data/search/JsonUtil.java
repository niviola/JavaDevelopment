package com.data.search;

import com.google.common.collect.TreeMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonUtil {
    
    private String path;

    public void setPath(String path) {
        this.path = path;
    }
    
    ArrayList deserializeFromJSON(String filename, String type) {

        filename = path + filename;
        Gson gson = new Gson();
        java.lang.reflect.Type typeOf = null;
        if (type.equals("SearchResult")) {
            typeOf = new TypeToken<ArrayList<SearchResult>>() {
            }.getType();
        } 
        
        
//        else if (type.equals("User")) {
//            typeOf = new TypeToken<ArrayList<User>>() {
//            }.getType();
//        }

        String fileData;
        try {
            fileData = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            fileData = null;
        }

        ArrayList al = null;
        try {
            al = gson.fromJson(fileData, typeOf);
        } catch (JsonSyntaxException | ClassCastException e) {
            e.printStackTrace(System.err);
        }
        if (al == null) {
            al = new ArrayList();
        }

        return al;
    }

    void serialize2JSON(ArrayList list, String filename) {

        String custompath = path + filename;
//      Gson gson = new Gson();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(list);

        try {
            Files.write(Paths.get(custompath), json.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(JsonUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

//        PrintWriter writer;
//        try {
//            writer = new PrintWriter(custompath, "UTF-8");
//            writer.print(json);
//            writer.close();
//        } catch (FileNotFoundException | UnsupportedEncodingException e) { // добавления Исключений для try через | , а не через отдельные catch
//            e.printStackTrace(System.err);
//        }
    }
    
     void serializeMap2JSON(TreeMultimap map, String filename) {

        String custompath = path + filename;
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(map.asMap());

        try {
            Files.write(Paths.get(custompath), json.getBytes());
        } catch (IOException ex) {
           // Logger.getLogger(JSONworker.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    

    
}
