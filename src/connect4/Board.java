package connect4;

// I apologize for the lack of a nice comment structure in this file
public class Board {
	
	private int rowSize;
	private int columnSize;
	private int connect;
	public char set[][];
	private boolean gameOver;
	private char winner;
	private int winState;
	
	public Board(Board other)
	{
		gameOver = other.gameOver;
		rowSize = other.rowSize;
		columnSize = other.columnSize;
		connect = other.connect;
		set = new char[rowSize][columnSize];
		copySet(other.set);
		winner = other.winner;
		winState = other.winState;
	}
	private void copySet(char s[][])
	{
		for(int i = 0; i < rowSize; i++)
			for(int j = 0; j < columnSize; j++)
				set[i][j] = s[i][j];
	}
	
	public Board(int x, int y, int c)
	{
		gameOver = false;
		rowSize = x;
		columnSize = y;
		connect = c;
		set = new char[x][y];
		fillBoard();
		winner = 0;
		winState = 0;

	}
	private void fillBoard()
	{
		for(int i = 0; i < rowSize; i++)
			for(int j = 0; j < columnSize; j++)
				set[i][j] = ' ';
	}
	public String whoWon()
	{
		if(winState == 1)
			return "Player one has won the game!";
		else if(winState == 2)
			return "Player two has won the game!";
		else if(winState==3)
			return "The game resulted in a tie.";
		else
			return "";
	}
	public boolean isOver()
	{
		return gameOver;
	}
	
