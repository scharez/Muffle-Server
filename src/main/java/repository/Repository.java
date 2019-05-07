package repository;

import entity.Muffler;

import java.util.ArrayList;

import entity.Song;
import entity.Playlist;
import com.google.gson.Gson;

public class Repository {

    private static Repository instance = null;

    //All users of our app will be saved in this list
    private ArrayList<Muffler> muffleUsers = new ArrayList<Muffler>();

    private Repository() {
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    public void addNewUser(Muffler newMuffler) {
        this.muffleUsers.add(newMuffler);
    }

    public String loginUser(Muffler muffler){
        for(Muffler m : muffleUsers){
            if(m.getUsername().equals(muffler.getUsername()) && m.getPassword().equals(muffler.getPassword())){
                return new Gson().toJson(m);
            }
        }
        return "";
    }

    public String getPlaylists(String username) {
        Gson gson = new Gson();
        for (Muffler m : muffleUsers) {
            if (m.getUsername().equals(username)) {
                return gson.toJson(m.getPlaylists());
            }
        }
        return "";
    }

    public String addNewSong(String username, String playlistName, Song song) {
        Gson gson = new Gson();
        Muffler muffler = null;
        for (Muffler m : muffleUsers) {
            if (m.getUsername().equals(username)) {
                muffler = m;
                for (entity.Playlist p : m.getPlaylists()) {
                    if (p.getName().equals(playlistName)) {
                        p.getSongs().add(song);
                    }
                }
            }
        }
        return "";
    }

    public void initUsers() {
        Song s1 = new Song("www.google.com", "Dear Darling", "Olly Murse", 3.42);
        Song s2 = new Song("www.google.com", "Code it!", "The Feppers", 2.57);
        Song s3 = new Song("www.google.com", "The Fighter", "Keith Urban", 3.24);
        Song s4 = new Song("www.google.com", "Eye of the Tiger", "Survivor", 3.42);
        Song s5 = new Song("www.google.com", "Sattelite", "Riste Against", 4.02);
        Song s6 = new Song("www.google.com", "Roar", "Katy Perry", 3.33);
        Song s7 = new Song("www.google.com", "Sucker", "Jonas Brothers", 3.42);
        Song s8 = new Song("www.google.com", "Don't Call Me Up", "Mabel", 2.58);

        Playlist p1 = new Playlist("Chill");
        p1.getSongs().add(s1);
        p1.getSongs().add(s2);
        p1.getSongs().add(s3);
        Playlist p2 = new Playlist("Fresh");
        p2.getSongs().add(s3);
        p2.getSongs().add(s2);
        p2.getSongs().add(s8);
        Playlist p3 = new Playlist("Workout");
        p3.getSongs().add(s5);
        p3.getSongs().add(s4);
        p3.getSongs().add(s3);
        Playlist p4 = new Playlist("Gaming");
        p4.getSongs().add(s7);
        p4.getSongs().add(s1);
        p4.getSongs().add(s4);

        Muffler m1 = new Muffler("manifadi", "passme", "Manuel", "Fadljevic");
        m1.getPlaylists().add(p1);
        Muffler m2 = new Muffler("schrez", "passme", "Stefan", "Scharinger");
        m2.getPlaylists().add(p2);
        Muffler m3 = new Muffler("feppa", "passme", "Daniel", "Pfeffer");
        m3.getPlaylists().add(p3);
        Muffler m4 = new Muffler("killaPinguin", "passme", "Julian", "Danninger");
        m4.getPlaylists().add(p4);

        muffleUsers.add(m1);
        muffleUsers.add(m2);
        muffleUsers.add(m3);
        muffleUsers.add(m4);
    }

    public String createNewPlaylist(String user, String name) {
        Gson gson = new Gson();
        for (Muffler m : muffleUsers) {
            if (m.getUsername().equals(user)) {
                m.getPlaylists().add(new Playlist(name));
            }
        }
        return "New Playlist created: " + name;
    }
}
