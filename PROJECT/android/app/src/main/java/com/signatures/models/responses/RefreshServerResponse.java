package com.signatures.models.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshServerResponse implements ServerResponse {
    private final String token;
}
