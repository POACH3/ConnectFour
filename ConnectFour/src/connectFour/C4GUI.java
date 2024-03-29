package connectFour;

import java.util.List;
import edu.princeton.cs.algs4.*;

import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The C4GUI class represents a GUI class that displays a Connect Four game.
 * 
 * @author Kimani Griffin + Trenton Stratton
 * 
 */
@SuppressWarnings("serial")
public class C4GUI extends JFrame {
	private JButton[][] buttons = new JButton[Board.ROWS][Board.COLS];
	private JPanel emptySpaces;
	private JButton btnNewButton;
	private JButton btnPlayerHistory;
	private JButton btnLeaderboard;
	//private JButton btnUndoMove;
	private JLabel gameTitleLabel;
	private JFrame statsFrame;
	private JFrame leaderboardFrame;
	private ImageIcon playerIcon;
	private ImageIcon computerIcon;

	private Player player;
	private ComputerPlayer computerPlayer;
	private Board currentBoard;

	/**
	 * Contructs the connect four GUI
	 * 
	 * @param connectFour
	 * @param player
	 * @param computer
	 * @param gameBoard
	 */
	public C4GUI(Player appPlayer, ComputerPlayer computer, Board gameBoard) {
		getContentPane().setBackground(new Color(94, 94, 94));
		setBackground(new Color(94, 94, 94));
		this.player = appPlayer;
		this.computerPlayer = computer;
		this.currentBoard = gameBoard;
		this.statsFrame = new JFrame("Player History");
		this.leaderboardFrame = new JFrame("Leaderboard");
		this.playerIcon = new ImageIcon("res/playerIcon.png");
		this.computerIcon = new ImageIcon("res/computerIcon.png");
		initializeGUI();
	}

	/**
	 * Creates a connect four game GUI
	 */
	public void initializeGUI() {
		getContentPane().setLayout(null);

		emptySpaces = new JPanel(new GridLayout(Board.ROWS, Board.COLS));
		emptySpaces.setBackground(new Color(39, 60, 174));
		emptySpaces.setBounds(84, 104, 580, 284);
		getContentPane().add(emptySpaces);
		emptySpaces.setLayout(new GridLayout(Board.ROWS, Board.COLS));

		gameTitleLabel = new JLabel("CONNECT FOUR");
		gameTitleLabel.setForeground(new Color(39, 60, 174));
		gameTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		gameTitleLabel.setFont(new Font("Uni Sans", Font.BOLD, 70));
		gameTitleLabel.setBounds(0, 16, 757, 107);
		getContentPane().add(gameTitleLabel);

		btnNewButton = new JButton("NEW GAME");
		btnNewButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		btnNewButton.setFont(new Font("Verdana", Font.PLAIN, 12));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startNewGame();
			}

			/**
			 * Handles GUI display when new GameBtn is clicked
			 */
			private void startNewGame() {
				currentBoard.clearBoard();
				updateGUI();
				while (true) {
					String playerName = JOptionPane.showInputDialog(null,"PLAYER 1 NAME","",JOptionPane.PLAIN_MESSAGE).toUpperCase();

					// Check if user clicked Cancel
					if (playerName == null || "Computer".equalsIgnoreCase(playerName)) {
						break;
					}

					if (!playerName.isBlank()) {

						// Check if player already exists
						if (Player.getGameStats().contains(playerName)) {
							// Player exists, retrieve existing game stats
							player = Player.getGameStats().get(playerName);

							// Erase last game's board data
							currentBoard = new Board(Board.ROWS, Board.COLS);

							break;
						} else {
							// Player does not exist, create a new player and stats
							player = new Player(playerName, 0, 0, 0);

							// Erase last game's board data
							currentBoard = new Board(Board.ROWS, Board.COLS);

							// Write the new player to CSV
							Player.setPlayerStats(player);

							break;
						}
					} else {
						JOptionPane.showMessageDialog(null, "Invalid name. Please try again.");
					}
				}

				while (true) {
					String difficultyInput = JOptionPane.showInputDialog(null,"DIFFICULTY (a number 1 to 3)","",JOptionPane.PLAIN_MESSAGE);

					// Check if user clicked Cancel
					if (difficultyInput == null) {
						break;
					}
					try {
						// Parse the difficulty input as an integer
						int difficulty = Integer.parseInt(difficultyInput);

						// Check if the difficulty is within the valid range (1-3) and
						// sets computer difficulty
						if (difficulty >= 1 && difficulty <= 3) {
							ComputerPlayer.setComputerDifficulty(difficulty);
							// Enable btns to be clicked
							resetButtons();
							break;
						} else {
							JOptionPane.showMessageDialog(null,
									"Invalid difficulty Entered. Please enter a value between 1 and 3.");
						}
					} catch (NumberFormatException ex) {
						// Input is not a valid integer
						JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid number.");
					}
				}
				
			}
			
		});
		btnNewButton.setBounds(318, 420, 117, 29);
		getContentPane().add(btnNewButton);

		btnPlayerHistory = new JButton("PLAYER STATS");
		btnPlayerHistory.setFont(new Font("Verdana", Font.PLAIN, 12));
		btnPlayerHistory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openGameStats();
			}
		});
		btnPlayerHistory.setBounds(119, 420, 117, 29);
		getContentPane().add(btnPlayerHistory);

		btnLeaderboard = new JButton("LEADERBOARD");
		btnLeaderboard.setFont(new Font("Verdana", Font.PLAIN, 12));
		btnLeaderboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openLeaderboard();
			}
		});
		btnLeaderboard.setBounds(524, 420, 117, 29);
		getContentPane().add(btnLeaderboard);
		
