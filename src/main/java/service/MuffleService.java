package service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import annoation.NotSecure;
import annoation.Secure;
import transferObjects.MufflerTO;
import repository.Repository;
import transferObjects.PlaylistTO;
import transferObjects.SongTO;

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

    @Path("verify")
    @GET
    @NotSecure
    @Produces(MediaType.TEXT_HTML)
    public String confirmMail(@QueryParam("token") String token) {

        return Repository.getInstance().confirmMail(token);
    }
    /**
     * ---------------------------------------------------------------------------------------------------------------
     */

    @Path("addSongFromURL")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secure
    public String addSongFromURL(SongTO song) {
        return Repository.getInstance().addSongFromURL(song);
    }

    @Path("refreshPlaylist")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secure
    public String refreshPlaylist(PlaylistTO playlist) {
        return Repository.getInstance().refreshPlaylist(playlist);
    }

    @Path("createPlaylist")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secure
    public String createPlaylist(PlaylistTO playlist) { return Repository.getInstance().creatPlaylist(playlist); }

    @Path("getPlaylists")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secure
    public String getPlaylists() { return Repository.getInstance().getPlaylists(); }

    @Path("getSongsFromPlaylist")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secure
    public String getSongs(PlaylistTO playlist) { return Repository.getInstance().getSongs(playlist); }

}
