package filter;

import annoation.NotSecure;
import annoation.RolesAllowed;
import entity.Role;
import helper.JsonBuilder;
import jwt.JwtHelper;
import repository.Repository;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;

@Priority(Priorities.AUTHENTICATION)
@Provider
public class AuthFilter implements ContainerRequestFilter {

    JsonBuilder jb = new JsonBuilder();
    JwtHelper jwt = new JwtHelper();

    private String token;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext rc){

        String authorizationHeader = rc.getHeaderString(HttpHeaders.AUTHORIZATION);

        Method resourceMethod = resourceInfo.getResourceMethod();
        

        RolesAllowed rolesAllowed = resourceMethod.getAnnotation(RolesAllowed.class);

        if(resourceMethod.isAnnotationPresent(NotSecure.class) || resourceMethod.isAnnotationPresent(RolesAllowed.class)) {

            if(resourceMethod.isAnnotationPresent(NotSecure.class)) {
                return;
            } else {

                try {

                    token = authorizationHeader.substring("Bearer".length()).trim();
                    jwt.checkSubject(token);

                    if (isUserInRole(jwt.getRole(token), rolesAllowed.value())) {

                        Repository.getInstance().saveHeader(token);
                    } else {

                        Response res = validateRequest("You are not allowed");
                        rc.abortWith(res);
                    }


                } catch (Exception ex) {
                    Response res = validateRequest("Auth-Token Error");
                    rc.abortWith(res);
                }
            }

        } else {

            Response responseForInvalidRequest = validateRequest("Server Error");

            rc.abortWith(responseForInvalidRequest);
        }
    }

    private boolean isUserInRole(Role userRole, Role[] roles) {

        if(userRole == Role.PREMUFFLER) {  // highest user rank
            return true;
        }

        for(Role role: roles) {
            if(role.equals(userRole))
                return true;
        }
        return false;
    }


    private Response validateRequest(String content) {

            String msg = String.format(jb.generateResponse("Error", "Unauthorized",content));
            CacheControl cc = new CacheControl();
            cc.setNoStore(true);
            Response response = Response.status(Response.Status.UNAUTHORIZED)
                    .cacheControl(cc)
                    .entity(msg)
                    .build();
            return response;
    }
}

