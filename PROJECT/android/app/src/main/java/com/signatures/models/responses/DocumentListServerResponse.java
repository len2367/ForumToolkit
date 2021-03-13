package com.signatures.models.responses;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DocumentListServerResponse extends ArrayList<DocumentServerResponse> implements ServerResponse {


    public DocumentListServerResponse(@NonNull Collection<? extends DocumentServerResponse> c) {
        super(c);
    }

    public DocumentListServerResponse filterByLang(String lang) {
        return new DocumentListServerResponse(
                this.stream().filter((DocumentServerResponse d) -> d.getLanguage().equals(lang)).collect(Collectors.toList())
        );
    }
}
