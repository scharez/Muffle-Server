package entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Song {

    /*
    Song wird durch die URL erkannt --> URL = Primary Key //Falsch
    */

    /*
    Song sollte ein ID haben und einen Path, wo die URL gespeichert ist

    Url is jo irg a youtube url, de kann se ah ändern
     */

    @Id
    String url;
    String title;
    String artist;
    double duration;


    public Song() {
    }

    public Song(String url, String title, String artist, double duration) {
        this.url = url;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
