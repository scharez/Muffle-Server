package repository;

import com.google.gson.Gson;
import entity.*;
import helper.JsonBuilder;
import helper.JwtHelper;
import mail.Mail;
import org.bouncycastle.util.encoders.Hex;
import org.json.JSONArray;
import transferObjects.PlaylistTO;
import utils.FileUtil;
import utils.PropertyUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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

    private Properties messageProps;
    private Properties configProps;

    private Repository() {
        this.messageProps = PropertyUtil.getInstance().getMessageProps();
        this.configProps = PropertyUtil.getInstance().getConfigProps();
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
            return jb.generateResponse("error", "register", this.messageProps.getProperty("auth.userExists"));
        }

        if (numberOfEntriesEmail != 0) {
            return jb.generateResponse("error", "register", messageProps.getProperty("auth.emailExists"));
        }

        user.setVerificationToken(verificationToken);

        executor.execute(() -> mailer.sendConfirmation(verificationToken, user));

        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();

        return jb.generateResponse("success", "register", messageProps.getProperty("auth.confirmMail"));
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
            return jb.generateResponse("error", "login", messageProps.getProperty("auth.userNotExists")); // Error
        }

        Muffler user = result.get(0);

        if (!user.isVerified()) {
            return jb.generateResponse("error", "login", messageProps.getProperty("auth.userNotVerified"));
        }

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Hex.decode(user.getSalt()));

            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));

            if (!new String(Hex.encode(hash)).equals(user.getPassword())) {
                return jb.generateResponse("error", "login", messageProps.getProperty("auth.wrongPassword"));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        user.setRole(Role.MUFFLER);

        String jwtToken = jwt.create(user.getUsername(), user.getRole());

        return jb.generateResponse("success", "login", jwtToken);
    }

    public String confirmMail(String token) {
        Map<String, String> flags = new HashMap<>();
        TypedQuery<VerificationToken> queryToken = em.createQuery("SELECT v FROM VerificationToken v WHERE v.token = :token", VerificationToken.class);
        queryToken.setParameter("token", token);

        List<VerificationToken> tokenList = queryToken.getResultList();

        if (tokenList.size() == 0) {
            // Error HTML Template
            return FileUtil.getInstance().readFromFile(configProps.getProperty("general.errorPage"), flags);
        }

        VerificationToken verifyToken = tokenList.get(0);

        Date currentDate = new Date();
        Date tokenDate = verifyToken.getExpire();

        if (tokenDate.compareTo(currentDate) >= 0) {
            Muffler muffler = verifyToken.getMuffler();
            flags.put("#USER#", muffler.getUsername());
            muffler.setVerified(true);

            em.getTransaction().begin();
            em.remove(verifyToken);
            em.getTransaction().commit();

            return FileUtil.getInstance().readFromFile(configProps.getProperty("general.verifiedPage"), flags);
        }

        return FileUtil.getInstance().readFromFile(configProps.getProperty("general.tokenExpiredPage"), flags);
    }

    /**
     * Add a Song from URL --> Download new Song
     */
    public String addSongFromURL(String url) {

        Muffler muffler = getMuffler();

        if (muffler == null) {
            return jwtError();
        }

        downloadSong(url);

        // youtube-dl -o "/Users/scharez/Desktop/%(title)s.%(ext)s" -f bestaudio --extract-audio --audio-format mp3 --audio-quality 0 https://www.youtube.com/watch?v=Xm8-bw3nLMA

        return jb.generateResponse("hint", "Download done", "");
    }

    /**
     * Song is already downloaded & registered in the Muffle App
     */
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
            return jb.generateResponse("hint", "getPlaylists", messageProps.getProperty("playlist.noPlaylist"));
        }
        result.forEach(p -> p.setSongs(null));

        return jb.generateDataResponse("data", "getPlaylists", new JSONArray(result));
    }

    /**
     * Creates a Playlist out of a given PlaylistObject
     *
     * @param playlist - playlist object
     */
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
        p.setCreated(new Date());

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
            return jb.generateResponse("hint", "getSongs", messageProps.getProperty("playlist.noPlaylist"));
        }

        for (Playlist p : result) {
            if (p.getName().equals(playlist.getName())) {
                if (p.getSongs() == null) {
                    return jb.generateResponse("hint", "getSongs", messageProps.getProperty("playlist.empty"));
                }
                songs = p.getSongs();
            }
        }
        return jb.generateDataResponse("data", "getSongs", new JSONArray(songs));
    }


    /**
     * get the JWT-Token into Repository from the Filter
     *
     * @param token - Auth token
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
        return jb.generateResponse("error", "jwt", messageProps.getProperty("error.invalidToken"));
    }

    /**
     * @param songUrl - Youtube Link
     */
    private void downloadSong(final String songUrl) {
        // String storageURL = "/var/www/muffle.scharez.at/assets/songs/%(title)s.%(ext)s";
        String storageURL = "%(title)s.%(ext)s";

        //Zuerst soll in der Datenbank überprüft werden, ob der song schonmal downgeloaded worden ist

        Runtime rt = Runtime.getRuntime();

        executor.execute(() -> {
            try {
                Song song = fetchSongInfo(songUrl);
                Process p = rt.exec("youtube-dl.exe -o " + storageURL + " -f bestaudio --ffmpeg-location C:\\FFmpeg\\bin --extract-audio --audio-format mp3 --audio-quality 0 " + songUrl);
                //  String[] file = IOUtils.toString(p.getInputStream()).split(" ", 5);
                // String filename = file[4].split("\n")[0].split(" ", 2)[1].replace(".webm", ".mp3");
                if (song != null) {
                    em.getTransaction().begin();
                    em.persist(song);
                    em.getTransaction().commit();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param videoURL - Link to our Youtube video
     * @return Song- the Song with SongData
     */
    private Song fetchSongInfo(final String videoURL) {
        try {
            URL url = new URL("https://www.youtube.com/oembed?url=" + videoURL + "&format=json");
            Reader reader = new InputStreamReader(url.openStream(), "UTF-8");
            Map<String, Object> result = new Gson().fromJson(reader, Map.class);
            Song song = new Song();
            song.setAdded(new Date());
            song.setTitle((String) result.get("title"));
            song.setArtist((String) result.get("author_name"));
            song.setUrl(videoURL);
            song.setStoragePath("songs/downloaded/" + song.getTitle().replace(" ", "_") + ".mp3");
            return song;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Could not fetch Videodata!");
        }
        return null;
    }
}
