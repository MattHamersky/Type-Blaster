package GameState;

import Main.GamePanel;

import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class OpeningState extends GameState {

	private BufferedImage logo;
	
	private float alpha = 0f;
	
	private boolean halfway = false;
	private boolean finished = false;
	
	public OpeningState(GameStateManager gsm) {
		super(gsm);
		init();
		
	}
	
	public synchronized void finished() {
		finished = true;
	}
	public synchronized boolean isFinished() {
		return finished;
	}
	
	public void init() {
		try {
			logo = ImageIO.read(getClass().getResourceAsStream("/images/logo/logo.png"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update() {
		if(!halfway) {
			alpha += 0.01f;
			if(alpha >= 1f) {
				alpha = 1f;
				halfway = true;
			}
		}
		if(halfway) {
			alpha += -0.01f;
			if(alpha <= 0) {
				alpha = 0;
				finished();
			}
		}
	}
	
	public void draw(Graphics2D g, double percentBetweenUpdates) {
		if(isFinished()) {
			//reset alpha to fully opaque
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			gsm.changeState(GameStateManager.MAINMENUSTATE);
			return;
		}
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.clearRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		g.drawImage(
				logo,
				0,
				0,
				null
		);
	}
	
	@Override
	public void keyPressed(int key) {
		if(key == KeyEvent.VK_UP ||
		   key == KeyEvent.VK_ESCAPE ||
		   key == KeyEvent.VK_SPACE){
			finished();
		}
	}

	@Override
	public void keyReleased(int key) {}
	
}
