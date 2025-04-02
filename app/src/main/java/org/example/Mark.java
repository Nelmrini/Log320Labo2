package org.example;

/**
 * A case on the board.
 * @author Brian Normant
 */
public enum Mark {
	/** red player play with crosses. **/
	X,
	/** black player play with circles. **/
	O,
	/** no one played here. **/
	EMPTY,
	/** the game is a tie. **/
	TIE;

	public Mark other() {
		return switch (this) {
			case X -> O;
			case O -> X;
			default -> this;
		};
	}
}

