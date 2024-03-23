package manager.ScoreManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import manager.GameEngine;
import manager.GameStatus;

public class ScoreSaver implements GameObserver {
    private String fileName;

    public ScoreSaver(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void update(GameEngine gameEngine) {
        if (gameEngine.getGameStatus() == GameStatus.GAME_OVER || gameEngine.getGameStatus() == GameStatus.MISSION_PASSED) {
            // Lấy điểm số và lưu vào file
            int score = gameEngine.getScore();
            int score2 = gameEngine.getScore2();

            LocalDateTime now = LocalDateTime.now();

            // If you want to format the date and time, use DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = now.format(formatter); // Custom format
             
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
                writer.write("Mario: " + score + " - Mario2: " + score2 + " - Date: " + formattedDateTime + " \n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
