package GameState;

import java.awt.Graphics2D;
import java.awt.AlphaComposite;

public class ScoreEffect {

	private long score;
	
	private int x;
	private int y;
	private int deltaX;
	private int deltaY;
	
	private float alpha = 1f;
	private float alphaTime = 0.1f;
	
	private long startTime;
	private long transitionTime = 75;
	
	private boolean shouldBeDeleted = false;
	
	public ScoreEffect(long score, int x, int y, int deltaX, int deltaY) {
		this.score = score;
		this.x = x;
		this.y = y;
		this.deltaX = deltaX;
		this.deltaY = deltaY;
		this.startTime = System.nanoTime();
	}
	
	public void update() {
		if(System.nanoTime() - startTime > transitionTime) {
			alpha -= alphaTime;
			if(alpha <= 0) {
				alpha = 0;
				shouldBeDeleted = true;
			}
			x += deltaX;
			y += deltaY;
		}
	}
	
	public void draw(Graphics2D g) {
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		g.drawString(
				Long.toString(score),
				x,
				y
		);
		
		//reset alpha so subsequent draws aren't fading
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}
	
	public boolean shouldBeDeleted() {
		return shouldBeDeleted;
	}
	
}
