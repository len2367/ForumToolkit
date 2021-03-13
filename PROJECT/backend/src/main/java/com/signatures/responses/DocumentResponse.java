package com.signatures.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentResponse implements Response {
    private final Long id;
    private final String name;
    private final String description;
    private final String language;
}