	public boolean modifyBoard(int col, char letter)
	{
		int row = findHighestInCol(col);

		if(row <= -1)
		{
			return false;
		}
		set[row][col] = letter;
		return true;
	}
	public boolean placePiece(int col, char letter)
	{
		int row = findHighestInCol(col);
		
		if(row == -1)
		{
			System.out.println("Sorry, that is not a valid move.  "
					+ "You can not place your piece in column "+col);
			return false;
		}
		else if(row == -2)
		{
			System.out.println("Please do not throw your pieces.  "
					+ "\nThat will not improve your odds of winning...");
			return false;
		}
		set[row][col] = letter;
		
		terminalTest(row, col, letter);
		
		return true;
	}
	public boolean terminalTest(char letter)
	{	
		for(int j = 0; j < columnSize; j++)
		{
			int row = findHighestInCol(j);
			while(row < rowSize && row > -1)
			{
				if(set[row][j] != letter)
					row++;
				else
				{
					int result = evaluateRow(row, j,letter);
					if(result == connect)
						return true;
					result = evaluateColumn(row, j,letter);
					if(result == connect)
						return true;
					result = evaluateMainDiag(row, j,letter);
					if(result == connect)
						return true;
					result = evaluateOtherDiag(row, j,letter);
					if(result == connect)
						return true;
					row++;
				}
			}
		}
		return false;
	}
	private void terminalTest(int row, int col, char letter)
	{

		boolean result = checkWinner(row,col, letter);
		if(result)
		{
			System.out.println("\n\n\n");
			System.out.println("=================");
			System.out.println("= Player " + letter +" Won! =");
			System.out.println("=================");
			System.out.println("\nFinal Game Board displayed below.");
			gameOver = true;
		}
		else if(checkTie())
		{
			System.out.println("\n\n\n");
			System.out.println("=================");
			System.out.println("= Tie Game!     =");
			System.out.println("=================");
			
			System.out.println("No more moves are available, with neither player connecting " 
					+ connect + " Pieces in a row");
			
			System.out.println("\nFinal Game Board displayed below.");
			gameOver = true;
		}
	}
	private boolean checkTie()
	{
		for(int j = 0; j < columnSize; j++)
		{
			if(set[0][j] == ' ')
				return false;
		}
		return true;
	}
	private boolean checkWinner(int row, int col, char letter)
	{
		if(checkRow(row, col, letter))
			return true;
		else if(checkColumn(row, col, letter))
			return true;
		else if (checkMainDiag(row, col, letter))
			return true;
		else if(checkOtherDiag(row, col, letter))
			return true;
		else
			return false;
	}
	private boolean checkRow(int row, int col, char letter)
	{
		int count = 0;
		for(int j = col; j < columnSize; j++)
		{
			if(set[row][j] == letter)
				count++;
			else 
				break;
		}
		count--; // prevents counting itself twice 
		for(int j = col; j > -1; j--)
		{
			if(set[row][j] == letter)
				count++;
			else 
				break;
		}
		if(count >= connect)
			return true;
		else
			return false;
	}
	private boolean checkColumn(int row, int col, char letter)
	{
		int count = 0;
		for(int i = row; i < rowSize; i++)
		{
			if(set[i][col] == letter)
				count++;
			else 
				break;
		}
		count--; // prevents counting itself twice 
		for(int i = row; i > -1; i--)
		{
			if(set[i][col] == letter)
				count++;
			else 
				break;
		}
		if(count >= connect)
			return true;
		else
			return false;
	}
	private boolean checkMainDiag(int row, int col, char letter)
	{
		int i = row, j = col, count = 0;
		while(i < rowSize && j < columnSize)
			{
				if(set[i][j] == letter)
					count++;
				else 
					break;
				i++; j++;
			}
		i = row; j = col;
		count--; // prevents counting itself twice 
		while(i > -1 && j > -1)
		{
			if(set[i][j] == letter)
				count++;
			else 
				break;
			i--; j--;
		}
		if(count>=connect)
			return true;
		else
			return false;
	}
	private boolean checkOtherDiag(int row, int col, char letter)
	{
		int i = row, j = col, count = 0;
		while(i < rowSize && j > -1)
			{
				if(set[i][j] == letter)
					count++;
				else 
					break;
				i++; j--;
			}
		i = row; j = col;
		count--; // prevents counting itself twice 
		while(i > -1 && j < columnSize)
		{
			if(set[i][j] == letter)
				count++;
			else 
				break;
			i--; j++;
		}
		if(count >= connect)
			return true;
		else
			return false;
	}
	
	
	/**************************************************
	 * find highest in column
	 * 
	 * This method is at the core of the program.  It
	 * will, given a column, find the next empty slot
	 * and return that value.  Ideal for finding where
	 * to place the next piece in a game board.
	 * 
	 * Can be modified to find the last placed peice
	 * as well.  
	 * @param col	The column in which to search
	 * @return		The next available row value
	 * 				-1 if the row is full
	 **************************************************/
	public int findHighestInCol(int col)
	{
		if(col >= columnSize || col < 0)
			return -2;
		for(int i = 0; i < rowSize; i++)
			if(set[i][col] != ' ')
				return i-1;
		if(set[rowSize-1][col]==' ')
			return rowSize-1;
		return -1;
	}
	
	
	/***************************************************
	 * Print Methods
	 * @return Formated Printing, long and short forms
	 ***************************************************/
	private String printHeading()
	{
		String out = "";
		for(int j = 1; j <= columnSize; j++)
			if(j<10)
				out+= " "+j+"  ";
			else if(j<100)
				out+= " "+j+" ";
			else if(j < 1000)
				out+= j+" ";
			else
				out+= j;
		out+= "\n";
		return out;
	}
	private String printBar()
	{
		String out="-";
		for(int j = 0; j < columnSize; j++)
			out+="----";
		return out;
	}
	public String toString()
	{
		String out = "\n";
		out+=printHeading();
		
		int i;
		out+=printBar();
		out+="\n";
		for(i = 0; i < rowSize; i++)
		{
			out+="|";
			for(int j = 0; j < columnSize; j++)
				out += " "+set[i][j]+" |";
			out+="\n";
		}
		out+=printBar();
		out+="\n";
		out+=printHeading();
		
		return out;
	}
	/**************************************************
	 * Tree Printing Method
	 * 
	 * This is the short form of the Board printing 
	 * It is used for printing it in a tree format
	 * 
	 * @param tab	Spacing to line up in tree
	 * @return		String representation of the board
	 **************************************************/
	public String treePrint(String tab)
	{
			String out = "";
			out += printHeading();
			int i;
			out+=tab;
			out+=printBar();
			out+="\n";
			for(i = 0; i < rowSize; i++)
			{
				out+=tab;
				out+="|";
				for(int j = 0; j < columnSize; j++)
					out += " "+set[i][j]+" |";
				out+="\n";
			}
			out+=tab;
			out+=printBar();
			out+= "\n";
			out+=tab;
			out+=printHeading();
			
			return out;
	}

	
	/***************************************************
	 * Utility Function
	 * 
	 * Does Magic, scores boards according to a large
	 * number of functions and criteria
	 * 
	 * for detailed answers please see
	 * the attached paper
	 * 
	 * @return the perceived utility of the board for
	 * the given letter
	 ***************************************************/
	
