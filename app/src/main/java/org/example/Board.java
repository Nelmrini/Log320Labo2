package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

//import javax.annotation.Nullable;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait
// être le cas)

/**
 * This class is responsible for representing the current state of one board.
 * @author Brian Normant
 */
public final class Board {
	/** The internal representation of the tic tact toe board. **/
	private final Mark[][] board;
	Mark[][] resultboard;


	// Ne pas changer la signature de cette méthode
	public Board() {
		board = new Mark[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j] = Mark.EMPTY;
			}
		}
	}

	public Board(final Board previous) {
		this.board = new Mark[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.board[i][j] = previous.board[i][j];
			}
		}
	}

	// Place la pièce 'mark' sur le plateau, à la
	// position spécifiée dans Move
	//
	// Ne pas changer la signature de cette méthode
	public void play(final Move m, final Mark mark) {
		board[m.getRow()][m.getCol()] = mark;
	}

	public Board immutablePlay(final Move m, final Mark mark) {
		var b = new Board(this);
		b.play(m, mark);
		return b;
	}

	public void undo(final Move m) {
		board[m.getRow()][m.getCol()] = Mark.EMPTY;
	}

	/**
	 * Pretty print of the board
	 */
	@Override
	public String toString() {
		final var empty =  "  ";
		final var cross =  "❌";
		final var circle = "🔵";
		final String top = "┌──────┬──────┬──────┐\n";
		final String mid = "├──────┼──────┼──────┤\n";
		final String bot = "└──────┴──────┴──────┘\n";

		final StringBuilder sb = new StringBuilder();

		sb.append(top);
		for (int i = 0; i < 9; i++) {
			sb.append("│");
			for (int j = 0; j < 9; j++) {
				sb.append(switch (board[j][i]) {
					case EMPTY -> empty;
					case X -> cross;
					case O -> circle;
				});
				if (j % 3 == 2) sb.append("│");
			}
			sb.append("\n");
			if (i == 8) break;
			if (i % 3 == 2) sb.append(mid);
		}
		sb.append(bot);

		return sb.toString();
	}

	/**
	 * We want to win
	 * @param us the player we want to win
	 * @return Inf if we win, -Inf if we loose or tie. 0 otherwise
	 */
	public int isGameWon(final Mark us) {
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
				switch (subBoardStatus(new Move(j, i), us)) {
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
		if (tmp1[0] + tmp1[1] + tmp1[2] == 3) return (us == Mark.X)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp1[3] + tmp1[4] + tmp1[5] == 3) return (us == Mark.X)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp1[6] + tmp1[7] + tmp1[8] == 3) return (us == Mark.X)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp1[0] + tmp1[3] + tmp1[6] == 3) return (us == Mark.X)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp1[1] + tmp1[4] + tmp1[7] == 3) return (us == Mark.X)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp1[2] + tmp1[5] + tmp1[8] == 3) return (us == Mark.X)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp1[0] + tmp1[4] + tmp1[8] == 3) return (us == Mark.X)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp1[2] + tmp1[4] + tmp1[6] == 3) return (us == Mark.X)?Integer.MAX_VALUE:Integer.MIN_VALUE;

		if (tmp2[0] + tmp2[1] + tmp2[2] == 3) return (us == Mark.O)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp2[3] + tmp2[4] + tmp2[5] == 3) return (us == Mark.O)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp2[6] + tmp2[7] + tmp2[8] == 3) return (us == Mark.O)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp2[0] + tmp2[3] + tmp2[6] == 3) return (us == Mark.O)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp2[1] + tmp2[4] + tmp2[7] == 3) return (us == Mark.O)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp2[2] + tmp2[5] + tmp2[8] == 3) return (us == Mark.O)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp2[0] + tmp2[4] + tmp2[8] == 3) return (us == Mark.O)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		if (tmp2[2] + tmp2[4] + tmp2[6] == 3) return (us == Mark.O)?Integer.MAX_VALUE:Integer.MIN_VALUE;
		// if every subboard is closed and we didn't/loose. This is a tie
		if (count == 9) return Integer.MIN_VALUE;

		return 0;
	}

	// retourne  100 pour une victoire
	//          -100 pour une défaite
	//           0   pour un match nul
	// Ne pas changer la signature de cette méthode
	private void computeSubBoard(final Mark mark) {
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
	}

	public int evaluateHeuristicCustom(Mark mark, Move move) {
		this.computeSubBoard(mark);
		int totalScore = 0;
		Mark opponent = mark.other();
		
		for (int subRow = 0; subRow < 3; subRow++) {
			for (int subCol = 0; subCol < 3; subCol++) {
				int baseRow = subRow * 3;
				int baseCol = subCol * 3;
				int subScore = evaluateSubBoard(this.board, baseRow, baseCol, mark, opponent);

				// reward the corners and the center
				if (subRow == 0 && subCol == 0) subScore *= 5;
				if (subRow == 0 && subCol == 2) subScore *= 5;
				if (subRow == 2 && subCol == 0) subScore *= 5;
				if (subRow == 2 && subCol == 2) subScore *= 5;
				if (subRow == 1 && subCol == 1) subScore *= 10;

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
		// Be extremly agressive with subboard control.
		if (countMark == 3) return  100000;
		if (countOpp == 3)  return -100000;

		// We want to reward partial control
		if (countMark == 2 && countEmpty == 1) return 500;
		if (countOpp  == 2 && countEmpty == 1) return -500;

		return 0;
	}
	

	private int evaluateGlobalBoard(Mark mark, Mark opponent, Move move) {
		
		int score = 0;
		
		// we want to control the corners, and the center
		if (resultboard[0][0] == mark) score += 500;
		if (resultboard[0][2] == mark) score += 500;
		if (resultboard[2][0] == mark) score += 500;
		if (resultboard[2][2] == mark) score += 500;
		if (resultboard[1][1] == mark) score += 100;


		if (resultboard[0][0] == opponent) score -= 500;
		if (resultboard[0][2] == opponent) score -= 500;
		if (resultboard[2][0] == opponent) score -= 500;
		if (resultboard[2][2] == opponent) score -= 500;
		if (resultboard[1][1] == opponent) score -= 100;

		return score;
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
						// var cond = i / 3 != vsMove.getRow() % 3
						// 	    || j / 3 != vsMove.getCol() % 3;
						var cond = !isSubBoardDone(new Move(j, i));
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
	 * Check if a specific sub board is won/lost/filled.
	 * @param move any place on the sub board to check
	 * @return true if the sub board can't be played on
	 */
	private Mark subBoardStatus(final Move move, final Mark us) {
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


		if (tmp1[0] + tmp1[1] + tmp1[2] == 3) return Mark.X;
		if (tmp1[3] + tmp1[4] + tmp1[5] == 3) return Mark.X;
		if (tmp1[6] + tmp1[7] + tmp1[8] == 3) return Mark.X;
		if (tmp1[0] + tmp1[3] + tmp1[6] == 3) return Mark.X;
		if (tmp1[1] + tmp1[4] + tmp1[7] == 3) return Mark.X;
		if (tmp1[2] + tmp1[5] + tmp1[8] == 3) return Mark.X;
		if (tmp1[0] + tmp1[4] + tmp1[8] == 3) return Mark.X;
		if (tmp1[2] + tmp1[4] + tmp1[6] == 3) return Mark.X;

		if (tmp2[0] + tmp2[1] + tmp2[2] == 3) return Mark.O;
		if (tmp2[3] + tmp2[4] + tmp2[5] == 3) return Mark.O;
		if (tmp2[6] + tmp2[7] + tmp2[8] == 3) return Mark.O;
		if (tmp2[0] + tmp2[3] + tmp2[6] == 3) return Mark.O;
		if (tmp2[1] + tmp2[4] + tmp2[7] == 3) return Mark.O;
		if (tmp2[2] + tmp2[5] + tmp2[8] == 3) return Mark.O;
		if (tmp2[0] + tmp2[4] + tmp2[8] == 3) return Mark.O;
		if (tmp2[2] + tmp2[4] + tmp2[6] == 3) return Mark.O;

		if (count == 9) return us.other();

		return Mark.EMPTY;
	}


	public int moveHeurisitic(final Mark us, final Move move) {
		// We want to punish a move that let the oponent play anywhere.
		var c = this.getPossibleMoves(move).size();

		if (c > 9) {
			return -1000;
		}

		// a move that that let the opponent play in a dominated field is a bad move
		if (c < 5) {
			var col = move.getCol()/3;
			var row = move.getRow()/3;

			var them = 0;
			var ours = 0;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (this.board[row*3+i][col*3+j] == us) {
						ours++;
					} else {
						them++;
					}
				}
			}
			if (them > 5) return -1000;
			if (ours > 5) return 1000;
		}
		return 0;
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
