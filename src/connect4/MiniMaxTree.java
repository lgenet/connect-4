package connect4;

import java.util.ArrayList;

public class MiniMaxTree 
{	
	/**************************************************
	 * Instance Variables
	 **************************************************/
	Node root;							// Root of the tree
	int searches, bestMove, MAX_DEPTH;	// Max Depth that the tree should search
	protected static double best;		// Best Utility found to date
	protected boolean kickOut;			// determines if the tree is done @deprecated 
	protected boolean endSearch;		// determines whether the searching should stop
	
	/**************************************************
	 * Constructor
	 * 
	 * builds a root, from which a tree may be created
	 **************************************************/
	public MiniMaxTree()
	{
		root = new Node();
		searches = 0;
		endSearch = true;
	}
	/**************************************************
	 * Constructor - Parameterized 
	 * 
	 * Creates the root from a tree from a given state
	 * and runs to a given depth
	 * @param s		State for the root node to hold
	 * @param d		The max depth
	 **************************************************/
	public MiniMaxTree(State s, int d)
	{
		root = new Node(s);
		searches = 0;
		best = Integer.MIN_VALUE;
		endSearch = false;

		bestMove = (int)Math.random()*root.myState.myBoard.getColumnSize();
		setMaxDepth(d);
	}

	
	/**************************************************
	 * check for Killer moves
	 * 
	 * This is the method that will do all of the 
	 * forward pruning for the Tree/Ai.  It will select
	 * moves on priority of utility > utility more likely
	 * to be selected.  
	 * 
	 * @return	The best move found or 0 if not move 
	 * 			was found to be critical 
	 **************************************************/
	private int killers()
	{
		int i = 0;
		try{
			// Makes sure that none of the nodes are null
			for( i = 0; i < root.children.size();)
				if(root.getChild(i) == null)
					root.children.remove(i);
				else
					i++;
			
			// checks for a winning move
			for( i = 0; i < root.children.size(); i++)
				if(root.getChild(i).myState.getUtility() >= 10)
					return root.getChild(i).myState.getKey();
			
			// Checks for a Blocking Move
			for( i = 0; i < root.children.size(); i++)
				if(root.getChild(i).myState.getUtility() >= 5)
					return root.getChild(i).myState.getKey();
			
			// Checks for X_XX case to block
			for(i = 0; i < root.children.size(); i++)
				if(root.getChild(i).myState.getUtility() >=4)
					return root.getChild(i).myState.getKey();

			// Removes moves that will negatively affect the player
			for(i = 0; i < root.children.size(); i++)
				if(root.getChild(i).myState.getUtility() <= -5)
					root.children.remove(i);

			// prevents double wins where __XX__ are possible
			for(i = 0; i < root.children.size(); i++)
				if(root.getChild(i).myState.getUtility() >= 3)
					return root.getChild(i).myState.getKey();
			
			// This section will determine that if all moves are equal, to go center
			double current = -1;
			if(root.children.size() > 0)
				current = root.getChild(0).myState.getUtility();
			for(i = 0; i < root.children.size(); i++)
				if(root.getChild(i).myState.getUtility() != current)
					return -1;
			
			// Fail safe to prevent a bad central move
			if(root.myState.myBoard.findHighestInCol(root.myState.myBoard.getColumnSize()/2) >= 0 )
				return root.myState.myBoard.getColumnSize()/2;
			else
				return -1;
		}
		catch(Exception e)
		{
			System.err.println ("An error occured while checking for killer moves.  A node might have been null...");
			for(i = 0; i < root.children.size(); i++)
				System.out.print("Child " + i + "\n" + root.getChild(i));
			System.err.println(e);
			e.printStackTrace();
		}
		return -1;
	}
	
	
	
