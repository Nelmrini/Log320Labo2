package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

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
	private final int MAX_DEPTH = 8;

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
		this.executor = Executors.newFixedThreadPool(cores - 1); // who even runs less than a dual core?
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
		
		var possibleMoves = board.getPossibleMoves(lastMove);
		// BlockingQueue<Move> workQueue = new LinkedBlockingQueue<>(possibleMoves);

		List<Future<Tuple<Move, Integer>>> futures = new ArrayList<>();

		// dispatch the evaluation of the moves to the workers
		for (Move m : possibleMoves) {
			futures.add(executor.submit(() -> {
				// var score = evaluateMove(m, board, 0, -100, 100);
				var score = monteCarlo(m, board, mySide);
				return new Tuple<>(m, score);
			}
		));
		}

		int bestScore = -100;
		var bestMove = new ArrayList<Move>();

		// dispatch the tree expansion to the workers
		try {
			for (Future<Tuple<Move, Integer>> f : futures) {
				if (f.get().second() > bestScore) {
					bestScore = f.get().second();
					bestMove.add(f.get().first());
				}
			}
		} catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }

		return bestMove;
	}

	/**
	 * Multithreaded implementation of
	 * min max with alpha beta pruning
	 * @param move the move to evaluate
	 * @param board the board to play on
	 * @param depth current depth of the search
	 * @param alpha the alpha value, starting at -100
	 * @param beta the beta value, starting at 100
	 */
	private Integer evaluateMove(final Move move, final Board board, final int depth, int alpha, int beta) {
		Board b = board.immutablePlay(move, this.mySide);
		Mark player = (depth % 2 == 0)?mySide:mySide.other();
		Mark done = b.isBoardDone();

		if (done == Mark.TIE) {
			return 0;
		} else if (done == player) {
			return 100;
		} else if (done == player.other()) {
			return -100;
		}

		if (depth == MAX_DEPTH) {
			int score = b.evaluate(player);
			return score;
		};

		List<Move> possibleMoves = b.getPossibleMoves(move);

		int bestScore = (player == this.mySide)?-100:100;

		for (Move m : possibleMoves) {
			var r = evaluateMove(m, b, depth + 1, alpha, beta);
			if (player == this.mySide) {
				// We want to maximize the score
				bestScore = Math.max(bestScore, r);
				alpha = Math.max(alpha, bestScore);
				if (alpha >= beta) break;
			} else {
				// We want to minimize the score
				bestScore = Math.min(bestScore, r);
				beta = Math.min(beta, bestScore);
				if (beta <= alpha) break;
			}
		}

		return bestScore;
	}


	/** how many play in each game **/
	private static final int MONTE_CARLO_ITERATIONS = 50;
	/** how many games **/
	private static final int MONTE_CARLO_PLAYOUTS = 10000;
	private Integer monteCarlo(final Move move, final Board board, final Mark player) {
		List<Future<Integer>> futures = new ArrayList<>();

		for (int i = 0; i < MONTE_CARLO_PLAYOUTS; i++) {
			futures.add(executor.submit(() -> {
				Random rand = new Random();
				Board localBoard = new Board(board);
				localBoard.play(move, player);
				Move localLstMove = move;
				for (int j = 0; j < MONTE_CARLO_ITERATIONS; j++) {
					Mark currentPlayer = localBoard.nextPlayer();
					var currentMoves = localBoard.getPossibleMoves(localLstMove);
					if (currentMoves.size() == 0) break;
					var currentMove = currentMoves.get(rand.nextInt(currentMoves.size()));
					localLstMove = currentMove;
					localBoard.play(currentMove, currentPlayer);
					if (localBoard.isBoardDone() != Mark.EMPTY) break;
				}
				var done = localBoard.isBoardDone();
				if (done == player) return 100;
				if (done == player.other()) return -100;
				return 0;
			}));
		}

		int sum = 0;
		try {
			for (Future<Integer> f : futures) {
				sum += f.get();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return sum / MONTE_CARLO_PLAYOUTS;
	}
}
