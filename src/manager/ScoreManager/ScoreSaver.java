package manager.ScoreManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import manager.GameEngine;
import manager.GameStatus;

public class ScoreSaver implements GameObserver {
    private String fileName;

    public ScoreSaver(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void update(GameEngine gameEngine) {

        if (gameEngine.getGameStatus() == GameStatus.GAME_OVER) {
            // Lấy điểm số và lưu vào file
            int score = gameEngine.getScore();
            int score2= gameEngine.getScore2();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                writer.write("Mario: " + score+" Mario2: "+score2);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
