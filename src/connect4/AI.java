package connect4;
import java.util.TimerTask;
public class AI extends TimerTask{

	/**************************************************
	 * Instance Variables for the AI
	 **************************************************/
	private int MAX_DEPTH;			// Max Depth to build the tree to
	private int columnSize;			// The size of the board X direction (max columns)
	private int rowSize;			// The size of the board Y direction (max rows)
	private int bestMove;			// The best move that has been found thus far
	protected MiniMaxTree mmT;		// The actual MiniMaxTree Object
	private int numNodes;			// number of nodes generated.  Deprecated
	private boolean stopBuilding;		// Unused Variable for denoting when to stop building the tree
	private char playerLetter;		// The letter for player1
	private char aiLetter;			// The letter for player2
	
	private Board masterBoard;		// A copy of the current board
	protected boolean active;		// Whether or not the thread should print
	
	
	/*******************************
	 * Constructor
	 * 
	 * Gives the AI dummy values,
	 * This is not used in practice
	 *******************************/
	public AI()
	{
		columnSize = 10;
		stopBuilding = false;
	}
	/**************************************************
	 * Constructor - Parameterized 
	 * 
	 * This constructor takes in a variety of parameters
	 * to help construct and set up an AI
	 * @param n			number of clumns
	 * @param m			number of rows
	 * @param maxD		max depth to build to
	 * @param player	the player(opponents) letter
	 * @param aiLet		the AI(owners) letter
	 **************************************************/
	public AI(int n, int m, int maxD, char player, char aiLet)
	{
		MAX_DEPTH = maxD;
		playerLetter = player;
		aiLetter = aiLet;
		columnSize = n;
		rowSize = m;
		stopBuilding = false;
		if(MAX_DEPTH >= columnSize)
			MAX_DEPTH = columnSize/2;
		mmT = new MiniMaxTree();
		active = true;
	}
	
	
	
	/**************************************************
	 * guess
	 * 
	 * This was the first stab at AI.  It randomly
	 * picks a value that is in the set 0-n
	 * 
	 * Used by the random Agents
	 * @return		a random move
	 **************************************************/
	public int guess()
	{
		return (int)(Math.abs(Math.random()*columnSize));
	}
	
	
	
	/**************************************************
	 * think
	 * 
	 * The dispatcher of the AIs logical components
	 * First builds the miniMax tree from game
	 * 
	 * The best move is defaulted to 4.
	 * If the AI Thread still has time left, analytics
	 * will print.
	 * 
	 * The tree will then be searched for the best move
	 * This includes use of the pruning/killer() func
	 * 
	 * If it is done, print out the analytics
	 * 
	 * @param game		The current game board
	 * @return			The move that should be taken
	 ***************************************************/
	public int think(Board game) 
	{

		GameWorld.doneThinking = false;
		active = true;
		long start = System.currentTimeMillis();
		buildTree(game);
		long stop = System.currentTimeMillis();
		bestMove = columnSize/2;
		if(active)
			System.out.println("Generated Tree in: " + (stop-start) + " ms");
		
		start = System.currentTimeMillis();
		bestMove = mmT.miniMax(aiLetter);
		stop = System.currentTimeMillis();

		if(active)
		{
			System.out.println("Found move in: " + (stop-start) + " ms");
			Driver.mainThread.interrupt();
		}
		
		GameWorld.doneThinking = true;
		return bestMove;
	}
	
	
	
	/**************************************************
	 * build Tree
	 * 
	 * This is the method that calls the recusive version
	 *  for each child of the root
	 * It is from here that the miniMax Tree is built.  
	 * 
	 * @param currentBoard	the starting board
	 * @return		TRUE if the tree was built
	 * 				Successfully.
	 * 				FALSE if the tree failed to be 
	 * 				built for whatever reason
	 **************************************************/
	public boolean buildTree(Board currentBoard)
	{
		numNodes = 0;
		mmT = new MiniMaxTree(new State(new Board(currentBoard),playerLetter,aiLetter),MAX_DEPTH);
		int depth = 0;
		for(int i = 0; i < columnSize; i++)
		{
			mmT.root.children.add(build(mmT.root,i,depth));
			if(i < mmT.root.children.size() && mmT.root.children.get(i) != null)
				mmT.root.children.get(i).myState.determineUtility();
		}
		return true;
	}
	
