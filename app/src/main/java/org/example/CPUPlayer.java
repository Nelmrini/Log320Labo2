package org.example;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
	private final long MAX_TIME_MILLI = 3000 - 100;
	private Instant IA_STARTED;

	/**
	 * Initialise le joueur CPU.
	 * De plus initialize l'executor pour le multithreading
	 * @param cpu Le joueur dont on cherche a maximizer le score
	 */
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
	public List<Move> getNextMoveMinMax(
			final Board board,
			final Move lastMove) {

		IA_STARTED = Instant.now();

		var possiblesModes = board.getPossibleMoves(lastMove);

		var sortedMoves = ForkJoinPool.commonPool().submit(() ->
			possiblesModes
				.stream()
				.parallel()
				.map(move -> {
					// this b is a new Board, no race condition should occur
					var b = board.immutablePlay(move, mySide);
					var score = minMaxMulti(
							b,
							1,
							mySide.other(),
							move
							);
					System.out.println("max Profondeur " + score.second());
					return new Tuple<>(move, score.first());
				})
				.sorted(Comparator.comparingInt(Tuple::second))
				.toList()
		).join();

		for (var move : sortedMoves) {
			System.out.println(move.first() + " " + move.second());
		}

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
		// At this point bestMoves contains the best moves According to MinMax

		// Now we sort the moves according to evaluator function
		bestMoves = bestMoves.stream()
			.sorted(Comparator.comparingInt(m -> board.evaluateHeuristicCustom(mySide, m)))
			.toList();
		

		// And we select the best one

		System.out.printf("We had %d ms left\n", 3000 - (Duration.between(IA_STARTED, Instant.now()).toMillis()));

		return List.of(bestMoves.getLast());
	}

	public Tuple<Integer, Integer> minMaxMulti(final Board board, final int profondeur,
			final Mark turn, final Move lastMove) {

		// time cutoff
		// This replace the depth cutoff as we go as depth as possible
		if (Duration.between(IA_STARTED, Instant.now()).toMillis() > MAX_TIME_MILLI) {
			var score = board.isGameWon(mySide);
			if (score != 0) return new Tuple<>(score, profondeur);
			var h1 = board.evaluateHeuristicCustom(mySide, lastMove);
			// var h2 = board.moveHeurisitic(mySide, lastMove);
			// var d  = profondeur+1; d *= d;
			return new Tuple<>(h1, profondeur);
		}

		var score = board.isGameWon(mySide);
		if (score != 0) {
			return new Tuple<>(score, profondeur);
		}

		var possibleMoves = board.getPossibleMoves(lastMove);
		var sortedScores = ForkJoinPool.commonPool().submit(() ->
			possibleMoves
				.stream()
				.parallel()
				.map(move -> {
					// this b is a new Board, no race condition should occur
					var b = board.immutablePlay(move, turn);
					return minMaxMulti(
							b,
							profondeur + 1,
							turn.other(),
							move
							);
				})
				.sorted(Comparator.comparingInt(Tuple::first))
				.toList()
		).join();

		if (turn != mySide) {
			return sortedScores.getFirst();
		} else {
			return sortedScores.getLast();
		}
	}
}
