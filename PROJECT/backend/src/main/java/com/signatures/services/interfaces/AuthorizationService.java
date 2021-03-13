package com.signatures.services.interfaces;

import com.signatures.exceptions.BadCodeException;

public interface AuthorizationService<ID, T> {
    T getByIdAndCode(ID id, String code) throws BadCodeException;

    boolean isCodeEquals(String code1, String code2);
}
