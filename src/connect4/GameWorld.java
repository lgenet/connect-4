package connect4;

import java.io.IOException;
import java.util.Scanner;

public class GameWorld implements Runnable
{
	/******************************************************************************************
	 * Instance Variables for the main game thread
	 ******************************************************************************************/
	protected static Board game;			// The game board
	protected static Scanner scan;			// Scanner for reading input
	protected static char playerPiece;		// player 1's piece
	protected static char player2Piece;		// player 2's piece
	protected static AI player1;			// AI for Player1
	protected static AI player2;			// AI for Player2
	protected static double duration;		// Time a player gets to make a move
	protected static boolean doneThinking;	// Boolean to tell the main thread the Ai is done thinking
	private static int maxDepth = 4;		// The maximum Depth that the program will go to by default
	private static int aiWin = 0, randomWin = 0;
	
	
	
	
	/******************************************************************************************
	 * Constructor 
	 * It is empty and more empty 
	 * it does nothing interesting 
	 ******************************************************************************************/
	public GameWorld()
	{	}
	
	
	
	/******************************************************************************************
	 * init
	 * 
	 * This method handles the setup of all the variables for setting up the original board
	 * Reads in the row and columns from the user, defines the player pieces, and then
	 * determines how many pieces have to connect in order to win
	 * 
	 * This method also asks for the time limit for AI moves.  
	 * It will then set up an AI for both player 1 and player 2, just in case a AI v AI 
	 * Option is chose in the selection menu.
	 * 
	 *  @param nothing
	 *  @return nothing
	 ******************************************************************************************/
	private void init()
	{
		// Set up Row, Column sizes
		scan = new Scanner(System.in);
		System.out.println("How big should the board be?");
		System.out.print("Input number of rows: ");
		int x = scan.nextInt();
		System.out.print("Input number of columns: ");
		int y = scan.nextInt();
		
		doneThinking = false;
		
		// Define pieces
		playerPiece = 'R';
		player2Piece = 'Q';
		
		// determine how many chips are needed to win
		System.out.print("\nInput how many pieces must connect to win: ");
		int c = scan.nextInt();
		
		System.out.println("\nTo what depth should I search?");
		maxDepth = scan.nextInt();
		
		System.out.print("\nHow long is a player given to make their move? (in miliseconds) ");
		duration = scan.nextInt();
		
		game = new Board(x,y,c); 										// creates the board	
		player2 = new AI(y,x, maxDepth, playerPiece, player2Piece); 	// creates the AI
		player1 = new AI(y,x, maxDepth, player2Piece, playerPiece);		// Creates AI for player 1 (not always used)
	}
	

	
	/******************************************************************************************
	 * run
	 * 
	 * This method is the starting point for the main thread.  It calls init which 
	 * sets up the game boards.  It will then allow for the selection of a game mode
	 * These are listed in the menu method.
	 * 
	 * 	@param 	none
	 *  @return none
	 ******************************************************************************************/
	public void run() 
	{
		init();
		selectGameMode();
	}
	
	
	/******************************************************************************************
	 * print Menu
	 * 
	 * This lists all the current menu options for the various game modes that are available
	 * 
	 * @param 	none
	 * @return	none
	 ******************************************************************************************/
	private void printMenu()
	{
		System.out.println("=====================");
		System.out.println("=       Menu        =");
		System.out.println("=====================");
		System.out.println("= 1) Human Vs AI    =");
		System.out.println("= 2) AI vs Human    =");
		System.out.println("= 3) AI vs AI       =");
		System.out.println("= 4) Random vs AI   =");
		System.out.println("= 5) Human vs Human =");
		System.out.println("=====================");
	}
	
	
	
	/******************************************************************************************
	 * selectGameMode
	 * 
	 * This method is the dispatcher for the program.  IT is here that the user is allowed
	 * to pick their game mode.  There are currently four, soon to be five game modes.  
	 *
	 * See the printMenu method for a full listing of the modes.  
	 * 
	 * @param none
	 * @return none
	 * @see printMenu
	 ******************************************************************************************/
	private void selectGameMode()
	{
		printMenu();
		System.out.println("Please enter the game mode you want: ");
		int choice = scan.nextInt();
		
		System.out.println("Player 1 will always place tokens with the letter \"" + playerPiece  + "\"\n");
		System.out.println("Player 2 will always place tokens with the letter \"" + player2Piece + "\"\n");
		switch(choice)
		{
			case 1:
				displayRulesPvsAi();
				playerVAi();
				break;
			case 2:
				displayRulesAivsP();
				AIvPlayer();
				break;
			case 3:
				displayRulesCompVComp();
				compVcomp();
				break;
			case 4:
				displayRulesAivsRandom();
				RandomVsAi();
				break;
			case 5:
				displayRulesPvP();
				playerVplayer();
				break;
			case 6:
				System.out.print("About to run a stress test v Random");
				runAnalytics();
				break;
			default:
				System.out.println("Invalid Choice...");	
				break;
		}
	}
	
	
	