	/************************************************************
	 * Checks to see if the given letter has won the game
	 * 
	 * @param letter	The owning letter
	 * @param col		the column entered to gen board
	 * @return			10, if the letter won, else 0
	 ************************************************************/
	public double checkForKillerMoves(char letter, int col)
	{
		int row = findHighestInCol(col);
		
		if(row == -1)
			row=0;
		else if (row < -1)
			return 0;
		while(row < rowSize && set[row][col]!=letter)
			row++;
		
		if(checkWinner(row, col, letter))
			return 10;
		else return 0;
	}
	/************************************************************
	 * determines if this move will cause piggy-backing
	 * thus by placing this piece, allowing the opponent to win
	 * the game.
	 * 
	 * @param col		The column where the owner placed their 
	 * 					Piece, and therefore the column 
	 * 					in question
	 * @param oppLetter	The letter of the opposing player
	 * @param myLetter	The letter of the owning player
	 * @return			-5 if it is a piggy backing move, else 0
	 ************************************************************/
	public double avoidPiggyBacking(int col, char oppLetter, char myLetter)
	{
		int row = findHighestInCol(col);
		if(row == -1)
			row=0;
		else if (row < -1)
			return 0;
		while(row < rowSize && set[row][col]!=myLetter)
			row++;
		
		row--;
		if(row >=0 && set[row][col] == ' ')
		{
			set[row][col] = oppLetter;
			if(checkWinner(row, col, oppLetter))
			{
				set[row][col] = ' ';
				return -5;
			}
			set[row][col] = ' ';
		}
		return 0;
	}
	/************************************************************
	 * checks for a standing blocking pattern or r-1
	 * @param yours		The owner's letter
	 * @param opponent	The opponent's letter
	 * @param col		The column for the row in question
	 * @return			5 if it is a blocking move, else 0
	 ************************************************************/
	public double checkForBlockingMove(char yours, char opponent, int col)
	{
		int row = findHighestInCol(col);
		if(row == -1)
			row = 0;
		else if (row < -1)
			return 0;
		while(row < rowSize && set[row][col]!=yours)
				row++;
		
		set[row][col] = opponent;
		
		if(checkWinner(row,col,opponent))
		{
			set[row][col] = yours;
			return 5;
		}
		set[row][col] = yours;
		return 0;
	}

