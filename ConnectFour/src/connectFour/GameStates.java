package connectFour;

import java.util.ArrayList;
import java.util.Arrays;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

public class GameStates {

	static int thisPlayer = 2; // the computer is player 2

	private static int[][] emptyBoard = { { 0, 0, 0, 0, 0, 0, 0 }, 
										  { 0, 0, 0, 0, 0, 0, 0 }, 
										  { 0, 0, 0, 0, 0, 0, 0 },
										  { 0, 0, 0, 0, 0, 0, 0 }, 
										  { 0, 0, 0, 0, 0, 0, 0 }, 
										  { 0, 0, 0, 0, 0, 0, 0 }, };

	public static int getMove(int[][] gameBoardState, int difficulty) {

		ArrayList<int[][]> gameStates = new ArrayList<>();
		ArrayList<Integer> winStatesIndex = new ArrayList<>(); // the index of gameStates that is a win

		getAllMoves(gameBoardState, gameStates);

		Digraph graph = createDigraph(gameStates);
		BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(graph, 0);

		int rand = Math.abs((int) System.nanoTime() % 7);

		switch (difficulty) {
		case 1:
			// ==== DIFFICULTY: 1 ====
			// win on next turn if possible
			// else block on next turn if necessary
			// else choose a random move

			if (winIt(gameStates.get(0), 2) != -1) {
				if (isValidMove(gameBoardState, winIt(gameStates.get(0), 2))) {
					return winIt(gameStates.get(0), 2);
				}
			} else if (blockIt(gameStates.get(0), 2) != -1) {
				if (isValidMove(gameBoardState, blockIt(gameStates.get(0), 2))) {
					return blockIt(gameStates.get(0), 2);
				}
			} else {
				while (!isValidMove(gameBoardState, rand)) {
					rand = Math.abs((int) System.nanoTime() % 7);
				}
				return rand;
			}

		case 2:
			// ==== DIFFICULTY: 2 ====
			// win on next turn if possible
			// block on next turn if necessary
			// find all winning states in next 5 turns
			// eliminate all states with a P1 win on path
			// select the one with the shortest path (choose at random if there are multiple)
			// get move of first state on path or if no win state path exists, do a random move

			int bestState = 0;
			int moveToBestState = 0;

			if (winIt(gameStates.get(0), 2) != -1) {
				return winIt(gameStates.get(0), 2);
			} else if (blockIt(gameStates.get(0), 2) != -1) {
				return blockIt(gameStates.get(0), 2);
			} else {
				for (int j = 0; j < gameStates.size(); j++) {
					if (isWin(gameStates.get(j), 2)) {
						winStatesIndex.add(j);
					}
				}

				for (int k = 0; k < winStatesIndex.size(); k++) {
					for (int v : bfs.pathTo(winStatesIndex.get(k))) {
						if (isWin(gameStates.get(v), 2)) {
							// remove all options that have a path where other player can win
							winStatesIndex.remove(k); 
						}
					}
				}

				int k = 0;
				while (k < winStatesIndex.size()) {
					if (bfs.distTo(winStatesIndex.get(k)) > bfs.distTo(winStatesIndex.get(0))) {
						// remove all options that aren't the shortest path
						winStatesIndex.remove(k);
						continue;
					}
					k++;
				}

				if (winStatesIndex.size() == 0) {
					while (!isValidMove(gameBoardState, rand)) {
						rand = Math.abs((int) System.nanoTime() % 7);
					}
					return rand;
				} else {
					// choose a random best win state if there are multiple
					bestState = winStatesIndex.get(Math.abs((int) (System.nanoTime() % winStatesIndex.size())));
				}

				int count = 0;
				for (int i : bfs.pathTo(bestState)) {

					if (count == 1) {
						moveToBestState = i;
						break; // only look at second state
					}
					count++;
				}

				System.out.println("\nShortest path ideal win states:");
				for (int i = 0; i < winStatesIndex.size(); i++) {
					System.out.println(bfs.pathTo(winStatesIndex.get(i)));
				}

				System.out.println("\nSelected: " + bfs.pathTo(bestState) + "\n");
				for (int i : bfs.pathTo(bestState)) {
					printMatrix(gameStates.get(i));
				}

				return moveColumn(gameStates.get(0), gameStates.get(moveToBestState));

			}

			// ==== DIFFICULTY: 3 ====
			// play in center columns in beginning, next to opponent
			// look further ahead than one turn for opponent wins, avoid opponent
			// double wins
			// find all double win states in next 5 turns
			// eliminate all states with a P1 win on path
		case 3:

			// Choosing a random move
			int randDifficult = Math.abs((int) System.nanoTime() % 7);

			// Check for immediate wins or blocks
			int immediateWin = winIt(gameStates.get(0), 2);
			int immediateBlock = blockIt(gameStates.get(0), 2);

			if (immediateWin != -1 && isValidMove(gameBoardState, immediateWin)) {
				return immediateWin;
			} else if (immediateBlock != -1 && isValidMove(gameBoardState, immediateBlock)) {
				return immediateBlock;
			} else {
				// Look further ahead for opponent wins and avoid double wins
				ArrayList<Integer> opponentWins = new ArrayList<>();
				ArrayList<Integer> doubleWins = new ArrayList<>();

				for (int j = 0; j < gameStates.size(); j++) {
					if (isWin(gameStates.get(j), thatPlayer(thisPlayer))) {
						opponentWins.add(j);
					}
				}

				for (int k = 0; k < opponentWins.size(); k++) {
					for (int v : bfs.pathTo(opponentWins.get(k))) {
						if (isWin(gameStates.get(v), thatPlayer(thisPlayer))) {
							doubleWins.add(opponentWins.get(k));
							break; // break if double win found
						}
					}
				}

				// Eliminate states with P1 win on path
				doubleWins.removeAll(opponentWins);

				// Choose a random move from remaining states
				if (!doubleWins.isEmpty()) {
					int selectedState = doubleWins.get(Math.abs((int) (System.nanoTime() % doubleWins.size())));
					int moveToSelectedState = bfs.pathTo(selectedState).iterator().next();
					return moveColumn(gameStates.get(0), gameStates.get(moveToSelectedState));
				} else {
					// If no double wins, choose a random move
					while (!isValidMove(gameBoardState, randDifficult)) {
						randDifficult = Math.abs((int) System.nanoTime() % 7);
					}
					return randDifficult;
				}
			}

		default:
			return Math.abs((int) (System.nanoTime() % winStatesIndex.size())); // default return random move
		}
	}

