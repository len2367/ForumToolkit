package com.signatures.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "token expired")
public class ExpiredTokenException extends AbstractResponseException {
}
