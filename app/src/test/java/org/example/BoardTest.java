package org.example;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

public class BoardTest {
	@Test
	public void testStrToInd() {
		assertArrayEquals(new int[]{0, 0}, Board.strToInd("A9"));
		assertArrayEquals(new int[]{8, 8}, Board.strToInd("I1"));
		assertArrayEquals(new int[]{3, 5}, Board.strToInd("D4"));
	}

	@Test
	public void testToString() {
		String expected = ""
			+ "|------|------|------|\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|------|------|------|\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|------|------|------|\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|------|------|------|\n";
		assertEquals(expected, new Board().toString());

		var board = new Board();
		board.play(new Move(0, 0), Mark.X);
		board.play(new Move(8, 8), Mark.O);
		board.play(new Move(3, 5), Mark.X);
		expected = ""
			+ "|------|------|------|\n"
			+ "|❌    |      |      |\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|------|------|------|\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|      |❌    |      |\n"
			+ "|------|------|------|\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|      |      |    🔵|\n"
			+ "|------|------|------|\n";
		assertEquals(expected, board.toString());

		expected = ""
			+ "|------|------|------|\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|    ❌|    ❌|    ❌|\n"
			+ "|------|------|------|\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|      |      |      |\n"
			+ "|------|------|------|\n"
			+ "|      |      |🔵🔵🔵|\n"
			+ "|      |      |      |\n"
			+ "|      |      |    ❌|\n"
			+ "|------|------|------|\n";
		board = new Board();
		board.play(Board.strToMov("C7"), Mark.X);
		board.play(Board.strToMov("F7"), Mark.X);
		board.play(Board.strToMov("I7"), Mark.X);
		board.play(Board.strToMov("I1"), Mark.X);
		board.play(Board.strToMov("G3"), Mark.O);
		board.play(Board.strToMov("H3"), Mark.O);
		board.play(Board.strToMov("I3"), Mark.O);

		assertEquals(expected, board.toString());
	}


	@Test
	public void testIsBoardDoneDiag() {
		// board done
		// "|------|------|------|\n" +
		// "|      |      |  ❌  |\n" +
		// "|      |      |  ❌  |\n" +
		// "|      |      |  ❌  |\n" +
		// "|------|------|------|\n" +
		// "|      |    ❌|      |\n" +
		// "|      |  ❌  |      |\n" +
		// "|      |❌    |      |\n" +
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|❌❌❌|      |      |\n" +
		// "|------|------|------|\n";
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

		assertEquals(Mark.X, board.isBoardDone());
	}
	@Test
	public void testIsBoardDoneLost() {
		// board done
		// "|------|------|------|\n" +
		// "|      |🔵    |    🔵|\n" +
		// "|      |🔵    |  🔵  |\n" +
		// "|🔵🔵🔵|🔵    |🔵    |\n" +
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|------|------|------|\n";
		var board = new Board();
		board.play(Board.strToMov("A7"), Mark.O);
		board.play(Board.strToMov("B7"), Mark.O);
		board.play(Board.strToMov("C7"), Mark.O);
		board.play(Board.strToMov("D7"), Mark.O);
		board.play(Board.strToMov("D8"), Mark.O);
		board.play(Board.strToMov("D9"), Mark.O);
		board.play(Board.strToMov("G7"), Mark.O);
		board.play(Board.strToMov("H8"), Mark.O);
		board.play(Board.strToMov("I9"), Mark.O);

		assertEquals(Mark.O, board.isBoardDone());
	}


	@Test
	public void testIsBoardDoneWin() {
		// board done
		// "|------|------|------|\n" +
		// "|      |❌    |    ❌|\n" +
		// "|      |❌    |  ❌  |\n" +
		// "|❌❌❌|❌    |❌    |\n" +
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|------|------|------|\n";
		var board = new Board();
		board.play(Board.strToMov("A7"), Mark.X);
		board.play(Board.strToMov("B7"), Mark.X);
		board.play(Board.strToMov("C7"), Mark.X);
		board.play(Board.strToMov("D7"), Mark.X);
		board.play(Board.strToMov("D8"), Mark.X);
		board.play(Board.strToMov("D9"), Mark.X);
		board.play(Board.strToMov("G7"), Mark.X);
		board.play(Board.strToMov("H8"), Mark.X);
		board.play(Board.strToMov("I9"), Mark.X);

		assertEquals(Mark.X, board.isBoardDone());
	}

	@Test
	public void testGetPossibleMovesH8() {
		var board = new Board();
		Set<Move> moves = new HashSet<>(board.getPossibleMoves(Board.strToMov("H8")));
		Set<Move> expected = new HashSet<>();
		expected.add(Board.strToMov("D6"));
		expected.add(Board.strToMov("D5"));
		expected.add(Board.strToMov("D4"));
		expected.add(Board.strToMov("E6"));
		expected.add(Board.strToMov("E5"));
		expected.add(Board.strToMov("E4"));
		expected.add(Board.strToMov("F6"));
		expected.add(Board.strToMov("F5"));
		expected.add(Board.strToMov("F4"));
		assertEquals(expected, moves);
	}