	/**
	 * Creates a digraph when given an ArrayList of game states.
	 * 
	 * @param gameStates ArrayList of all possible game states
	 * @return
	 */
	public static Digraph createDigraph(ArrayList<int[][]> gameStates) {
		Digraph graph = new Digraph(gameStates.size());

		int i = 1;
		for (int k = 0; k < 7; k++) {
			graph.addEdge(0, i);
			i++;
		}

		for (int j = 1; j < 8; j++) {
			for (int k = 0; k < 7; k++) {
				graph.addEdge(j, i);
				i++;
			}
		}

		for (int j = 8; j < 57; j++) {
			for (int k = 0; k < 7; k++) {
				graph.addEdge(j, i);
				i++;
			}
		}

		for (int j = 57; j < 400; j++) {
			for (int k = 0; k < 7; k++) {
				graph.addEdge(j, i);
				i++;
			}
		}

		for (int j = 400; j < 2801; j++) {
			for (int k = 0; k < 7; k++) {
				graph.addEdge(j, i);
				i++;
			}
		}

		return graph;
	}

	/**
	 * Gets all possible moves for next turn.
	 * 
	 * @param currentState a matrix that represents the current board layout
	 * @param thisPlayer   the player whose turn it is
	 * @return
	 */
	public static ArrayList<int[][]> getNextMoves(int[][] currentState, int thisPlayer) {

		ArrayList<int[][]> possibleMoves = new ArrayList<>();
		int[][] nextMove = new int[6][7];

		for (int c = 0; c < 7; c++) {
			int r = 0;
			nextMove = copyMatrix(currentState);

			if (nextMove[r][c] != 0) {
				possibleMoves.add(copyMatrix(emptyBoard)); // matrix of zeros to represent
															// illegal move
				continue;
			} else {
				while (r < 5 && nextMove[r + 1][c] == 0)
					r++;
			}
			nextMove[r][c] = thisPlayer;
			possibleMoves.add(copyMatrix(nextMove));
		}

		return possibleMoves;
	} // end of getNextMoves()

