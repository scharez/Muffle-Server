package entity;

import javax.persistence.Enumerated;

public enum Role {

    @Enumerated
    MUFFLER, PREMUFFLER, EVERYONE
}
