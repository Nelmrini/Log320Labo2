package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * A classic tic tac toe board
 * This class role is to evaluate how good a position is on a subboard
 * For this, we generate every possible move for the opponent and
 * count how many will result in a win
 *
 * @author Brian Normant
 */
public class MiniBoard {

	private static final int WIN_FACTOR = 1;
	private static final int LOSS_FACTOR = 1;


	private final Mark[][] board;
	private final MiniBoard me;

	/**
	 * transform the full scale board depending of
	 * each subboard being won/lost/tie/free
	 * @param board the full scale board
	 */
	public MiniBoard(Board board) {
		this.board = new Mark[3][3];
		this.me = this;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Move m = new Move(i*3, j*3);
				this.board[i][j] = board.isSubBoardDone(m);
			}
		}
	}

	/**
	 * Select a subboard from the full scale board
	 * @param mark any place in the subboard
	 * @param board the full scale board
	 */
	public MiniBoard(Board board, Move mark) {
		int row = mark.getRow()/3;
		int col = mark.getCol()/3;
		this.me = this;
		this.board = new Mark[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				var m = new Move(row * 3 + i, col * 3 + j);
				this.board[i][j] = board.getMark(m);
			}
		}
	}

	/**
	 * Close this object
	 * @param of the object to close
	 */
	private MiniBoard(MiniBoard of) {
		this.board = new Mark[3][3];
		this.me = this;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.board[i][j] = of.board[i][j];
			}
		}
	}

	/**
	 * Get all possible move
	 * @return a list of all possible move
	 */
	public List<Move> getAvailableMoves() {
		var moves = new ArrayList<Move>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				switch (board[i][j]) {
					case X,O,TIE -> {}
					case EMPTY -> {moves.add(new Move(i, j));}
				}
			}
		}
		return moves;
	}

	/**
	 * Return the status of the board
	 * This means X if X victory
	 *            O if O victory
	 *            TIE if tie
	 *            EMPTY if the game is still going
	 * @return the status of the board
	 */
	public Mark boardStatus() {
		int count = 0;
		// For player Mark.X
		int[] tmp1 = new int[] {
			1, 1, 1,
			1, 1, 1,
			1, 1, 1
		};

		// For player Mark.O
		int[] tmp2 = new int[] {
			1, 1, 1,
			1, 1, 1,
			1, 1, 1
		};

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				switch (board[i][j]) {
					case O -> {
						tmp1[j * 3 + i] *= 0;
						count++;
					}
					case X -> {
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

		if (count == 9) return Mark.TIE;

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

		return Mark.EMPTY;
	}

	/**
	 * play a move on this subboard
	 * @param m where to play the move
	 * @param mark who is playing
	 */
	public void play(final Move m, final Mark mark) {
		this.board[m.getRow()][m.getCol()] = mark;
	}

	public void play(String str, Mark mark) {
		int row = str.charAt(0) - 'A';
		int col = str.charAt(1) - '1';
		this.board[row][col] = mark;
	}

	/**
	 * compute how many move result in a win/loss
	 * @return the sum of all win/loss
	 */
	public int heuristic(Mark player) {
		// a win is wonderfull
		if (this.boardStatus() == player) {
			return 100;
		}
		if (this.boardStatus() == player.other()) {
			return -100;
		}
		
		// We should minimize the opponent ability to win
		Integer s = ForkJoinPool.commonPool().submit(() ->
			me.getAvailableMoves()
				.stream()
				.parallel()
				.map(move -> {
					int score = 0;
					var m = new MiniBoard(me);
					m.play(move, player.other());
					if (m.boardStatus() == player) {
						score += 75;
					} else if (m.boardStatus() == player.other()) {
						score -= 100;
					}
					m = new MiniBoard(me);
					return score;
				})
				.reduce((a,b) -> a+b)
				.orElse(0)
		).join();

		if (s < 0) {
			s = -1 * (s*s);
		} else {
			s = s*s;
		}

		// controlling the center is good
		if (board[1][1] == player) {s += 3;}
		if (board[1][1] == player.other()) {s -= 3;}

		// controlling to opposing diagonals is good
		if (board[0][0] == player && board[2][2] == player) {s += 2;}
		if (board[2][0] == player && board[0][2] == player) {s += 2;}
		if (board[0][0] == player.other() && board[2][2] == player.other()) {s -= 2;}
		if (board[2][0] == player.other() && board[0][2] == player.other()) {s -= 2;}

		return s;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(board);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MiniBoard other = (MiniBoard) obj;
		if (!Arrays.deepEquals(this.board, other.board))
			return false;
		return true;
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				sb.append( switch(board[i][j]) {
					case X -> "✖";
					case O -> "●";
					case TIE -> "○";
					case EMPTY -> " ";
				});
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
