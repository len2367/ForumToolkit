package com.signatures.services.interfaces;

import com.signatures.exceptions.ExpiredTokenException;
import com.signatures.exceptions.InvalidTokenException;
import com.signatures.exceptions.NotExpiredTokenException;
import com.signatures.exceptions.TooExpiredTokenException;

public interface TokenService<T> {
    T getByToken(String token) throws ExpiredTokenException;

    String generateToken(T object);

    String refreshToken(String token) throws InvalidTokenException, NotExpiredTokenException, TooExpiredTokenException;
}
