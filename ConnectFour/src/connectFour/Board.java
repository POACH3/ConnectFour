package connectFour;

import java.util.Stack;

//import edu.princeton.cs.algs4.Stack;

/**
 * The Board class represents a Connect Four playing game board
 */
public class Board {
	public static final int ROWS = 6;
	public static final int COLS = 7;

	private static int[][] connectFourBoard;
	private static Stack<Integer> moveHistory = new Stack<>();

	/**
	 * Creates a 6x7 board with empty cells
	 *
	 */
	public Board(int rows, int cols) {
		connectFourBoard = new int[rows][cols];

		// Initialize the grid with empty spaces
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				connectFourBoard[i][j] = 0;
			}
		}
	}

	/**
	 * Get a connectFourBoard
	 * 
	 * @return - int[][]
	 */
	public int[][] getConnectFourBoard() {
		return connectFourBoard;
	}

	/**
	 * Copies the state of another board to this board.
	 *
	 * @param otherBoard The board from which to copy the state.
	 */
	public void copyFrom(Board otherBoard) {
		int[][] otherConnectFourBoard = otherBoard.getConnectFourBoard();
		for (int i = 0; i < ROWS; i++) {
			System.arraycopy(otherConnectFourBoard[i], 0, connectFourBoard[i], 0, COLS);
		}
	}

	/**
	 * Converts an int[][] to a char[][].
	 * 
	 * @param board the 2D array to be converted
	 */
	public static char[][] convertBoard(int[][] board) {
		char[][] charBoard = new char[6][7];

		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				if (board[row][col] == 1) {
					charBoard[row][col] = 'X';
				} else if (board[row][col] == 2) {
					charBoard[row][col] = 'O';
				} else {
					charBoard[row][col] = ' ';
				}
			}
		}

		return charBoard;
	} // end of convertBoard()

	/**
	 * Checks if board is full (with no empty cells)
	 * 
	 * @return - true if there is no empty cell available
	 */
	public boolean isFull() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (connectFourBoard[i][j] == 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Determines if the move is valid by checking if there is an available row in
	 * the column to move into.
	 * 
	 * @param column The column in which the player wants to make the move.
	 * @return true if the move is valid, false if the column is full
	 */
	public boolean isValidMove(int column) {
		for (int row = 0; row < ROWS; row++) {
			if (connectFourBoard[row][column] == 0) {
				return true;
			}
		}
		return false;
	}

	 /**
	 * Makes move and checks if valid before placing symbol
	 *
	 * @param column
	 * @param symbol
	 * @return - true if move was successful
	 */
	public void makeMove(int column, int player) {
		if (isValidMove(column)) {
			for (int row = ROWS - 1; row >= 0; row--) {
				if (connectFourBoard[row][column] == 0) {
					connectFourBoard[row][column] = player;
					// System.out.println("Successful valid move made");
					moveHistory.push(column);
					return;
				}
			}
			System.out.println("Column is full. Was not a valid move.");
		} else {
			System.out.println("Was not a valid move.");
		}
	}

	/**
	 * Undo the last two moves.
	 *
	 * @param column The column in which to undo the last move.
	 */
	public static void undoMove() {
		
		// undo computer's move
		int col = moveHistory.pop();
		for (int row = 0; row < ROWS; row++) {
			if (connectFourBoard[row][col] == 0) {
				continue;
			}
			if (connectFourBoard[row][col] != 0) {
				connectFourBoard[row][col] = 0;
				break;
			}
		}
		
		// undo player's move
		col = moveHistory.pop();
		for (int row = 0; row < ROWS; row++) {
			if (connectFourBoard[row][col] == 0) {
				continue;
			}
			if (connectFourBoard[row][col] != 0) {
				connectFourBoard[row][col] = 0;
				break;
			}
		}
		return;
	}

	/**
	 * Checks for a win: 4 consecutive in a row, or 4 consecutive in a column, or 4
	 * consecutive diagonally
	 * 
	 * @return true if a win
	 */
	public boolean checkWin() {
		return checkRows() || checkColumns() || checkDiagonals();
	}

	/**
	 * Checks for four consecutive pieces are in a row horizontally
	 * 
	 * @return true if four in a row
	 */
	private boolean checkRows() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j <= COLS - 4; j++) {
				if (connectFourBoard[i][j] != 0 && connectFourBoard[i][j] == connectFourBoard[i][j + 1]
						&& connectFourBoard[i][j] == connectFourBoard[i][j + 2]
						&& connectFourBoard[i][j] == connectFourBoard[i][j + 3]) {
					return true; // Win in a row
				}
			}
		}
		return false;
	}

	/**
	 * Checks if four consecutive pieces are in a vertical line
	 * 
	 * @return true if four in a column
	 */
	private boolean checkColumns() {
		for (int j = 0; j < COLS; j++) {
			for (int i = 0; i <= ROWS - 4; i++) {
				if (connectFourBoard[i][j] != 0 && connectFourBoard[i][j] == connectFourBoard[i + 1][j]
						&& connectFourBoard[i][j] == connectFourBoard[i + 2][j]
						&& connectFourBoard[i][j] == connectFourBoard[i + 3][j]) {
					return true; // Win in a column
				}
			}
		}
		return false;
	}

	/**
	 * Checks if four consecutive pieces are in a line diagonally
	 * 
	 * @return true if four consecutive diagonally
	 */
	private boolean checkDiagonals() {
		for (int i = 0; i <= ROWS - 4; i++) {
			for (int j = 0; j <= COLS - 4; j++) {
				if (connectFourBoard[i][j] != 0 && (connectFourBoard[i][j] == connectFourBoard[i + 1][j + 1]
						&& connectFourBoard[i][j] == connectFourBoard[i + 2][j + 2]
						&& connectFourBoard[i][j] == connectFourBoard[i + 3][j + 3])) {
					return true; // Win in a diagonal (top-left to bottom-right)
				}

				if (connectFourBoard[i + 3][j] != 0 && (connectFourBoard[i + 3][j] == connectFourBoard[i + 2][j + 1]
						&& connectFourBoard[i + 3][j] == connectFourBoard[i + 1][j + 2]
						&& connectFourBoard[i + 3][j] == connectFourBoard[i][j + 3])) {
					return true; // Win in a diagonal (bottom-left to top-right)
				}
			}
		}
		return false;
	}

	/**
	 * Clears the game board, reseting it to an empty state.
	 */
	public void clearBoard() {
		connectFourBoard = new int[ROWS][COLS];
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				connectFourBoard[i][j] = 0;
			}
		}
	}
}
