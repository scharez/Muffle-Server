package jwt;

import entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.util.Date;


public class JwtHelper {

    private final long ACCESS_TOKEN_VALIDITY_SECONDS = 100000;
    private final String KEY = "secret";

    public String create(String subject, Role role) {

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, KEY)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .claim("role", role.toString())
                .compact();
    }

    public String checkSubject(String token) {

        try {
            return Jwts.parser()
                    .setSigningKey(KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (SignatureException e) {
            return null;
        }
    }

    public Role getRole(String token) {

        String role;

        role =  Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);

        return Role.valueOf(role);
    }

}

