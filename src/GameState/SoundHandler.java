package GameState;

import java.util.Random;

public class SoundHandler implements Runnable {
	
	private volatile boolean isRunning = false;
	
	private Sound[] songs = {new Sound("complex.wav", 261000), new Sound("pamgaea.wav", 168000), new Sound("space_fighter.wav", 103000), new Sound("undaunted.wav", 211000)};
	private int currSongIndex;
	
	private Random rand = new Random(System.currentTimeMillis());
	
	private Thread thread = null;
	
	public synchronized void start() {
		if(isRunning) {
			return;
		}
		currSongIndex = rand.nextInt(songs.length);
		isRunning = true;
		thread = new Thread(this);
		thread.start();
		
	}
	
	public synchronized void stop() {
		isRunning = false;
		thread.interrupt();
	}
	
	public void run() {
		while(isRunning) {
			if(!songs[currSongIndex].isRunning()) {
				songs[currSongIndex].play();
			}
			try {
				Thread.sleep(songs[currSongIndex].getSongLength());
				currSongIndex++;
				if(currSongIndex >= songs.length)
					currSongIndex = 0;
			} catch(InterruptedException e) {
				songs[currSongIndex].stop();
				e.printStackTrace();
			}
		}
	}
}
