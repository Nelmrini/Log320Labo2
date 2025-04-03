package org.example;

public record Tuple<T1, T2>(T1 first, T2 second) {
	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}
};
