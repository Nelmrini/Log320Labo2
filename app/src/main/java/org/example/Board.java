package org.example;

import java.util.ArrayList;
import java.util.List;

//import javax.annotation.Nullable;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait
// être le cas)
public class Board {
	private Mark[][] board;


	// Ne pas changer la signature de cette méthode
	public Board() {
		board = new Mark[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j] = Mark.EMPTY;
			}
		}
	}

	// Place la pièce 'mark' sur le plateau, à la
	// position spécifiée dans Move
	//
	// Ne pas changer la signature de cette méthode
	public void play(Move m, Mark mark) {
		board[m.getRow()][m.getCol()] = mark;
	}
    
	public void undo(Move m){
		board[m.getRow()][m.getCol()] = Mark.EMPTY;
	}

	@Override
	public String toString() {
		final var EMPTY =  "  ";
		final var CROSS =  "❌";
		final var CIRCLE = "🔵";

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			if (i % 3 == 0) sb.append("|------|------|------|\n");
			for (int j = 0; j < 9; j++) {
				if (j % 3 == 0) sb.append("|");
				sb.append(switch (board[j][i]) {
					case EMPTY -> EMPTY;
					case X -> CROSS;
					case O -> CIRCLE;
				});
			}
			sb.append("|\n");
		}
		sb.append("|------|------|------|\n");

		return sb.toString();
	}


	// retourne  100 pour une victoire
	//          -100 pour une défaite
	//           0   pour un match nul
	// Ne pas changer la signature de cette méthode
	public int evaluate(Mark mark) {
		throw new UnsupportedOperationException();
	}

	public Mark nextPlayer() {
		int redcnt = 0, blkcnt = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				switch (board[j][i]) {
					case X -> redcnt++;
					case O -> blkcnt++;
					case EMPTY -> {}
				}
			}
		}

		// unless red just played, it's always red turn.
		return (redcnt > blkcnt)? Mark.O : Mark.X;
	}
    public Mark flip(Mark mark){
        if(mark==Mark.O){
            mark=Mark.X;
            return mark;
        }else if(mark==Mark.X){
            mark=Mark.O;
           return mark;
        }else{return mark;}
    }
	/**
	 * List all possible move for a player
	 * @param player who's moves it is
	 * @param vsMove the move of the opponents
	 * @return a list of all possible move
	 */
	public List<Move> getPossibleMoves(Mark Player, Move vsMove) {
		if (vsMove == null) {
			List<Move> res = new ArrayList<>();
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					res.add(new Move(j, i));
				}
			}
			return res;
		} else {
			List<Move> res = new ArrayList<>();

			if (isSubBoardDone(new Move(vsMove.getRow()%3 * 3, vsMove.getCol()%3 * 3))) {
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						if ((i / 3 != vsMove.getRow()%3 ||
							 j / 3 != vsMove.getCol()%3) &&
							board[j][i] == Mark.EMPTY) {
							res.add(new Move(j, i));
						}
					}
				}
			} else {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						int row = 3*(vsMove.getRow()%3) + i;
						int col = 3*(vsMove.getCol()%3) + j;
						if (board[row][col] == Mark.EMPTY) {
							res.add(new Move(row, col));
						}
					}
				}
			}

			return res;
		}
	}

	/**
	 * Check if a spefic subboard is won/lost/filled
	 */
	private final boolean isSubBoardDone(Move move) {
		int row = move.getRow() / 3;
		int col = move.getCol() / 3;

		int count = 0;
		int[] tmp1 = new int[] { 1,1,1, 1,1,1, 1,1,1 };
		int[] tmp2 = new int[] { 1,1,1, 1,1,1, 1,1,1 };

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				switch (board[3*row + i][3*col + j]) {
					case X -> {
						tmp1[j*3 + i] *= 0;
						count++;
					}
					case O -> {
						tmp2[j*3 + i] *= 0;
						count++;
					}
					default -> {
						tmp1[j*3 + i] *= 0;
						tmp2[j*3 + i] *= 0;
					}
				}
			}
		}

		if (count == 9) return true;

		if (tmp1[0] + tmp1[1] + tmp1[2] == 3) return true;
		if (tmp1[3] + tmp1[4] + tmp1[5] == 3) return true;
		if (tmp1[6] + tmp1[7] + tmp1[8] == 3) return true;
		if (tmp1[0] + tmp1[3] + tmp1[6] == 3) return true;
		if (tmp1[1] + tmp1[4] + tmp1[7] == 3) return true;
		if (tmp1[2] + tmp1[5] + tmp1[8] == 3) return true;
		if (tmp1[0] + tmp1[4] + tmp1[8] == 3) return true;
		if (tmp1[2] + tmp1[4] + tmp1[6] == 3) return true;

		if (tmp2[0] + tmp2[1] + tmp2[2] == 3) return true;
		if (tmp2[3] + tmp2[4] + tmp2[5] == 3) return true;
		if (tmp2[6] + tmp2[7] + tmp2[8] == 3) return true;
		if (tmp2[0] + tmp2[3] + tmp2[6] == 3) return true;
		if (tmp2[1] + tmp2[4] + tmp2[7] == 3) return true;
		if (tmp2[2] + tmp2[5] + tmp2[8] == 3) return true;
		if (tmp2[0] + tmp2[4] + tmp2[8] == 3) return true;
		if (tmp2[2] + tmp2[4] + tmp2[6] == 3) return true;

		return false;
	}

	/**
	 * helper to convert a string like A5 to a position on the board
	 */
	public static int[] strToInd(String str) {
		if (str.length() != 2) return null;
		if (!Character.isLetter(str.charAt(0))) return null;
		if (!Character.isDigit(str.charAt(1))) return null;

		var res = new int[2];

		var c = str.charAt(0);
		res[0] = ((int)c > 90)?(c - 'a'):(c - 'A');

		var d = str.charAt(1);
		res[1] = (d - '9') * -1;

		return res;
	}

	public static Move strToMov(String str) {
		var tmp = strToInd(str);
		return new Move(tmp[0], tmp[1]);
	}
}
