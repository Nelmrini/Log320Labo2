package org.example;

import java.util.ArrayList;

// IMPORTANT: Il ne faut pas changer la signature des méthodes
// de cette classe, ni le nom de la classe.
// Vous pouvez par contre ajouter d'autres méthodes (ça devrait
// être le cas)

/**
 * The AI that determines the best move to play in any scenario.
 * @author Nabil Elmrini
 */
public final class CPUPlayer {

	// Contient le nombre de noeuds visités (le nombre
	// d'appel à la fonction MinMax ou Alpha Beta)
	// Normalement, la variable devrait être incrémentée
	// au début de votre MinMax ou Alpha Beta.
	/** How many nodes where explored. **/
	private int numExploredNodes;
	/** Who's is the AI on the board. **/
	private Mark mySide;
	private final int MAX_DEPTH = 4;

	// Le constructeur reçoit en paramètre le
	// joueur MAX (X ou O)
	public CPUPlayer(final Mark cpu) {
		this.mySide = cpu;
	}

	// Ne pas changer cette méthode
	public int getNumOfExploredNodes() {
		return numExploredNodes;
	}

	/**
	 * Retourne la liste des coups possibles.  Cette liste contient
	 * plusieurs coups possibles si et seuleument si plusieurs coups
	 * ont le même score.
	 * @param board The board to play on.
	 * @param lastMove The last move played by the opponent.
	 * @return the best play possible
	 */
	public ArrayList<Move> getNextMoveMinMax(
			final Board board,
			final Move lastMove) {
		numExploredNodes = 0;
		ArrayList<Move> meilleurMoves = new ArrayList<>();
		int highscore = Integer.MIN_VALUE;
		for (Move move: board.getPossibleMoves(lastMove)) {
			board.play(move, this.mySide);
			numExploredNodes++;
			//this.turn=board.flip(turn);
			int score = minMax(board, MAX_DEPTH, board.flip(mySide), move);
			//this.turn=board.flip(turn);
			board.undo(move);

			if (score > highscore) {
				highscore = score;
				meilleurMoves.clear();
				meilleurMoves.add(move);
			} else if (highscore == score) {
				meilleurMoves.add(move);
			}

		}

		return meilleurMoves;
	}

	public int minMax(final Board board, final int profondeur,
			final Mark turn, final Move lastMove) {
		int score = board.evaluate(mySide);
		if (profondeur == 0) return score;
		if (score == 100 || score == -100) {
			//||board.isFull() pas oublier de ajouter le .isFull()
			return score;
		}

		if (mySide == turn) {
			int highscore = Integer.MIN_VALUE;
			for (Move move: board.getPossibleMoves(lastMove)) {
				board.play(move, this.mySide);
				this.numExploredNodes++;
				score = minMax(board, profondeur - 1, board.flip(turn), move);
				board.undo(move);
				if (score > highscore) {
					highscore = score;
				}

			}
			return highscore;
		} else {
			int lowscore = Integer.MAX_VALUE;
			for (Move move: board.getPossibleMoves(lastMove)) {
				board.play(move, this.mySide);
				this.numExploredNodes++;
				score = minMax(board, profondeur - 1, board.flip(turn), move);
				board.undo(move);
				if (score < lowscore) {
					lowscore = score;
				}

			}
			return lowscore;
		}
	}


	/**
	 * Retourne la liste des coups possibles.  Cette liste contient
	 * plusieurs coups possibles si et seuleument si plusieurs coups
	 * ont le même score.
	 * @param board The board to play on.
	 * @param lastMove The last move played by the opponent.
	 * @return the best possible move (in a list if they have the same score)
	 */
	public ArrayList<Move> getNextMoveAB(
			final Board board, final Move lastMove) {
		numExploredNodes = 0;
		ArrayList<Move> meilleurMoves = new ArrayList<>();
		int highscore = Integer.MIN_VALUE;
		for (Move move: board.getPossibleMoves(lastMove)) {
			board.play(move, this.mySide);
			numExploredNodes++;

			//this.turn=board.flip(turn);

			int score = elagageAB(board, 0,
					Integer.MIN_VALUE, Integer.MAX_VALUE,
					board.flip(this.mySide), move);

			//this.turn=board.flip(turn);
			board.undo(move);
			if (score > highscore) {
				highscore = score;
				meilleurMoves.clear();
				meilleurMoves.add(move);
			} else if (highscore == score) {
				meilleurMoves.add(move);
			}
		}
		return meilleurMoves;
	}

	public int elagageAB(final Board board,
			final int profondeur, final int alpha,
			final int beta, final Mark turn, final Move lastMove) {
		int score = board.evaluate(this.mySide);

		if (score == 100 || score == -100) {
			//||board.isFull() pas oublier de ajouter le .isFull()
			return score;
		}

		if (mySide == turn) {
			int highscore = Integer.MIN_VALUE;
			for (Move move : board.getPossibleMoves(lastMove)) {
				board.play(move, turn);
				this.numExploredNodes++;
				// this.turn=board.flip(turn);
				score = elagageAB(board, profondeur + 1, alpha, beta,
						board.flip(turn), move);
				// this.turn=board.flip(turn);
				board.undo(move);
				highscore = Math.max(highscore, score);
				var alphap = Math.max(alpha, highscore);
				if (beta <= alphap) {
					return highscore;
				}
			}

			return highscore;
		} else {
			int lowscore = Integer.MAX_VALUE;
			for (Move move: board.getPossibleMoves(lastMove)) {
				board.play(move, turn);
				this.numExploredNodes++;
				//this.turn=board.flip(turn);
				score = elagageAB(board, profondeur + 1, alpha, beta,
						board.flip(turn), move);
				//this.turn=board.flip(turn);
				board.undo(move);
				lowscore = Math.min(score, lowscore);
				var betap = Math.min(lowscore, beta);
				if (betap <= alpha) {
					return lowscore;
				}
			}

			return lowscore;
		}
	}
}
