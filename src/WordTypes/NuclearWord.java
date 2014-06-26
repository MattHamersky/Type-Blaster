package WordTypes;

import java.awt.image.BufferedImage;

public class NuclearWord extends FlashyWord {

	public NuclearWord(String word, int row, BufferedImage[] explosion, BufferedImage[] destroyedExplosion) {
		super(word, row, explosion, destroyedExplosion);
	}
	
	@Override
	public int getHealthReduction() {
		return 75;
	}
	
}
