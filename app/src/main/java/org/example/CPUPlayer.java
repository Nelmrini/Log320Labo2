package org.example;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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
	private final int MAX_DEPTH = 7;

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
				var score = evaluateMove(m, board, 0, -100, 100);
				return new Tuple<>(m, score);
			}
		));
		}

		var results = futures.stream().map(t -> {
			try {
				return t.get();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}).collect(Collectors.toList());

		//return only the moves that have the same score
		// System.out.println("All moves: " + results);
		results.sort(Comparator.comparing(t -> ((Tuple<Move,Integer>) t).second()).reversed());
		// System.out.println("Sorted moves: " + results);
		List<Move> bestMoves = new ArrayList<>();
		int bestScore = 101;
		for (Tuple<Move, Integer> t : results) {
			if (bestScore == 101) {
				bestScore = t.second();
				bestMoves.add(t.first());
			} else if (t.second() == bestScore) {
				bestMoves.add(t.first());
			} else {
				break;
			}
		}
		System.out.println("Best moves: " + bestMoves);

		return bestMoves;

		//return bestMove;
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
			b.evaluate(player);
			int score = b.evaluateHeuristicCustom(mySide);
			// System.out.println("Score is " + score);
			return score;
		}

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

	/*
	public int minMax(final Board board, final int profondeur,
			final Mark turn, final Move lastMove) {
		int score = board.evaluate(mySide);
		if (score == 100 || score == -100) {
			//||board.isFull() pas oublier de ajouter le .isFull()
			return score;
		}
		if (profondeur == 0) {
			 
			if(mySide==Mark.X){
				return board.evaluateHeuristic(lastMove, mySide)-board.evaluateHeuristic(lastMove, Mark.O);
			} else {
				return board.evaluateHeuristic(lastMove, mySide)-board.evaluateHeuristic(lastMove, Mark.X);
			}
				
				//return (turn == mySide) ? board.evaluateHeuristic(lastMove, mySide) : -board.evaluateHeuristic(lastMove, mySide);
		}

		if (mySide == turn) {
			int highscore = Integer.MIN_VALUE;
			for (Move move: board.getPossibleMoves(lastMove)) {
				board.play(move, turn);
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
	*/


	/**
	 * Retourne la liste des coups possibles.  Cette liste contient
	 * plusieurs coups possibles si et seuleument si plusieurs coups
	 * ont le même score.
	 * @param board The board to play on.
	 * @param lastMove The last move played by the opponent.
	 * @return the best possible move (in a list if they have the same score)
	 */
	/*
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
	*/
}
