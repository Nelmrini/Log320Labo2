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
		this.board = previous.board.clone();
	}

	// Place la pièce 'mark' sur le plateau, à la
	// position spécifiée dans Move
	//
	// Ne pas changer la signature de cette méthode
	public void play(final Move m, final Mark mark) {
		board[m.getRow()][m.getCol()] = mark;
	}

	public void undo(final Move m) {
		board[m.getRow()][m.getCol()] = Mark.EMPTY;
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

	public int evaluatethree(Mark[][] board, final Mark mark, int k, int w){
		int result=0;
		for (int i = 0; i < 3; i++) {
			if (board[k+i][w+0] == board[k+i][w+1] && board[k+i][w+1] == board[k+i][w+2] && board[k+i][w+0] != Mark.EMPTY) {
				//return (board[i][0] == mark) ? 100 : -100;
				//resultboard[k/3][w/3]=board[k+i][w+0];
				if(board[k+i][w+0]==mark){
					result+=10;
				}
			}
		}
	
		for (int j = 0; j < 3; j++) {
			if (board[k+0][w+j] == board[k+1][w+j] && board[k+1][w+j] == board[k+2][w+j] && board[k+0][w+j] != Mark.EMPTY) {
				//return (board[0][j] == mark) ? 100 : -100;
				//resultboard[k/3][w/3]=board[k+0][w+j];
				if(board[k+0][w+j]==mark){
					result+=10;
				}
			}
		}
	
		if (board[k+0][w+0] == board[k+1][w+1] && board[k+1][w+1] == board[k+2][w+2] && board[k+0][w+0] != Mark.EMPTY) {
			//return (board[0][0] == mark) ? 100 : -100;
			//resultboard[k/3][w/3]=board[k+0][w+0];
			if(board[k+0][w+0]==mark){
				result+=10;
			}
		}
	
		if (board[k+0][w+2] == board[k+1][w+1] && board[k+1][w+1] == board[k+2][w+0] && board[k+0][w+2] != Mark.EMPTY) {
			//return (board[0][2] == mark) ? 100 : -100;
			//resultboard[k/3][w/3]=board[k+0][w+2];
			if(board[k+0][w+2]==mark){
				result+=10;
			}
		}
		return result;
	}

	public int evaluateHeuristic(Move move, final Mark mark){
		int result = 0;
		result+=evaluateHeuristicBigBoard(move, mark)*4;
		if(move.getCol()%3==1&&move.getRow()%3==1){
			result+=10;
		}else if(move.getRow()%3!=1&&move.getCol()%3!=1){
			result+=5;
		}
		for (int i = move.getRow()-move.getRow()%3; i < move.getRow()-move.getRow()%3+3; i++) {
			for(int j = move.getCol()-move.getCol()%3; j < move.getCol()-move.getCol()%3+3; j++){
				if(board[i][j]==mark&&(i==move.getRow()||j==move.getCol())){
					if(board[i][j]==mark){
						result+=1;
					}
					if(board[i][j]!=mark && board[i][j]!=Mark.EMPTY){
						result-=1;
					}
				} 
				if(board[i][j]==mark && i==j && move.getRow()==move.getCol()){
					if(board[i][j]==mark){
						result+=1;
					}
					if(board[i][j]!=mark && board[i][j]!=Mark.EMPTY){
						result-=1;
					}
				}
				if(board[i][j]==mark && (i%3)+(j%3)==2 && ((move.getRow()%3)+(move.getCol()%3)==2)){
					if(board[i][j]==mark){
						result+=1;
					}
					if(board[i][j]!=mark && board[i][j]!=Mark.EMPTY){
						result-=1;
					}
				}
			}
		}
		if(mark==Mark.X){
			board[move.getRow()][move.getCol()]=Mark.O;
			result+=evaluatethree(board, Mark.O, move.getRow()-move.getRow()%3, move.getCol()-move.getCol()%3);
			board[move.getRow()][move.getCol()]=Mark.X;
			result+=evaluatethree(board, Mark.X, move.getRow()-move.getRow()%3, move.getCol()-move.getCol()%3)*5;
		} else {
			board[move.getRow()][move.getCol()]=Mark.X;
			result+=evaluatethree(board, Mark.X, move.getRow()-move.getRow()%3, move.getCol()-move.getCol()%3);
			board[move.getRow()][move.getCol()]=Mark.O;
			result+=evaluatethree(board, Mark.O, move.getRow()-move.getRow()%3, move.getCol()-move.getCol()%3)*5;

		}

		
		return result;
	}

	public int evaluateHeuristicBigBoard(Move move, final Mark mark){
		int result = 0;
		if(move.getCol()%3==1&&move.getRow()%3==1){
			result+=10;
		}else if(move.getRow()%3!=1&&move.getCol()%3!=1){
			result+=5;
		}
		if(resultboard[move.getRow()%3][move.getCol()%3]!=Mark.EMPTY){
			result-=5;
		}
		for (int i = 0; i < resultboard.length; i++) {
			for(int j = 0; j <  resultboard[0].length; j++){
				if((i==move.getRow()%3||j==move.getCol()%3)){
					if(resultboard[i][j]==mark){
						result+=1;
					}
					if(resultboard[i][j]!=mark && resultboard[i][j]!=Mark.EMPTY){
						result-=1;
					}
				} 
				if(i==j && move.getRow()%3==move.getCol()%3){
					if(resultboard[i][j]==mark){
						result+=1;
					}
					if(resultboard[i][j]!=mark && resultboard[i][j]!=Mark.EMPTY){
						result-=1;
					}
				}
				if((i%3)+(j%3)==2 && ((move.getRow()%3)+(move.getCol()%3)==2)){
					if(resultboard[i][j]==mark){
						result+=1;
					}
					if(resultboard[i][j]!=mark && resultboard[i][j]!=Mark.EMPTY){
						result-=1;
					}
				}
			}
		}
		if(mark==Mark.X){
			Mark tempMark = resultboard[move.getRow()%3][move.getCol()%3];
			resultboard[move.getRow()%3][move.getCol()%3]=Mark.O;
			result-=evaluatethree(resultboard, Mark.O, 0, 0)*3;
			resultboard[move.getRow()%3][move.getCol()%3]=Mark.X;
			result+=evaluatethree(resultboard, Mark.X, 0, 0);
			resultboard[move.getRow()%3][move.getCol()%3] = tempMark;
		} else {
			Mark tempMark = resultboard[move.getRow()%3][move.getCol()%3];
			resultboard[move.getRow()%3][move.getCol()%3]=Mark.X;
			result-=evaluatethree(resultboard, Mark.X, 0, 0)*3;
			resultboard[move.getRow()%3][move.getCol()%3]=Mark.O;
			result+=evaluatethree(resultboard, Mark.O, 0, 0);
			resultboard[move.getRow()%3][move.getCol()%3] = tempMark;

		}

		
		return result;
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
