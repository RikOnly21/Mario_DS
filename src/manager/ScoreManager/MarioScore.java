package manager.ScoreManager;

import java.time.LocalDateTime;

public class MarioScore {
    private int marioScore;
    private int mario2Score;
    private String Date;

    public MarioScore(int marioScore, int mario2Score, String Date) {
        this.marioScore = marioScore;
        this.mario2Score = mario2Score;
        this.Date = Date;
    }

    public int getTotalScore() {
        return marioScore + mario2Score;
    }

    public LocalDateTime getPlayTime() {
        return LocalDateTime.parse(Date);
    }

    @Override
    public String toString() {
        return "Mario: " + String.format("%-5d", marioScore) + " - Mario2: " + String.format("%-5d", mario2Score) + " - Date: " + Date;
    }
}