package edu.yu.cs.com1320.project.stage1.impl;

import edu.yu.cs.com1320.project.stage1.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage1.DocumentStore;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {
    public HashTableImpl<URI, DocumentImpl> table = new HashTableImpl<URI, DocumentImpl>();



    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     */
    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        DocumentImpl doc;
        if(input == null) {
            doc = null;
        }
        else if(format == DocumentStore.DocumentFormat.TXT){
            byte[] content = input.readAllBytes();
            String str = new String(content);
            doc = new DocumentImpl(uri, str);
        }
        else{
            byte[] content = input.readAllBytes();
            doc = new DocumentImpl(uri, content);
        }
        DocumentImpl end = table.put(uri, doc);
        if(end != null){
            return end.hashCode();
        }
        return 0;
    }


    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public DocumentImpl getDocument(URI uri) {
        return table.get(uri);
    }


    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean deleteDocument(URI uri) {
        if(table.get(uri) == null){
            return false;
        }
        table.put(uri, null);
        return true;

    }
}
