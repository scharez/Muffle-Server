package entity;

import org.bouncycastle.util.encoders.Hex;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Muffler {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    long id;

    String username;
    String password;
    String email;

    Role role;

    String salt;

    //List<Playlist> playlists = new ArrayList();

    public Muffler(String username, String password, String email) {
        this.username = username;
        hashPassword(password);
        this.email = email;
    }

    public Muffler() {
    }


    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        hashPassword(password);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    //public List<Playlist> getPlaylists() {
       // return playlists;
    //}

    //public void setPlaylists(ArrayList<Playlist> playlists) {
      //  this.playlists = playlists;
    //}

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    private void hashPassword(String password) {

        // generate the salt
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        try {
            // setup the encryption
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);

            // encrypt
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            String encryptedPassword = new String(Hex.encode(hash));
            this.password = encryptedPassword;
            String saltString = new String(Hex.encode(salt));
            this.salt = saltString;

            System.out.println("Encrypted Pwd: " + encryptedPassword + ", Salt: " + saltString);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
