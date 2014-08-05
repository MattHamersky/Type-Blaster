package WordTypes;

import Main.GamePanel;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class StrobeWord extends FlashyWord {

	private long startTime;
	private long delay = 100;
	private int numOfStrobes = 10;
	private int currNumOfStrobes = 0;
	
	private boolean drawStrobe = false;
	private int updates = 0;
	
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
		if(isBeingDestroyed || isExploding) {
			updates++;
			
			//every other update we draw a strobe
			if(updates % 2 == 0) {
				drawStrobe = true;
				currNumOfStrobes++;
			}
			else {
				drawStrobe = false;
			}			
		}
		if(currNumOfStrobes >= numOfStrobes) {
			toBeDeleted = true;
		}
	}
	
	@Override
	public void drawExplosionProgress(Graphics2D g) {
		//strobe it up
		if(drawStrobe) {
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
