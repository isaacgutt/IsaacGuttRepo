package edu.yu.cs.com1320.project.stage3.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage3.Document;
import edu.yu.cs.com1320.project.stage3.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage3.DocumentStore;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, DocumentImpl> table = new HashTableImpl<URI, DocumentImpl>();
    private StackImpl<Undoable> stack = new StackImpl<>();
    private TrieImpl<Document> trie = new TrieImpl<>();




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
        GenericCommand com = new GenericCommand(uri, undo ->{
            table.put(uri, end);
            deleteDoc(doc);
            return true;
        });
        stack.push(com);
        addDoc(doc);
        if(end != null){
            deleteDoc(end);
            return end.hashCode();
        }
        return 0;
    }
    private void addDoc(Document doc){
        for(String word : doc.getWords()){
            trie.put(word, doc);
        }
    }
    private void deleteDoc(Document doc){
        for(String word : doc.getWords()){
            trie.delete(word, doc);
        }
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document getDocument(URI uri) {
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
        stack.push(new GenericCommand(uri, undo ->{
            table.put(uri, end);
            addDoc(end);
            return true;
        }));
        deleteDoc(end);
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
        Undoable command = stack.peek();
        if(command instanceof CommandSet){
            ((CommandSet<?>) command).undoAll();
            stack.pop();
            return;
        }
        command.undo();
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
        Undoable com;
        StackImpl<Undoable> tempStack = new StackImpl<>();
            while(checkTop(this.stack, uri) == 0 && stack.size() > 1){
                com = stack.pop();
                tempStack.push(com);
            }
        int go = checkTop(this.stack, uri);
            if(go == 1) {
                check = true;
                undo();
            }else if (go == 2){
                check = true;
                if(((CommandSet<URI>)stack.peek()).size() == 1) {
                    ((CommandSet<URI>) stack.peek()).undoAll();
                    stack.pop();
                }else{
                    ((CommandSet<URI>) stack.peek()).undo(uri);
                }
            }
        while(tempStack.size() != 0){
                com = tempStack.pop();
                stack.push(com);
            }
            if(!check){
                throw new IllegalStateException();
            }
        }

    private int checkTop(StackImpl stack, URI uri){
        if(stack.peek() instanceof GenericCommand) {
            if (((GenericCommand<?>) stack.peek()).getTarget() == uri) {
                return 1;
            }
        }
        else if(stack.peek() instanceof CommandSet){
            if(((CommandSet<URI>)stack.peek()).containsTarget(uri)){
            return 2;
            }
        }
        return 0;
    }
    /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> search(String keyword){
        List<Document> list = trie.getAllSorted(keyword, (doc1, doc2) -> {
            if (doc1.wordCount(keyword) > doc2.wordCount(keyword)) {
                return -1;
            } else if(doc2.wordCount(keyword) > doc1.wordCount(keyword)) {
                return 1;
            }
            return 0;});
        return list;
    }

    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    @Override
    public List<Document> searchByPrefix(String keywordPrefix){
        HashMap<Document, Integer> map = new HashMap<>();
        List<Document> list = new ArrayList<>(trie.getAllWithPrefixSorted(keywordPrefix, (doc1, doc2) -> 0));
        for(Document l : list){
            for(String word : l.getDocumentTxt().replaceAll("[^A-Za-z0-9 ]", "").toLowerCase().split(" ")){
                if(word.length() >= keywordPrefix.length()) {
                    if (keywordPrefix.equals(word.substring(0, keywordPrefix.length()))) {
                        if (map.containsKey(l)) {
                            map.put(l, map.get(l) + 1);
                        } else {
                            map.put(l, 1);
                        }
                    }
                }
            }
        }
        list.sort((doc1, doc2) -> {
            if (map.get(doc1) > map.get(doc2)) {
                return -1;
            } else if(map.get(doc2) > map.get(doc1)) {
                return 1;
            }
            return 0;});
        return list;

    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAll(String keyword){
        Set<Document> docSet = trie.deleteAll(keyword);
        Set<URI> uriSet = new HashSet<>();
        CommandSet commandSet = new CommandSet();
        for(Document doc : docSet){
            uriSet.add(doc.getKey());
            DocumentImpl end = table.put(doc.getKey(), null);
            commandSet.addCommand(new GenericCommand(doc.getKey(), undo ->{
                table.put(doc.getKey(), end);
                addDoc(end);
                return true;
            }));;
        }
        stack.push(commandSet);
        return uriSet;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){
        Set<Document> docSet = trie.deleteAllWithPrefix(keywordPrefix);
        Set<URI> uriSet = new HashSet<>();
        CommandSet commandSet = new CommandSet();
        for(Document doc : docSet){
            uriSet.add(doc.getKey());
            DocumentImpl end = table.put(doc.getKey(), null);
            commandSet.addCommand(new GenericCommand(doc.getKey(), undo ->{
                table.put(doc.getKey(), end);
                addDoc(end);
                return true;
            }));;
        }
        stack.push(commandSet);
        return uriSet;
    }
}




