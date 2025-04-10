package org.example;

/**
 * This class describe a position on the board.
 * Furthermore, This class is immutable
 * This helps a lot to avoid unidented modification
 * @author Brian Normant
 */
public final class Move {
	/** row of the move on the board. **/
	private final int row;
	/** column of the move on the board. **/
	private final int col;

	/**
	 * Create a move on the board
	 * @param r the row, 0 is top, 8 is bottom
	 * @param c the column, 0 is A, 8 is I
	 */
	public Move(final int r, final int c) {
		row = r;
		col = c;
	}

	/**
	 * Get the row of the move on the board
	 * @return a index that match the implementation of Board
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Get the column of the move on the board
	 * @return a index that match the implementation of Board
	 */
	public int getCol() {
		return col;
	}

	@Override
	public String toString() {
		return "" + (char) (row + 'A') + (char) ('1' - (col - 8));
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null) return false;
		if (this == o) return true;
		if (o instanceof Move move) {
			return row == move.row && col == move.col;
		}
		return false;
	}
}
