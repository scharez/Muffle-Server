package service;

import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import transferObjects.MufflerTO;
import repository.Repository;

@Path("muffle")
public class MuffleService {

    @Path("message")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String message() {
        return " REST Service powered by scharez.at ";
    }

    /**
     * Register a new Muffler
     *
     * @param muffler the Transfer Object of the Muffler entity
     * @return a json which can contain an error or a successfully register message
     */

    @Path("register")
    @POST
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
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String loginUser(MufflerTO muffler) {
        return Repository.getInstance().loginUser(muffler.getUsername(), muffler.getPassword());
    }

    /**---------------------------------------------------------------------------------------------------------------*/


    @Path("url")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String test(@HeaderParam(HttpHeaders.AUTHORIZATION) String authHeader, @QueryParam("url") String url) {


        // Überprüfen ob header leer ist
        String username = getUsername(authHeader);


        return Repository.getInstance().test(username, url);
    }



    private String getUsername(String authHeader) {

        String[] auth = authHeader.split("\\s");

        return auth[1];
    }

    //private String

    /*
    Methode schreiben, wo man den Token untersucht, und dann schaut, ob er diese methode aufrufen darf.

    private String lol(Role allowedRole, String authHeader) {

            JwtHelper jwtbuilder = new JwtHelper();
            private JsonBuilder jb = new JsonBuilder();

            return "";
    }

     - Lehrer fragen wie das mit Custom Annotations funktioniert

     - statt exception Response zurückschreiben
     */
}

