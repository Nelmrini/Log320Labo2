/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
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
        Link link = Link.getInstance();
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

        Random random = new Random();

        while (true) {
            var moves = link.getBoard().getPossibleMoves(
                    link.getPlayer(),
                    link.getLastPlay()
                    );
            var m = moves.get(random.nextInt(moves.size()));
            link.play(m);

            System.out.println(link.getBoard());
        }
    }
}
