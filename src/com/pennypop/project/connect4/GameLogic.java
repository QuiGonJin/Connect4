package com.pennypop.project.connect4;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.pennypop.project.ProjectApplication;
import com.pennypop.project.screens.GameScreen;
import com.pennypop.project.screens.MainScreen;

public class GameLogic {

	private Cell[][] board;

	private int turn = -1; // keeps track of turn. -1 = player 1, 1 = player 2.
	private int turnNo = 0; // track number of turns
	private int rows;
	private int columns;

	private AI ai; // may or may not be initialized depending on game type
	private boolean isAI; // flag indicating whether or not game is an AI game
	private boolean gameOver = false;

	public GameLogic(Cell[][] board, boolean isAI) {
		this.isAI = isAI;
		this.board = board;
		this.columns = board.length;
		this.rows = board[0].length;

		// If this is an AI game, create an AI
		if (isAI) {
			ai = new AI(this);
		}
	}

	/**
	 * This method consults game state to determine a valid move.
	 * 
	 * Searches the column selected by the user for the first available row
	 * 
	 * @param x
	 *            coordinate of selected player Cell
	 * @param y
	 *            coordinate of selected player Cell
	 * @return true if there's a valid move based on player input, false
	 *         otherwise
	 */
	public boolean placePiece(int x, int y) {
		// Stop processing moves if game is over
		if (gameOver)
			return false;

		// We search the selected column for an available position to drop the
		// piece starting from the bottom
		for (int i = 0; i < rows; i++) {
			if (board[x][i].isOccupied() == 0) {
				// If a valid spot is found, set the Cell to the current
				// player's color
				board[x][i].setCell(turn);
				turnNo++;

				// Check for victory
				if (isVictory(4, x, i) == 4) {
					victory(turn);
					return true;
				}

				// Check for stale mate
				if (turnNo == rows * columns) {
					victory(0);
				}

				// Swap player turns
				turn = turn * -1;

				// If this is an AI game, AI gets a turn
				if (isAI && turn == 1) {
					ai.makeMove();
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * resets game
	 */
	public void reset() {
		GameScreen.screen.dispose();
		Screen newGame = new GameScreen(isAI);
		ProjectApplication.app.setScreen(newGame);
	}

	/**
	 * Handles the victory
	 * 
	 * Provides buttons to exit to the menu, or reset the board
	 * 
	 * @param the
	 *            victorious player. (-1 = Player 1, 1 = Player 2, 0 = y'all are
	 *            losers)
	 */
	public void victory(int player) {
		gameOver = true;
		Label win;

		// red win
		if (player == -1) {
			win = new Label("VICTORY RED", MainScreen.skin, "colored");
			win.setColor(Color.RED);
		} else if (player == 1) {
			// yellow win
			win = new Label("VICTORY YELLOW", MainScreen.skin, "colored");
			win.setColor(Color.YELLOW);
		} else {
			// Nobody win
			win = new Label("STALE MATE", MainScreen.skin, "colored");
			win.setColor(Color.DARK_GRAY);
		}
		win.setPosition(Gdx.graphics.getWidth() / 2 - win.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		GameScreen.stage.addActor(win);

		// Add 'Menu' button and listener
		ImageButton menuButton = new ImageButton(MainScreen.skin, "menuButton");
		menuButton.setPosition(win.getX(), (Gdx.graphics.getHeight() / 2) - 100);
		menuButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				ProjectApplication.app.setScreen(MainScreen.screen);
				return true;
			}
		});
		GameScreen.stage.addActor(menuButton);

		// Add 'Reset' button and listener
		ImageButton resetButton = new ImageButton(MainScreen.skin, "resetButton");
		resetButton.setPosition(win.getX() + 100, (Gdx.graphics.getHeight() / 2) - 100);
		resetButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				reset();
				return true;
			}
		});
		GameScreen.stage.addActor(resetButton);
	}

	/**
	 * Checks for win condition.
	 * 
	 * Win is met when either player accumulates more than 'n' consecutive
	 * pieces on the board in vertical, horizontal, or diagonal orientation.
	 * 
	 * Is called after every player move
	 * 
	 * Simply takes the position of the most recently placed game piece and
	 * checks the row, column, and diagonals branching from that piece for
	 * victory condition.
	 * 
	 * Note: This function doesn't really need to keep track of maxConsec But I
	 * was guessing max-consecutive pieces would be used as a heuristic for the
	 * AI to be implemented later
	 * 
	 * @param n
	 *            how many consecutive pieces are needed for win. Usually set to
	 *            4
	 * @param x
	 *            X coordinate of most recently placed piece
	 * @param y
	 *            Y coordinate of most recently placed piece
	 * @return max consecutives
	 */
	public int isVictory(int n, int x, int y) {
		int maxConsec = 1;
		int thisColor = board[x][y].isOccupied();
		int consec = 0;

		/*-----------------Check horizontal-------------*/
		for (int i = 0; i < columns; i++) {
			if (board[i][y].isOccupied() == thisColor) {
				consec++;
				if (consec > maxConsec) {
					maxConsec = consec;
				}
				if (maxConsec == n) {
					return maxConsec;
				}
			} else {
				consec = 0;
			}
		}

		/*-------------------Check vertical-------------*/
		consec = 0;
		for (int i = 0; i < rows; i++) {
			if (board[x][i].isOccupied() == thisColor) {
				consec++;
				if (consec > maxConsec) {
					maxConsec = consec;
				}
				if (maxConsec == n) {
					return maxConsec;
				}
			} else {
				consec = 0;
			}
		}

		/* ------Check Diagonal in this direction -> "/"--- */
		consec = 0;
		int curX = x;
		int curY = y;
		// first find starting point
		while (curX > 0 && curY > 0) {
			curX--;
			curY--;
		}
		int startX = curX;
		int startY = curY;
		// Now begin check from bottom left to top right
		while (startX < columns && startY < rows) {
			if (board[startX][startY].isOccupied() == thisColor) {
				consec++;
				if (consec > maxConsec) {
					maxConsec = consec;
				}
				if (maxConsec == n) {
					return maxConsec;
				}
			} else {
				consec = 0;
			}
			startX++;
			startY++;
		}

		/*--------Check Diagonal in this direction -> "\" ----*/
		// first find starting point
		curX = x;
		curY = y;
		consec = 0;
		while (curX < columns - 1 && curY > 0) {
			curX++;
			curY--;
		}
		startX = curX;
		startY = curY;
		// Now begin check from bottom right to top left
		while (startX >= 0 && startY < rows) {
			if (board[startX][startY].isOccupied() == thisColor) {
				consec++;
				if (consec > maxConsec) {
					maxConsec = consec;
				}
				if (maxConsec == n) {
					return maxConsec;
				}
			} else {
				consec = 0;
			}
			startX--;
			startY++;
		}
		return maxConsec;
	}

	public int getNumRows() {
		return rows;
	}

	public int getNumColumns() {
		return columns;
	}
}
