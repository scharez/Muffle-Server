package repository;

import com.google.gson.JsonArray;
import entity.*;

import helper.JsonBuilder;
import helper.JwtHelper;
import mail.Mail;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONArray;
import org.json.JSONObject;
import transferObjects.PlaylistTO;
import transferObjects.SongTO;

import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private static Repository instance = null;

    private JsonBuilder jb = new JsonBuilder();
    private JwtHelper jwt = new JwtHelper();
    private Mail mailer = new Mail();

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MufflePU");
    private EntityManager em = emf.createEntityManager();

    private ExecutorService executor = Executors.newCachedThreadPool();

    private String token;

    private Repository() {
    }

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
     * @param email    email of the user
     * @return a custom json
     */

    public String registerUser(String username, String password, String email) {

        Muffler user = new Muffler(username, password, email);
        user.setRole(Role.MUFFLER);

        VerificationToken verificationToken = new VerificationToken(user);

        TypedQuery<Long> queryUniqueName = em.createQuery("SELECT COUNT(m) FROM Muffler m WHERE m.username = :username", Long.class);
        queryUniqueName.setParameter("username", username);

        TypedQuery<Long> queryUniqueEmail = em.createQuery("SELECT COUNT(m) FROM Muffler m WHERE m.email = :email", Long.class);
        queryUniqueEmail.setParameter("email", email);

        long numberOfEntriesName = queryUniqueName.getSingleResult();
        long numberOfEntriesEmail = queryUniqueEmail.getSingleResult();

        if (numberOfEntriesName != 0) {
            return jb.generateResponse("error", "register", "Username already exists");
        }

        if (numberOfEntriesEmail != 0) {
            return jb.generateResponse("error", "register", "Email already exists");
        }

        user.setVerificationToken(verificationToken);

        executor.execute(() -> mailer.sendConfirmation(verificationToken, user));

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        return jb.generateResponse("success", "register", "Please confirm your email now");
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
            return jb.generateResponse("error", "login", "User does not exist"); // Error
        }

        Muffler user = result.get(0);

        if(!user.isVerified()) {
            return jb.generateResponse("error", "login", "Please confirm your email first");
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Hex.decode(user.getSalt()));

            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            if (!new String(Hex.encode(hash)).equals(user.getPassword())) {
                return jb.generateResponse("error", "login", "Wrong Password");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        user.setRole(Role.MUFFLER);

        String jwtToken = jwt.create(user.getUsername(), user.getRole());

        return jb.generateResponse("success", "login", jwtToken);
    }

    public String confirmMail(String token) {

        TypedQuery<VerificationToken> queryToken = em.createQuery("SELECT v FROM VerificationToken v WHERE v.token = :token", VerificationToken.class);
        queryToken.setParameter("token", token);

        List<VerificationToken> tokenList = queryToken.getResultList();

        if (tokenList.size() == 0) {
            return "<html><head><title>Something went wrong!</title><meta charset=\"UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"https://muffle.scharez.at/assets/web/favicon.ico\"><style>*{font-family:\"Roboto\",\"Helvetica Neue\",sans-serif;text-align:center}body{background-image:url(\"https://muffle.scharez.at/assets/web/background.jpg\");background-repeat:no-repeat;background-size:cover}.middlePosition{width:30%;height:45vh;position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);border-radius:2em;padding:2em}.middlePosition h1{color:#ffddab}.middlePosition p{color:rgba(255,221,171,0.7)}.middlePosition button{border-radius:4em;border:1px solid #ffab2d;margin:1em;opacity:.6;transition:opacity .4s;background-color:#ffab2d;padding:.8em;color:white;margin-top:5em}.middlePosition button:hover{opacity:.85}</style></head><body><div class=\"middlePosition\"><img src=\"https://muffle.scharez.at/assets/web/logo.svg\" width=\"40%\"><h1>Something went wrong</h1><p>Please contact support</p><a href=\"https://support.scharez.at\"><button>Support</button></a></div></body></html>";
        }

        VerificationToken verifyToken = tokenList.get(0);

        Date currentDate = new Date();
        Date tokenDate = verifyToken.getExpire();

        if (tokenDate.compareTo(currentDate) >= 0) {
            Muffler muffler = verifyToken.getMuffler();

            muffler.setVerified(true);

            em.getTransaction().begin();
            em.remove(verifyToken);
            em.getTransaction().commit();

            return "<html><head><title>Verificated!</title><meta charset=\"UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"https://muffle.scharez.at/assets/web/favicon.ico\"><style>*{font-family:\"Roboto\",\"Helvetica Neue\",sans-serif;text-align:center}body{background-image:url(\"https://muffle.scharez.at/assets/web/background.jpg\");background-repeat:no-repeat;background-size:cover}.middlePosition{width:30%;height:45vh;position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);border-radius:2em;padding:2em}.middlePosition h1{color:#ffddab}.middlePosition p{color:rgba(255,221,171,0.7)}.middlePosition button{border-radius:4em;border:1px solid #ffab2d;margin:1em;opacity:.6;transition:opacity .4s;background-color:#ffab2d;padding:.8em;color:white;margin-top:5em}.middlePosition button:hover{opacity:.85}</style></head><body><div class=\"middlePosition\"><img src=\"https://muffle.scharez.at/assets/web/logo.svg\" width=\"40%\"><h1>" + muffler.getUsername().toUpperCase() +  " you are now verified!</h1><p>You can now use Muffle!</p><a href=\"https://muffle.scharez.at\"><button>Go to Muffle</button></a></div></body></html>";
        }

        return "<html><head><title>Token expired</title><meta charset=\"UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"https://muffle.scharez.at/assets/web/favicon.ico\"><style>*{font-family:\"Roboto\",\"Helvetica Neue\",sans-serif;text-align:center}body{background-image:url(\"https://muffle.scharez.at/assets/web/background.jpg\");background-repeat:no-repeat;background-size:cover}.middlePosition{width:30%;height:45vh;position:absolute;left:50%;top:50%;transform:translate(-50%,-50%);border-radius:2em;padding:2em}.middlePosition h1{color:#ffddab}.middlePosition p{color:rgba(255,221,171,0.7)}.middlePosition button{border-radius:4em;border:1px solid #ffab2d;margin:1em;opacity:.6;transition:opacity .4s;background-color:#ffab2d;padding:.8em;color:white;margin-top:5em}.middlePosition button:hover{opacity:.85}</style></head><body><div class=\"middlePosition\"><img src=\"https://muffle.scharez.at/assets/web/logo.svg\" width=\"40%\"><h1>Your Token has expired!</h1><p>Register again</p><a href=\"https://muffle.scharez.at/register\"><button>Register again</button></a></div></body></html>";
    }


    public String addSongFromURL(String url) {

        Muffler muffler = getMuffler();

        if (muffler == null) {
            return jwtError();
        }

        String storageURL = "/var/www/muffle.scharez.at/assets/songs/%(title)s.%(ext)s";


        //Zuerst soll in der Datenbank überprüft werden, ob der song schonmal downgeloaded worden ist

        Runtime rt = Runtime.getRuntime();

        executor.execute(() -> {
            try {
                rt.exec("youtube-dl -o " + storageURL+ " -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 " + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // youtube-dl -o "/Users/scharez/Desktop/%(title)s.%(ext)s" -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 https://www.youtube.com/watch?v=Xm8-bw3nLMA

        return jb.generateResponse("lol", "lol", "lol");
    }

    public String addSongFromSearch() {

        return "";

    }

    public String getPlaylists() {

        Muffler muffler = getMuffler();

        if (muffler == null) {
            return jwtError();
        }

        List<Playlist> result = muffler.getPlaylists();

        if (result.size() == 0) {
            return jb.generateResponse("hint", "getPlaylists", "No Playlists");
        }
        result.forEach(p -> p.setSongs(null));

        return jb.generateDataResponse("data", "getPlaylists", new JSONArray(result));
    }

    public String creatPlaylist(PlaylistTO playlist) {

        Muffler muffler = getMuffler();

        if (muffler == null) {
            return jwtError();
        }

        for (Playlist p : muffler.getPlaylists()) {
            if (p.getName().equalsIgnoreCase(playlist.getName())) {
                return jb.generateResponse("hint", "createPlaylist", playlist.getName() + " already exist");
            }
        }

        Playlist p = new Playlist();

        p.setName(playlist.getName());

        muffler.getPlaylists().add(p);

        em.getTransaction().begin();
        em.merge(muffler);
        em.getTransaction().commit();

        return jb.generateResponse("hint", "createPlaylist", playlist.getName() + " created");
    }

    public String getSongs(PlaylistTO playlist) {

        Muffler muffler = getMuffler();

        if (muffler == null) {
            return jwtError();
        }

        List<Song> songs = null;

        List<Playlist> result = muffler.getPlaylists();

        if (result.size() == 0) {
            return jb.generateResponse("hint", "getSongs", "No Playlist");
        }

        for (Playlist p : result) {
            if (p.getName().equals(playlist.getName())) {
                if (p.getSongs() == null) {
                    return jb.generateResponse("hint", "getSongs", "Playlist contains no Songs");
                }
                songs = p.getSongs();
            }
        }
        return jb.generateDataResponse("data", "getSongs", new JSONArray(songs));
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
     * @return Muffler
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

    private String jwtError() {
        return jb.generateResponse("error", "jwt", "Wrong Token!");
    }
}