	@Test
	public void testGetPossibleMoves() {
		// second play
		var board = new Board();
		board.play(Board.strToMov("D5"), Mark.X);

		Set<Move> moves =
			new HashSet<>(board.getPossibleMoves(Board.strToMov("D5")));

		Set<Move> expected = new HashSet<>();
		expected.add(new Move(0, 3));
		expected.add(new Move(0, 4));
		expected.add(new Move(0, 5));
		expected.add(new Move(1, 3));
		expected.add(new Move(1, 4));
		expected.add(new Move(1, 5));
		expected.add(new Move(2, 3));
		expected.add(new Move(2, 4));
		expected.add(new Move(2, 5));

		assertEquals(expected, moves);

		// first play
		board = new Board();
		moves = new HashSet<>(board.getPossibleMoves(null));

		expected = new HashSet<>();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				expected.add(new Move(j, i));
			}
		}
		assertEquals(expected, moves);

		// sub board done
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|    ❌|    ❌|    ❌|\n" +
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|------|------|------|\n" +
		// "|      |      |🔵🔵🔵|\n" +
		// "|      |      |      |\n" +
		// "|      |      |    ❌|\n" +
		// "|------|------|------|\n";
		board = new Board();
		board.play(Board.strToMov("C7"), Mark.X);
		board.play(Board.strToMov("F7"), Mark.X);
		board.play(Board.strToMov("I7"), Mark.X);
		board.play(Board.strToMov("I1"), Mark.X);
		board.play(Board.strToMov("G3"), Mark.O);
		board.play(Board.strToMov("H3"), Mark.O);
		board.play(Board.strToMov("I3"), Mark.O);
		moves = new HashSet<>(board.getPossibleMoves(Board.strToMov("I7")));
		String[] tmp = {
			"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3",
			"D1", "D2", "D3", "E1", "E2", "E3", "F1", "F2", "F3",
			"A4", "A5", "A6", "B4", "B5", "B6", "C4", "C5", "C6",
			"D4", "D5", "D6", "E4", "E5", "E6", "F4", "F5", "F6",
			"G4", "G5", "G6", "H4", "H5", "H6", "I4", "I5", "I6",
			"A7", "A8", "A9", "B7", "B8", "B9", "C8", "C9",
			"D7", "D8", "D9", "E7", "E8", "E9", "F8", "F9",
			"G7", "G8", "G9", "H7", "H8", "H9", "I8", "I9"
		};
		expected = Arrays
			.stream(tmp)
			.map(s -> Board.strToMov(s))
			.collect(Collectors.toSet());
		assertEquals(expected, moves);
		tmp = null;


		// sub board done
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|    ❌|    ❌|    ❌|\n" +
		// "|------|------|------|\n" +
		// "|      |      |      |\n" +
		// "|      |      |      |\n" +
		// "|      |🔵🔵🔵|      |\n" +
		// "|------|------|------|\n" +
		// "|      |      |🔵🔵🔵|\n" +
		// "|      |      |      |\n" +
		// "|      |      |    ❌|\n" +
		// "|------|------|------|\n";
		board = new Board();
		board.play(Board.strToMov("C7"), Mark.X);
		board.play(Board.strToMov("F7"), Mark.X);
		board.play(Board.strToMov("I7"), Mark.X);
		board.play(Board.strToMov("I1"), Mark.X);
		board.play(Board.strToMov("G3"), Mark.O);
		board.play(Board.strToMov("H3"), Mark.O);
		board.play(Board.strToMov("I3"), Mark.O);
		board.play(Board.strToMov("D4"), Mark.O);
		board.play(Board.strToMov("E4"), Mark.O);
		board.play(Board.strToMov("F4"), Mark.O);
		moves = new HashSet<>(board.getPossibleMoves(Board.strToMov("I7")));
		String[] tmp1 = {
			"A1", "A2", "A3", "B1", "B2", "B3", "C1", "C2", "C3",
			"D1", "D2", "D3", "E1", "E2", "E3", "F1", "F2", "F3",
			"A4", "A5", "A6", "B4", "B5", "B6", "C4", "C5", "C6",
			"G4", "G5", "G6", "H4", "H5", "H6", "I4", "I5", "I6",
			"A7", "A8", "A9", "B7", "B8", "B9", "C8", "C9",
			"D7", "D8", "D9", "E7", "E8", "E9", "F8", "F9",
			"G7", "G8", "G9", "H7", "H8", "H9", "I8", "I9"
		};
		expected = Arrays
			.stream(tmp1)
			.map(s -> Board.strToMov(s))
			.collect(Collectors.toSet());
		assertEquals(expected, moves);
	}
}
