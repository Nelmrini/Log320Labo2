package org.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class MiniBoardTest {

	@Test
	public void equalsTest() {
		var b1 = new MiniBoard(new Board());
		var b2 = new MiniBoard(new Board());

		assertEquals(b1, b2);
	}
	
	@Test
	public void equalsTest2() {
		var b1 = new MiniBoard(new Board());
		b1.play("A1", Mark.X);
		b1.play("A2", Mark.X);
		b1.play("A3", Mark.X);
		
		var b2 = new MiniBoard(new Board());
		b2.play("A1", Mark.O);
		b2.play("A2", Mark.O);
		b2.play("A3", Mark.O);

		assertNotEquals(b1, b2);
	}

	@Test
	public void equalsTest3() {
		var b1 = new MiniBoard(new Board());
		b1.play("A1", Mark.X);
		b1.play("A2", Mark.X);
		b1.play("A3", Mark.X);
		var b2 = new MiniBoard(new Board());
		b2.play("A1", Mark.X);
		b2.play("A2", Mark.X);
		b2.play("A3", Mark.X);
		b2.play("C3", Mark.X);

		assertNotEquals(b1, b2);
	}

	@Test
	public void equalsTest4() {
		var b1 = new MiniBoard(new Board());
		b1.play("A1", Mark.X);
		b1.play("A2", Mark.X);
		b1.play("A3", Mark.X);
		
		var b2 = new MiniBoard(new Board());
		b2.play("A1", Mark.X);
		b2.play("A2", Mark.X);
		b2.play("A3", Mark.X);

		assertEquals(b1, b2);
	}

	@Test
	public void newBoardBigTest() {
		var board = new Board();
		board.play(Board.strToMov("A1"), Mark.X);
		board.play(Board.strToMov("B1"), Mark.X);
		board.play(Board.strToMov("C1"), Mark.X);
		board.play(Board.strToMov("D4"), Mark.X);
		board.play(Board.strToMov("E5"), Mark.X);
		board.play(Board.strToMov("F6"), Mark.X);
		board.play(Board.strToMov("H7"), Mark.X);
		board.play(Board.strToMov("H8"), Mark.X);
		board.play(Board.strToMov("H9"), Mark.X);
		var test = new MiniBoard(board);


		var expected = new MiniBoard(board);
		expected.play("A3", Mark.X);
		expected.play("B2", Mark.X);
		expected.play("C1", Mark.X);


		assertEquals(expected, test);
	}

	public void newBoardBigTest2() {
		var board = new Board();
		board.play(Board.strToMov("A1"), Mark.X);
		board.play(Board.strToMov("B1"), Mark.X);
		board.play(Board.strToMov("C1"), Mark.X);
		board.play(Board.strToMov("D4"), Mark.X);
		board.play(Board.strToMov("E5"), Mark.X);
		board.play(Board.strToMov("F6"), Mark.X);
		board.play(Board.strToMov("H7"), Mark.X);
		board.play(Board.strToMov("H8"), Mark.X);
		board.play(Board.strToMov("H9"), Mark.X);
		var test = new MiniBoard(board);


		var expected = new MiniBoard(board);
		expected.play("A3", Mark.X);
		expected.play("B2", Mark.X);
		expected.play("C1", Mark.X);
		expected.play("A2", Mark.X);


		assertNotEquals(expected, test);
	}

	public void newBoardBigTest3() {
		var board = new Board();
		board.play(Board.strToMov("A1"), Mark.X);
		board.play(Board.strToMov("A3"), Mark.X);
		board.play(Board.strToMov("C1"), Mark.X);
		board.play(Board.strToMov("C3"), Mark.X);
		board.play(Board.strToMov("B2"), Mark.X);
		board.play(Board.strToMov("A2"), Mark.O);
		board.play(Board.strToMov("B1"), Mark.O);
		board.play(Board.strToMov("B3"), Mark.O);
		board.play(Board.strToMov("C2"), Mark.O);
		var test = new MiniBoard(board);


		var expected = new MiniBoard(board);
		expected.play("A3", Mark.TIE);

		assertEquals(expected, test);
	}
	
	public void newBoardBigTest4() {
		var board = new Board();
		board.play(Board.strToMov("G6"), Mark.X);
		board.play(Board.strToMov("I4"), Mark.O);
		var test = new MiniBoard(board, Board.strToMov("G6"));

		var expected = new MiniBoard(board);
		expected.play("A1", Mark.X);
		expected.play("C3", Mark.O);

		assertEquals(expected, test);
	}

	@Test
	public void statusTest1() {
		var test = new MiniBoard(new Board());
		test.play("A1", Mark.X);
		test.play("A2", Mark.X);
		test.play("A3", Mark.X);

		assertEquals(Mark.X, test.boardStatus());
	}

	@Test
	public void heuristicTest1() {
		var test = new MiniBoard(new Board());
		test.play("A1", Mark.X);
		test.play("A2", Mark.X);

		assertEquals(1, test.heuristic(Mark.X));
	}
}
