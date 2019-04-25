package Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Song {

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    long id;




}