	/**************************************************
	 * miniMax
	 * 
	 * Will first try to find a move from the set of 
	 * killer moves.  If something other than -1 was 
	 * returned, then that is returned as the best 
	 * move possible.  
	 * 
	 * if -1 was returned, then the miniMax tree will 
	 * be searched for the best move possible.
	 * 
	 * @param letter	unused parameter
	 * @return			The best move according to the
	 * 					miniMax tree. 
	 **************************************************/
	public int miniMax(char letter)
	{
		bestMove = killers();
		if(bestMove != -1)
			return bestMove;

		miniMax(root, MAX_DEPTH, true);
		if(bestMove == -1 || best == Integer.MIN_VALUE)
			bestMove = (int)Math.random()*root.myState.myBoard.getColumnSize();

		return bestMove;
	}


	
	/************************************************************
	 * miniMax - Worker
	 * 
	 * This is the heart of the Mini Max Search
	 * 
	 * It will check for base cases, and makes sure
	 * that these base case nodes have valid utilities
	 * 
	 * It will recurse down to the bottom of the tree and 
	 * then allow the results to be bubbled back up the tree 
	 * in the traditional MiniMax fashion.  
	 * 
	 * Picking the max f all of the AI's moves, to be returned 
	 * to the parent, which will return the minimum of itself 
	 * and all of its Siblings.  This is due ot the fact that 
	 * the opponent (whose moves are represented on this tier) 
	 * would pick the worst option for the AI
	 * 
	 * @param n				The current node who is being searched
	 * @param depth			The depth at which the node is found
	 * @param maxPlayer		whether or not this is a max-ing level
	 * @return				bubbles the best utility back to the 
	 * 						top, thus it is returned.  This
	 * 						return is utilized by the recursive
	 * 						nature of this method, rather than
	 * 						the calling method that started the
	 * 						the recursive trail.
	 ************************************************************/
	private double miniMax(Node n, int depth, boolean maxPlayer)
	{
		boolean atBase = false;
		double bestValue = -1;
		
		/**************
		 * Base cases *
		 **************/
		if(maxPlayer && depth > 0)
			bestValue = Integer.MIN_VALUE;
		else if (depth > 0)
			bestValue = Integer.MAX_VALUE;
		
		if(n == null) // if the current node is null
			return -10; // return a sentinel to indicate this
		
		if(depth==0 || n.children.size() == 0) // base case
		{
			kickOut = true;
			atBase = true; // Lets it be known that the recurse sould not continue down
			if(n.myState != null)
				n.myState.determineUtility();
			else
				return -7;	// another null sentinel 
		}
		
		/*******************
		 * Recursing Block *
		 *******************/
		if(atBase == false)
		{
			for(int i = 0; i < n.children.size(); i++)
			{
				while(i < n.children.size() && n.getChild(i) == null) // finds null children
					n.children.remove(i); // removes those null children

				// recusrses down, and alternates the min and max
				if(maxPlayer)
					n.myState.setUtility(miniMax(n.children.get(i), depth -1, false));
				else
					n.myState.setUtility(miniMax(n.children.get(i),depth -1, true));
			}
		}
		
		/*********************
		 * Min and Max Logic *
		 *********************/
		if(maxPlayer)
		{
			for(int i = 0; i < n.children.size(); i++)
			{		
				if(n.getChild(i)==null)
					continue; // skipp null children
				double val = n.children.get(i).myState.getUtility(); // temporarily store the candidate's utility
				int pos = n.getChild(i).myState.getKey();		// temporarily store the candidate's move
				bestValue = max(pos,bestValue, val);			// does the max of best and candidate
				best = bestValue;								// records  this result globally for analysis later
			}
			return bestValue;	// returns best value once all children are checked
								// This value will become the parent's utility
		}
		else
		{
			for(int i = 0; i < n.children.size(); i++)
			{
				
				double val = n.children.get(i).myState.getUtility();// temporarily store the candidate's utility
				int pos = n.getChild(i).myState.getKey();	// temporarily store the candidate's move
				bestValue = min(pos,bestValue, val);		// does the min of best and candidate
				best = bestValue;							// records  this result globally for analysis later
			}
			return bestValue;	// returns best value once all children are checked
								// This value will become the parent's utility
		}
	}
	
	
	
	/**************************************************
	 * min
	 * 
	 * Will take in the current highest value, 
	 * as well as the current nodes utility and move
	 * then determine if the new value is smaller
	 * than the old value
	 * 
	 * @param pos		The move that needs to be taken 
	 * 					if this new utility is better
	 * 					than the current highest
	 * @param current	The current lowest Utility 
	 * @param newVal	The candiate's utility
	 * @return			the lowest utility
	 ***************************************************/
	private double min(int pos, double current, double newVal)
	{
		if(newVal < current)
		{
			bestMove = pos;
			return newVal;
		}
		return current;
	}
	/**************************************************
	 * max
	 * 
	 * Will take in the current highest value, 
	 * as well as the current nodes utility and move
	 * then determine if the new value is larger
	 * than the old value
	 * 
	 * @param pos		The move that needs to be taken 
	 * 					if this new utility is better
	 * 					than the current highest
	 * @param current	The current Highest Utility 
	 * @param newVal	The candiate's utility
	 * @return			the lowest utility
	 ***************************************************/
	private double max(int pos, double current, double newVal)
	{
		if(newVal > current)
		{
			bestMove = pos;
			return newVal;
		}
		return current;
	}
	
	
	
	/********************
	 * Printing Methods *
	 ********************/
	public String toString()
	{
		String out = "";

		out+=gatherOut(root, out,"");
		
		return out;
	}
	private String gatherOut(Node current, String out,String level)
	{
		if(current.myState == null)
		{
			return out;
		}
		out+=level+current.myState.myBoard.treePrint(level) +"\n";
		level+="\t";
		for(int i = 0; i < current.children.size(); i++)
			out = gatherOut(current.getChild(i),out,level);
		return out;
	}

	
	
	/**************************************************
	 * Setters and Getters
	 * 
	 * These are the necessary setters and getters for
	 * the MiniMax Tree. One sets the max depth
	 * the other returns the best move.
	 * 
	 * @param d		setMaxDepth Takes in the max Depth
	 * @return		getBestMove returns the best Move 
	 * 				found
	 ***************************************************/
	public void setMaxDepth(int d)
	{
		MAX_DEPTH = d;
	}
	public int getBestMove()
	{
		return bestMove;
	}
}
