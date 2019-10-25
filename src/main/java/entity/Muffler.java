package entity;

import org.bouncycastle.util.encoders.Hex;

import javax.persistence.*;
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
    private long id;

    private String username;
    private String password;
    private String email;

    private boolean verified;

    private Role role;

    private String salt;

    @OneToMany
    private List<Playlist> playlists;

    @OneToOne(cascade = CascadeType.PERSIST)
    private VerificationToken verificationToken;

    public Muffler(String username, String password, String email) {
        this.username = username;
        hashPassword(password);
        this.email = email;
        this.verified = false;
    }

    public Muffler() {
        this.playlists = new ArrayList<>();
        this.verified = false;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(ArrayList<Playlist> playlists) {
        this.playlists = playlists;
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
            this.password = new String(Hex.encode(hash));
            this.salt = new String(Hex.encode(salt));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public VerificationToken getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(VerificationToken verificationToken) {
        this.verificationToken = verificationToken;
    }
}