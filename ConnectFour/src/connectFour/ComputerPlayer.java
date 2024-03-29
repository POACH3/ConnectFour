package connectFour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import edu.princeton.cs.algs4.StdOut;

/**
 * The Computer Player class represents a computer player in a Connect Four
 * game.
 * 
 * @author Kimani Griffin + Trenton Stratton
 */
public class ComputerPlayer extends Player {
	/** Constant representing the symbol for the computer player. */
	private static final int COMPUTER_SYMBOL = 2;
	private static int computerDifficulty;
	static ArrayList<int[][]> gameStates = new ArrayList<>();

	/**
	 * Constructs an Computer Player object with the specified name, wins, draws,
	 * and losses.
	 *
	 * @param name   The name of the computer player.
	 * @param wins   The number of wins for the computer player.
	 * @param draws  The number of draws for the computer player.
	 * @param losses The number of losses for the computer player.
	 */
	public ComputerPlayer(String name, int difficulty, int wins, int draws, int losses) {
		super("Computer", 0, 0, 0);
		computerDifficulty = difficulty;
	}

	/**
	 * Sets level of game difficulty.
	 * 
	 * @param computerDifficulty
	 */
	public static void setComputerDifficulty(int computerDifficulty) {
		ComputerPlayer.computerDifficulty = computerDifficulty;
	}

	/**
	 * Makes a move on the game board provided it is a valid move.
	 *
	 * @param board  - game board
	 * @param symbol - player's symbol
	 * @return true - if the move was successful
	 */
	public boolean makeMove(Board board, int symbol) {
		int move;
		move = getTurn(board.getConnectFourBoard());

		while (!board.isValidMove(move)) {
			move = getTurn(board.getConnectFourBoard());
		}

		board.makeMove(move, COMPUTER_SYMBOL);
		return true;
	}

	/**
	 * Gets computer player stats from a CSV file.
	 *
	 * @param fileName The name of the file containing computer player stats.
	 * @return An array of AI objects representing computer player stats.
	 */
	public static ComputerPlayer[] getComputerStats(String fileName) {
		List<ComputerPlayer> list = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			reader.readLine();

			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");
				String computerName = tokens[0].trim();
				int computerDifficulty = Integer.parseInt(tokens[1].trim());
				int wins = Integer.parseInt(tokens[2].trim());
				int draws = Integer.parseInt(tokens[3].trim());
				int losses = Integer.parseInt(tokens[4].trim());

				if ("Computer".equals(computerName)) {
					ComputerPlayer computer = new ComputerPlayer(computerName, computerDifficulty, wins, draws, losses);
					list.add(computer);
					break; // Stop reading after finding the "Computer" player
				}
			}
		} catch (IOException e) {
			StdOut.println("A problem occurred reading in the files.");
			e.printStackTrace();
		}
		return list.toArray(new ComputerPlayer[0]);
	}

	public static int getTurn(int[][] currentBoardState) {
		return GameStates.getMove(currentBoardState, computerDifficulty);
	}
}
