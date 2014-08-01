package WordTypes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import GameState.LevelState;
import Main.GamePanel;

public class ClearWord extends FlashyWord{

	private int widthCounter = 16;
	private int heightCounter = 9;
	private static final int RATIO = 2;
	private static final int EDGE_WIDTH = 5;
	
	public ClearWord(String word, int row, BufferedImage[] explosion) {
		super(word, row, explosion, null);
	}
	
	@Override
	public void update() {
		//if we aren't exploding then continue movement
		if(!isExploding && !isBeingDestroyed) {
			x -= speed;
		}
		else if(isExploding) {
			animation.update();
		}
		else {
			if(widthCounter > GamePanel.WIDTH && heightCounter > GamePanel.HEIGHT) {
				toBeDeleted = true;
			}
			else {
				widthCounter *= RATIO;
				heightCounter *= RATIO;
			}
		}
	}
	
	@Override
	public void drawExplosionProgress(Graphics2D g) {
		//play explosion animations here
		if(isExploding) {
			BufferedImage sprite = animation.getCurrentSprite();
			if(sprite == null) {
				toBeDeleted = true;
			}
			else {
				g.drawImage(
						sprite,
						(int) x - sprite.getWidth() / 2,
						(int) y - sprite.getHeight() / 2,
						null
				);
			}
		}
		else {
			g.setColor(Color.WHITE);
			for(int i = 0; i < EDGE_WIDTH; i++) {
				g.draw(new Rectangle(
						(GamePanel.WIDTH / 2) - (widthCounter / 2) - (i + 1),
						(GamePanel.HEIGHT / 2) - (heightCounter / 2) - (i + 1),
						widthCounter+((i+1)*2),
						heightCounter+((i+1)*2)
				));
			}
		}
	}
	
	@Override
	public void nextLetter(char letterTyped) {
		if(!isExploding) {
			char letter = word.charAt(correctLetters);
			if(letterTyped == letter) {
				correctLetters++;
				if(correctLetters >= word.length()) {
					destroyed();
					if(isSpecialWord) {
						LevelState.increaseScore(word.length() * WordFinder.SPECIAL_WORD_SCORE_MODIFIER);
					}
					else {
						LevelState.increaseScore(word.length());
					}
					LevelState.setClearCorrectLettersFlag(true);
					LevelState.clearAllWords(this);
				}
			}
			else {
				correctLetters = 0;
			}
		}
	}	
}
