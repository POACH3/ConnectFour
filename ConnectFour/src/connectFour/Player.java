package connectFour;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import edu.princeton.cs.algs4.*;

/**
 * The Player class represents a player in a Connect Four game.
 * 
 * @author Kimani Griffin + Trenton Stratton
 */
public class Player implements Comparable<Player> {
	private String name;
	private int wins;
	private int losses;
	private int draws;
	private int lastMoveColumn;

	/**
	 * Creates a player object with a name and record of wins losses and draws
	 * 
	 * @param name
	 * @param wins
	 * @param losses
	 * @param draws
	 */
	public Player(String name, int wins, int draws, int losses) {
		this.name = name;
		this.wins = wins;
		this.draws = draws;
		this.losses = losses;
	}

	/**
	 * @return the players Name
	 */
	public String getPlayerName() {
		return name;
	}

	/**
	 * @return number of wins
	 */
	public int getWins() {
		return wins;
	}

	/**
	 * @return number of draws
	 */
	public int getDraws() {
		return draws;
	}

	/**
	 * @return number of losses
	 */
	public int getLosses() {
		return losses;
	}

	/**
	 * @return the lastMoveColumn
	 */
	public int getLastMoveColumn() {
		return lastMoveColumn;
	}

	/**
	 * @param name - the name to set
	 */
	public void setPlayerName(String name) {
		this.name = name;
	}

	/**
	 * @param wins - the wins to set
	 */
	public void setWins(int wins) {
		this.wins = wins;
	}

	/**
	 * @param draws - the draws to set
	 */
	public void setDraws(int draws) {
		this.draws = draws;
	}

	/**
	 * @param losses - the losses to set
	 */
	public void setLosses(int losses) {
		this.losses = losses;
	}

	/**
	 * @param lastMoveColumn - the lastMoveColumn to set
	 */
	public void setLastMoveColumn(int lastMoveColumn) {
		this.lastMoveColumn = lastMoveColumn;
	}

	/**
	 * Increment total wins by 1
	 */
	public void incrementWins() {
		wins++;
	}

	/**
	 * Increment total losses by 1
	 */
	public void incrementLosses() {
		losses++;
	}

	/**
	 * Increment total draws by 1
	 */
	public void incrementDraws() {
		draws++;
	}

	/**
	 * Resets values of current player stats
	 */
	public void reset() {
		this.wins = 0;
		this.losses = 0;
		this.draws = 0;
	}

	/**
	 * Makes a move on the game board provided it is a valid move
	 *
	 * @param column - column index for the move
	 * @param symbol - player's symbol
	 * @param board  - game board
	 * @return true - if the move was successful
	 */
	public void makeMove(int column, int symbol, Board board) {
		board.makeMove(column, symbol);
		setLastMoveColumn(column);
	}

	/**
	 * @return String in format: Name, Wins, Draws, Losses
	 */
	@Override
	public String toString() {
		return String.format("%s, %d, %d, %d", name, wins, draws, losses);
	}

	/**
	 * Compares one player's wins to another
	 */
	@Override
	public int compareTo(Player other) {
		return Integer.compare(this.wins, other.wins);
	}

	/**
	 * Returns the leaderboard based on data from a file
	 *
	 * @param count The number of top players to retrieve.
	 * @return List of top players based on the number of wins
	 */
	public static List<Player> getLeaderBoard(int count) {
		String fileName = "res/ConnectFourGameStats.csv";
		Player[] playersData = getPlayerStats(fileName);

		List<Player> allPlayers = Arrays.asList(playersData);
		allPlayers.sort((player1, player2) -> {
			if (player1.getWins() != player2.getWins()) {
				return Integer.compare(player2.getWins(), player1.getWins());
			} else {
				return Integer.compare(player1.getLosses(), player2.getLosses());
			}
		});

		int topPlayersCount = Math.min(allPlayers.size(), count);
		return allPlayers.subList(0, topPlayersCount);

	}

	/**
	 * Retrieves a player by name from the game history.
	 *
	 * @param playerName The name of the player to retrieve.
	 * @return The player if found, otherwise null.
	 */
	public static Player playerHistory(String playerName) {
		return getGameStats().get(playerName);
	}

	/**
	 * Loads game's stats into a RedBlackBST
	 *
	 * @return - game history stats as RedBlackBST
	 */
	public static RedBlackBST<String, Player> getGameStats() {
		String fileName = "res/ConnectFourGameStats.csv";
		Player[] playersData = getPlayerStats(fileName);

		RedBlackBST<String, Player> gameHistoryStats = new RedBlackBST<>();

		for (Player player : playersData) {
			gameHistoryStats.put(player.getPlayerName(), player);
		}
		return gameHistoryStats;
	}

	/**
	 * Saves players score data to a CSV file
	 * 
	 */
	/**
	 * Saves players score data to a CSV file
	 */
	public static void setPlayerStats(Player currentPlayer) {
		String fileName = "res/ConnectFourGameStats.csv";
		Player[] allPlayers = getPlayerStats(fileName);
		boolean playerFound = false;

		// Update player data with the current game session
		for (int i = 0; i < allPlayers.length; i++) {
			Player player = allPlayers[i];
			if (currentPlayer.getPlayerName().equalsIgnoreCase(player.getPlayerName())) {
				player.setWins(currentPlayer.getWins());
				player.setDraws(currentPlayer.getDraws());
				player.setLosses(currentPlayer.getLosses());
				playerFound = true;
				break;
			}
		}

		// If player not found, add the current player to the list
		if (!playerFound) {
			List<Player> updatedPlayersList = new ArrayList<>(Arrays.asList(allPlayers));
			updatedPlayersList.add(currentPlayer);
			allPlayers = updatedPlayersList.toArray(new Player[0]);
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
			writer.write("Name,Wins,Draws,Losses\n");

			// Write updated player data
			for (Player player : allPlayers) {
				writer.write(String.format("%s,%d,%d,%d\n", player.getPlayerName().toUpperCase(), player.getWins(),
						player.getDraws(), player.getLosses()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets player stats from a CSV file
	 *
	 * @param fileName The file containing player data
	 * @return Player array - game stats for players
	 */
	public static Player[] getPlayerStats(String fileName) {
		List<Player> list = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			String line;
			// Skip the header line
			reader.readLine();

			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");
				if (tokens.length >= 4) {
					String playerName = tokens[0].trim();
					int wins = Integer.parseInt(tokens[1].trim());
					int draws = Integer.parseInt(tokens[2].trim());
					int losses = Integer.parseInt(tokens[3].trim());

					Player player = new Player(playerName, wins, draws, losses);
					list.add(player);
				} else {
					System.err.println("Invalid line format in CSV file: " + line);
				}
			}
		} catch (IOException e) {
			StdOut.println("A problem occurred reading in the files.");
			e.printStackTrace();
		}
		return list.toArray(new Player[0]);
	}

}
