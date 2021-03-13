package com.signatures.entities;

import com.signatures.responses.Response;

public interface Responsible<R extends Response> {
    R generateResponse();
}
