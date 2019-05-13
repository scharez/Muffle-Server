package filter;

import jwt.JwtBuilder;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class JwtFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext rc, ContainerResponseContext responseContext){

        JwtBuilder jwtbuilder = new JwtBuilder();

        if (rc.getUriInfo().getPath().contains("login") || rc.getUriInfo().getPath().contains("register")) {

            System.out.println("No JWT Token needed, lol");
            return;
        }

        try {
            String [] auth = rc.getHeaderString("Authorization").split("\\s");

            jwtbuilder.checkSubject(auth[1]);

            System.out.println("He der hat kan Token, lol");

        } catch ( Exception ex ) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

}