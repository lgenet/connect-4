package connect4;

public class Driver {

	static Thread mainThread;		// The main Game Thread
	static GameWorld myGame;		// THe main game to feed the Thread
	public static void main(String args[])
	{
		myGame = new GameWorld();	// Create the game
		mainThread = new Thread(myGame,"Main Connect 4 Game Thread"); // create/setup the thread
		
		mainThread.run();			// start the thread
	}
}
