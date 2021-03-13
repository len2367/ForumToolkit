package com.signatures.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class DocumentServerResponse implements ServerResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final String language;
}
