package manager;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import model.GameObject;
import model.Map;
import model.brick.Brick;
import model.brick.OrdinaryBrick;
import model.enemy.Enemy;
import model.hero.Fireball;
import model.hero.Mario;
import model.hero.Mario2;
import model.prize.BoostItem;
import model.prize.Coin;
import model.prize.Prize;
import view.ImageLoader;

public class MapManager {

	private Map map;

	public MapManager() {
	}

	public void updateLocations() {
		if (map == null)
			return;

		map.updateLocations();
	}

	public void resetCurrentMap(GameEngine engine) {
		Mario mario = getMario();
		Mario2 mario2 = getMario2();

		mario.resetPoint();
		mario2.resetPoint();

		mario.resetLocation();
		mario2.resetLocation();

		engine.resetCamera();
		createMap(engine.getImageLoader(), map.getPath());

		map.setMario(mario);
		map.setMario2(mario2);
	}

	public boolean createMap(ImageLoader loader, String path) {
		MapCreator mapCreator = new MapCreator(loader);
		map = mapCreator.createMap("/maps/" + path, 400);

		return map != null;
	}

	public void acquirePoints(int point) {
		map.getMario().acquirePoints(point);
	}

	public void acquirePoints2(int point) {
		// tạo bảng points riêng ( chưa làm )
		map.getMario2().acquirePoints(point);
	}

	public Mario getMario() {
		return map.getMario();
	}

	public Mario2 getMario2() {
		return map.getMario2();
	}

	public void fire(GameEngine engine) {
		Fireball fireball = getMario().fire();
		if (fireball != null) {
			map.addFireball(fireball);
			engine.playFireball();
		}
	}

	public void fire2(GameEngine engine) {
		Fireball fireball = getMario2().fire();
		if (fireball != null) {
			map.addFireball(fireball);
			engine.playFireball();
		}
	}

	public boolean isGameOver() {
		// 1 trong 2 thằng chết hoặc hết thời gian
		return (getMario().getRemainingLives() + getMario2().getRemainingLives()) == 0 || map.isTimeOver();
	}

	public int getScore() {
		return getMario().getPoints();
	}

	public int getScore2() {
		return getMario2().getPoints();
	}

	public int getRemainingLives() {
		return getMario().getRemainingLives();
	}

	public int getRemainingLives2() {
		return getMario2().getRemainingLives();
	}

	public int getCoins() {
		return getMario().getCoins();
	}

	public int getCoins2() {
		return getMario2().getCoins();
	}

	public void drawMap(Graphics2D g2) {
		map.drawMap(g2);
	}

	public int passMission() {
		if ((getMario().getX() >= map.getEndPoint().getX() && !map.getEndPoint().isTouched())
				|| (getMario2().getX() >= map.getEndPoint().getX() && !map.getEndPoint().isTouched())) {
			map.getEndPoint().setTouched(true);
			int height = (int) getMario().getY();
			return height * 2;
		} else
			return -1;
	}

	public boolean endLevel() {
		return getMario().getX() >= map.getEndPoint().getX() + 320;
	}

	public boolean endLevel2() {
		return getMario2().getX() >= map.getEndPoint().getX() + 320;
	}

	public void checkCollisions(GameEngine engine) {
		if (map == null) {
			return;
		}

		checkBottomCollisions(engine);
		checkBottomCollisions2(engine);

		checkTopCollisions(engine);
		checkTopCollisions2(engine);

		checkMarioHorizontalCollision(engine);
		checkMarioHorizontalCollision2(engine);

		checkEnemyCollisions();
		checkPrizeCollision();
		checkPrizeContact(engine);
		checkFireballContact();
	}

