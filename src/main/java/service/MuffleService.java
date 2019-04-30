package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import Entity.Muffler;
import Entity.Song;
import Repository.Repository;
import com.sun.media.jfxmedia.Media;

@Path("muffle")
public class MuffleService {

    Repository repo = Repository.getInstance();

    @Path("message")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String message() {
        return " REST Service powered by scharez.at ";
    }


    @Path("newuser")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void addNewUser(Muffler muffler) {
        repo.addNewUser(muffler);
    }

    @Path("addsong")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addNewSong(@QueryParam("username") String username, @QueryParam("playlist") String playlistName, Song song) {
        return repo.addNewSong(username, playlistName, song);
    }

    @Path("createplaylist")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String createNewPlaylist(@QueryParam("username") String user, @QueryParam("playlist") String name) {
        return repo.createNewPlaylist(user, name);
    }

    @Path("getplaylists")
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

