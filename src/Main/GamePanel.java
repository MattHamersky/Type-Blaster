package Main;

import GameState.GameStateManager;

import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class GamePanel extends JPanel implements Runnable, KeyListener {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	
	private Thread thread;
	private int fps = 30;
	private int targetTime = 1000 / fps;
	private boolean isRunning = true;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private GameStateManager gsm;
	
	public GamePanel() {
		
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
		
	}
	
	public void addNotify() {
		super.addNotify();
		if(thread == null) {
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
	}
	
	public void run() {
		
		init();
		
		while(isRunning) {
			
			long startTime = System.nanoTime();
			
			update();
			render();
			draw();
			
			long timeElapsed = (System.nanoTime() - startTime) / 1000000;
			long waitTime = targetTime - timeElapsed;
			if(waitTime > 0) {
				try {
					Thread.sleep(waitTime);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void init() {
		
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
		g = image.createGraphics();
		
		gsm = new GameStateManager();
		
	}
	
	public void update() {
		gsm.update();
	}
	
	public void render() {
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		gsm.draw(g);
	}
	
	
	public void draw() {
		
		Graphics bbg = getGraphics();
		bbg.drawImage(image, 0, 0, WIDTH, HEIGHT, null);
		bbg.dispose();
		image.flush();
		
	}
	
	@Override
	public void keyPressed(KeyEvent key) {
		gsm.keyPressed(key.getKeyCode());
	}
	
	@Override
	public void keyReleased(KeyEvent key) {
		gsm.keyReleased(key.getKeyCode());
		gsm.keyTyped(key.getKeyChar());
	}
	
	
	//not using
	@Override
	public void keyTyped(KeyEvent key) {}
	
}
