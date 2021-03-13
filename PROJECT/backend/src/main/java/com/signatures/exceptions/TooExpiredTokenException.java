package com.signatures.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT, reason = "token is too expired, try to relogin")
public class TooExpiredTokenException extends AbstractResponseException {
}