	/************************************************************
	 * Advanced Blocking Move #1, split Multi-Win Case
	 * check to see if the owner blocked the opponent from 
	 * making a double win case with a split winning move
	 * _X_XX_ is the r=4 example
	 * @param yours		The Owner's letter
	 * @param opponent	The opponent's letter
	 * @param col		The column for the Row in question
	 * @return			returns 4 if this is a block, else 0
	 ************************************************************/
	public double blockMultiWinCases(char yours, char opponent, int col)
	{
		int row = findHighestInCol(col);
		if(row == -1)
			row = 0;
		else if (row < -1)
			return 0;
		while(row < rowSize && set[row][col]!=yours)
				row++;
			
		set[row][col] = opponent;
			
		if(checkForTwoWinHussle(row, col, yours, opponent))
		{
			set[row][col] = yours;
			return 4;
		}

		set[row][col] = yours;
		return 0;
	}
	// Helper method that handles the logic for the split-multi-win case
	private boolean checkForTwoWinHussle(int row, int col, char yours, char other)
	{
		boolean validCol=true, validRow=true;
		if(row-1 == -1 || row+1 == rowSize)
			validRow =  false;
		if(col-1 == -1 || col+1 == columnSize )
			validCol = false;
		
		if(((validCol == true) && set[row][col-1] == other && set[row][col+1] == other) ||
				((validCol == true && validRow == true) && set[row-1][col-1] == other && set[row+1][col+1] == other) ||
				((validCol == true && validRow == true) && set[row-1][col+1] == other && set[row+1][col-1] == other)
				)
			return true;
		else
			return false;
	}
	
	/************************************************************
	 * Advanced Blocking Move #2, block/stump Case
	 * This checks for any time the opponent has a row that is 
	 * r-2 away from being filled and has both ends open
	 * 
	 * prevents __xx__ from turning into a double winning 
	 * __xxx_
	 * @param yours		The Owner's letter
	 * @param opponent	The opponent's letter
	 * @param col		The column for the Row in question
	 * @return			returns 3 if this is a block, else 0
	 ************************************************************/
	public double blockStumpTraps(char yours, char opponent, int col)
	{
		int row = findHighestInCol(col);
		if(row == -1)
			row = 0;
		else if (row < -1)
			return 0;
		while(row < rowSize && set[row][col]!=yours)
				row++;
			
		if(checkLeftStump(row, col, yours, opponent) || checkRightStump(row, col, yours, opponent))
			return 3;
		return 0;
	}
	// Checks if it blocks a 'stump' in the left hand direction
	private boolean checkLeftStump(int row, int col, char yours, char other)
	{
		if(col - (connect-1) > -1)
		{
			int col2 = col - (connect - 1);
			int row2 = findHighestInCol(col2);
			if(row2 == -1)
				return false;
			set[row][col] = other;
			set[row2][col2] = other;
			if(checkWinner(row,col,other))
			{
				set[row][col] = yours;
				set[row2][col2] = ' ';
				return true;
			}
			set[row][col] = yours;
			set[row2][col2] = ' ';
		}	
		return false;
	}
	// Checks if it blocks a 'stump' in the right hand direction
	private boolean checkRightStump(int row, int col, char yours, char other)
	{
		if(col + (connect-1) < columnSize)
		{
			int col2 = col+connect-1;
			int row2 = findHighestInCol(col2);
			if(row2 == -1)
				return false;
			set[row][col] = other;
			set[row2][col2] = other;
			if(checkWinner(row,col,other))
			{
				set[row][col] = yours;
				set[row2][col2] = ' ';
				return true;
			}
			set[row][col] = yours;
			set[row2][col2] = ' ';
		}
		return false;
	}
	
	
	/**************************************************
	 * Standard Scoring - Count Patterns
	 * This is the core dispatcher for the standard
	 * rating scale.  It is here all boards that are 
	 * not considered be be critical (blocking/winning)
	 * are scored and given utility.  
	 * 
	 * @param letter	The owners Letter
	 * @return			The Utility of the board, 
	 * 					Should be between -1 and 1
	 * 					However, could be beyond
	 ***************************************************/
	public double countPatterns(char letter)
	{
		double util = 0;
		
		for(int j = 0; j < columnSize; j++)	// For each column on the board
		{
			int row = findHighestInCol(j); 	// Find its highest Row
			while(row < rowSize && row > -1)	// traverse downward, provided its not full
			{
				if(set[row][j] != letter)		// find your letter
					row++;
				else
				{
					int result = evaluateRow(row, j,letter);	// check how many of your letter are on the given row
					util = score(result, util);					// and score it
					result = evaluateColumn(row, j,letter);		// find out how many of your letters are on a given column
					util = score(result, util);					// and score it
					result = evaluateMainDiag(row, j,letter);	// find out how many of your letters are on a given diagonal
					util = score(result, util);					// and score it!
					result = evaluateOtherDiag(row, j,letter);	// find out how many of your letters are on a given diagonal
					util = score(result, util);					// and score it....
					row++;										// increment the row. (had an infinite loop because I forget this)
				}
			}
		}
		return util;	// Return the determined utilty of the given board for the letter inputed
	}
	/**************************************************
	 * Score
	 * 
	 * Gives a value to the number of sets collected
	 * 
	 * @param result	Number of sets that are 
	 * 					connected and of size >= r-1
	 * @param util		The previous value of utility
	 * @return			the updated value of utility
	 **************************************************/
	private double score(int result, double util)
	{
		if(result <= connect/8)
			util+=.01;
		else if(result <= connect/4)
			util+=0.25;
		else if(result <= connect/2)
			util+=0.5;
		else if(result <= (connect*3)/4)
			util+=.75;
		else if(result < connect)
			util+=.8;
		else
			util+=1;
		return util;
	}
	
