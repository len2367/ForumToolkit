package com.signatures.services;

import com.signatures.models.responses.DocumentListServerResponse;
import com.signatures.models.responses.DocumentServerResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DocumentService {
    private final static String TAG = "DocumentService";

    private DocumentListServerResponse documents;
    private DocumentListServerResponse currentDocuments;

    public void setDocuments(DocumentListServerResponse documents) {
        this.documents = documents;
    }

    public DocumentServerResponse getByPosition(int position) {
        return currentDocuments.get(position);
    }

    public List<HashMap<String, String>> getDocumentsInfoByLang(String lang) {
        currentDocuments = documents.filterByLang(lang);

        List<HashMap<String, String>> list = new ArrayList<>();

        for (DocumentServerResponse doc : currentDocuments) {
            HashMap<String, String> map = new HashMap<>();
            map.put("Name", doc.getName());
            map.put("Description", doc.getDescription());

            list.add(map);
        }

        return list;
    }
}
