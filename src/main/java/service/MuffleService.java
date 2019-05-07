package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import entity.Muffler;
import entity.Song;
import repository.Repository;

@Path("muffle")
public class MuffleService {

    Repository repo = Repository.getInstance();

    @Path("message")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String message() {
        return " REST Service powered by scharez.at ";
    }


    @Path("register")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addNewUser(Muffler muffler) {
        repo.addNewUser(muffler);
    }

    @Path("login")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String loginUser(Muffler muffler){
        return repo.loginUser(muffler);
    }



    @Path("addSong")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addNewSong(@QueryParam("username") String username, @QueryParam("playlist") String playlistName, Song song) {
        return repo.addNewSong(username, playlistName, song);
    }

    @Path("createPlaylist")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String createNewPlaylist(@QueryParam("username") String user, @QueryParam("playlist") String name) {
        return repo.createNewPlaylist(user, name);
    }

    @Path("getPlaylists")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUserPlaylist(@QueryParam("username") String username) {
        return repo.getPlaylists(username);
    }

    @Path("init")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String initUser() {
        repo.initUsers();
        return "Init finished!";
    }


}

