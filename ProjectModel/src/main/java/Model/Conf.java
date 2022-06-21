package Model;

import java.io.Serializable;
import java.util.List;

public class Conf implements Serializable {
    private int id;

    private String letters;
    private String w1;
    private String w2;
    private String w3;

    public Conf(){

    }

    public Conf(String letters, String w1, String w2, String w3) {
        this.letters = letters;
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
    }

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public String getW1() {
        return w1;
    }

    public void setW1(String w1) {
        this.w1 = w1;
    }

    public String getW2() {
        return w2;
    }

    public void setW2(String w2) {
        this.w2 = w2;
    }

    public String getW3() {
        return w3;
    }

    public void setW3(String w3) {
        this.w3 = w3;
    }

    @Override
    public String toString() {
        return "Conf{" +
                "id=" + id +
                ", letters='" + letters + '\'' +
                ", w1='" + w1 + '\'' +
                ", w2='" + w2 + '\'' +
                ", w3='" + w3 + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
