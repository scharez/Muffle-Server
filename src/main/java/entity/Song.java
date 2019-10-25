package entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String url;
    private String storagePath;
    private String title;
    private String artist;
    private double duration;

    @Temporal(TemporalType.DATE)
    private Date added;


    public Song() {
    }

    public Song(long id, String url, String storagePath, String title, String artist, double duration, Date added) {
        this.id = id;
        this.url = url;
        this.storagePath = storagePath;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.added = added;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }
}
