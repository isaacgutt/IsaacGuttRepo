package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.stage1.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class DocumentImpl implements Document{

    public URI uri;
    public String text;
    public byte[] binaryData;


    public DocumentImpl(URI uri, String txt){
        if(uri == null || uri.toString().equals("") ||  txt == null || txt.equals("")){
            throw new IllegalArgumentException("Empty parameters");
        }
        this.uri = uri;
        this.text = txt;
    }

    public DocumentImpl(URI uri, byte[] binaryData){
        if(uri == null || uri.toString().equals("") || binaryData == null || binaryData.length == 0){
            throw new IllegalArgumentException("Empty parameters");
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }

    @Override
    public String getDocumentTxt(){
        return this.text;
    }

    @Override
    public byte[] getDocumentBinaryData(){
        return this.binaryData;
    }

    @Override
    public URI getKey(){
        return this.uri;
    }

    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    public boolean equals(Object second){
        return second.hashCode() == hashCode();
    }


}
