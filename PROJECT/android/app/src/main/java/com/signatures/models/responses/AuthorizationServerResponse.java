package com.signatures.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthorizationServerResponse implements ServerResponse {
    private final String token;
    private final CompanyServerResponse company;
}
