package connect4;

public class State 
{
	/**************************************************
	 * Instance Variables
	 * 
	 * These are all the variables that belong to the
	 * State Class.
	 **************************************************/
	public Board myBoard;		// The current board for this state
	private double utility;		// The utility of this board to the given player
	private char owner;			// The owner of this state (the person who would place a piece to create this board
	private char opponent;		// The opposing Player
	private int key;			// The move that would generate this board from its parent state
	
	
	
	/**************************************************
	 * Copy Constructor
	 * 
	 * This constructor is used for mutating boards
	 * Mutating is used for lack of a better word.
	 * It takes in the parent state and generates a 
	 * complete copy of it.  This is done so that the
	 * parent board is not modified in creation of the
	 * child board.  
	 * 
	 * Defaults utility to random and key to old.
	 * 
	 * @param old	The parent state that this board
	 * 				is to be based off.
	 **************************************************/
	public State(State old)
	{
		myBoard = new Board(old.myBoard);
		owner = old.owner;
		opponent = old.opponent;
		key = old.key;
		utility = Math.random();
	}
	/**************************************************
	 * Default Constructor
	 * 
	 * Unused, but kept for base needs.  
	 * Defaults boards to obviously wrong values
	 **************************************************/
	public State()
	{
		myBoard = new Board(10,10,10);
		owner = '|';
		opponent = '^';
		key = 0;
	}
	/**************************************************
	 * Parameterize Constructor
	 * 
	 * This is used for creating a brand new state.  
	 * This is mainly done when the tree is started
	 * The root would be generated with this constructor
	 * 
	 * @param c			The Current Game Board
	 * @param letter	The letter of the owning Player
	 * @param o			The letter of the opposing Player
	 **************************************************/
	public State(Board c, char letter,char o)
	{
		myBoard = c;
		owner = letter;
		opponent = o;
		key = -1;					// Initializes Key to be an invalid Move
		utility = Math.random();	// Initializes Utility to be Random
	}
	
	
	/**************************************************
	 * Accessors and Mutators
	 * 
	 * These are the accessor and mutator methods for
	 * the instances variables in the State class
	 **************************************************/
	public void setOwner(char letter) 
	{
		owner = letter;
	}
	public int getKey()
	{
		return key;
	}
	public void setKey(int place) 
	{
		key = place;
	}
	public void setOpponent(char letter) 
	{
		opponent = letter;
	}
	public double getUtility() {
		return utility;
	}
	public char getOwner() 
	{
		return owner;
	}
	public void setUtility(double util) {
		utility = util;
	}

	/**************************************************
	 * Determine Utility
	 * 
	 * This method will call upon the various pieces of the Heuristic to determine the utility of a given board
	 * It first checks to see if the move is a winning move (killer).  
	 * If not, it will then check to see if the move
	 * is involved in a standard blocking move. preventing r-1 from being connected
	 * 
	 * Now it will check for other blocking techniques and stupid moves
	 * 
	 * Avoiding piggy-backing moves in which the Ai placing a piece allows 
	 * for the opponent to then place a piece and win the game
	 * 
	 * Block Multi-Win cases prevent moves that allow for double wins such as _R_RR_
	 * If they AI does not fill in the middle slot between the R's the opponent will
	 * get a multi-win case (winning in two directions)
	 * 
	 * If none are these are the case, it will then count the number of available winning moves
	 * for the Ai, then subtracting from it the number of moves the opponent can make that would
	 * result in them winning.  That then becomes the base value.  
	 **************************************************/
	public void determineUtility()
	{
		utility = myBoard.checkForKillerMoves(owner, key);
		if(utility != 0)
			return; // no need to check anything else, this is obviously the best move possible (or in a set of best moves)
		
		// if you do not win from this move, then you need to see if this move will block your opponent.
		utility = myBoard.checkForBlockingMove(owner, opponent, key);
		if(utility != 0) // if you can block, you should take that move
			return;
		
		utility = myBoard.avoidPiggyBacking(key, opponent,owner);
		if(utility != 0)
			return;
		
		utility = myBoard.blockMultiWinCases(owner, opponent, key);
		if(utility!=0)
			return;
		
		utility = myBoard.blockStumpTraps(owner, opponent, key);
		if(utility!=0)
			return;
		
		// if no critical move is to be made, rate accordingly
		utility += myBoard.countPatterns(owner);
		utility -= myBoard.countPatterns(opponent);
	}
	
	
	/**************************************************
	 * toString
	 * 
	 * Tells a state how to print itself.  It simply
	 * prints out the board by making a call to the 
	 * toString method in the Board class.
	 **************************************************/
	public String toString()
	{
		String out ="";
		out += myBoard;
		return out;
	}
}
