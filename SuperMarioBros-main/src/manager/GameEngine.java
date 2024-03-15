package manager;

import model.hero.Mario;
import model.hero.Mario2;
import view.ImageLoader;
import view.StartScreenSelection;
import view.UIManager;
import javax.swing.*;
import java.awt.*;

public class GameEngine implements Runnable {

	private final static int WIDTH = 1920, HEIGHT = 1080;

	private MapManager mapManager;
	private UIManager uiManager;
	private SoundManager soundManager;
	private GameStatus gameStatus;
	private boolean isRunning;
	private Camera camera;
	private ImageLoader imageLoader;
	private Thread thread;
	private StartScreenSelection startScreenSelection = StartScreenSelection.START_GAME;
	private int selectedMap = 0;

	private GameEngine() {
		init();
	}

	private void init() {
		imageLoader = new ImageLoader();
		// InputManager inputManager = new InputManager(this);
		gameStatus = GameStatus.START_SCREEN;
		camera = new Camera();
		uiManager = new UIManager(this, WIDTH, HEIGHT);
		soundManager = new SoundManager();
		mapManager = new MapManager();

		JFrame frame = new JFrame("Super Mario Bros.");
		frame.add(uiManager);

		// frame.addKeyListener(inputManager);
		// frame.addMouseListener(inputManager);

		frame.pack();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		KeyBindingManager inputManager = new KeyBindingManager(this, frame);

		start();
	}

	private synchronized void start() {
		if (isRunning)
			return;

		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	private void reset() {
		resetCamera();
		setGameStatus(GameStatus.START_SCREEN);
	}

	public void resetCamera() {
		camera = new Camera();
		soundManager.restartBackground();
	}

	public void selectMapViaMouse() {
		String path = uiManager.selectMapViaMouse(uiManager.getMousePosition());
		if (path != null) {
			createMap(path);
		}
	}

	public void selectMapViaKeyboard() {
		String path = uiManager.selectMapViaKeyboard(selectedMap);
		if (path != null) {
			createMap(path);
		}
	}

	public void changeSelectedMap(boolean up) {
		selectedMap = uiManager.changeSelectedMap(selectedMap, up);
	}

	private void createMap(String path) {
		boolean loaded = mapManager.createMap(imageLoader, path);
		if (loaded) {
			setGameStatus(GameStatus.RUNNING);
			soundManager.restartBackground();
		}

		else
			setGameStatus(GameStatus.START_SCREEN);
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		long timer = System.currentTimeMillis();

		while (isRunning && !thread.isInterrupted()) {

			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				if (gameStatus == GameStatus.RUNNING) {
					gameLoop();
				}
				delta--;
			}
			render();

			if (gameStatus != GameStatus.RUNNING) {
				timer = System.currentTimeMillis();
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				mapManager.updateTime();
			}
		}
	}

	private void render() {
		uiManager.repaint();
	}

	private void gameLoop() {
		updateLocations();
		checkCollisions();
		updateCamera();

		if (isGameOver()) {
			setGameStatus(GameStatus.GAME_OVER);
		}

		int missionPassed = passMission();
		if (missionPassed > -1) {
			mapManager.acquirePoints(missionPassed);
			setGameStatus(GameStatus.MISSION_PASSED);
		} else if (mapManager.endLevel())
			setGameStatus(GameStatus.MISSION_PASSED);
	}

	private void updateCamera() {
		Mario mario = mapManager.getMario();
		Mario2 mario2 = mapManager.getMario2();

		double marioX = mario.getX();
		double mario2X = mario2.getX();

		double minX = Math.min(marioX, mario2X);
		double maxX = Math.max(marioX, mario2X);

		double midpoint = (minX + maxX) / 2 - 600;
		double cameraX = camera.getX();

		if (midpoint - cameraX > 100) {
			// Camera is behind players, move forwards
			camera.moveCam(5);
		} else if (cameraX - midpoint > 100) {
			// Camera is ahead of players, move backwards
			camera.moveCam(-5);
		}

		// Keep camera X bounded
		camera.setX(Math.max(0, camera.getX()));

	}

	private void updateLocations() {
		mapManager.updateLocations();
	}

	private void checkCollisions() {
		mapManager.checkCollisions(this);
	}

	public void receiveInputMario(ButtonAction input) {
		if (gameStatus != GameStatus.RUNNING)
			return;

		Mario mario = mapManager.getMario();

		if (input == ButtonAction.M_JUMP) {
			mario.jump(this);
		} else if (input == ButtonAction.M_RIGHT) {
			mario.move(true, camera);
		} else if (input == ButtonAction.M_LEFT) {
			mario.move(false, camera);
		} else if (input == ButtonAction.ACTION_COMPLETED) {
			mario.setVelX(0);
		} else if (input == ButtonAction.M_FIRE) {
			mapManager.fire(this);
		}
	}

