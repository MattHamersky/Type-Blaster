package WordTypes;

import java.awt.image.BufferedImage;
import java.awt.Color;


/*
 * Word that does what it says when it's typed i.e. the word "nuke" makes a massive explosion,
 * the word "flash" makes a bright flash
 */
public class FlashyWord extends Word {

	public FlashyWord(String word, int row, BufferedImage[] explosions, BufferedImage[] destroyedExplosion) {
		super(word, row, explosions, destroyedExplosion);
		isSpecialWord = true;
		wordColor = Color.GRAY;
	}

}
