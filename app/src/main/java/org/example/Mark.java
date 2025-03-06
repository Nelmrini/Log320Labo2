package org.example;

public enum Mark {
	X,
	O,
	EMPTY;

	public Mark other() {
		return switch (this) {
			case X -> O;
			case O -> X;
			case EMPTY -> EMPTY;
		};
	}
}

