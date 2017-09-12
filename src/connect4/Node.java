package connect4;

import java.util.ArrayList;
public class Node 
{
	/************************************************************
	 * Instance Variables
	 * 
	 * These are all the variables that belong to the Node Class.
	 ************************************************************/
	State myState;						// The current state the node represents
	private double aggregateUtility;	// A deprecated variable listing the sum of each child's utility
	ArrayList<Node> children;			// A list of all child nodes
	
	
	
	/************************************************************
	 * Default Constructor
	 * 
	 * This constructor does nothing interesting
	 * It will create a state that is null, an empty list of
	 * children and defaults the aggregate to 0
	 ************************************************************/
	public Node()
	{
		myState = null;
		children = new ArrayList<Node>();
		aggregateUtility = 0;
	}
	
	
	/************************************************************
	 * Copy Constructor
	 * 
	 * This constructor takes in an old node and will
	 * make an exact copy that is completely independent
	 * of its parent node.  This node can then be modified
	 * and turned into a new node.
	 * 
	 * This constructor will perform a deep copy
	 * 
	 * @param old	The previous node that is to be copied
	 ************************************************************/
	public Node(Node old)
	{
		myState = new State(old.myState);	// deep copies the state
		copyChildren(old);  				// deep copies any and all children
		aggregateUtility = old.aggregateUtility;
	}
	// This method deep copies all the children of a given node
	// It is intended to be used as a helper method for the copy constructor
	private void copyChildren(Node old)
	{
		for(int i = 0; i < children.size(); i++)
			children.add(new Node(old.children.get(i)));
	}
	
	
	/************************************************************
	 * parametized Constructor
	 * 
	 * Creates a new Node in memory from a given state
	 * This is how the first node would be created (root)
	 * 
	 * This method will deep copy the given state, and in turn
	 * generate an empty list of children
	 * 
	 * @param s		The state the node will hold/represent
	 ************************************************************/
	public Node(State s)
	{
		myState = new State(s);
		children = new ArrayList<Node>();
		aggregateUtility = myState.getUtility();
	}
	
	
	
	/**************************************************
	 * updateUtility
	 * 
	 * This method simply took in a value (utility)
	 * and then would add it to the aggregate utility
	 * Since this method was abandoned, the method 
	 * has been deprecated
	 * 
	 * @deprecated
	 * @param update	Value to change the aggregate
	 * 					by.
	 **************************************************/
	private void updateUtility(double update)
	{
		aggregateUtility += update;
	}
	
	
	
	/**************************************************
	 * addChild
	 * 
	 * The proper method for adding a child node to the 
	 * list of children.  In revisions to ensure proper
	 * Object Oriented style, this should be used instead
	 * of accessing the children Arraylist directly
	 * 
	 * @param c		The child to be added to the list
	 **************************************************/
	public void addChild(Node c)
	{
		children.add(c);
	}	
	
	
	/**************************************************
	 * getChild
	 * 
	 * This method is the proper method for accessing
	 * child nodes of the current node.  In revisions
	 * to ensure proper Object Oriented style, this
	 * should be used instead of directly accessing
	 * the children Arraylist.  
	 * 
	 * @param i		An index to a possible child of 
	 * 				the current node
	 * @return		Returns the child node if it exists
	 * 				else null is returned
	 **************************************************/
	public Node getChild(int i)
	{
		if(i < children.size())
			return children.get(i);
		System.err.println("No valid Children at index " + i);
		return null;
	}
	
	
	
	/**************************************************
	 * toString
	 * 
	 * Tells the node how to print itself
	 * This simply tells it to print out the current
	 * state (invoking it's toString method in turn
	 **************************************************/
	public String toString()
	{
		String out ="";
		out += myState;
		return out;
	}
}
