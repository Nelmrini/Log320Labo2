package org.example;

/**
 * This class describe a positiono on the board.
 * @author Brian Normant
 */
public final class Move {
	/** row of the move on the board. **/
	private int row;
	/** column of the move on the board. **/
	private int col;

	public Move() {
		row = -1;
		col = -1;
	}

	public Move(final int r, final int c) {
		row = r;
		col = c;
	}

	public int getRow() {
		return row;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + row;
		result = prime * result + col;
		return result;
	}

}