	/******************************************************************************
	 * Display Menus
	 * 
	 * These are the menus and prompts for the user prior to playing the games
	 * They are designed to give the suer a little bit of information about
	 * the particular game mode.  
	 * 
	 * They all require the user to press enter at the end of the message to
	 * continue into the game.
	 * 
	 * @param 	none
	 * @return 	none
	 *****************************************************************************/
	private void displayRulesCompVComp()
	{
		
		System.out.println("Player 1 (An AI)) will be given a chance to move.  "
				+ "\nThe AI for Player 1 will select a move, and place it into a valid column."
				+ "\nPlayer 2 (a second AI) will then be given a chance to make a move. Provided the first AI (Player 1) didnt win."
				+ "\nThe AI for Player 2 will select a move, and place it into a valid column."
				+ "\nThe game will continue until either AI (player 1 or 2) wins."
				+ "\nor the board becomes unplayable.");
		System.out.println("Please input your choice via the column numbers dispalyed on the screen."
				+ "\nThe first colum should be entered as 1, and not 0.\n");
		System.out.println("\nPress enter key to start the game...");
		try 
		{ System.in.read(); } 
		catch (IOException e) 
		{ }
	}
	// Displays rules for the Ai going first
	private void displayRulesAivsP()
	{
		
		System.out.println("Player 2 (the AI)) will be given a chance to move.  "
				+ "\nThe Ai will select a move, and place it into a valid column."
				+ "\nPlayer 1 will then be given a chance to make a move. Provided the AI didnt win."
				+ "\nPlayer one should enter just the column number they wish to place their piece into."
				+ "\nThe game will continue until a player wins."
				+ "\nor the board becomes unplayable.");
		System.out.println("Please input your choice via the column numbers dispalyed on the screen."
				+ "\nThe first colum should be entered as 1, and not 0.\n");
		System.out.println("\nPress enter key to start the game...");
		try 
		{ System.in.read(); } 
		catch (IOException e) 
		{ }
	}
	// Displays info about the AI vs Random AI
	private void displayRulesAivsRandom()
	{
		
		System.out.println("Player 1 (the AI)) will be given a chance to move.  "
				+ "\nThe Ai will select a move, and place it into a valid column."
				+ "\nPlayer 2 will be a random Ai."
				+ "\nThis meaning that it will place a piece in a random column."
				+ "\nThe game will continue until a player wins."
				+ "\nor the board becomes unplayable.");
		System.out.println("There is no user input for this mode.  This is simply for viewing pleasure.  "
				+ "becuase honestly, who does not like to see an AI defeat a random agent.\n");
		System.out.println("\nPress enter key to start the game...");
		try 
		{ System.in.read(); } 
		catch (IOException e) 
		{ }
	}
	// Dispalys the rules for the user going first (AI goes second)
	private static void displayRulesPvsAi()
	{
		
		System.out.println("Player 1 will be given a chance to move.  "
				+ "\nPlayer one should enter just the column number they wish to place their piece into"
				+ "\nThe Ai will then make its move provided Player 1 did not win."
				+ "\nThe game will continue until a player wins"
				+ "\nor the board becomes unplayable.");
		System.out.println("Please input your choice via the column numbers dispalyed on the screen."
				+ "\nThe first colum should be entered as 1, and not 0.\n");
		System.out.println("\nPress enter key to start the game...");
		try 
		{ System.in.read(); } 
		catch (IOException e) 
		{ }
	}
	// Displays the rules for a PvP game
	private void displayRulesPvP()
	{
		
		System.out.println("Player 1 will be given a chance to move.  "
				+ "\nPlayer one should enter just the column number they wish to place their piece into"
				+ "\nThen Player 2 will be given a chance to place, provided Player 1 did not win."
				+ "\nThe game will continue until a player wins"
				+ "\nor the board becomes unplayable.");
		System.out.println("\nPress enter key to continue...");
		try 
		{ System.in.read(); } 
		catch (IOException e) 
		{ }
	}
	
	
	/********************************************************************************
	 * Player Move 
	 * 
	 * Allows for the user to input their choice of columns to place a piece into
	 * The program will then attempt to place it into the current board.  
	 * The placePiece method will handle errors in piece placement and updating
	 * 
	 * @see 	placePiece
	 * @return 	true, if the user was able to place their piece.  
	 * 			false, if the user's specified column was A) Full or B) invalid 
	 * @Note: 	The boolean return feature has been deprecated in the running loop.
	 * 			The true false value is no longer used
	 ********************************************************************************/
	private boolean playerMove()
	{
		System.out.println("Player 1, it is your turn to make a move.");
		System.out.print("Player 1: Column ");
		int choice = scan.nextInt();
		choice--;
		if(!game.placePiece(choice, playerPiece))
			return false;
		else 
			return true;
	}
	private boolean player2Move()
	{
		System.out.println("Player 2, it is your turn to make a move.");
		System.out.print("Player 2: Column ");
		int choice = scan.nextInt();
		choice--;
		if(!game.placePiece(choice, player2Piece))
			return false;
		else 
			return true;
	}
	
	
	/********************************************************************************
	 * player2AiMove
	 * 
	 * This method invokes the AI's logic to make a move.  It handles all output
	 * and calls the placePiece method to actually place the piece into the current
	 * game board
	 * 
	 * In further detail, this method uses threading to help achieve its goals in a
	 * timely manner.  A thread is spawned that will run on the Player2 object.
	 * The thread is primed by first (before spawning it) updating its starting point
	 * with the current game board.  It will use this as the root for the minimax 
	 * tree that will be generated.  
	 * 
	 * The Ai Thread will start, and call its think method.  The Main thread will 
	 * sleep for the duration that was inputed during init.  After which point,
	 * the Ai's thread will be interrupted, and the best move possible will be 
	 * obtained.  All further output from the thread will be disabled and wait for
	 * the thread to terminate itself properly.  
	 * 
	 * Notice that this will give a move, regardless of whether or not the AI finished
	 * its search for a valid move.  
	 * 
	 * If the AI did not finish, the quality of the returned move my by subpar.
	 * 
	 * @see AI Think
	 * @see placePiece
	 * 
	 ********************************************************************************/
	private void player2AiMove()
	{
		System.out.println("Player 2, is thinking of a move.");

		player2.setMasterBoard(game);
		Thread AiThread = new Thread(player2);

		AiThread.start();

		try 
		{  
			Thread.sleep(((long) duration)/2);
			System.out.println("Player 2, Half of your time has expired.");
			if(doneThinking == false)
			{
				Thread.sleep(((long) duration)/2);
				System.out.println("Player 2, your time is up.  Please make your move.");
			}
		} 
		catch (InterruptedException e) 
		{	System.err.println("Ive been interupted!");
			e.printStackTrace();  }
		
		AiThread.interrupt();
		player2.setActive(false);
		int choice = player2.getBestMove();

		System.out.println("Player 2: column " + (choice+1));
		
		boolean validity = game.placePiece(choice, player2Piece);
		while(validity == false)
		{
			choice++;
			if(choice >= game.getColumnSize())
				choice = 0;
			validity = game.modifyBoard(choice, player2Piece);
		}
	}
	
