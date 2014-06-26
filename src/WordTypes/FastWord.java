package WordTypes;

import java.awt.image.BufferedImage;

import GameState.MainMenuState;

public class FastWord extends Word {

	public static final int SCORE_MODIFIER = 3;
	public static final int SPEED_MODIFIER = 3;
	
	public FastWord(String word, int row, BufferedImage[] explosions, BufferedImage[] destroyedExplosion) {
		super(word, row, explosions, destroyedExplosion);
		speed = (MainMenuState.difficulty + 1) * SPEED_MODIFIER;
		isSpecialWord = true;
	}	
}