//		btnUndoMove = new JButton("Undo");
//		btnUndoMove.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				Board.undoMove();
//				updateGUI();
//			}
//		});
//		getContentPane().add(btnUndoMove);
//		btnUndoMove.setBounds(676, 200, 77, 29);

		// Create buttons based on ROWS and COLS
		currentBoard.getConnectFourBoard();

		createBoard();
	}

	/////////////// GUI DISPLAY METHODS ///////////////

	/**
	 * Enables buttons to be clicked after new game is started.
	 */
	private void resetButtons() {
		for (int i = Board.ROWS - 1; i >= 0; i--) {
			for (int j = 0; j < Board.COLS; j++) {
				buttons[i][j].setText(" ");
				buttons[i][j].setEnabled(true);
				buttons[i][j].setBackground(new Color(255,50,50));
			}
		}
	}

	/**
	 * Creates a 6x7 grid of buttons that are disabled until new game is started.
	 */
	private void createBoard() {
		for (int i = 0; i < Board.ROWS; i++) {
			for (int j = 0; j < Board.COLS; j++) {
				JButton button = new JButton("");
				button.setFont(new Font("Arial", Font.PLAIN, 40));
				button.setBackground(new Color(0,255,0));
				buttons[i][j] = button;
				int clickedColumn = j;

				buttons[i][j].addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						handleTurns(clickedColumn);
					}
				});
				emptySpaces.add(buttons[i][j]);
				buttons[i][j].setEnabled(false);

			}
		}
	}

	/**
	 * Updates the GUI based on the current state of the buttons.
	 */
	private void updateGUI() {
		char[][] board = Board.convertBoard(currentBoard.getConnectFourBoard());

		for (int i = Board.ROWS - 1; i >= 0; i--) {
			for (int j = 0; j < Board.COLS; j++) {
				if (board[i][j] == 'X') {
					buttons[i][j].setIcon(playerIcon);
					buttons[i][j].setHorizontalAlignment(SwingConstants.CENTER);
					buttons[i][j].setVerticalAlignment(SwingConstants.CENTER);

				} else if (board[i][j] == 'O') {
					buttons[i][j].setIcon(computerIcon);
					buttons[i][j].setHorizontalAlignment(SwingConstants.CENTER);
					buttons[i][j].setVerticalAlignment(SwingConstants.CENTER);
				} else {
					buttons[i][j].setIcon(null);
					buttons[i][j].setBackground(null);
				}
				if (currentBoard.checkWin()) {
					buttons[i][j].setEnabled(false);
				}
			}
		}
	}

	/**
	 * Displays new window of top 5 players on leader board based off number of wins
	 * 
	 */
	private void openLeaderboard() {
		List<Player> top5Leaders = Player.getLeaderBoard(5);

		JTextArea leaderboardTextArea = new JTextArea();
		leaderboardTextArea.setEditable(false);
		leaderboardTextArea.setBackground(new Color(94, 94, 94));

		for (Player player : top5Leaders) {
			leaderboardTextArea.append(player.toString() + "\n");
		}
		JScrollPane scrollPane = new JScrollPane(leaderboardTextArea);
		scrollPane.setPreferredSize(new Dimension(300, 300));

		leaderboardFrame.getContentPane().add(scrollPane);
		leaderboardFrame.pack();
		leaderboardFrame.setLocationRelativeTo(null);
		leaderboardFrame.setSize(300,200);
		leaderboardFrame.setVisible(true);
	}

	/**
	 * Displays new window of the game's all-time player stats
	 */
	private void openGameStats() {
		RedBlackBST<String, Player> gameHistoryStats = Player.getGameStats();

		JTextArea statsTextArea = new JTextArea();
		statsTextArea.setEditable(false);
		statsTextArea.setBackground(new Color(94, 94, 94));

		for (String playerName : gameHistoryStats.keys()) {
			Player player = gameHistoryStats.get(playerName.toUpperCase());
			statsTextArea.append(player.toString() + "\n");
		}
		JScrollPane scrollPane = new JScrollPane(statsTextArea);
		scrollPane.setPreferredSize(new Dimension(300, 300));

		statsFrame.getContentPane().add(scrollPane);
		statsFrame.pack();
		statsFrame.setLocationRelativeTo(null);
		statsFrame.setSize(300,250);
		statsFrame.setVisible(true);
	}

	/**
	 * Handles GUI display for player's turn.
	 *
	 * @param column The column index of the move.
	 */
	private void handleTurns(int column) {
		if (currentBoard.isValidMove(column)) {
			player.makeMove(column, 1, currentBoard);
			updateGUI();

			if (currentBoard.checkWin()) {
				player.incrementWins();
				computerPlayer.incrementLosses();
				Player.setPlayerStats(player);
				ComputerPlayer.setPlayerStats(computerPlayer);
				JOptionPane.showMessageDialog(null, player.getPlayerName() + " WINS!","",JOptionPane.PLAIN_MESSAGE);
				return;

			} else {
				computerPlayer.makeMove(currentBoard, 2);
				updateGUI();

				if (currentBoard.checkWin()) {
					computerPlayer.incrementWins();
					player.incrementLosses();
					ComputerPlayer.setPlayerStats(computerPlayer);
					Player.setPlayerStats(player);
					JOptionPane.showMessageDialog(null, "COMPUTER WINS!","",JOptionPane.PLAIN_MESSAGE);
					return;
				}
			}

			if (currentBoard.isFull() && !currentBoard.checkWin()) {
				player.incrementDraws();
				computerPlayer.incrementDraws();
				Player.setPlayerStats(player);
				ComputerPlayer.setPlayerStats(computerPlayer);
				JOptionPane.showMessageDialog(null, "Board is full! IT'S A DRAW!!","",JOptionPane.PLAIN_MESSAGE);
				return;
			}
		}

	}
}
