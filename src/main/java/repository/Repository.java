package repository;

import entity.Muffler;

import entity.Playlist;
import entity.Role;
import entity.Song;
import helper.JsonBuilder;
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
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private static Repository instance = null;

    private JsonBuilder jb = new JsonBuilder();
    private JwtHelper jwt = new JwtHelper();

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MufflePU");
    private EntityManager em = emf.createEntityManager();

    private ExecutorService executor = Executors.newCachedThreadPool();

    private String token;

    private Repository() {}

    public static synchronized Repository getInstance() {
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

        TypedQuery<Long> queryUniqueEmail = em.createQuery("SELECT COUNT(m) FROM Muffler m WHERE m.email = :email", Long.class);
        queryUniqueEmail.setParameter("email", email);

        long numberOfEntriesName = queryUniqueName.getSingleResult();
        long numberOfEntriesEmail = queryUniqueName.getSingleResult();

        if(numberOfEntriesName != 0) {
            return jb.generateResponse("error","register","Username already exists");
        }

        if(numberOfEntriesEmail != 0) {
            return jb.generateResponse("error","register","Email already exists");
        }

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        return jb.generateResponse("success","register","Successfully Registered");
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
            return jb.generateResponse("error","login","User does not exist"); // Error
        }

        Muffler user = result.get(0);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Hex.decode(user.getSalt()));

            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            if (!new String(Hex.encode(hash)).equals(user.getPassword())) {
                return jb.generateResponse("error","login","Wrong Password");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String jwtToken = jwt.create(user.getUsername(), user.getRole());

        return jb.generateResponse("success", "login", jwtToken);
    }


    public String test(String username, String url) {

        //Zuerst soll in der Datenbank überprüft werden, ob der song schonmal downgeloaded worden ist

        Runtime rt = Runtime.getRuntime();

        /*
        executor.execute(() -> {
            try {
                rt.exec("youtube-d
                l -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 " + url);
            } catch (IOException e) {


            }

        });

        */

        /*value="${jdbc.url}"  In persistence xml file*/

        // youtube-dl -o "/Users/scharez/Desktop/%(title)s.%(ext)s" -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 https://www.youtube.com/watch?v=Xm8-bw3nLMA





        return jb.generateResponse("","","");
    }


    public String getPlaylists() {

        Muffler m = getMuffler();

        if(Objects.requireNonNull(m).getPlaylists() == null) {
            return jb.generateResponse("error", "getPlaylists", "No Playlists");
        }

        return m.getPlaylists().toString();
    }

    public String getSongs() {

        Muffler m = getMuffler();


        return "";

    }


    /**
     * get the JWT-Token into Repository from the Filter
     *
     * @param token
     */

    public void saveHeader(String token) {
            this.token = token;
    }

    /**
     * get a User from DB with his Token
     *
     * @return
     */

    private Muffler getMuffler() {

        String username = jwt.checkSubject(this.token);

        TypedQuery<Muffler> query = em.createQuery("SELECT m FROM Muffler m WHERE m.username = :username", Muffler.class);
        query.setParameter("username", username);

        List<Muffler> result = query.getResultList();

        // check if user exists, but user should exist bc he has a token lol
        if (result.size() == 0) {
            return null;
        }

        return result.get(0);
    }


}
