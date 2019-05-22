package helper;

import entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

import java.util.Date;


public class JwtHelper {

    private final long ACCESS_TOKEN_VALIDITY_SECONDS = 100000;

    private PropertyLoader pl = new PropertyLoader();

    public String create(String subject, Role role) {

        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, pl.prop.getProperty("jwt.key"))
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .claim("role", role.toString())
                .compact();
    }

    public String checkSubject(String token) {

        try {
            return Jwts.parser()
                    .setSigningKey(pl.prop.getProperty("jwt.key"))
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
                .setSigningKey(pl.prop.getProperty("jwt.key"))
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);

        return Role.valueOf(role);
    }

}

