package Model;

import java.io.Serializable;

public class GameDto implements Serializable {
    private int id;

    private String username;
    private int totalPoints;
    private String guessedLetters;

    public GameDto(String username, int totalPoints, String guessedLetters) {
        this.username = username;
        this.totalPoints = totalPoints;
        this.guessedLetters = guessedLetters;
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

    public String getGuessedLetters() {
        return guessedLetters;
    }

    public void setGuessedLetters(String guessedLetters) {
        this.guessedLetters = guessedLetters;
    }

    @Override
    public String toString() {
        return "GameDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", totalPoints=" + totalPoints +
                ", guessedLetters=" + guessedLetters +
                '}';
    }
}
