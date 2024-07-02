import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Item {
	public int x, y, width, height, dx, dy;
}

class ControlWindow extends JPanel implements Runnable, KeyListener {
	public static final int WIDTH = 800;
	public static final int HEIGHT = 600;

	public static final int BRICK_X = 10;
	public static final int BRICK_Y = 6;
	public static final int BRICK_WIDTH = 62;
	public static final int BRICK_HEIGHT = 34;

	private Item player, ball;
	private Item[] bricks;

	private java.util.Random rand = new java.util.Random();

	private Thread gameThread;

	private boolean gameOver = false, gameWin = false;
	private int amount = 0;
	private int score = 0;

	private void setup() {
		gameThread = new Thread(this);
		gameThread.start();

		player = new Item();
		player.width = 120;
		player.height = 22;
		player.x = WIDTH / 2 - player.width / 2;
		player.y = HEIGHT - player.height - 50;
		player.dx = 14;

		ball = new Item();
		ball.width = 24;
		ball.height = 24;
		ball.x = WIDTH / 2 - ball.width / 2;
		ball.y = HEIGHT / 2 - ball.height / 2 - 50;
		ball.dx = -3;
		ball.dy = 4;

		bricks = new Item[BRICK_X * BRICK_Y];
		for (int i = 0; i < BRICK_X; i++) {
			for (int j = 0; j < BRICK_Y; j++) {
				bricks[amount] = new Item();
				bricks[amount].x = i * BRICK_WIDTH + 82;
				bricks[amount].y = j * BRICK_HEIGHT + 30;
				amount++;
			}
		}
	}

	private void logic() {
		if (!gameOver && !gameWin) {
			ball.x += ball.dx;
			for (int i = 0; i < amount; i++) {
				if (new Rectangle(bricks[i].x, bricks[i].y, BRICK_WIDTH, BRICK_HEIGHT).intersects(
						new Rectangle(ball.x, ball.y, ball.width, ball.height))) {
					ball.dx *= -1;
					bricks[i].x = -100;
					score++;
				}
			}

			ball.y += ball.dy;
			for (int i = 0; i < amount; i++) {
				if (new Rectangle(bricks[i].x, bricks[i].y, BRICK_WIDTH, BRICK_HEIGHT).intersects(
						new Rectangle(ball.x, ball.y, ball.width, ball.height))) {
					ball.dy *= -1;
					bricks[i].x = -100;
					score++;
				}
			}

			if (ball.x < 0 || ball.x > (WIDTH - ball.width))
				ball.dx *= -1;
			if (ball.y < 0)
				ball.dy *= -1;

			if (ball.y > HEIGHT - ball.height)
				gameOver = true;

			if (score == amount) {
				gameWin = true;
			}

			if (new Rectangle(player.x, player.y, player.width, player.height).intersects(
					new Rectangle(ball.x, ball.y, ball.width, ball.height))) {
				ball.dy = -Math.abs(ball.dy);
				ball.dx = rand.nextInt(5 - 2) + 2; 		// nextInt(max - min) + min
				ball.dx = rand.nextInt(2) == 1 ? ball.dx : -ball.dx;
			}
		}
	}

	@Override
	public void run() {
		long time1 = System.nanoTime();
		long time2;
		double delta = 0.0;
		double ticks = 20.0;
		double secs = 1e9 / ticks;

		while (gameThread != null) {
			time2 = System.nanoTime();
			delta += (time2 - time1) / secs;
			time1 = time2;

			if (delta >= 1) {
				logic();
				repaint();
				delta--;
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.BLUE);
		g.fillRect(player.x, player.y, player.width, player.height);

		g.setColor(Color.RED);
		g.fillOval(ball.x, ball.y, ball.width, ball.height);

		//amount = 2;
		for (int i = 0; i < amount; i++) {
			g.setColor(Color.GREEN);
			g.fillRect(bricks[i].x, bricks[i].y, BRICK_WIDTH, BRICK_HEIGHT);
		}

		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("Score: " + score, 10, 20);

		if (gameOver) {
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			g.drawString("Game Over", WIDTH / 2 - 100, HEIGHT / 2);
		}

		if (gameWin) {
			g.setColor(Color.GREEN);
			g.setFont(new Font("Arial", Font.BOLD, 40));
			g.drawString("You Win!", WIDTH / 2 - 80, HEIGHT / 2);
		}
	}

	public ControlWindow() {
		setup();
		addKeyListener(this);
		setFocusable(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_LEFT) {
			if (!gameOver && !gameWin) {
				if (player.x > 0)
					player.x -= player.dx;
			}
		}

		if (keyCode == KeyEvent.VK_RIGHT) {
			if (!gameOver && !gameWin) {
				if (player.x < WIDTH - player.width)
					player.x += player.dx;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}

public class Breakout {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Breakout");
		ControlWindow controlWindow = new ControlWindow();
		frame.add(controlWindow);
		frame.setSize(ControlWindow.WIDTH, ControlWindow.HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}