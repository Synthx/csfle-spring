package com.example.pocjwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Override
    public Jwt decode(String token) throws JwtException {
        final var algorithm = Algorithm.HMAC512("ceci-est-un-secret");

        try {
            final var verifier = JWT.require(algorithm)
                    .withIssuer("auth0")
                    .build();
            final var decodedToken = verifier.verify(token);

            return Jwt.withTokenValue(token)
                    .subject(decodedToken.getSubject())
                    .issuer(decodedToken.getIssuer())
                    .header("test", "test")
                    .claims(c -> c.putAll(decodedToken.getClaims()))
                    .build();
        } catch (JWTVerificationException e) {
            throw new JwtException(e.getMessage(), e);
        }
    }
}
