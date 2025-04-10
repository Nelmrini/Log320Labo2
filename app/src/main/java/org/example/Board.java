package org.example;

import java.util.ArrayList;
import java.util.List;

//import javax.annotation.Nullable;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait
// être le cas)

/**
 * This class is responsible for representing the current state of one board.
 * @author Brian Normant
 * @author Stéfane Maltais
 */
public final class Board {
	/** The internal representation of the tic tact toe board. **/
	private final Mark[][] board;
	/** Normal sized tictactoe board that have the result of each subboard **/
	Mark[][] resultboard;


	// Ne pas changer la signature de cette méthode
	/**
	 * Return new empty board
	 */
	public Board() {
		board = new Mark[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j] = Mark.EMPTY;
			}
		}
	}

	/**
	 * Create a copy of a board
	 * @param previous the board to copy
	 */
	public Board(final Board previous) {
		this.board = new Mark[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.board[i][j] = previous.board[i][j];
			}
		}
	}

	// Ne pas changer la signature de cette méthode
	/**
	 * Place la pièce 'mark' sur le plateau, à la
	 * position spécifiée dans Move
	 * This method SHOULD NOT be used as it modifie this reference to the board
	 * which create race conditions in others thread
	 * @param m the position to play
	 * @param mark whose playing
	 */
	public void play(final Move m, final Mark mark) {
		board[m.getRow()][m.getCol()] = mark;
	}

	/**
	 * Place la pièce 'mark' sur le plateau, à la
	 * position spécifiée dans Move
	 * This method SHOULD be used instead of play
	 * @param m the position to play
	 * @param mark whose playing
	 * @return a new board with the play
	 */
	public Board immutablePlay(final Move m, final Mark mark) {
		var b = new Board(this);
		b.play(m, mark);
		return b;
	}

	@Override
	public String toString() {
		final var empty =  "  ";
		final var cross =  "❌";
		final var circle = "🔵";

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 9; i++) {
			if (i % 3 == 0) sb.append("|------|------|------|\n");
			for (int j = 0; j < 9; j++) {
				if (j % 3 == 0) sb.append("|");
				sb.append(
						switch (board[j][i]) {
							case EMPTY -> empty;
							case X -> cross;
							case O -> circle;
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
	public int evaluate(final Mark mark) {
		resultboard = new Mark[3][3];

		for(int i = 0; i<3; i++){
			for(int j = 0; j<3; j++){
				resultboard[i][j] = Mark.EMPTY;
			}
		}
		for(int k = 0; k < 9; k+=3){
			for(int w = 0; w < 9; w+=3){
				for (int i = 0; i < 3; i++) {
					if (board[k+i][w+0] == board[k+i][w+1] && board[k+i][w+1] == board[k+i][w+2] && board[k+i][w+0] != Mark.EMPTY) {
						//return (board[i][0] == mark) ? 100 : -100;
						resultboard[k/3][w/3]=board[k+i][w+0];
					}
				}
			
				for (int j = 0; j < 3; j++) {
					if (board[k+0][w+j] == board[k+1][w+j] && board[k+1][w+j] == board[k+2][w+j] && board[k+0][w+j] != Mark.EMPTY) {
						//return (board[0][j] == mark) ? 100 : -100;
						resultboard[k/3][w/3]=board[k+0][w+j];
					}
				}
			
				if (board[k+0][w+0] == board[k+1][w+1] && board[k+1][w+1] == board[k+2][w+2] && board[k+0][w+0] != Mark.EMPTY) {
					//return (board[0][0] == mark) ? 100 : -100;
					resultboard[k/3][w/3]=board[k+0][w+0];
				}
			
				if (board[k+0][w+2] == board[k+1][w+1] && board[k+1][w+1] == board[k+2][w+0] && board[k+0][w+2] != Mark.EMPTY) {
					//return (board[0][2] == mark) ? 100 : -100;
					resultboard[k/3][w/3]=board[k+0][w+2];
				}
			}
			
		}

		for (int i = 0; i < 3; i++) {
			if (resultboard[i][0] == resultboard[i][1] && resultboard[i][1] == resultboard[i][2] && resultboard[i][0] != Mark.EMPTY) {
				return (resultboard[i][0] == mark) ? 100 : -100;
			}
		}

		for (int j = 0; j < 3; j++) {
			if (resultboard[0][j] == resultboard[1][j] && resultboard[1][j] == resultboard[2][j] && resultboard[0][j] != Mark.EMPTY) {
				return (resultboard[0][j] == mark) ? 100 : -100;
			}
		}

		if (resultboard[0][0] == resultboard[1][1] && resultboard[1][1] == resultboard[2][2] && resultboard[0][0] != Mark.EMPTY) {
			return (resultboard[0][0] == mark) ? 100 : -100;
		}

		if (resultboard[0][2] == resultboard[1][1] && resultboard[1][1] == resultboard[2][0] && resultboard[0][2] != Mark.EMPTY) {
			return (resultboard[0][2] == mark) ? 100 : -100;
		}

		for (int j = 0; j < 3; j++) {
			if (resultboard[0][j] == resultboard[1][j] && resultboard[1][j] == resultboard[2][j] && resultboard[0][j] != Mark.EMPTY) {
				return (resultboard[0][j] == mark) ? 100 : -100;
			}
		}

		if (resultboard[0][0] == resultboard[1][1] && resultboard[1][1] == resultboard[2][2] && resultboard[0][0] != Mark.EMPTY) {
			return (resultboard[0][0] == mark) ? 100 : -100;
		}

		if (resultboard[0][2] == resultboard[1][1] && resultboard[1][1] == resultboard[2][0] && resultboard[0][2] != Mark.EMPTY) {
			return (resultboard[0][2] == mark) ? 100 : -100;
		}

		return 0;
		//throw new UnsupportedOperationException();
	}

	public int evaluateHeuristicCustom(Mark mark, Move move) {
		this.evaluate(mark);
		int totalScore = 0;
		Mark opponent = mark.other();
		
		for (int subRow = 0; subRow < 3; subRow++) {
			for (int subCol = 0; subCol < 3; subCol++) {
				int baseRow = subRow * 3;
				int baseCol = subCol * 3;
				int subScore = evaluateSubBoard(this.board, baseRow, baseCol, mark, opponent);
				if (subRow == 1 && subCol == 1) {
					subScore *= 2;
				}
				totalScore += subScore;
			}
		}
	
		totalScore += evaluateGlobalBoard(mark, opponent, move);
		
		return totalScore;
	}
	
	private int evaluateSubBoard(Mark[][] board, int baseRow, int baseCol, Mark mark, Mark opponent) {
		int score = 0;
	
		for (int i = 0; i < 3; i++) {
			int rowCountMark = 0, rowCountOpp = 0, rowCountEmpty = 0;
			int colCountMark = 0, colCountOpp = 0, colCountEmpty = 0;
			for (int j = 0; j < 3; j++) {
				if (board[baseRow + i][baseCol + j] == mark) {
					rowCountMark++;
				} else if (board[baseRow + i][baseCol + j] == opponent) {
					rowCountOpp++;
				} else {
					rowCountEmpty++;
				}
				// Column
				if (board[baseRow + j][baseCol + i] == mark) {
					colCountMark++;
				} else if (board[baseRow + j][baseCol + i] == opponent) {
					colCountOpp++;
				} else {
					colCountEmpty++;
				}
			}
			score += evaluateLine(rowCountMark, rowCountOpp, rowCountEmpty);
			score += evaluateLine(colCountMark, colCountOpp, colCountEmpty);
		}
	
		// Evaluate diagonals
		int diag1Mark = 0, diag1Opp = 0, diag1Empty = 0;
		int diag2Mark = 0, diag2Opp = 0, diag2Empty = 0;
		for (int i = 0; i < 3; i++) {
			// Main diagonal
			if (board[baseRow + i][baseCol + i] == mark) {
				diag1Mark++;
			} else if (board[baseRow + i][baseCol + i] == opponent) {
				diag1Opp++;
			} else {
				diag1Empty++;
			}
			// Anti-diagonal
			if (board[baseRow + i][baseCol + (2 - i)] == mark) {
				diag2Mark++;
			} else if (board[baseRow + i][baseCol + (2 - i)] == opponent) {
				diag2Opp++;
			} else {
				diag2Empty++;
			}
		}
		score += evaluateLine(diag1Mark, diag1Opp, diag1Empty);
		score += evaluateLine(diag2Mark, diag2Opp, diag2Empty);
		
		return score;
	}
	

	private int evaluateLine(int countMark, int countOpp, int countEmpty) {
		if (countMark == 3) return 1000;
		if (countOpp == 3) return -1000;
		
		int score = 0;
		if (countMark == 2 && countEmpty == 1) score += 50;
		if (countMark == 1 && countEmpty == 2) score += 10;
		if (countOpp == 2 && countEmpty == 1) score -= 50;
		if (countOpp == 1 && countEmpty == 2) score -= 10;
		
		return score;
	}
	

	private int evaluateGlobalBoard(Mark mark, Mark opponent, Move move) {
		
		int score = 0;
		
		for (int i = 0; i < resultboard.length; i++) {
			for (int j = 0; j < resultboard[0].length; j++) {
				if (resultboard[i][j] == opponent) {
					if(resultboard[move.getRow()%3][move.getCol()%3]!=Mark.EMPTY){
						score -= 100;
					}
				}
			}
		}
			
			
		return score+= evaluateSubBoard(resultboard, 0, 0, mark, opponent);
	}

	public Mark nextPlayer() {
		int redcnt = 0, blkcnt = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				switch (board[j][i]) {
					case X -> redcnt++;
					case O -> blkcnt++;
					default -> { }
				}
			}
		}

		// unless red just played, it's always red turn.
		return (redcnt > blkcnt) ? Mark.O : Mark.X;
	}

	public Mark flip(final Mark mark) {
		return mark.other();
	}

	/**
	 * List all possible move for a player.
	 * @param vsMove the move of the opponents
	 * @return a list of all possible move
	 */
	public List<Move> getPossibleMoves(final Move vsMove) {
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
			var done = isSubBoardDone(new Move(
						vsMove.getRow() % 3 * 3,
						vsMove.getCol() % 3 * 3));
			if (done) {
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						var cond = i / 3 != vsMove.getRow() % 3
							|| j / 3 != vsMove.getCol() % 3;
						cond &= !isSubBoardDone(new Move(j, i));
						if (cond && board[j][i] == Mark.EMPTY) {
							res.add(new Move(j, i));
						}
					}
				}
			} else {
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						int row = 3 * (vsMove.getRow() % 3) + i;
						int col = 3 * (vsMove.getCol() % 3) + j;
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
	 * Check if a specific sub board is won/lost/filled.
	 * @param move any place on the sub board to check
	 * @return true if the sub board can't be played on
	 */
	private boolean isSubBoardDone(final Move move) {
		int row = move.getRow() / 3;
		int col = move.getCol() / 3;

		int count = 0;
		int[] tmp1 = new int[] {
			1, 1, 1,
				1, 1, 1,
				1, 1, 1
		};

		int[] tmp2 = new int[] {
			1, 1, 1,
				1, 1, 1,
				1, 1, 1
		};

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				switch (board[3 * row + i][3 * col + j]) {
					case X -> {
						tmp1[j * 3 + i] *= 0;
						count++;
					}
					case O -> {
						tmp2[j * 3 + i] *= 0;
						count++;
					}
					default -> {
						tmp1[j * 3 + i] *= 0;
						tmp2[j * 3 + i] *= 0;
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
	 * helper to convert a string like A 5 to a position on the board.
	 * @param str a string like "A5" or "I1"
	 * @return an array of two integers representing the row and column
	 */
	public static int[] strToInd(final String str) {
		if (str.length() != 2) return null;
		if (!Character.isLetter(str.charAt(0))) return null;
		if (!Character.isDigit(str.charAt(1))) return null;

		var res = new int[2];

		var c = str.charAt(0);
		res[0] = ((int) c > 90) ? (c - 'a') : (c - 'A');

		var d = str.charAt(1);
		res[1] = (d - '9') * -1;

		return res;
	}

	public static Move strToMov(final String str) {
		var tmp = strToInd(str);
		return new Move(tmp[0], tmp[1]);
	}
}
