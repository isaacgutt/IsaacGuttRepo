package edu.yu.cs.com1320.project.stage5.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

import java.io.*;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * created by the document store and given to the BTree via a call to BTree.setPersistenceManager
 */
public class DocumentPersistenceManager implements PersistenceManager<URI, Document> {
    private final File file;

    public DocumentPersistenceManager(File baseDir) throws IOException {
        this.file = baseDir;
        file.createNewFile();

    }

    @Override
    public void serialize(URI uri, Document val) throws IOException {
        GsonBuilder gson = new GsonBuilder();
        JsonObject holder = new JsonObject();
        holder.add("URI", gson.create().toJsonTree(uri.toString()));
        holder.add("map", gson.create().toJsonTree(val.getWordMap()));
        if(val.getDocumentTxt() == null){
            byte[] bytes = val.getDocumentBinaryData();
            holder.add("bytes", gson.create().toJsonTree(bytes));
        }
        else{
            holder.add("Text", gson.create().toJsonTree(val.getDocumentTxt()));
        }


        File d = new File(getDirectory(uri));
        d.getParentFile().mkdirs();
        d.createNewFile();

        Writer fileWriter = new FileWriter(d);
        fileWriter.write(holder.toString());
        fileWriter.flush();
        fileWriter.close();
    }
    private String getDirectory(URI uri){
        String uriPath = uri.toString();
        if(uri.getScheme() != null){
            uriPath = uriPath.replace(uri.getScheme(), "");
        }
        if(!uriPath.contains("/")){
            return file + File.separator + uriPath + ".json";
        }
        uriPath = uriPath.replace("://", "").replace("/", File.separator);
        String name = uriPath.substring(uriPath.lastIndexOf(File.separator));
        uriPath = uriPath.substring(0, uriPath.lastIndexOf(File.separator)+1);
        return file + File.separator + uriPath + name + ".json";
    }
    @Override
    public Document deserialize(URI uri) throws IOException {
        File grab = new File(getDirectory(uri));
        if(!grab.exists()){
            return null;
        }

        FileInputStream stream = new FileInputStream(grab);
        byte[] content = stream.readAllBytes();
        String string = new String(content);
        stream.close();

        GsonBuilder gson  = new GsonBuilder();
        gson.registerTypeAdapter(Document.class, new DocumentDeserializer()).create();
        Document doc = gson.setLenient().create().fromJson(string, Document.class);
        delete(uri);
        return doc;
    }

    @Override
    public boolean delete(URI uri) throws IOException {
        File gone = new File(getDirectory(uri));
        return gone.delete();
    }

    private class DocumentDeserializer implements JsonDeserializer<Document>{

        @Override
        public Document deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            Gson gson = new Gson();
            JsonObject json = jsonElement.getAsJsonObject();
            URI uri = null;
            try {
                uri = new URI(json.get("URI").getAsString());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String string;
            Document doc = null;
            try {
                string = json.get("Text").getAsString();
                doc = new DocumentImpl(uri, string, null);
            }catch(NullPointerException e){
                Type t = new TypeToken<byte[]>(){}.getType();
                string = json.get("bytes").toString();
                byte[] receive = gson.fromJson(string, t);
                doc = new DocumentImpl(uri, null,receive);
            }
            String mapp = json.get("map").toString();
            Type t = new TypeToken<HashMap<String, Integer>>(){}.getType();
            HashMap<String, Integer> map = gson.fromJson(mapp, t);
            doc.setWordMap(map);
            return doc;
        }
    }
}