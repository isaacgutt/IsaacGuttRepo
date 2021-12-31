package edu.yu.cs.com1320.project.stage2.impl;

import edu.yu.cs.com1320.project.Command;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage2.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage2.DocumentStore;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.impl.HashTableImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, DocumentImpl> table = new HashTableImpl<URI, DocumentImpl>();

    private StackImpl<Command> stack = new StackImpl<>();



    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     */
    @Override
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException {
        if(uri == null || format == null){
            throw new IllegalArgumentException("null argument");
        }
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
        Command com = new Command(uri, undo ->{
            table.put(uri, end);
            return true;
        });
        stack.push(com);
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
        DocumentImpl end = table.put(uri, null);
        stack.push(new Command(uri, undo ->{
            table.put(uri, end);
            return true;
        }));
        return true;
    }

    /**
     * undo the last put or delete command
     *
     * @throws IllegalStateException if there are no actions to be undone, i.e. the command stack is empty
     */
    @Override
    public void undo() throws IllegalStateException {
        if(stack.size() == 0){
            throw new IllegalStateException();
        }
        Command com = stack.peek();
        com.undo();
        stack.pop();
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     *
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI uri) throws IllegalStateException {
        boolean check = false;
        if(stack.size() == 0){
            throw new IllegalStateException();
        }

        Command com = stack.peek();
        if(com.getUri() == uri){
            undo();
            return;
        }
        StackImpl<Command> tempStack = new StackImpl<>();
        while(stack.peek().getUri() != uri && stack.size() > 1){
            com = stack.pop();
            tempStack.push(com);
        }
        if(stack.peek().getUri() == uri){
            undo();
            check = true;
        }
        while(tempStack.size() != 0){
            com = tempStack.pop();
            stack.push(com);
        }
        if(!check){
            throw new IllegalStateException();
        }

    }


}