	private void checkBottomCollisions(GameEngine engine) {
		Mario mario = getMario();
		ArrayList<Brick> bricks = map.getAllBricks();
		ArrayList<Enemy> enemies = map.getEnemies();
		ArrayList<GameObject> toBeRemoved = new ArrayList<>();

		Rectangle marioBottomBounds = mario.getBottomBounds();

		if (!mario.isJumping())
			mario.setFalling(true);

		for (Brick brick : bricks) {
			Rectangle brickTopBounds = brick.getTopBounds();
			if (marioBottomBounds.intersects(brickTopBounds)) {
				mario.setY(brick.getY() - mario.getDimension().height + 1);
				mario.setFalling(false);
				mario.setVelY(0);
			}
		}

		for (Enemy enemy : enemies) {
			Rectangle enemyTopBounds = enemy.getTopBounds();
			if (marioBottomBounds.intersects(enemyTopBounds)) {
				mario.acquirePoints(100);
				toBeRemoved.add(enemy);
				engine.playStomp();
			}
		}

		if (mario.getY() + mario.getDimension().height >= map.getBottomBorder()) {
			mario.setY(map.getBottomBorder() - mario.getDimension().height);
			mario.setFalling(false);
			mario.setVelY(0);
		}

		removeObjects(toBeRemoved);
	}

	private void checkTopCollisions(GameEngine engine) {
		Mario mario = getMario();
		ArrayList<Brick> bricks = map.getAllBricks();
		Rectangle marioTopBounds = mario.getTopBounds();

		for (Brick brick : bricks) {
			Rectangle brickBottomBounds = brick.getBottomBounds();
			if (marioTopBounds.intersects(brickBottomBounds)) {
				mario.setVelY(0);
				mario.setY(brick.getY() + brick.getDimension().height);

				if (brick instanceof OrdinaryBrick) {
					brick.breakBrick(engine, mario);
				} else {
					Prize prize = brick.reveal(engine);
					if (prize != null)
						map.addRevealedPrize(prize);
				}

			}
		}
	}

	private void checkBottomCollisions2(GameEngine engine) {
		Mario2 mario2 = getMario2();
		ArrayList<Brick> bricks = map.getAllBricks();
		ArrayList<Enemy> enemies = map.getEnemies();
		ArrayList<GameObject> toBeRemoved = new ArrayList<>();

		Rectangle marioBottomBounds2 = mario2.getBottomBounds();

		if (!mario2.isJumping()) {
			mario2.setFalling(true);
		}
		for (Brick brick : bricks) {
			Rectangle brickTopBounds2 = brick.getTopBounds();
			if (marioBottomBounds2.intersects(brickTopBounds2)) {
				mario2.setY(brick.getY() - mario2.getDimension().height + 1);
				mario2.setFalling(false);
				mario2.setVelY(0);
			}
		}

		for (Enemy enemy : enemies) {
			Rectangle enemyTopBounds2 = enemy.getTopBounds();
			if (marioBottomBounds2.intersects(enemyTopBounds2)) {
				mario2.acquirePoints(100);
				toBeRemoved.add(enemy);
				engine.playStomp();
			}
		}

		if (mario2.getY() + mario2.getDimension().height >= map.getBottomBorder()) {
			mario2.setY(map.getBottomBorder() - mario2.getDimension().height);
			mario2.setFalling(false);
			mario2.setVelY(0);
		}

		removeObjects(toBeRemoved);
	}

	private void checkTopCollisions2(GameEngine engine) {
		Mario2 mario2 = getMario2();
		ArrayList<Brick> bricks = map.getAllBricks();
		Rectangle marioTopBounds2 = mario2.getTopBounds();

		for (Brick brick : bricks) {
			Rectangle brickBottomBounds2 = brick.getBottomBounds();
			if (marioTopBounds2.intersects(brickBottomBounds2)) {
				mario2.setVelY(0);
				mario2.setY(brick.getY() + brick.getDimension().height);

				if (brick instanceof OrdinaryBrick) {
					brick.breakBrick2(engine, mario2);
				} else {
					Prize prize = brick.reveal(engine);
					if (prize != null)
						map.addRevealedPrize(prize);
				}
			}
		}
	}

