package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

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
	private final int MAX_DEPTH = 6;

	private ExecutorService executor;

	/**
	 * Initialise le joueur CPU.
	 * De plus initialize l'executor pour le multithreading
	 * @param cpu Le joueur dont on cherche a maximizer le score
	 */
	public CPUPlayer(final Mark cpu) {
		this.mySide = cpu;
		int cores = Runtime.getRuntime().availableProcessors();
		System.out.println("This system has " + cores + " cores");
		this.executor = Executors.newFixedThreadPool(cores); // who even runs less than a dual core?
	}

	public void shutdownExecutor() {
		this.executor.shutdown();
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
	public List<Move> getNextMoveMinMax(
			final Board board,
			final Move lastMove) {

		var possiblesModes = board.getPossibleMoves(lastMove);

		var sortedMoves = ForkJoinPool.commonPool().submit(() ->
			possiblesModes
				.stream()
				.parallel()
				.map(move -> new Tuple<>(move, minMax(board, MAX_DEPTH, mySide.other(), move)))
				.sorted(Comparator.comparingInt(Tuple::second))
				.toList()
		).join();

		Integer highscore = null;
		List<Move> bestMoves = new ArrayList<Move>();
		for (var t : sortedMoves.reversed()) {
			if (highscore == null) {
				highscore = t.second();
			}
			if (highscore == t.second()) {
				bestMoves.add(t.first());
			}
		}
		System.out.println(bestMoves);
		// At this point bestMoves contains the best moves According to MinMax

		// Now we sort the moves according to evaluator function
		bestMoves = bestMoves.stream()
			.sorted(Comparator.comparingInt(m -> board.evaluateHeuristicCustom(mySide, m)))
			.toList();
		
		System.out.println(bestMoves);
		System.out.println("Best Move is " + bestMoves.getLast());

		// And we select the best one
		return List.of(bestMoves.getLast());
	}

	public int minMax(final Board board, final int profondeur,
			final Mark turn, final Move lastMove) {
		int score = board.evaluate(mySide);
		if (score == 100 || score == -100) {
			score*=100 ;
			//||board.isFull() pas oublier de ajouter le .isFull()
			return score;
		}
		if (profondeur == 0) {
			/*
			if(mySide==Mark.X){
				return board.evaluateHeuristic(lastMove, mySide)-board.evaluateHeuristic(lastMove, Mark.O);
			} else {
				return board.evaluateHeuristic(lastMove, mySide)-board.evaluateHeuristic(lastMove, Mark.X);
			}
			*/
			
			return board.evaluateHeuristicCustom(mySide, lastMove);
		}

		if (mySide == turn) {
			int highscore = Integer.MIN_VALUE;
			for (Move move: board.getPossibleMoves(lastMove)) {
				board.play(move, turn);
				this.numExploredNodes++;
				score = minMax(board, profondeur - 1, board.flip(turn), move);
				//System.out.println("Profondeur: " + profondeur + " MAX ");
				board.undo(move);
				if (score > highscore) {
					highscore = score;

				}
			}
			return highscore;
		} else {
			int lowscore = Integer.MAX_VALUE;
			for (Move move: board.getPossibleMoves(lastMove)) {
				board.play(move, turn);
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
