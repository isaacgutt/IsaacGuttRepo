package edu.yu.cs.com1320.project.stage5.impl;

import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.impl.*;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.DocumentStore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;

public class DocumentStoreImpl implements DocumentStore {
    private BTreeImpl<URI, Document> btree = new BTreeImpl<>();
    private StackImpl<Undoable> stack = new StackImpl<>();
    private TrieImpl<URI> trie = new TrieImpl<>();
    private MinHeapImpl<Pittman> heap = new MinHeapImpl<>();
    private HashMap<URI, Pittman> map = new HashMap<>();
    private HashSet<URI> justDisked = new HashSet<>();
    private HashSet<URI> onDisk = new HashSet<>();
    private File file;
    private Set<URI> diskDeleted = new HashSet<>();
    private int maxDocs = -1;
    private int maxBytes = -1;
    private int bytes;
    private int docs;

public DocumentStoreImpl(File baseDir) {
    this.file = baseDir;
    DocumentPersistenceManager dpm = null;
    try {
        dpm = new DocumentPersistenceManager(baseDir);
    } catch (IOException e) {
        e.printStackTrace();
    }
    btree.setPersistenceManager(dpm);
}
public DocumentStoreImpl() {
    this.file = new File(System.getProperty("user.dir"));
    DocumentPersistenceManager dpm = null;
    try {
        dpm = new DocumentPersistenceManager(file);
    } catch (IOException e) {
        e.printStackTrace();
    }
    btree.setPersistenceManager(dpm);
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
        if(doc == null) return deleteFromPut(uri);
        doc.setLastUseTime(System.nanoTime());
        Document end = btree.put(uri, doc);
        if(onDisk.remove(uri)) return putWhenOnDisk(uri, end);
        if(end != null) return replaceDoc(doc, end);
        addToHeap(uri);
        GenericCommand<URI> com = createCommand(uri, end, doc, false);
        //updateStorage(doc);
        //stack.push(com);
        undoAfterPut(com, doc);
        addDoc(doc);
        return 0;
    }
    private int putWhenOnDisk(URI uri, Document back){
        addToHeap(uri);
        updateStorage(back);
        stack.push(new GenericCommand<URI>(uri, undo -> {
            removeFromHeap(back);
            goToDisk(uri);
            onDisk.add(uri);
            for(URI disked : justDisked) {
                btree.get(disked);
                //addToHeap(disked);
            }

            return true;
        }));
        return back.hashCode();
    }

    private void undoAfterPut(GenericCommand<URI> com, Document in){
        boolean disk = updateStorage(in);
        if (disk) {
                com = (new GenericCommand<URI>(in.getKey(), undo -> {
                    removeFromHeap(in);
                    btree.put(in.getKey(), null);
                    deleteDoc(in);
                    for(URI uri : justDisked) {
                        btree.get(uri);
                    }
                    return true;
                }));
        }
        stack.push(com);
    }
    private int replaceDoc(Document in, Document out){
        GenericCommand<URI> com = createCommand(in.getKey(), out, in, false);
        heap.reHeapify(map.get(in.getKey()));
        stack.push(com);
        addDoc(in);
        deleteDoc(out);
        return out.hashCode();
    }

    private void addToHeap(URI uri){
        Pittman heapDoc = null;
        if(map.containsKey(uri)){
            heapDoc = map.get(uri);
            heap.reHeapify(heapDoc);
            return;
        }
        heapDoc = new Pittman(uri);
        map.put(uri, heapDoc);
        heap.insert(heapDoc);
    }

    private GenericCommand<URI> createCommand(URI uri, Document end, Document doc, boolean isDelete){
        GenericCommand<URI> com;
        if(isDelete){
            GenericCommand<URI> delete = new GenericCommand<URI>(uri, undo ->{
                btree.put(uri, end);
                addDoc(end);
                return true;
            });
            return delete;
        }
        if(end == null) {
            com = new GenericCommand<URI>(uri, undo -> {
                removeFromHeap(doc);
                btree.put(uri, end);
                deleteDoc(doc);
                return true;
            });
        //if it's replacing a document, delete new from trie and put back new
        }else{
            com = new GenericCommand<URI>(uri, undo -> {
                btree.put(uri, end);
                deleteDoc(doc);
                addDoc(end);
                return true;
            });
        }
        return com;
    }
    private int deleteFromPut(URI uri){
        if(map.containsKey(uri)) removeFromHeap(btree.get(uri));
        Document before = btree.put(uri, null);
        deleteDoc(before);
        if(!onDisk.contains(uri))stack.push(createCommand(uri, before, null, true));
        else{
            stack.push(justDiskCommand(uri, before));
            onDisk.remove(uri);
        }
        return before.hashCode();
    }

