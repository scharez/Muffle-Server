package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import annoation.NotSecure;
import annoation.RolesAllowed;
import entity.Playlist;
import transferObjects.MufflerTO;
import repository.Repository;

import java.util.List;

@Path("muffle")
public class MuffleService {

    /**
     * Register a new Muffler
     *
     * @param muffler the Transfer Object of the Muffler entity
     * @return a json which can contain an error or a successfully register message
     */

    @Path("register")
    @POST
    @NotSecure
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String registerUser(MufflerTO muffler) {
        return Repository.getInstance().registerUser(muffler.getUsername(), muffler.getPassword(), muffler.getEmail());
    }

    /**
     * Login a Muffler
     *
     * @param muffler the Transfer Object of the Muffler entity
     * @return a json which can contain an error or a successfully login message
     */

    @Path("login")
    @POST
    @NotSecure
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String loginUser(MufflerTO muffler) {
        return Repository.getInstance().loginUser(muffler.getUsername(), muffler.getPassword());
    }

    /**
     * ---------------------------------------------------------------------------------------------------------------
     */

    @Path("getPlaylists")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed()
    public String getPlaylists() {
        return Repository.getInstance().getPlaylists();
    }

}
