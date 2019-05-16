package repository;

import entity.Muffler;

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

        String jwtToken = jwt.create(user.getUsername(), user.getRole());

        return jb.generateResponse("", "jawoi", jwtToken); //Token und stuff
    }


    public String test(String username, String url) {

        //Zuerst soll in der Datenbank überprüft werden, ob der song schonmal downgeloaded worden ist

        Runtime rt = Runtime.getRuntime();

        /*
        executor.execute(() -> {
            try {
                rt.exec("youtube-dl -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 " + url);
            } catch (IOException e) {


            }

        });

        */

        /*value="${jdbc.url}"  In persistence xml file*/

        // youtube-dl -o "/Users/scharez/Desktop/%(title)s.%(ext)s" -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 https://www.youtube.com/watch?v=Xm8-bw3nLMA





        return jb.generateResponse("","","");
    }

    /**
     *
     * @param token
     */
    public void saveHeader(String token) {
            this.token = token;
    }

    /**
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
