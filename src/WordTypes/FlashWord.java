package WordTypes;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.AlphaComposite;

import Main.GamePanel;

public class FlashWord extends FlashyWord {
	
	private long startTime;
	private long delay = 150;
	private float alpha = 1f;
	private float decrement = .1f;
	
	public FlashWord(String word, int row) {
		super(word, row, null, null);
	}
	
	@Override
	public void update() {
		//if we aren't exploding then continue movement
		if(!isExploding && !isBeingDestroyed) {
			x -= speed;
		}
	}
	
	@Override
	public void drawExplosionProgress(Graphics2D g) {
		if(startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		else {
			if((System.currentTimeMillis() - startTime) > delay) {
				startTime = System.currentTimeMillis();
				alpha -= decrement;
			}
			if(alpha < 0) {
				toBeDeleted = true;
			}
			else {
				g.setColor(Color.WHITE);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
				g.fillRect(
						0,
						0,
						GamePanel.WIDTH,
						GamePanel.HEIGHT
				);
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
			}
		}
	}
}
