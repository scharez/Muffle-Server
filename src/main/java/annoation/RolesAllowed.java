package annoation;

import entity.Role;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RolesAllowed {

    Role[] value() default Role.MUFFLER;
}
