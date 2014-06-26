package GameState;

import java.awt.image.BufferedImage;

public class Animation {

	private BufferedImage[] spriteSheet;
	private int currentSpriteIndex;
	
	private int delay;
	private long startTime;
	
	private boolean playOnce;
	
	public Animation() {}
	
	public void setSprites(BufferedImage[] sprites, boolean playOnce) {
		this.playOnce = playOnce;
		setSprites(sprites);
	}
	
	public void setSprites(BufferedImage[] sprites) {
		startTime = 0;
		spriteSheet = sprites;
	}
	
	public void setDelay(int time) {
		delay = time;
	}
	
	public void update() {
		if(startTime == 0) {
			startTime = System.currentTimeMillis();
		}
		if(System.currentTimeMillis() - startTime >= delay) {
			currentSpriteIndex++;
			startTime = System.currentTimeMillis();
			if(currentSpriteIndex == spriteSheet.length && !playOnce) {
				currentSpriteIndex = 0;
			}
		}
	}
	
	public BufferedImage getCurrentSprite() {
		if(currentSpriteIndex >= spriteSheet.length) {
			return null;
		}
		return spriteSheet[currentSpriteIndex];
	}
	
}