	/**************************************************
	 * build
	 * 
	 * This method is recursive and builds branches 
	 * of the minimax tree.
	 * 
	 * @param current		The current node that is 
	 * 						being built off of
	 * @param placement		The column for a given board
	 * 						to be mutated with (adding 
	 * 						a piece to this board) 
	 * @param depth			The current dpeth 
	 * @return				a constructed child node
	 * 						or branch
	 **************************************************/
	private Node build(Node current, int placement, int depth)
	{
		Node n = new Node();
		
		// Base Case, handles reaching the max Depth or filling a colum (invalid moves)
		if(depth == MAX_DEPTH || current.myState.myBoard.findHighestInCol(placement) == 0)
		{
			n = makeNode(current.myState, placement, depth);
			return n;
		}
		// If the depth is not invalid and is a valid move
		if(depth < MAX_DEPTH)
		{
			n = makeNode(current.myState, placement, depth); // creates the node
			if(n.myState.getUtility() == -500) // makes sure that it is valid
				return null;	// Introduces a null node!
			// for each column, generate a child (possible move)
			for(int i = 0; i < columnSize; i++)
			{
				Node child = build(n,i, depth+1);
				if(child==null) // if it is null, remove it, if it exists in the list
					n.children.remove(child);
				else if(child.myState.getUtility() <= -3 && child.myState.getOwner() == aiLetter)
					n.children.remove(child); // if it needs forward pruning, prune it
				else						// This is a recent addition that might simplify code in killer()
					n.addChild(child);	// IF the utility/state is good it will be added
			}
		}	
		return n;
	}

	/************************************************************
	 * make Node
	 * 
	 * This method takes in parameters, and makes a 
	 * deep copy of these parameters into a new node
	 * 
	 * All important info for the node is set up here, 
	 * including having its utility determined'
	 * 
	 * @param s			The state the node will hold
	 * @param place		Where the mutation occurs
	 * 					by mutation, i mean the letter
	 * 					that is added to the board to 
	 * 					make it different than its parent
	 * 					this mutation also represents a
	 * 					valid move from the parent board
	 * @param depth		What depth can this node be
	 * 					found at.
	 * @return			the newly constructed node, 
	 ************************************************************/
	private Node makeNode(State s, int place, int depth)
	{
		Node n = new Node(s);
		if(depth%2 == 0)
		{
			n.myState.setOwner(aiLetter);
			n.myState.setOpponent(playerLetter);
		}
		else
		{
			n.myState.setOwner(playerLetter);
			n.myState.setOpponent(aiLetter);
		}
		
		n.myState.setKey(place);
		if(n.myState.myBoard.modifyBoard(place, n.myState.getOwner()))
			n.myState.determineUtility();
		else
			n.myState.setUtility(-500);
		
		numNodes++;
		return n;
	}
	
	
	
	/**************************************************
	 * Getters and Setters
	 * All of the accessor and mutator methods for 
	 * the AI class
	 **************************************************/
	/****************************************
	 * Sets bestMove to a new value
	 * @param bm	the new best move
	 ****************************************/
	public void setBestMove(int bm)
	{
		bestMove = bm;
	}
	/****************************************
	 * sets the starting board to g
	 * @param g		the starting board
	 ****************************************/
	public void setMasterBoard(Board g)
	{
		masterBoard = g;
	}
	/****************************************
	 * Switches the alue of active on and off
	 * @param v		whether or not active is 
	 * 				true or false
	 ****************************************/
	public void setActive(boolean v)
	{
		active = v;
	}
	/******************************
	 * @return the columnSize
	 ******************************/
	public int getColumnSize() 
	{
		return columnSize;
	}
	/******************************
	 * @return	returns the best
	 * 			move found thus far
	 ******************************/
	public int getBestMove()
	{
		return bestMove;
	}
	
	
	
	@Override
	/****************************************
	 * run
	 * The starting point for the thread
	 * calls the think method, which does 
	 * all of the real work
	 ****************************************/
	public void run() {
		think(masterBoard);
		
	}
}