	private void checkMarioHorizontalCollision(GameEngine engine) {
		Mario mario = getMario();
		ArrayList<Brick> bricks = map.getAllBricks();
		ArrayList<Enemy> enemies = map.getEnemies();
		ArrayList<GameObject> toBeRemoved = new ArrayList<>();

		boolean marioDies = false;
		boolean toRight = mario.getToRight();

		Rectangle marioBounds = toRight ? mario.getRightBounds() : mario.getLeftBounds();

		for (Brick brick : bricks) {
			Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
			if (marioBounds.intersects(brickBounds)) {
				mario.setVelX(0);
				if (toRight)
					mario.setX(brick.getX() - mario.getDimension().width);
				else
					mario.setX(brick.getX() + brick.getDimension().width);
			}
		}

		for (Enemy enemy : enemies) {
			Rectangle enemyBounds = !toRight ? enemy.getRightBounds() : enemy.getLeftBounds();
			if (marioBounds.intersects(enemyBounds)) {
				marioDies = mario.onTouchEnemy(engine);
				toBeRemoved.add(enemy);
			}
		}
		removeObjects(toBeRemoved);

		if (mario.getX() <= engine.getCameraLocation().getX() && mario.getVelX() < 0) {
			mario.setVelX(0);
			mario.setX(engine.getCameraLocation().getX());
		}

		if (marioDies) {
			resetCurrentMap(engine);
		}
	}

	private void checkMarioHorizontalCollision2(GameEngine engine) {
		Mario2 mario2 = getMario2();
		ArrayList<Brick> bricks = map.getAllBricks();
		ArrayList<Enemy> enemies = map.getEnemies();
		ArrayList<GameObject> toBeRemoved = new ArrayList<>();

		boolean mario2Dies = false;
		boolean toRight = mario2.getToRight();

		Rectangle marioBounds2 = toRight ? mario2.getRightBounds() : mario2.getLeftBounds();

		for (Brick brick : bricks) {
			Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
			if (marioBounds2.intersects(brickBounds)) {
				mario2.setVelX(0);
				if (toRight)
					mario2.setX(brick.getX() - mario2.getDimension().width + 5);
				else
					mario2.setX(brick.getX() + brick.getDimension().width + 5);
			}
		}

		for (Enemy enemy : enemies) {
			Rectangle enemyBounds = !toRight ? enemy.getRightBounds() : enemy.getLeftBounds();
			if (marioBounds2.intersects(enemyBounds)) {
				mario2Dies = mario2.onTouchEnemy(engine);
				toBeRemoved.add(enemy);
			}
		}
		removeObjects(toBeRemoved);

		if (mario2.getX() <= engine.getCameraLocation().getX() && mario2.getVelX() < 0) {
			mario2.setVelX(0);
			mario2.setX(engine.getCameraLocation().getX());
		}

		if (mario2Dies) {
			resetCurrentMap(engine);
		}
	}

	private void checkEnemyCollisions() {
		ArrayList<Brick> bricks = map.getAllBricks();
		ArrayList<Enemy> enemies = map.getEnemies();

		for (Enemy enemy : enemies) {
			boolean standsOnBrick = false;

			for (Brick brick : bricks) {
				Rectangle enemyBounds = enemy.getLeftBounds();
				Rectangle brickBounds = brick.getRightBounds();

				Rectangle enemyBottomBounds = enemy.getBottomBounds();
				Rectangle brickTopBounds = brick.getTopBounds();

				if (enemy.getVelX() > 0) {
					enemyBounds = enemy.getRightBounds();
					brickBounds = brick.getLeftBounds();
				}

				if (enemyBounds.intersects(brickBounds)) {
					enemy.setVelX(-enemy.getVelX());
				}

				if (enemyBottomBounds.intersects(brickTopBounds)) {
					enemy.setFalling(false);
					enemy.setVelY(0);
					enemy.setY(brick.getY() - enemy.getDimension().height);
					standsOnBrick = true;
				}
			}

			if (enemy.getY() + enemy.getDimension().height > map.getBottomBorder()) {
				enemy.setFalling(false);
				enemy.setVelY(0);
				enemy.setY(map.getBottomBorder() - enemy.getDimension().height);
			}

			if (!standsOnBrick && enemy.getY() < map.getBottomBorder()) {
				enemy.setFalling(true);
			}
		}
	}