	/**
	 * Creates an ArrayList of possible game states. This looks five moves ahead.
	 * 
	 * @param currentState matrix representing the current board layout
	 * @param gameStates   ArrayList of possible game states
	 * @return
	 */
	public static ArrayList<int[][]> getAllMoves(int[][] currentState, ArrayList<int[][]> gameStates) {

		gameStates.add(currentState);

		for (int i = 0; i < getNextMoves(currentState, thisPlayer).size(); i++) {
			gameStates.add(getNextMoves(currentState, thisPlayer).get(i));
		}

		for (int i = 1; i < 8; i++) {
			for (int j = 0; j < 7; j++) {
				gameStates.add(getNextMoves(gameStates.get(i), thatPlayer(thisPlayer)).get(j));
			}
		}

		for (int i = 8; i < 57; i++) {
			for (int j = 0; j < 7; j++) {
				gameStates.add(getNextMoves(gameStates.get(i), thisPlayer).get(j));
			}
		}

		for (int i = 57; i < 400; i++) {
			for (int j = 0; j < 7; j++) {
				gameStates.add(getNextMoves(gameStates.get(i), thatPlayer(thisPlayer)).get(j));
			}
		}

		// 5 moves ahead
		for (int i = 400; i < 2801; i++) { // 19607 total
			for (int j = 0; j < 7; j++) {
				gameStates.add(getNextMoves(gameStates.get(i), thisPlayer).get(j));
			}
		}

		return gameStates;
	}

	/**
	 * Returns the column index if it is possible to win this turn. If not possible,
	 * returns a -1.
	 * 
	 * @param currentState matrix representing current board layout
	 * @param thisPlayer   player whose turn it is
	 * @return
	 */
	public static int winIt(int[][] currentState, int thisPlayer) {
		int move = -1; // index of the column to place token in

		for (int[][] a : getNextMoves(currentState, thisPlayer)) {
			if (isWin(a, thisPlayer)) {
				move = moveColumn(currentState, a);
			}
		}

		return move;
	}

	/**
	 * Returns the column index of the play where the opponent would win. If
	 * opponent cannot win on their next turn, returns a -1.
	 * 
	 * @param currentState matrix representing current board layout
	 * @param thisPlayer   the player whose turn it currently is
	 * @return
	 */
	public static int blockIt(int[][] currentState, int thisPlayer) {
		int move = -1; // index of the column to place token in

		for (int[][] a : getNextMoves(currentState, thisPlayer)) {

			for (int[][] b : getNextMoves(a, thatPlayer(thisPlayer))) {
				if (isWin(b, thatPlayer(thisPlayer))) {
					move = moveColumn(a, b);
				}
			}
		}

		return move;
	}

	/**
	 * Compares two adjacent (one move apart) boards and returns the index of the
	 * column that is the difference between the two. Throws
	 * IllegalArgumentExceptions if boards are identical or not adjacent.
	 * 
	 * @param board1 a matrix representing a board state
	 * @param board2 a matrix representing a possible next board state
	 * @return
	 */
	public static int moveColumn(int[][] board1, int[][] board2) {
		int diff;
		int numDiff = 0;

		// check adjacency
		for (int c = 0; c < 7; c++) {
			for (int r = 0; r < 6; r++) {
				diff = board1[r][c] - board2[r][c];
				if (diff != 0) {
					numDiff++;
				}
			}
		}

		if (numDiff > 1)
			throw new IllegalArgumentException("Board 2 cannot be reached from board 1 in one move");
		if (numDiff == 0)
			throw new IllegalArgumentException("Board states cannot be identical");

		// find column
		for (int c = 0; c < 7; c++) {
			for (int r = 0; r < 6; r++) {
				diff = board1[r][c] - board2[r][c];
				if (diff != 0) {
					return c;
				}
			}
		}

		return -1; // lazy code
	}

