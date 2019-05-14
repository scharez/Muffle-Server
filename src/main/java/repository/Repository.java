package repository;

import entity.Muffler;

import entity.Role;
import entity.Song;
import helper.JsonBuilder;
import helper.MP3Downloader;
import jwt.JwtHelper;
import org.bouncycastle.util.encoders.Hex;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private static Repository instance = null;

    MP3Downloader dw = new MP3Downloader();
    JsonBuilder jb = new JsonBuilder();
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MufflePU");
    private EntityManager em = emf.createEntityManager();
    private JwtHelper jwtb = new JwtHelper();

    private Repository() {
    }

    public static Repository getInstance() {
        if (instance == null) {
            instance = new Repository();
        }
        return instance;
    }

    /**
     * register a Muffler
     *
     * @param username username of the user
     * @param password password of the user
     * @param email email of the user
     * @return a custom json
     */

    public String registerUser(String username, String password, String email) {



        Muffler user = new Muffler(username, password, email);

        user.setRole(Role.MUFFLER);

        TypedQuery<Long> queryUniqueName = em.createQuery("SELECT COUNT(m) FROM Muffler m WHERE m.username = :username", Long.class);
        queryUniqueName.setParameter("username", username);

        long numberOfEntriesName = queryUniqueName.getSingleResult();

        if(numberOfEntriesName != 0) {
            return jb.generateResponse("error","",""); //Username schon vergeben
        }

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        return jb.generateResponse("","jawoi register!","");
    }

    /**
     * login a Muffler
     *
     * @param username username of the user
     * @param password password of the user
     * @return a custom json
     */

    public String loginUser(String username, String password) {

        TypedQuery<Muffler> query = em.createQuery("SELECT m FROM Muffler m WHERE m.username = :username", Muffler.class);
        query.setParameter("username", username);

        List<Muffler> result = query.getResultList();

        if (result.size() == 0) {
            return jb.generateResponse("error","",""); // Error
        }

        Muffler user = result.get(0);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Hex.decode(user.getSalt()));

            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            if (!new String(Hex.encode(hash)).equals(user.getPassword())) {
                return jb.generateResponse("login","","");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String jwtToken = jwtb.create(user.getUsername());

        return jb.generateResponse("", "jawoi", jwtToken); //Token und stuff
    }








    public String test(String username, String url) {

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(() -> {

        });

        System.out.println(dw.download("", url));

        //Zuerst soll in der Datenbank überprüft werden, ob der song schonmal downgeloaded worden ist


        return "";
    }


    /*
    todo Muffle Entity Namedqueries machen
     */


















    public String getPlaylists(String username) {

        return "";
    }

    public String addNewSong(String username, String playlistName, Song song) {


        return "";
    }

    public String createNewPlaylist(String user, String name) {

        return "";
    }
}
