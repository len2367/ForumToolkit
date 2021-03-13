package com.signatures.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "invalid token")
public class InvalidTokenException extends AbstractResponseException {
}
