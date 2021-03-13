package com.signatures.models.responses.errors;

import com.signatures.models.responses.ServerResponse;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerErrorResponse implements ServerResponse {
    private final Date timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
}
