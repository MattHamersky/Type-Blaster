package Main;

import GameState.GameStateManager;

import java.awt.Canvas;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;

import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class GamePanel extends Canvas implements Runnable, KeyListener {
	
	private Thread thread;
	private int fps;
	private int updatesPerSecond = 30;
	private int targetUpdateTime = 1000 / updatesPerSecond;
	private boolean isRunning = true;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private GameStateManager gsm;
	
	private static volatile boolean drawInfo = false;
	private Font infoFont;
	private long timeElapsed;
	private long updateElapsedTime;
	private long renderElapsedTime;
	private long drawElapsedTime;
	private long accumulatedTime = 0;
	
	private double percentBetweenUpdates;
	
	public GamePanel() {
		
		super();
		setFocusable(true);
		requestFocus();
		
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
		
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		if(Init.fullscreen) {
			//setDoubleBuffered(false);
		}
		else {
			//setDoubleBuffered(true);
		}
	}
	
	public void run() {
		
		fullInit();
		
		while(isRunning) {
			
			//start the clock!
			long startTime = System.nanoTime();
			
			//update the game if enough time has been accumulated
			while(accumulatedTime >= targetUpdateTime) {
				update();
				accumulatedTime -= targetUpdateTime;
			}
			
			//how long all updates took
			updateElapsedTime = System.nanoTime() - startTime;
			
			//how far we are between the last update and the next update
			percentBetweenUpdates = ((double) accumulatedTime) / targetUpdateTime;
			render();
			
			//how long it took to render the frame
			renderElapsedTime = System.nanoTime() - startTime - updateElapsedTime;
			
			draw();
			
			//how long it took to draw the final frame to the screen
			drawElapsedTime = System.nanoTime() - startTime - renderElapsedTime;
			
			//total time it took to finalize one tick
			timeElapsed = (System.nanoTime() - startTime - updateElapsedTime) / 1000000;
			if(timeElapsed <= 0) {
				timeElapsed = 1;
			}
			
			//how much time gets added to the accumulator which keeps tabs on when our next update should be
			accumulatedTime += timeElapsed;
			
			fps = (int) (1000 / timeElapsed);
			
			if(Init.doesScreenChange()) {
				partialInit();
			}
		}
	}
	
	public void fullInit() {
		
		partialInit();
		
		gsm = new GameStateManager();
		
		infoFont = new Font("Arial", Font.PLAIN, 12);
		
	}
	
	public void partialInit() {
		if(!Init.fullscreen) {
			image = new BufferedImage(Init.WIDTH, Init.HEIGHT, BufferedImage.TYPE_INT_ARGB);
			g = image.createGraphics();
		}
	}
	
	public void update() {
		gsm.update();
	}
	
	public void render() {
		
		if(Init.fullscreen && !Init.restart) {
			g = (Graphics2D) Init.bufferStrat.getDrawGraphics();
		}
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, Init.WIDTH, Init.HEIGHT);
		
		
		double tweenValueForUpdates = (Math.min(1.0, percentBetweenUpdates));
		gsm.draw(g, tweenValueForUpdates);
		
		
		//draw debug info
		if(drawInfo) {
			g.setColor(Color.WHITE);
			g.setFont(infoFont);
			g.drawString(
					"FPS: " + Integer.toString(fps),
					15,
					15
			);
			
			g.drawString(
					"Target update time: " + Double.toString(targetUpdateTime) + " ms",
					15,
					30
			);
			
			g.drawString(
					"Tick time: " + Long.toString(timeElapsed) + " ms",
					15,
					45
			);
			
			g.drawString(
					"Update time: " + Long.toString(updateElapsedTime / 1000000) + " ms",
					15,
					60
			);
			
			g.drawString(
					"Render time: " + Long.toString(renderElapsedTime / 1000000) + " ms",
					15,
					75
			);
			
			g.drawString(
					"Draw time: " + Long.toString(drawElapsedTime / 1000000) + " ms",
					15,
					90
			);
			
			g.drawString(
					"Resolution: " + Integer.toString(Init.WIDTH) + "x" + Integer.toString(Init.HEIGHT),
					15,
					105
			);
					
		}
	}
	
	
	public void draw() {
		
		if(Init.fullscreen && !Init.restart) {
			g.dispose();
			Init.bufferStrat.show();
		}
		else {
			Graphics bbg = getGraphics();
			bbg.drawImage(image, 0, 0, Init.WIDTH, Init.HEIGHT, null);
			bbg.dispose();
			//image.flush();
		}
		
	}
	
	public static int GetScreenWorkingWidth() {
	    return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	public static int GetScreenWorkingHeight() {
	    return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		if(key.getKeyCode() == KeyEvent.VK_SHIFT) {
			if(drawInfo) {
				drawInfo = false;
			}
			else {
				drawInfo = true;
			}
		}
		gsm.keyPressed(key.getKeyCode());
	}
	
	@Override
	public void keyReleased(KeyEvent key) {
		gsm.keyReleased(key.getKeyCode());
		gsm.keyTyped(key.getKeyChar());
	}
	
	
	@Override
	public void keyTyped(KeyEvent key) {
		//if(key.get() == KeyEvent.VK_QUESTION_MARK);
	}
	
}
