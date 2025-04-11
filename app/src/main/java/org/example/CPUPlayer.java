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
 * @author Brian Normant
 */
public final class CPUPlayer {

	// Contient le nombre de noeuds visités (le nombre
	// d'appel à la fonction MinMax ou Alpha Beta)
	// Normalement, la variable devrait être incrémentée
	// au début de votre MinMax ou Alpha Beta.
	/** How many nodes where explored. **/
	private int numExploredNodes;
	
	/** Who's is the AI on the board. **/
	private final Mark mySide;
	/** How much time the AI has to play **/
	private final long MAX_TIME_MILLI = 3000 - 250;
	/** When did the AI start to play **/
	private Instant IA_STARTED;

	/**
	 * Initialise le joueur CPU.
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
	 * Retourne le meilleur coup possible.
	 * Si MinMax renvoie plus qu'un coup
	 * Alors nous prenons le meuilleur selon la fonction d'évaluation
	 * @param board The board to play on.
	 * @param lastMove The last move played by the opponent.
	 * @return the best play possible
	 */
	public List<Move> getNextMoveMinMax(
			final Board board,
			final Move lastMove) {
		IA_STARTED = Instant.now();

		//MinMax
		final var possiblesModes = board.getPossibleMoves(lastMove);
		final var sortedMoves = ForkJoinPool.commonPool().submit(() ->
			possiblesModes
				.stream()
				.parallel()
				.map(move -> {
					// this b is a new Board, no race condition should occur
					final var b = board.immutablePlay(move, mySide);
					final var score = minMaxMulti( b, mySide.other(), move, 0);
					return new Tuple<>(move, score);
				})
				.sorted(Comparator.comparingInt(Tuple::second))
				.toList()
		).join();

		// Extract the best moves
		Integer highscore = null;
		List<Move> bestMoves = new ArrayList<Move>();
		for (final var t : sortedMoves.reversed()) {
			if (highscore == null) {
				highscore = t.second();
			}
			if (highscore == t.second()) {
				bestMoves.add(t.first());
			}
		}

		// Sort according to evaluator
		bestMoves = bestMoves.stream()
			.sorted(Comparator.comparingInt(m -> board.evaluateHeuristicCustom(mySide, m)))
			.toList();
		

		System.out.printf("We had %d ms left\n",
				3000 - (Duration.between(IA_STARTED, Instant.now()).toMillis()));

		// And we select the best one
		return List.of(bestMoves.getLast());
	}

	/**
	 * Fully Multithreaded MinMax
	 * Each node is explored with as many thread as possible
	 * When the time is about to run out, we compute the value of the current node
	 * And sort them with minmax until the recursion stops
	 * @param board The board to play on.
	 * @param turn The side to play
	 * @param lastMove The last move played by the opponent.
	 */
	public Integer minMaxMulti(final Board board, final Mark turn, final Move lastMove,
			int depth) {
		// time cutoff
		var dur = Duration.between(IA_STARTED, Instant.now()).toMillis();
		if (depth >= 5 || dur >= MAX_TIME_MILLI) {
			return board.evaluateHeuristicCustom(mySide, lastMove);
		}
		
		// win/loss cutoff
		final var score = board.evaluate(mySide);
		if (Math.abs(score) == 100) return score*100;

		final var possibleMoves = board.getPossibleMoves(lastMove);

		// Recursive multithreading
		final var sortedScores = ForkJoinPool.commonPool().submit(() ->
			possibleMoves
				.stream()
				.parallel()
				.map(move -> {
					// this b is a new Board, no race condition should occur
					final var b = board.immutablePlay(move, turn);
					return minMaxMulti(
							b,
							turn.other(),
							move,
							depth + 1
							);
				})
				.sorted()
				.toList()
		).join();

		// We choose the worst move for the opponent
		// And the best for for us
		if (turn != mySide) {
			return sortedScores.getFirst();
		} else {
			return sortedScores.getLast();
		}
	}
}
