package WordTypes;

import Main.GamePanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class StrobeWord extends FlashyWord {

	//private float[] strobeIntensities = {.
	private long startTime;
	private long delay = 100;
	private int numOfStrobes = 10;
	private int currNumOfStrobes = 0;
	
	private boolean drawExplosion = false;
	
	public StrobeWord(String word, int row) {
		super(word, row, null, null);
	}
	
	@Override
	public void update() {
		//if we aren't exploding then continue movement
		if(!isExploding && !isBeingDestroyed) {
			x -= speed;
		}
		if(startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		if((System.currentTimeMillis() - startTime) > delay) {
			drawExplosion = true;
			currNumOfStrobes++;
			startTime = System.currentTimeMillis();
		}
		if(currNumOfStrobes >= numOfStrobes) {
			toBeDeleted = true;
		}
	}
	
	@Override
	public void drawExplosionProgress(Graphics2D g) {
		//strobe it up
		if(drawExplosion) {
			drawExplosion = false;
			g.setColor(Color.WHITE);
			g.fillRect(
					0,
					0,
					GamePanel.WIDTH,
					GamePanel.HEIGHT
			);
		}
	}
}