	private void checkPrizeCollision() {
		ArrayList<Prize> prizes = map.getRevealedPrizes();
		ArrayList<Brick> bricks = map.getAllBricks();

		for (Prize prize : prizes) {
			if (prize instanceof BoostItem) {
				BoostItem boost = (BoostItem) prize;
				Rectangle prizeBottomBounds = boost.getBottomBounds();
				Rectangle prizeRightBounds = boost.getRightBounds();
				Rectangle prizeLeftBounds = boost.getLeftBounds();
				boost.setFalling(true);

				for (Brick brick : bricks) {
					Rectangle brickBounds;

					if (boost.isFalling()) {
						brickBounds = brick.getTopBounds();

						if (brickBounds.intersects(prizeBottomBounds)) {
							boost.setFalling(false);
							boost.setVelY(0);
							boost.setY(brick.getY() - boost.getDimension().height + 1);
							if (boost.getVelX() == 0)
								boost.setVelX(2);
						}
					}

					if (boost.getVelX() > 0) {
						brickBounds = brick.getLeftBounds();

						if (brickBounds.intersects(prizeRightBounds)) {
							boost.setVelX(-boost.getVelX());
						}
					} else if (boost.getVelX() < 0) {
						brickBounds = brick.getRightBounds();

						if (brickBounds.intersects(prizeLeftBounds)) {
							boost.setVelX(-boost.getVelX());
						}
					}
				}

				if (boost.getY() + boost.getDimension().height > map.getBottomBorder()) {
					boost.setFalling(false);
					boost.setVelY(0);
					boost.setY(map.getBottomBorder() - boost.getDimension().height);
					if (boost.getVelX() == 0)
						boost.setVelX(2);
				}

			}
		}
	}

	private void checkPrizeContact(GameEngine engine) {
		ArrayList<Prize> prizes = map.getRevealedPrizes();
		ArrayList<GameObject> toBeRemoved = new ArrayList<>();

		Rectangle marioBounds = getMario().getBounds();
		Rectangle marioBounds2 = getMario2().getBounds();

		for (Prize prize : prizes) {
			Rectangle prizeBounds = prize.getBounds();

			if (prizeBounds.intersects(marioBounds)) {
				prize.onTouch(getMario(), engine);
				toBeRemoved.add((GameObject) prize);
			} else if (prize instanceof Coin) {
				prize.onTouch(getMario(), engine);
			}

			if (prizeBounds.intersects(marioBounds2)) {
				prize.onTouch2(getMario2(), engine);
				toBeRemoved.add((GameObject) prize);
			} else if (prize instanceof Coin) {
				prize.onTouch(getMario(), engine);
			}
		}

		removeObjects(toBeRemoved);
	}

	private void checkFireballContact() {
		ArrayList<Fireball> fireballs = map.getFireballs();
		ArrayList<Enemy> enemies = map.getEnemies();
		ArrayList<Brick> bricks = map.getAllBricks();
		ArrayList<GameObject> toBeRemoved = new ArrayList<>();

		for (Fireball fireball : fireballs) {
			Rectangle fireballBounds = fireball.getBounds();

			for (Enemy enemy : enemies) {
				Rectangle enemyBounds = enemy.getBounds();
				if (fireballBounds.intersects(enemyBounds)) {
					acquirePoints(100);
					toBeRemoved.add(enemy);
					toBeRemoved.add(fireball);
				}
			}

			for (Brick brick : bricks) {
				Rectangle brickBounds = brick.getBounds();
				if (fireballBounds.intersects(brickBounds)) {
					toBeRemoved.add(fireball);
				}
			}
		}

		removeObjects(toBeRemoved);
	}

	private void removeObjects(ArrayList<GameObject> list) {
		if (list == null)
			return;

		for (GameObject object : list) {
			if (object instanceof Fireball) {
				map.removeFireball((Fireball) object);
			} else if (object instanceof Enemy) {
				map.removeEnemy((Enemy) object);
			} else if (object instanceof Coin || object instanceof BoostItem) {
				map.removePrize((Prize) object);
			}
		}
	}

	public void addRevealedBrick(OrdinaryBrick ordinaryBrick) {
		map.addRevealedBrick(ordinaryBrick);
	}

	public void updateTime() {
		if (map != null)
			map.updateTime(1);
	}

	public int getRemainingTime() {
		return (int) map.getRemainingTime();
	}
}
