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
	private final long MAX_TIME_MILLI = 3000;
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
		cuffoff = 0;

		var possiblesModes = board.getPossibleMoves(lastMove);
		var moves = ForkJoinPool.commonPool().submit(() ->
			possiblesModes
				.stream()
				.parallel()
				.map(move -> {
					// this b is a new Board, no race condition should occur
					var pool = new ForkJoinPool();
					var b = board.immutablePlay(move, mySide);
					var score = minMaxMulti(
							b,
							1,
							mySide.other(),
							move,
							pool
							);
					System.out.println("current depth " + score.second());
					pool.shutdown();
					return new Tuple<>(move, score.first());
				})
				.parallel()
				.sorted(Comparator.comparingInt(Tuple::second))
				.toList()
		).join();

		System.out.println("CUTOFF: " + cuffoff);
		var sorted = moves;

		for (var move : sorted) {
			System.out.println(move.first() + " " + move.second());
		}

		Integer highscore = null;
		List<Move> bestMoves = new ArrayList<Move>();
		for (var t : sorted.reversed()) {
			if (highscore == null) {
				highscore = t.second();
			}
			if (highscore == t.second()) {
				bestMoves.add(t.first());
			}
		}
		System.out.println("HIGHSCORE " + highscore);
		System.out.printf("We had %d ms left\n", 3000 - (Duration.between(IA_STARTED, Instant.now()).toMillis()));

		var m = bestMoves
			.stream()
			.map(n -> new Tuple<>(n,
						board.evaluateHeuristicCustom(mySide, n) +
						board.moveHeurisitic(mySide, n)
						))
			.max(Comparator.comparingInt(Tuple::second))
			.map(Tuple::first)
			.orElse(bestMoves.get(0));


		return List.of(m);
	}

	private int cuffoff = 0;

	public Tuple<Integer, Integer> minMaxMulti(final Board board, final int profondeur,
			final Mark turn, final Move lastMove, ForkJoinPool pool) {
		Integer score = board.isGameWon(mySide);
		if (score != 0) {
			// score = (int)(score / Math.pow(profondeur, 2));
			return new Tuple<>(score, profondeur);
		}


		var now = Instant.now();
		var dur = Duration.between(IA_STARTED, now);
		// We squeze as much depth as possible depending how many possition
		// we have to explore
		// we dynamically ajust the max depth
		if (dur.toMillis() >= MAX_TIME_MILLI || profondeur > 7) {
			var h1 = board.evaluateHeuristicCustom(mySide, lastMove);
			// h1 += board.randomHeuristic(mySide, lastMove, pool);
			return new Tuple<>(h1, profondeur);
		}
		
		var possibleMoves = board.getPossibleMoves(lastMove);
		var ans = pool.submit(
				() -> possibleMoves
				.stream()
				.parallel()
				.map(move -> {
					// this b is a new Board, no race condition should occur
					var b = board.immutablePlay(move, turn);
					return minMaxMulti(
							b,
							profondeur + 1,
							turn.other(),
							move,
							pool
							);
				})
				.parallel()
				.sorted(Comparator.comparingInt(Tuple::first))
				.toList()
		).join();
		if (turn == mySide) {
			// return ans.getFirst();
			return ans.getLast();
		} else {
			return ans.getFirst();
			// return ans.getLast();
		}
	}
}
