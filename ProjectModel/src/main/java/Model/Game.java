package Model;

import java.io.Serializable;

public class Game implements Serializable {
    private int id;

    private String username;
    private int totalPoints;
    private int confId;
    private String time;
    private int tries;
    private String guessedLetters;
    private int totalGuessedLetters;

    public Game(){

    }

    public Game(String username, int totalPoints, int confId, String time) {
        this.username = username;
        this.totalPoints = totalPoints;
        this.confId = confId;
        this.time = time;
        this.tries = 0;
        guessedLetters = "";
        totalGuessedLetters = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getConfId() {
        return confId;
    }

    public void setConfId(int confId) {
        this.confId = confId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTries() {
        return tries;
    }

    public void setTries(int tries) {
        this.tries = tries;
    }

    public String getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(String guessedLetters) {
        this.guessedLetters = guessedLetters;
    }

    public int getTotalGuessedLetters() {
        return totalGuessedLetters;
    }

    public void setTotalGuessedLetters(int totalGuessedLetters) {
        this.totalGuessedLetters = totalGuessedLetters;
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", totalPoints=" + totalPoints +
                ", confId=" + confId +
                ", time='" + time + '\'' +
                ", tries=" + tries +
                ", guessedLetters='" + guessedLetters + '\'' +
                ", totalGuessedLetters=" + totalGuessedLetters +
                '}';
    }
}
