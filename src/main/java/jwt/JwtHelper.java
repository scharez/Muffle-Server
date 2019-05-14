package jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;


public class JwtHelper {

    private String key = "secret";

    public String create(String subject) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setSubject(subject)
                .compact();
    }

    public String checkSubject(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (SignatureException e) {
            return "LOL, der derf nd";
        }
    }

}