	/************************************************************
	 * Evaluation Methods
	 * These are the evaluation methods that count
	 * how many r-1 connected sets are no a row, 
	 * column, or diagonal.  Their code is nearly
	 * identical to each other and the check methods above.
	 * 
	 * @param row		Given Row
	 * @param col		Given Column
	 * @param letter	The letter to search for
	 * @return			how many r-1 connected sets exist
	 ************************************************************/
	
	private int evaluateRow(int row, int col, char letter)
	{
		int run = 0, count = 0;
		for(int j = 0; j < columnSize; j++)
		{
			if(set[row][j] == letter)
					run++;
			else if(set[row][j] == ' ' && run >=connect-1)
			{
				run = 0;
				count++;
			}
			else 
				run = 0;
		}
		return count;
	}
	private int evaluateColumn(int row, int col, char letter)
	{
	
		int run = 0, count = 0;
		for(int i = 0; i < rowSize; i++)
		{
			if(set[i][col] == letter)
				run++;
			else if(set[i][col] == ' ' && run >= connect-1)
			{
				run = 0;
				count++;
			}
			else
				run = 0;
		}
		return count;
	}
	private int evaluateMainDiag(int row, int col, char letter)
	{
		int i = row, j = col, run = 0, count = 0;
		while(i < rowSize && j < columnSize)
		{
			if(set[i][j] == letter)
				run++;
			else if(set[i][j] == ' ' && run >=connect-1)
			{
				run = 0;
				count++;
			}
			else
				run = 0;
			i++; j++;
		}
		return count;
	}
	private int evaluateOtherDiag(int row, int col, char letter)
	{
		int i = row, j = col, run = 0, count = 0;
		while(i < rowSize && j > -1)
		{
			if(set[i][col] == letter)
				run++;
			else if(set[i][col] == ' ' && run >=connect-1)
			{
				run = 0;
				count++;
			}
			else
				run = 0;
				i++; j--;
		}
		return count;
	}
	
	
	
	/**************************************************
	 * Getter and setters for the Board Class
	 **************************************************/
	
	public int getColumnSize() {
		return columnSize;
	}
	
	public int getRowSize() {
		return rowSize;
	}
	public void setRowSize(int rowSize) {
		this.rowSize = rowSize;
	}
	public int getConnect() {
		return connect;
	}
	public void setConnect(int connect) {
		this.connect = connect;
	}
	public char[][] getSet() {
		return set;
	}
	public void setSet(char[][] set) {
		this.set = set;
	}
	public void setColumnSize(int columnSize) {
		this.columnSize = columnSize;
	}

}
