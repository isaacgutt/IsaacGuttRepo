package edu.yu.cs.com1320.project.stage4.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage4.Document;
import edu.yu.cs.com1320.project.stage4.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage4.DocumentStore;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore {
    private HashTableImpl<URI, Document> table = new HashTableImpl<URI, Document>();
    private StackImpl<Undoable> stack = new StackImpl<>();
    private TrieImpl<Document> trie = new TrieImpl<>();
    private MinHeapImpl<Document> heap = new MinHeapImpl<>();
    private int maxDocs = -1;
    private int maxBytes = -1;
    private int bytes;
    private int docs;



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
        Document doc = null;
        if(format == DocumentStore.DocumentFormat.TXT && input != null){
            byte[] content = input.readAllBytes();
            String str = new String(content);
            doc = new DocumentImpl(uri, str);
        }
        else if(format == DocumentFormat.BINARY && input != null){
            byte[] content = input.readAllBytes();
            doc = new DocumentImpl(uri, content);
        }
        doc.setLastUseTime(System.nanoTime());
        Document end = table.put(uri, doc);
        GenericCommand com = createCommand(uri, end, doc, false);
        updateStorage(doc);
        stack.push(com);
        addDoc(doc);
        heap.insert(doc);
        heap.reHeapify(doc);
        if(end != null){
            removeFromHeap(end);
            deleteDoc(end);
            return end.hashCode();
        }
        return 0;
    }

    private GenericCommand createCommand(URI uri, Document end, Document doc, boolean isDelete){
        GenericCommand com;
        if(isDelete){
            GenericCommand delete = new GenericCommand(uri, undo ->{
                table.put(uri, end);
                addDoc(end);
                return true;
            });
            return delete;
        }
        if(end == null) {
            com = new GenericCommand(uri, undo -> {
                table.put(uri, end);
                deleteDoc(doc);
                return true;
            });
        //if it's replacing a document, delete new from trie and put back new
        }else{
            com = new GenericCommand(uri, undo -> {
                table.put(uri, end);
                deleteDoc(doc);
                addDoc(end);
                return true;
            });
        }
        return com;
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
    private void updateStorage(Document doc){
        int docData = doc.getDocumentBinaryData() == null? doc.getDocumentTxt().getBytes().length : doc.getDocumentBinaryData().length;
        if(tooFull(docData, 1)){
            emptyHeap(docData, 1);
        }
        this.docs++;
        this.bytes += docData;
    }
    private void emptyHeap(int docData, int add){
        StackImpl<Undoable> tempStack = new StackImpl<>();
        while(tooFull(docData, add)){
            Document d = heap.remove();
            URI uri = d.getKey();
            deleteDoc(d);
            table.put(uri, null);
            while(stack.size() >= 1){
                int go = checkTop(uri);
                boolean see = false;
                //see will remove the uri from the commandset, return true if set was popped
                if(go == 2){
                    see = removeFromCommandSet(uri, go);
                }
                Undoable u = stack.peek();
                if(!see){
                    u = stack.pop();
                }
                //if go == 0, uri isn't there so it goes in tempstack
                //if go == 2, will be popped if see == true
                //if go == 1, pop because just get rid of the uri
                if(go != 1 && !see){
                    tempStack.push(u);
                }
            }
            refillStack(tempStack);
            docs--;
            bytes -= d.getDocumentTxt() == null? d.getDocumentBinaryData().length: d.getDocumentTxt().getBytes().length;
        }
    }
    private boolean tooFull(int docData, int add){
        if(maxBytes == -1 && maxDocs == -1){
            return false;
        }
        if(docs + add > maxDocs && maxDocs != -1){
            return true;
        }
        return bytes + docData > maxBytes && maxBytes != -1;
    }
    private boolean removeFromCommandSet(URI uri, int go){
        CommandSet com = (CommandSet)stack.peek();
        if(com.size() == 1) {
            stack.pop();
            return true;
        }else{
            Iterator it = com.iterator();
            while(it.hasNext()) {
                GenericCommand g = (GenericCommand) it.next();
                if (g.getTarget() == uri) {
                    it.remove();
                    break;
                }
            }
        }
        return false;
    }

    private void removeFromHeap(Document doc){
        doc.setLastUseTime(0);
        heap.reHeapify(doc);
        heap.remove();
        docs--;
        bytes -= doc.getDocumentTxt() == null? doc.getDocumentBinaryData().length: doc.getDocumentTxt().getBytes().length;
    }

    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    @Override
    public Document getDocument(URI uri) {
        Document doc = table.get(uri);
        if(doc == null){
            return null;
        }
        doc.setLastUseTime(System.nanoTime());
        heap.reHeapify(doc);
        return doc;
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
        Document end = table.put(uri, null);
        stack.push(createCommand(uri, end, null, true));
        removeFromHeap(end);
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
            CommandSet<URI> com = (CommandSet<URI>) stack.pop();
            long time = System.nanoTime();
            for (Object o : com) {
                GenericCommand g = (GenericCommand) o;
                g.undo();
                heap.insert(table.get((URI)g.getTarget()));
                Document doc = getDocument((URI) g.getTarget());
                doc.setLastUseTime(time);
                updateStorage(doc);
            }
            return;
        }
        GenericCommand  com = (GenericCommand) stack.peek();
        Document before = getDocument((URI)com.getTarget());
        command.undo();
        heapAfterUndo((URI)com.getTarget(), before);
        stack.pop();
    }

    private void heapAfterUndo(URI uri, Document before){
        Document doc = table.get(uri);
        if(before == null){
            heap.insert(doc);
            updateStorage(getDocument(uri));
        }
        else if(doc != null){
            removeFromHeap(before);
            heap.insert(doc);
            updateStorage(getDocument(uri));
        }
        else{
            removeFromHeap(before);
        }
    }

    /**
     * undo the last put or delete that was done with the given URI as its key
     *
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
    @Override
    public void undo(URI uri) throws IllegalStateException {
        if(stack.size() == 0){
            throw new IllegalStateException();
        }
        StackImpl<Undoable> tempStack = new StackImpl<>();
            while(checkTop(uri) == 0 && stack.size() > 1){
                Undoable com = stack.pop();
                tempStack.push(com);
            }
        int go = checkTop(uri);
            if(go == 1) {
                undo();
            }else if (go == 2){
                if(((CommandSet<URI>)stack.peek()).size() == 1) {
                    ((CommandSet<URI>) stack.peek()).undoAll();
                    stack.pop();
                }else{
                    ((CommandSet<URI>) stack.peek()).undo(uri);
                }
                    heap.insert(table.get(uri));
                    updateStorage(getDocument(uri));
            }
        refillStack(tempStack);
            if(go == 0){
                throw new IllegalStateException();
            }
        }

    private void refillStack(StackImpl<Undoable> tempStack){
        while(tempStack.size() != 0){
            Undoable com = tempStack.pop();
            stack.push(com);
        }
    }


    private int checkTop(URI uri){
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
        long time = System.nanoTime();
        for(Document doc : list){
            doc.setLastUseTime(time);
            heap.reHeapify(doc);
        }

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
        long time = System.nanoTime();
        for(Document l : list){
            l.setLastUseTime(time);
            heap.reHeapify(l);
            if(list.size() <= 1){
                return list;
            }
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
        if(docSet.size() == 0){
            return uriSet;
        }
        return deleteSet(docSet, uriSet);

    }
    private Set<URI> deleteSet(Set<Document> docSet, Set<URI> uriSet){
        CommandSet commandSet = new CommandSet();
        for(Document doc : docSet){
            removeFromHeap(doc);
            uriSet.add(doc.getKey());
            Document end = table.put(doc.getKey(), null);
            commandSet.addCommand(createCommand(doc.getKey(), end, null, true));
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
        if(docSet.size() == 0){
            return uriSet;
        }
        return deleteSet(docSet, uriSet);
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        this.maxDocs = limit;
        if(tooFull(0,0)){
            emptyHeap(0, 0);
        }
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        this.maxBytes = limit;
        if(tooFull(0,0)){
            emptyHeap(0,0);
        }
    }
}




