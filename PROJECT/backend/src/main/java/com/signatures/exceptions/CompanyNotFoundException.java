package com.signatures.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "company not found")
public class CompanyNotFoundException extends AbstractResponseException {
}
