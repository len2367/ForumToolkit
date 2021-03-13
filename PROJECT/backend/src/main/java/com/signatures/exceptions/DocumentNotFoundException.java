package com.signatures.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "document not found")
public class DocumentNotFoundException extends AbstractResponseException {
}
