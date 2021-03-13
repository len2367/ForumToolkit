package com.signatures.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.TOO_EARLY, reason = "token is not expired")
public class NotExpiredTokenException extends AbstractResponseException {
}
