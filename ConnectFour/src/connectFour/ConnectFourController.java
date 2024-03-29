package connectFour;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
/**
 * Controller for Connect Four game
 * 
 * @author Kimani Griffin + Trenton Stratton
 *
 */

public class ConnectFourController
{
	private static C4GUI gui;
	private static Player appPlayer = new Player("Player", 0, 0, 0);
	private static ComputerPlayer computer = new ComputerPlayer("Computer", 2, 0, 0, 0);
	private static Board gameBoard = new Board(6, 7);

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() -> {
			gui = new C4GUI(appPlayer, computer, gameBoard);
			gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			gui.setPreferredSize(new Dimension(770, 500));
			gui.setResizable(true);
			gui.pack();
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);
		});
	}
}
