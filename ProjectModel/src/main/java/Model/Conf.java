package Model;

import java.io.Serializable;
import java.util.List;

public class Conf implements Serializable {
    private int id;

    private String mask;
    private String word;

    public Conf(){

    }


    public Conf(String mask, String word) {
        this.mask = mask;
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "Conf{" +
                "id=" + id +
                ", mask='" + mask + '\'' +
                ", word='" + word + '\'' +
                '}';
    }
}