	/******************************************************************************************
	 * player1 move
	 * 
	 * This method does the same thing as the player2AiMove method
	 * However, this one would do it for the player1 Ai. 
	 * 
	 * Only used for AI v AI mode.
	 * 
	 * @see player2AiMove
	 ******************************************************************************************/
	private void player1AiMove()
	{
		System.out.println("Player 1, is thinking of a move.");

		player1.setMasterBoard(game);
		Thread AiThread = new Thread(player1);

		AiThread.start();

		try 
		{  
			Thread.sleep(((long) duration)/2);
			System.out.println("Player 1, Half of your time has expired.");
			if(doneThinking == false)
			{
				Thread.sleep(((long) duration)/2);
				System.out.println("Player 1, your time is up.  Please make your move.");
			}
		} 
		catch (InterruptedException e) 
		{	System.err.println("Ive been interupted!");
			e.printStackTrace();  }
		
		AiThread.interrupt();
		player1.setActive(false);
		int choice = player1.getBestMove();

		System.out.println("Player 1: column " + (choice+1));
		
		boolean validity = game.placePiece(choice, playerPiece);
		while(validity == false)
		{
			choice++;
			if(choice >= game.getColumnSize())
				choice = 0;
			validity = game.modifyBoard(choice, playerPiece);
		}
	}
	
