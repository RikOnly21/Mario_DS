package view;

import manager.GameEngine;
import manager.GameStatus;
import manager.ScoreManager.MarioScore;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UIManager extends JPanel {
	private static UIManager instance;

	private GameEngine engine;
	private Font gameFont;
	private BufferedImage startScreenImage, aboutScreenImage, helpScreenImage, gameOverScreen, gameScore,mapScreen;
	private BufferedImage heartIcon;
	private BufferedImage coinIcon;
	private BufferedImage selectIcon;
	private MapSelection mapSelection;

	private UIManager(GameEngine engine, int width, int height) {
		setPreferredSize(new Dimension(width, height));
		setMaximumSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));

		this.engine = engine;
		ImageLoader loader = engine.getImageLoader();

		mapSelection = new MapSelection();

		BufferedImage sprite = loader.loadImage("/sprite.png");
		this.heartIcon = loader.loadImage("/heart-icon.png");
		this.coinIcon = loader.getSubImage(sprite, 1, 5, 48, 48);
		this.selectIcon = loader.loadImage("/select-icon.png");
		this.startScreenImage = loader.loadImage("/start-screen.png");
		this.mapScreen= loader.loadImage("/map-screen.jpg");
		this.helpScreenImage = loader.loadImage("/help-screen.png");
		this.aboutScreenImage = loader.loadImage("/about-screen.png");
		this.gameOverScreen = loader.loadImage("/game-over.png");
		this.gameScore = loader.loadImage("/game-score.jpg");
		try {
			InputStream in = getClass().getResourceAsStream("/media/font/mario-font.ttf");
			gameFont = Font.createFont(Font.TRUETYPE_FONT, in);
		} catch (FontFormatException | IOException e) {
			gameFont = new Font("Verdana", Font.PLAIN, 12);
			e.printStackTrace();
		}
	}

	public static UIManager getInstance(GameEngine engine, int width, int height) {
        if (instance == null) {
            instance = new UIManager(engine, width, height);
        }
        return instance;
    }

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		GameStatus gameStatus = engine.getGameStatus();
		if (gameStatus == GameStatus.START_SCREEN) {
			drawStartScreen(g2);
		} else if (gameStatus == GameStatus.MAP_SELECTION) {

			drawMapSelectionScreen(g2);
		} else if (gameStatus == GameStatus.ABOUT_SCREEN) {
			drawAboutScreen(g2);
		} else if (gameStatus == GameStatus.HELP_SCREEN) {
			drawHelpScreen(g2);
		} else if (gameStatus == GameStatus.GAME_OVER) {
			drawGameOverScreen(g2);
		} else if (gameStatus == GameStatus.GAME_SCORE) {
			List<MarioScore> scores = new ArrayList<>();
			List<String> lines = readScoresFromFile("./src/media/score/score.txt");
			if(lines.size()==0)
			{
				drawScoreList(g2, new ArrayList<>() , null);
				return;
			}
			Pattern pattern = Pattern.compile("Mario: (\\d+) - Mario2: (\\d+) - Date: (.+)");
			for (String line : lines) {
				Matcher matcher = pattern.matcher(line);
				if (matcher.matches()) {
					int marioScore = Integer.parseInt(matcher.group(1));
					int mario2Score = Integer.parseInt(matcher.group(2));
					String dateString = matcher.group(3);

					scores.add(new MarioScore(marioScore, mario2Score, dateString));
				}
			}

			MarioScore recentPlay = scores.get(scores.size() - 1);
			scores.sort(Comparator.comparingInt(MarioScore::getTotalScore).reversed());
			
			drawScoreList(g2, scores, recentPlay);
		
		
		} else {
			Point camLocation = engine.getCameraLocation();
			g2.translate(-camLocation.x, -camLocation.y);
			engine.drawMap(g2);
			g2.translate(camLocation.x, camLocation.y);

			drawPoints(g2);
			drawRemainingLives(g2);
			drawAcquiredCoins(g2);
			// drawRemainingTime(g2);

			if (gameStatus == GameStatus.PAUSED) {
				drawPauseScreen(g2);
			} else if (gameStatus == GameStatus.MISSION_PASSED) {
				drawVictoryScreen(g2);
			}
		}

		g2.dispose();
	}

	// private void drawRemainingTime(Graphics2D g2) {
	// g2.setFont(gameFont.deriveFont(25f));
	// g2.setColor(Color.WHITE);
	// String displayedStr = "TIME: " + engine.getRemainingTime();
	// g2.drawString(displayedStr, 800, 50);
	// }

	private void drawVictoryScreen(Graphics2D g2) {
		g2.setFont(gameFont.deriveFont(50f));
		g2.setColor(Color.WHITE);
		String displayedStr = "YOU WON!";
		int stringLength = g2.getFontMetrics().stringWidth(displayedStr);
		g2.drawString(displayedStr, (getWidth() - stringLength) / 2, getHeight() / 2);
	}

	private void drawHelpScreen(Graphics2D g2) {
		g2.drawImage(helpScreenImage, 0, 0, this.getWidth(), this.getHeight(), null);
	}

	private void drawAboutScreen(Graphics2D g2) {
		g2.drawImage(aboutScreenImage, 0, 0, this.getWidth(), this.getHeight(), null);
	}

	private void drawGameOverScreen(Graphics2D g2) {
		g2.drawImage(gameOverScreen, 0, 0, this.getWidth(), this.getHeight(), null);
		g2.setFont(gameFont.deriveFont(50f));
		g2.setColor(new Color(130, 48, 48));

		String acquiredPoints = "Mario: " + engine.getScore() + " Mario2: " + engine.getScore2();
		int stringLength = g2.getFontMetrics().stringWidth(acquiredPoints);
		int stringHeight = g2.getFontMetrics().getHeight();

		g2.drawString(acquiredPoints, (getWidth() - stringLength) / 2, getHeight() - stringHeight * 2);
	}

	private void drawPauseScreen(Graphics2D g2) {
		g2.setFont(gameFont.deriveFont(50f));
		g2.setColor(Color.WHITE);
		String displayedStr = "PAUSED";
		int stringLength = g2.getFontMetrics().stringWidth(displayedStr);
		g2.drawString(displayedStr, (getWidth() - stringLength) / 2, getHeight() / 2);
	}

	private void drawAcquiredCoins(Graphics2D g2) {
		g2.setFont(gameFont.deriveFont(30f));
		g2.setColor(Color.WHITE);
		String displayedStr = "" + engine.getCoins();
		g2.drawImage(coinIcon, getWidth() - 115, 10, null);
		g2.drawString(displayedStr, getWidth() - 65, 50);
	}

	private void drawRemainingLives(Graphics2D g2) {
		g2.setFont(gameFont.deriveFont(30f));
		g2.setColor(Color.WHITE);
		String displayedStr = "" + engine.getRemainingLives();
		g2.drawImage(heartIcon, 50, 10, null);
		g2.drawString(displayedStr, 100, 50);
	}

	private void drawPoints(Graphics2D g2) {
		g2.setFont(gameFont.deriveFont(25f));
		g2.setColor(Color.WHITE);

		String displayedStr = "Mario: " + engine.getScore();
		displayedStr += " Mario2: " + engine.getScore2();
		displayedStr += " TIME: " + engine.getRemainingTime();

		// int stringLength = g2.getFontMetrics().stringWidth(displayedStr);

		// g2.drawImage(coinIcon, 50, 10, null);
		g2.drawString(displayedStr, 300, 50);
	}

	// vẽ nấm trong giao diện bắt đầu (chỉ đang ở mục nào)
	private void drawStartScreen(Graphics2D g2) {
		int row = engine.getStartScreenSelection().getLineNumber();

		g2.drawImage(startScreenImage, 0, 0, this.getWidth(), this.getHeight(), null);
		g2.drawImage(selectIcon, this.getWidth() / 4,
				(this.getHeight() / 2 + (this.getHeight() / 18)) + (this.getHeight() / 12) * (row + 1),
				null);
	}

	private void drawMapSelectionScreen(Graphics2D g2) {
		
		g2.setFont(gameFont.deriveFont(50f));
		g2.setColor(Color.WHITE);
		mapSelection.draw(g2, mapScreen,() -> this.getWidth(), () -> this.getHeight());

		int row = engine.getSelectedMap();
		int y_location = row * 100 + 300 - selectIcon.getHeight();

		g2.drawImage(selectIcon, this.getWidth() / 4, y_location, null);
	}

	private void drawScoreList(Graphics2D g2, List<MarioScore> scores, MarioScore recentScore) {
		g2.drawImage(gameScore, 0, 0, this.getWidth(), this.getHeight(), null);
		g2.setFont(gameFont.deriveFont(24f)); // Set the font size
		g2.setColor(Color.WHITE); // Set the color

		int x = 200; // X position to start drawing scores
		int y = 200; // Starting Y position for the first score
		int yDelta = 30; // Distance between scores

		if (scores.size() == 0) {
			g2.drawString("No score", x, y);
			return;
		}

		var top20 = scores.subList(0, scores.size() > 20 ? 20 : scores.size());

		g2.drawString("Leaderboard", (engine.screenSize.width / 2) - ("Leaderboard").length() * 16, 150);
		for (MarioScore score : top20) {
			g2.drawString(String.format("%-2d", top20.indexOf(score) + 1) + " - " + score.toString(), x, y);
			y += yDelta; // Move down to the next line
		}

		g2.drawString("Recent: " + recentScore.toString(), x, engine.screenSize.height - 300);
	}

	private List<String> readScoresFromFile(String fileName) {
		List<String> scores = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = reader.readLine()) != null) {
				scores.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scores;
	}

	public String selectMapViaMouse(Point mouseLocation) {
		return mapSelection.selectMap(mouseLocation);
	}

	public String selectMapViaKeyboard(int index) {
		return mapSelection.selectMap(index);
	}

	public int changeSelectedMap(int index, boolean up) {
		return mapSelection.changeSelectedMap(index, up);
	}
}