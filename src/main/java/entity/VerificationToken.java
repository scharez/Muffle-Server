package entity;

import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String token;

    @Temporal(TemporalType.DATE)
    private Date expire;

    @OneToOne(mappedBy = "verificationToken", cascade = CascadeType.PERSIST)
    private Muffler muffler;

    public VerificationToken() {

    }

    public VerificationToken(Muffler muffler) {
        this.muffler = muffler;
        this.token = UUID.randomUUID().toString();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, 2);

        this.expire = calendar.getTime();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public Muffler getMuffler() {
        return muffler;
    }

    public void setMuffler(Muffler muffler) {
        this.muffler = muffler;
    }
}
