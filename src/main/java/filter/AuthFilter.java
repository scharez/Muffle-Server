package filter;

import annoation.NotSecure;
import annoation.Secure;
import entity.Role;
import helper.JsonBuilder;
import helper.JwtHelper;
import repository.Repository;
import utils.PropertyUtil;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.Properties;

@Priority(Priorities.AUTHENTICATION)
@Provider
public class AuthFilter implements ContainerRequestFilter {

    private JsonBuilder jb = new JsonBuilder();
    private JwtHelper jwt = new JwtHelper();

    private Properties message_props;

    private String token;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext rc) {
        message_props = PropertyUtil.getInstance().getMessageProps();
        String authorizationHeader = rc.getHeaderString(HttpHeaders.AUTHORIZATION);

        Method resourceMethod = resourceInfo.getResourceMethod();

        Secure secure = resourceMethod.getAnnotation(Secure.class);

        if (resourceMethod.isAnnotationPresent(NotSecure.class) || resourceMethod.isAnnotationPresent(Secure.class)) {

            if (resourceMethod.isAnnotationPresent(NotSecure.class)) {
                return;
            } else {

                try {

                    token = authorizationHeader.substring("Bearer".length()).trim();
                    jwt.checkSubject(token);

                    if (isUserInRole(jwt.getRole(token), secure.value())) {

                        Repository.getInstance().saveHeader(token);
                    } else {

                        Response res = validateRequest(message_props.getProperty("error.notAllowed"));
                        rc.abortWith(res);
                    }

                } catch (Exception ex) {
                    Response res = validateRequest(message_props.getProperty("error.invalidToken"));
                    rc.abortWith(res);
                }
            }

        } else {

            Response responseForInvalidRequest = validateRequest(message_props.getProperty("error.internal"));

            rc.abortWith(responseForInvalidRequest);
        }
    }

    private boolean isUserInRole(Role userRole, Role[] roles) {

        for (Role role : roles) {
            if (role.equals(userRole) || role.equals(Role.EVERYONE))
                return true;
        }
        return false;
    }


    private Response validateRequest(String content) {

        String msg = jb.generateResponse("Error", message_props.getProperty("error.unauthorized"), content);
        CacheControl cc = new CacheControl();
        cc.setNoStore(true);
        return Response.status(Response.Status.UNAUTHORIZED)
                .cacheControl(cc)
                .entity(msg)
                .build();
    }
}

