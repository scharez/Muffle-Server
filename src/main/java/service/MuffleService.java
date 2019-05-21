package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import annoation.NotSecure;
import annoation.RolesAllowed;
import entity.Playlist;
import transferObjects.MufflerTO;
import repository.Repository;

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

    @Path("addSongFromURL/{url}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed
    public String addSongFromURL(@PathParam("url")  String url, Playlist playlist) {
        return Repository.getInstance().addSongFromURL(url, playlist);
    }


    @Path("refreshPlaylist")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed
    public String refreshPlaylist(Playlist playlist) {
        return Repository.getInstance().refreshPlaylist(playlist);
    }

    @Path("createPlaylist")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed
    public String createPlaylist(Playlist playlist) { return Repository.getInstance().creatPlaylist(playlist); }

    @Path("getPlaylists")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed
    public String getPlaylists() { return Repository.getInstance().getPlaylists(); }

}