    private GenericCommand<URI> justDiskCommand(URI uri, Document before){
        GenericCommand<URI> uriGenericCommand = new GenericCommand<URI>(uri, undo -> {
            btree.put(uri, before);
            addDoc(before);
            //addToHeap(uri);
            return true;
        });
        return uriGenericCommand;
    }

    private void addDoc(Document doc){
        for(String word : doc.getWords()){
            trie.put(word, doc.getKey());
        }
    }
    private void deleteDoc(Document doc){
        for(String word : doc.getWords()){
            trie.delete(word, doc.getKey());
        }
    }
    private boolean updateStorage(Document doc){
        int docData = doc.getDocumentBinaryData() == null? doc.getDocumentTxt().getBytes().length : doc.getDocumentBinaryData().length;
        if(tooFull(docData, 1)){
            moveToDisk(docData, 1);
            this.docs++;
            this.bytes += docData;
            return true;
        }
        this.docs++;
        this.bytes += docData;
        return false;
    }
    private void moveToDisk(int docData, int add){
        this.justDisked.clear();
        HashSet<URI> set = new HashSet<>();
        while(tooFull(docData, add)){
            URI uri = heap.remove().getUri();
            justDisked.add(uri);
            map.remove(uri);
            Document d = btree.get(uri);
            goToDisk(uri);
            docs--;
            bytes -= d.getDocumentTxt() == null? d.getDocumentBinaryData().length: d.getDocumentTxt().getBytes().length;
        }
        onDisk.addAll(justDisked);
    }
    private void goToDisk(URI uri){
        try {
            btree.moveToDisk(uri);
        } catch (Exception e) {
            e.printStackTrace();
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


    private void removeFromHeap(Document doc){
        Pittman heapDoc = map.remove(doc.getKey());
        doc.setLastUseTime(0);
        heap.reHeapify(heapDoc);
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
        Document doc = btree.get(uri);
        if(doc == null){
            return null;
        }
        if(!map.containsKey(uri)){
            onDisk.remove(uri);
            updateStorage(doc);
            doc.setLastUseTime(System.nanoTime());
            addToHeap(uri);
            return doc;
        }
        Pittman heapDoc = map.get(uri);
        doc.setLastUseTime(System.nanoTime());
        heap.reHeapify(heapDoc);
        return doc;
    }


    /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with that URI
     */
    @Override
    public boolean deleteDocument(URI uri) {
        if(btree.get(uri) == null){
            return false;
        }
        if(onDisk.contains(uri)){
            Document before = btree.put(uri, null);
            onDisk.remove(uri);
            stack.push(justDiskCommand(uri, before));
            return true;
        }
        removeFromHeap(btree.get(uri));
        onDisk.remove(uri);
        Document end = btree.put(uri, null);
        stack.push(createCommand(uri, end, null, true));
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
        if(stack.size() == 0) throw new IllegalStateException();
        Undoable command = stack.peek();
        if(command instanceof CommandSet){
            CommandSet<URI> com = (CommandSet<URI>) stack.pop();
            long time = System.nanoTime();
            for (Object o : com) {
                GenericCommand g = (GenericCommand) o;
                g.undo();
                URI uri = (URI)g.getTarget();
                if(!diskDeleted.contains(uri)) {
                    Pittman heapDoc = !map.containsKey(uri) ? new Pittman(uri) : map.get(uri);
                    if (!map.containsKey(uri)) {
                        map.put(uri, heapDoc);
                    }
                    Document doc = btree.get(uri);
                    doc.setLastUseTime(time);
                    heap.insert(heapDoc);
                    updateStorage(doc);
                }else{
                    diskDeleted.remove(uri);
                }
            }
            return;
        }
        undoGeneric();
    }

    private void undoGeneric(){
        GenericCommand  com = (GenericCommand) stack.peek();
        int count = onDisk.size();
        com.undo();
        HashSet<URI> uri = isBackFromDisk();
        if(uri != null) {
            finishDiskUndo(uri, (URI)com.getTarget());
        }
        else if(count == onDisk.size()) heapAfterUndo((URI)com.getTarget());
        stack.pop();
    }


    private HashSet<URI> isBackFromDisk(){
        HashSet<URI> set = new HashSet<>();
        for(URI uri : onDisk){
            if(!new File(getDirectory(uri)).exists()){
                set.add(uri);
            }
        }
        return set.size() == 0? null : set;
    }
    private void finishDiskUndo(HashSet<URI> back, URI leave){
        /*if(back == leave){
            return;
        }*/
        for(URI uri : back) {
            onDisk.remove(uri);
            updateStorage(btree.get(uri));
            addToHeap(uri);
        }
    }

    private void heapAfterUndo(URI uri){
        Document doc = btree.get(uri);
        Pittman heapDoc = new Pittman(uri);
        //undoing a put
        if(doc != null && map.containsKey(uri)){
            doc.setLastUseTime(System.nanoTime());
            heap.reHeapify(map.get(uri));
        }
        //undoing a delete
        else if(doc != null && !map.containsKey(uri) ){
            updateStorage(doc);
            addToHeap(uri);
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
                Pittman heapDoc = new Pittman(uri);
                btree.get(uri).setLastUseTime(System.nanoTime());
                heap.insert(heapDoc);
                map.put(uri, heapDoc);
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
        List<URI> list = trie.getAllSorted(keyword, (uri1, uri2) -> {
            if (btree.get(uri1).wordCount(keyword) > btree.get(uri2).wordCount(keyword)) {
                return -1;
            } else if(btree.get(uri2).wordCount(keyword) > btree.get(uri1).wordCount(keyword)) {
                return 1;
            }
            return 0;});
        return getDocList(list);
    }
    private List<Document> getDocList(List<URI> uriList){
        long time = System.nanoTime();
        List<Document> docList = new ArrayList<>();
        for(URI uri : uriList){
            Document doc = getDocument(uri);
            doc.setLastUseTime(time);
            Pittman heapDoc = map.get(uri);
            heap.reHeapify(heapDoc);
            docList.add(doc);
        }

        return docList;
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
        HashMap<Document, Integer> prefixMap = new HashMap<>();
        List<URI> uriList = new ArrayList<>(trie.getAllWithPrefixSorted(keywordPrefix, (doc1, doc2) -> 0));
        long time = System.nanoTime();
        List<Document> list = getDocList(uriList);
        for(Document l : list){
            if(list.size() <= 1){
                return list;
            }
            for(String word : l.getDocumentTxt().replaceAll("[^A-Za-z0-9 ]", "").toLowerCase().split(" ")){
                if(word.length() >= keywordPrefix.length()) {
                    if (keywordPrefix.equals(word.substring(0, keywordPrefix.length()))) {
                        if (prefixMap.containsKey(l)) {
                            prefixMap.put(l, prefixMap.get(l) + 1);
                        } else {
                            prefixMap.put(l, 1);
                        }
                    }
                }
            }
        }
        list.sort((doc1, doc2) -> {
            if (prefixMap.get(doc1) > prefixMap.get(doc2)) {
                return -1;
            } else if(prefixMap.get(doc2) > prefixMap.get(doc1)) {
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
        Set<URI> set = trie.deleteAll(keyword);
        if(set.size() == 0){
            return set;
        }
        return deleteSet(set);

    }
    private Set<URI> deleteSet(Set<URI> set){
        CommandSet<URI> commandSet = new CommandSet();
        for(URI uri : set){
            if(!onDisk.contains(uri)) {
                //onDisk.remove(uri);
                Document doc = btree.get(uri);
                removeFromHeap(doc);
                Document end = btree.put(uri, null);
                commandSet.addCommand(createCommand(doc.getKey(), end, null, true));
            }else{
                Document before = btree.put(uri, null);
                diskDeleted.add(uri);
                commandSet.addCommand(justDiskCommand(uri, before));
                onDisk.remove(uri);
            }
        }
        stack.push(commandSet);
        return set;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    @Override
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){
        Set<URI> set = trie.deleteAllWithPrefix(keywordPrefix);
        if(set.size() == 0){
            return set;
        }
        return deleteSet(set);
    }

    @Override
    public void setMaxDocumentCount(int limit) {
        this.maxDocs = limit;
        if(tooFull(0,0)){
            moveToDisk(0, 0);
        }
    }

    @Override
    public void setMaxDocumentBytes(int limit) {
        this.maxBytes = limit;
        if(tooFull(0,0)){
            moveToDisk(0,0);
        }
    }
    private class Pittman implements Comparable<Pittman>{
        private URI uri;
        public Pittman(URI uri){
            this.uri = uri;
        }
        private URI getUri(){
            return uri;
        }

        @Override
        public int compareTo(Pittman o) {
            if(btree.get(uri).getLastUseTime() > btree.get(o.getUri()).getLastUseTime()){
                return 1;
            }
            return btree.get(o.getUri()).getLastUseTime() > btree.get(uri).getLastUseTime()? -1 : 0;
        }

    }
}