	public void receiveInputMario2(ButtonAction input) {
		if (gameStatus != GameStatus.RUNNING)
			return;

		Mario2 mario2 = mapManager.getMario2();

		if (input == ButtonAction.M2_JUMP) {
			mario2.jump(this);
		} else if (input == ButtonAction.M2_RIGHT) {
			mario2.move(true, camera);
		} else if (input == ButtonAction.M2_LEFT) {
			mario2.move(false, camera);
		} else if (input == ButtonAction.ACTION_COMPLETED2) {
			mario2.setVelX(0);
		} else if (input == ButtonAction.M2_FIRE) {
			mapManager.fire2(this);
		}
	}

	public void receiveInput(ButtonAction input) {
		if (gameStatus == GameStatus.START_SCREEN) {
			if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.START_GAME) {
				startGame();
			} else if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.VIEW_ABOUT) {
				setGameStatus(GameStatus.ABOUT_SCREEN);
			} else if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.VIEW_HELP) {
				setGameStatus(GameStatus.HELP_SCREEN);
			} else if (input == ButtonAction.GO_UP) {
				selectOption(true);
			} else if (input == ButtonAction.GO_DOWN) {
				selectOption(false);
			}
		} else if (gameStatus == GameStatus.MAP_SELECTION) {
			if (input == ButtonAction.SELECT) {
				selectMapViaKeyboard();
			} else if (input == ButtonAction.GO_UP) {
				changeSelectedMap(true);
			} else if (input == ButtonAction.GO_DOWN) {
				changeSelectedMap(false);
			}
		} else if (gameStatus == GameStatus.RUNNING) {
			if (input == ButtonAction.PAUSE_RESUME) {
				pauseGame();
			}
		} else if (gameStatus == GameStatus.PAUSED) {
			if (input == ButtonAction.PAUSE_RESUME) {
				pauseGame();
			}

		} else if (gameStatus == GameStatus.GAME_OVER && input == ButtonAction.GO_TO_START_SCREEN) {
			reset();
		} else if (gameStatus == GameStatus.MISSION_PASSED && input == ButtonAction.GO_TO_START_SCREEN) {
			reset();
		}

		if (input == ButtonAction.GO_TO_START_SCREEN) {
			setGameStatus(GameStatus.START_SCREEN);
		}
	}

	private void selectOption(boolean selectUp) {
		startScreenSelection = startScreenSelection.select(selectUp);
	}

	private void startGame() {
		if (gameStatus != GameStatus.GAME_OVER) {
			setGameStatus(GameStatus.MAP_SELECTION);
		}
	}

	private void pauseGame() {
		if (gameStatus == GameStatus.RUNNING) {
			setGameStatus(GameStatus.PAUSED);
			soundManager.pauseBackground();
		} else if (gameStatus == GameStatus.PAUSED) {
			setGameStatus(GameStatus.RUNNING);
			soundManager.resumeBackground();
		}
	}

	public void shakeCamera() {
		camera.shakeCamera();
	}

	private boolean isGameOver() {
		if (gameStatus == GameStatus.RUNNING)
			return mapManager.isGameOver();
		return false;
	}

	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public GameStatus getGameStatus() {
		return gameStatus;
	}

	public StartScreenSelection getStartScreenSelection() {
		return startScreenSelection;
	}

	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}

	public int getScore() {
		return mapManager.getScore() + mapManager.getScore2();
	}

	public int getRemainingLives() {
		return mapManager.getRemainingLives() + mapManager.getRemainingLives2();
	}

	public int getCoins() {
		return mapManager.getCoins() + mapManager.getCoins2();
	}

	public int getSelectedMap() {
		return selectedMap;
	}

	public void drawMap(Graphics2D g2) {
		mapManager.drawMap(g2);
	}

	public Point getCameraLocation() {
		return new Point((int) camera.getX(), (int) camera.getY());
	}

	private int passMission() {
		return mapManager.passMission();
	}

	public void playCoin() {
		soundManager.playCoin();
	}

	public void playOneUp() {
		soundManager.playOneUp();
	}

	public void playSuperMushroom() {
		soundManager.playSuperMushroom();
	}

	public void playMarioDies() {
		soundManager.playMarioDies();
	}

	public void playJump() {
		soundManager.playJump();
	}

	public void playFireFlower() {
		soundManager.playFireFlower();
	}

	public void playFireball() {
		soundManager.playFireball();
	}

	public void playStomp() {
		soundManager.playStomp();
	}

	public MapManager getMapManager() {
		return mapManager;
	}

	public static void main(String... args) {
		new GameEngine();
	}

	public int getRemainingTime() {
		return mapManager.getRemainingTime();
	}
}
