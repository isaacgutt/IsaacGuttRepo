package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.stage4.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class DocumentImpl implements Document{

    private URI uri;
    private String text;
    private byte[] binaryData;
    protected HashMap<String, Integer> words = new HashMap<>();
    private long lastTime = 0;


    public DocumentImpl(URI uri, String txt) {
        if (uri == null || uri.toString().equals("") || txt == null || txt.equals("")) {
            throw new IllegalArgumentException("Empty parameters");
        }
        this.uri = uri;
        this.text = txt;

        String[] newText = this.text.replaceAll("[^A-Za-z0-9 ]", "").toLowerCase().split(" ");
        for (String word : newText) {
            word = word.trim();
            if (words.containsKey(word)) {
                words.put(word, words.get(word) + 1);
            } else {
                words.put(word, 1);
            }
        }
    }

    public DocumentImpl(URI uri, byte[] binaryData) {
        if (uri == null || uri.toString().equals("") || binaryData == null || binaryData.length == 0) {
            throw new IllegalArgumentException("Empty parameters");
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }

    @Override
    public String getDocumentTxt() {
        return this.text;
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    @Override
    public boolean equals(Object second) {
        return second.hashCode() == hashCode();
    }


    /**
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     **/
    @Override
    public int wordCount(String word) {
        if (this.binaryData != null) {
            return 0;
        }
        word = word.replaceAll("[^A-Za-z0-9 ]", "").toLowerCase();
        try {
            return words.get(word);
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords() {
        return binaryData == null ? words.keySet() : new HashSet<>();
    }

    @Override
    public long getLastUseTime() {
        return this.lastTime;
    }

    @Override
    public void setLastUseTime(long timeInNanoseconds) {
        this.lastTime = timeInNanoseconds;
    }

    @Override
    public int compareTo(Document o) {
        if(this.lastTime > o.getLastUseTime()){
            return 1;
        }
        return o.getLastUseTime() > this.lastTime? -1 : 0;
    }
}
