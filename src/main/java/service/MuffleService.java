package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import annoation.NotSecure;
import annoation.RolesAllowed;
import entity.Role;
import transferObjects.MufflerTO;
import repository.Repository;

@Path("muffle")
public class MuffleService {

    @Path("message")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({Role.MUFFLER})
    public String message() {
        return " REST Service powered by scharez.at ";
    }

    @Path("test")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @RolesAllowed({Role.PREMUFFLER})
    public String test() {
        return " Only Premium";
    }

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


    @Path("url")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @NotSecure
    public String test(@QueryParam("url") String url) {



        return Repository.getInstance().test("", url);
    }

    private String getUsername (){

        return "";
    }

}
