package com.signatures.services.impl;

import com.signatures.entities.Company;
import com.signatures.exceptions.ExpiredTokenException;
import com.signatures.exceptions.InvalidTokenException;
import com.signatures.exceptions.NotExpiredTokenException;
import com.signatures.exceptions.TooExpiredTokenException;
import com.signatures.properties.TokenProperties;
import com.signatures.services.interfaces.TokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Slf4j
@Service
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenServiceImpl implements TokenService<Company> {
    private final TokenProperties tokenProperties;
    private final Key tokenKey;

    @Override
    public Company getByToken(String token) throws ExpiredTokenException {
        try {
            Long id = Jwts
                    .parserBuilder()
                    .setSigningKey(tokenKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("id", Long.class);

            log.info(
                    String.format(
                            "Token's company id for use is %d",
                            id
                    )
            );

            return new Company(id);
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (SignatureException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        }
    }

    @Override
    public String generateToken(Company company) {

        log.info(
                String.format(
                        "Company with id %d is generating token",
                        company.getId()
                )
        );

        return Jwts.builder()
                .setExpiration(
                        new Date(
                                (new Date()).getTime() + tokenProperties.getExpiration()
                        )
                )
                .claim("id", company.getId())
                .signWith(
                        tokenKey
                )
                .compact();
    }

    @Override
    public String refreshToken(String token) throws InvalidTokenException, NotExpiredTokenException, TooExpiredTokenException {
        try {
            Jwts
                    .parserBuilder()
                    .setSigningKey(tokenKey)
                    .build()
                    .parseClaimsJws(token);

            throw new NotExpiredTokenException();

        } catch (SignatureException | IllegalArgumentException e) {
            throw new InvalidTokenException();
        } catch (ExpiredJwtException e) {

            // Если токен слишком старый, то отправляем юзера на авторизацию
            if (new Date().getTime() - e.getClaims().getExpiration().getTime() > tokenProperties.getRefreshExpiration()) {
                throw new TooExpiredTokenException();
            } else {

                Company company = new Company(
                        e.getClaims().get("id", Long.class)
                );

                log.info(
                        String.format(
                                "Company with id %d is refreshing token",
                                company.getId()
                        )
                );

                return generateToken(
                        company
                );
            }

        }
    }
}