	/**
	 * Given a column, checks to see if the move is valid. Verifies that it is 0-6
	 * and that the column isn't full.
	 * 
	 * @param gameBoardState matrix representing the current board layout
	 * @param column         int representing the column the move is for
	 * @return
	 */
	public static boolean isValidMove(int[][] gameBoardState, int column) {
		if (column >= 0 && column < 7) {
			if (gameBoardState[0][column] == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks for 4 tokens in a row in three directions (vertical, horizontal,
	 * diagonal).
	 * 
	 * @param boardState matrix representing current layout of the board
	 * @param thisPlayer player whose turn it currently is
	 * @return
	 */
	public static boolean isWin(int[][] boardState, int thisPlayer) {
		boolean isWin = false;

		if (colWin(boardState, thisPlayer) || rowWin(boardState, thisPlayer) || diagWin(boardState, thisPlayer)) {
			if (!colWin(boardState, thatPlayer(thisPlayer)) && !rowWin(boardState, thatPlayer(thisPlayer))
					&& !diagWin(boardState, thatPlayer(thisPlayer))) {
				isWin = true;
			}
			//isWin = true; // java swing errors without this??
		}

		return isWin;
	}

	/**
	 * Checks for vertical 4 in a row.
	 * 
	 * @param boardState
	 * @param thisPlayer
	 * @return
	 */
	public static boolean colWin(int[][] boardState, int thisPlayer) {

		for (int j = 0; j < 7; j++) {
			for (int i = 0; i <= 6 - 4; i++) {
				if (boardState[i][j] != 0 && boardState[i][j] == thisPlayer && boardState[i][j] == boardState[i + 1][j]
						&& boardState[i][j] == boardState[i + 2][j] && boardState[i][j] == boardState[i + 3][j]) {
					return true; // Win in a column
				}
			}
		}
		return false;

	}

	/**
	 * Checks for horizontal 4 in a row.
	 * 
	 * @param boardState
	 * @param thisPlayer
	 * @return
	 */
	public static boolean rowWin(int[][] boardState, int thisPlayer) {

		for (int i = 0; i < 6; i++) {
			for (int j = 0; j <= 7 - 4; j++) {
				if (boardState[i][j] != 0 && boardState[i][j] == thisPlayer && boardState[i][j] == boardState[i][j + 1]
						&& boardState[i][j] == boardState[i][j + 2] && boardState[i][j] == boardState[i][j + 3]) {
					return true; // Win in a row
				}
			}
		}
		return false;
	}

	/**
	 * Checks for diagonal 4 in a row. Both TR to BL and TL to BR.
	 * 
	 * @param boardState
	 * @param thisPlayer
	 * @return
	 */
	public static boolean diagWin(int[][] boardState, int thisPlayer) {

		for (int i = 0; i <= 6 - 4; i++) {
			for (int j = 0; j <= 7 - 4; j++) {
				if (boardState[i][j] != 0 && boardState[i][j] == thisPlayer
						&& (boardState[i][j] == boardState[i + 1][j + 1] && boardState[i][j] == boardState[i + 2][j + 2]
								&& boardState[i][j] == boardState[i + 3][j + 3])) {
					return true; // Win in a diagonal (top-left to bottom-right)
				}

				if (boardState[i + 3][j] != 0 && boardState[i + 3][j] == thisPlayer
						&& (boardState[i + 3][j] == boardState[i + 2][j + 1]
								&& boardState[i + 3][j] == boardState[i + 1][j + 2]
								&& boardState[i + 3][j] == boardState[i][j + 3])) {
					return true; // Win in a diagonal (bottom-left to top-right)
				}
			}
		}
		return false;

	}

	/**
	 * Prints a 2D array.
	 * 
	 * @param board
	 */
	public static void printMatrix(int[][] board) {

		for (int row = 0; row < 6; row++) {
			for (int col = 0; col < 7; col++) {
				System.out.print(" " + board[row][col]);
			}
			System.out.println();
		}

		System.out.println("\n\n");
	}

	/**
	 * Copies a 2D array.
	 * 
	 * @param toCopy
	 * @return
	 */
	public static int[][] copyMatrix(int[][] toCopy) {
		int[][] copy = new int[toCopy.length][toCopy[0].length];

		for (int r = 0; r < toCopy.length; r++) {
			copy[r] = Arrays.copyOf(toCopy[r], toCopy[0].length);
		}

		return copy;
	}

	/**
	 * Returns the other player.
	 * 
	 * @param thisPlayer
	 * @return
	 */
	public static int thatPlayer(int thisPlayer) {
		if (thisPlayer == 1) {
			return 2;
		} else {
			return 1;
		}
	}

}
