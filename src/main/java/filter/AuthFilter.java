package filter;

import helper.JsonBuilder;
import jwt.JwtHelper;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Priority(1)
@Provider
public class AuthFilter implements ContainerResponseFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext rc, ContainerResponseContext responseContext){

        JsonBuilder jb = new JsonBuilder();
        JwtHelper jwt = new JwtHelper();

        Response responseForInvalidRequest = validateRequest();

        if (rc.getUriInfo().getPath().contains("login") || rc.getUriInfo().getPath().contains("register")) {

            System.out.println("No JWT Token needed, lol");

            return;
        } else {

            try {

                String [] auth = rc.getHeaderString("Authorization").split("\\s");

                jwt.checkSubject(auth[1]);



            } catch ( Exception ex ) {

                System.out.println("try2");

                rc.abortWith(responseForInvalidRequest);

                //System.out.println(responseForInvalidRequest.getEntity());
                //rc.abortWith();
            }
        }
    }



    private Response validateRequest() {

            String msg = String.format("HAHA");
            CacheControl cc = new CacheControl();
            cc.setNoStore(true);
            Response response = Response.status(Response.Status.NOT_IMPLEMENTED)
                    .cacheControl(cc)
                    .entity(msg)
                    .build();
            return response;
    }

    // Mit Switch Role überprüfen und mit einem Pattern mit dem Path vergleichen
}