	/******************************************************************************************
	 * player1 Random Move
	 * 
	 * This creates a random agent to generate a move that is between 1-n.  It is used for
	 * The Ai V Random mode and analytical testing modes.  
	 * 
	 * @note	This Random move is promised to be in the bounds of n, but not in the bounds
	 * 			of m.  It can place a piece in a full column.  
	 ******************************************************************************************/
	private void player1RandomMove()
	{
		System.out.println("Player 1(Random), is thinking of a move.");

		int choice = player1.guess();

		System.out.println("Player 1(Random): column " + (choice+1));
		game.placePiece(choice, playerPiece);
	}
	
	
	
	/******************************************************************************************
	 * run Analytics
	 * 
	 * This is a method that is not on the main menu.  It is the sixth running mode for the
	 * connect 4 game presented here.  It pits the Ai against a random agent in a bout of
	 * five hundred games.  It will display the win/lose statistics.  This takes a LONG 
	 * TIME to run!  Do not use unless you are dedicated to seeing the results.  
	 ******************************************************************************************/
	private void runAnalytics()
	{
		int count = 0;
		// Set up Row, Column sizes
		scan = new Scanner(System.in);
		System.out.println("How big should the board be?");
		System.out.print("Input number of rows: ");
		int x = scan.nextInt();
		System.out.print("Input number of columns: ");
		int y = scan.nextInt();
		
		doneThinking = false;
		
		// Define pieces
		playerPiece = 'R';
		player2Piece = 'Q';
		
		// determine how many chips are needed to win
		System.out.print("\nInput how many pieces must connect to win: ");
		int c = scan.nextInt();
		
		System.out.print("\nHow long is a player given to make their move? (in miliseconds) ");
		duration = scan.nextInt();
		
		
		while(count < 500)
		{
			game = new Board(x,y,c); 										// creates the board	
			player2 = new AI(y,x, maxDepth, playerPiece, player2Piece); 		// creates the AI
			player1 = new AI(y,x, maxDepth, player2Piece, playerPiece);
			analytics();
			count++;
		}
		System.out.println("In 100 games.  The ai won: " +aiWin + " | The random agent won: " + randomWin);
	}
	// This is a stripped down version of random v Ai.  No printing methods here.
	private void analytics()
	{
		while(!game.isOver())
		{
			int choice = player1.guess();
			game.placePiece(choice, playerPiece);
			
			if(game.isOver())
			{
				randomWin++;
				return;
			}
			
			choice = player2.think(game);
			game.placePiece(choice, player2Piece);
		}	
		aiWin ++;
		return;
	}
	
	/******************************************************************************************
	 * random Vs Ai
	 * 
	 * This is the running method for the random vs ai game mode.  The random Agent will pick
	 * a random move first, and place it.  The Ai will then think of a move and place it
	 * This continues until one or the other wins.  
	 ******************************************************************************************/
	private void RandomVsAi()
	{
		while(!game.isOver())
		{
			player1RandomMove();
			System.out.println(game);
			long st = System.currentTimeMillis();
			System.out.println("The board is worth: "+rateBoard(game,playerPiece,player2Piece)
					+ " to Player " + playerPiece);
			long stop = System.currentTimeMillis();
			
			System.out.println("Rated in " +(stop - st) + " ms");
			
			if(game.isOver())
				break;
			
			player2AiMove();
			System.out.println(game);
			System.out.println("The board is worth: "+rateBoard(game, player2Piece, playerPiece)
					+ " to Player " + player2Piece);
		}	
		System.out.println("Thank you for playing the game.");
	}
	
