/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

public final class App {
	public String getGreeting() {
		return "Hello World!";
	}

	public static void main(final String[] args) {
		Link link;
		if (args.length == 2) {
			var address = args[0];
			var port = Integer.parseInt(args[1]);
			link = Link.getInstance(address, port);
		} else if (args.length == 1) {
			var address = args[0];
			link = Link.getInstance(address);
		} else {
			link = Link.getInstance();
		}

		// test print board
		// Board board = new Board();
		// Scanner input = new Scanner(System.in);
		// System.out.println(board);
		// while (input.hasNext()) {
		//     String str = input.nextLine();
		//     int[] pos = Board.strToInd(str);
		//
		//     board.play(new Move(pos[0], pos[1]), Mark.X);
		//     System.out.println(board);
		// }

		// test read board from server
		System.out.println(link.getBoard());

		System.out.println("You are playing " + switch (link.getPlayer()) {
			case X -> "Red";
			case O -> "Black";
				default -> "This code is going to isekai itself";
		});

		// Scanner scanner = new Scanner(System.in);
		//
		// while (scanner.hasNext()) {
		//     var str = scanner.nextLine();
		//     link.play(Board.strToMov(str));
		//     System.out.println(link.getBoard());
		// }
		//
		// scanner.close();
		//

		// Random random = new Random();
		var ia = new CPUPlayer(link.getPlayer());

		while (true) {
			// var moves = link.getBoard().getPossibleMoves(
			// 		link.getLastPlay()
			// 		);
			//var m = moves.get(random.nextInt(moves.size()));
			var m = ia.getNextMoveMinMax(link.getBoard(), link.getLastPlay()).getFirst();
			System.out.println("Hallo?");
			link.play(m);

			System.out.println(link.getBoard());
		}
	}
}
