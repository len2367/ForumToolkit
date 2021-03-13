package com.signatures.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyServerResponse implements ServerResponse {
    private final String name;
    private final String languages;
}
