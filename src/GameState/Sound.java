package GameState;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;

public class Sound {

	private Clip clip;
	private long length;
	
	public Sound(String name, long length) {
		try {
			this.length = length;
			AudioInputStream audio = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/music/"+name));
			clip = AudioSystem.getClip();
			clip.open(audio);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void play() {
		if(clip == null) {
			return;
		}
		stop();
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void stop() {
		if(clip.isRunning()) {
			clip.stop();
		}
	}
	
	public boolean isRunning() {
		return clip.isRunning();
	}
	
	public long getSongLength() {
		return length;
	}
}
