package org.example;

import org.junit.Test;
import static org.junit.Assert.*;

public class MoveTest {
	@Test
	public void testToString() {
		assertEquals("A9", new Move(0, 0).toString());
		assertEquals("I1", new Move(8, 8).toString());
		assertEquals("D4", new Move(3, 5).toString());
	}
}
