package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import transferObjects.MufflerTO;
import entity.Song;
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
     * Registers a new Muffler
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


    @Path("url")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addSong(@QueryParam("url") String url) {

        return Repository.getInstance().downloadSong(url);
    }

    //private String

    /*
    Methode schreiben, wo man den Token untersucht, und dann schaut, ob er diese methode aufrufen darf.

    private String lol(Role allowedRole, String authHeader) {

            JwtBuilder jwtbuilder = new JwtBuilder();
            private JsonBuilder jb = new JsonBuilder();

            return "";
    }

    Lehrer fragen wie das mit Custom Annotations funktioniert und statt exception Response zurückschreiben
     */











    @Path("addSong")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addNewSong(@QueryParam("username") String username, @QueryParam("playlist") String playlistName, Song song) {
        return Repository.getInstance().addNewSong(username, playlistName, song);
    }

    @Path("createPlaylist")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String createNewPlaylist(@QueryParam("username") String user, @QueryParam("playlist") String name) {
        return Repository.getInstance().createNewPlaylist(user, name);
    }

    @Path("getPlaylists")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUserPlaylist(@QueryParam("username") String username) {
        return Repository.getInstance().getPlaylists(username);
    }
}