	/******************************************************************************************
	 * ai v ai
	 * 
	 * This function pits one intelgent agent against another.  They use the same logic and
	 * are both objects of the same class.  By that token, they should be identical.  I thought
	 * it would be interesting to see what would happen if i pitted two of my Ai's against
	 * each other.  This method is the result.
	 * 
	 * It should also be menu option number 3 on the menu board.  Menu option number 3...
	 ******************************************************************************************/
	private void compVcomp()
	{
		while(!game.isOver())
		{
			player1AiMove();
			System.out.println(game);
			long st = System.currentTimeMillis();
			System.out.println("The board is worth: "+rateBoard(game,playerPiece,player2Piece)
					+ " to Player " + playerPiece);
			long stop = System.currentTimeMillis();
			
			System.out.println("Rated in " +(stop - st) + " ms");
			
			if(game.isOver())
				break;
			
			player2AiMove();
			System.out.println(game);
			st = System.currentTimeMillis();
			System.out.println("The board is worth: "+rateBoard(game, player2Piece, playerPiece)
					+ " to Player " + player2Piece);
			stop = System.currentTimeMillis();
			
			System.out.println("Rated in " + (stop - st) + " ms");
		}	
		System.out.println("Thank you for playing the game.");
	}
	
	
	
	/******************************************************************************************
	 * player v player
	 * 
	 * Back by popular demand!  This is the fifth mode in the game menu.  It allows for two 
	 * players to compete against each other.  There is no Ai agent playing on either end
	 * 
	 * All the other functionality of the ai v player and player v ai modes are present.
	 ******************************************************************************************/
	private void playerVplayer()
	{
		System.out.println(game);
		while(!game.isOver())
		{
			playerMove();
			System.out.println(game);
			long st = System.currentTimeMillis();
			System.out.println("The board is worth: "+rateBoard(game,playerPiece,player2Piece)
					+ " to Player " + playerPiece);
			long stop = System.currentTimeMillis();
			
			System.out.println("Rated in " +(stop - st) + " ms");
			
			if(game.isOver())
				break;
			
			player2Move();
			System.out.println(game);
			System.out.println("The board is worth: "+rateBoard(game, player2Piece, playerPiece)
					+ " to Player " + player2Piece);
		}	
		System.out.println("Thank you for playing the game.");
	}
	
	
	/******************************************************************************************
	 * Player v Ai
	 * 
	 * This is the function that will allow for the player to go first and the Ai to go second
	 * It allows the player to make a move, then the Ai will make there movce.  And it will
	 * continue in this fashion until either player wins the game or the board becomes 
	 * unplayble (in the case of a tie.)
	 ******************************************************************************************/
	private void playerVAi()
	{
		System.out.println(game); // Initial board print for player 1 to make a move off of.
		while(!game.isOver())
		{
			playerMove();
			System.out.println(game);
			long st = System.currentTimeMillis();
			System.out.println("The board is worth: "+rateBoard(game,playerPiece,player2Piece)
					+ " to Player " + playerPiece);
			long stop = System.currentTimeMillis();
			
			System.out.println("Rated in " +(stop - st) + " ms");
			
			if(game.isOver())
				break;
			
			player2AiMove();
			System.out.println(game);
			System.out.println("The board is worth: "+rateBoard(game, player2Piece, playerPiece)
					+ " to Player " + player2Piece);
		}	
		System.out.println("Thank you for playing the game.");
	}
	
	
	/******************************************************************************************
	 * Ai v Player
	 * 
	 * This game mode allow for the player to go second, and the Ai to have the first move
	 * It works the same as the player v Ai move, only in reverse.  
	 ******************************************************************************************/
	private void AIvPlayer()
	{
		while(!game.isOver())
		{
			player2AiMove();
			System.out.println(game);
			System.out.println("The board is worth: "+rateBoard(game, player2Piece, playerPiece)
					+ " to Player " + player2Piece);
			
			if(game.isOver())
				break;
			
			playerMove();
			System.out.println(game);
			long st = System.currentTimeMillis();
			System.out.println("The board is worth: "+rateBoard(game,playerPiece,player2Piece)
					+ " to Player " + playerPiece);
			long stop = System.currentTimeMillis();
			
			System.out.println("Rated in " +(stop - st) + " ms");			
		}	
		System.out.println("Thank you for playing the game.");
	}
	
	
	/******************************************************************************************
	 * rate Board
	 * 
	 * Will generate a state and rate the current game board to give the player/ai an idea of 
	 * where they stand based on the boards rating.
	 ******************************************************************************************/
	private double rateBoard(Board snapshot, char letter, char oppChar)
	{

		State s = new State(snapshot,letter, oppChar);
		s.determineUtility();
		return s.getUtility();
	}
	
}
