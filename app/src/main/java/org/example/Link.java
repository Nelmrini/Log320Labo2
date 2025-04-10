package org.example;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Link the the application running.
 * Connection is done via a socket on the local network.
 *
 * This object has a internal memory of the current state of the game
 * Any attempt to modify the board from it's reference with
 * indroduce undefined behaviour
 * It is recommended to always query the board with getBoard when in need.
 *
 * @author Brian Normant
 */
public final class Link {

	/** singleton reference the server. **/
	private static Link instance = null;
	/** address of the server. **/
	private static String ADDRESS = "localhost";
	/** port of the server. **/
	private static Integer PORT = 8888;

	/** socket where the connection is done. **/
	private Socket socket;
	/** input stream. **/
	private BufferedInputStream input;
	/** output stream. **/
	private BufferedOutputStream output;

	/** the server board. **/
	private final Board board;
	/** the role the AI is playing. **/
	private final Mark player;
	/** the last move the server played. **/
	private Move lastServerPlay = null;


	/**
	 * Internal contructor, used once to initiate the connection to the server.
	 *
	 * Once the connection is initialized the board and info will be populated
	 * And this class ready to play the next move.
	 */
	private Link() {
		try {
			socket = new Socket(ADDRESS, PORT);
			input = new BufferedInputStream(socket.getInputStream());
			output = new BufferedOutputStream(socket.getOutputStream());
			board = new Board();

			System.out.printf("Connected to server %s:%d\n", ADDRESS, PORT);

			// check who is the player
			var cmd = (char) input.read();
			if (cmd == '1') {
				player = Mark.X;
			} else {
				player = Mark.O;
			}

			// read the current state of the board
			byte[] bbuf = new byte[1024];
			input.read(bbuf, 0, bbuf.length);
			String s = new String(bbuf).trim();
			String[] boardValues = s.split(" ");

			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					var m = new Move(j, i);
					var c = Integer.parseInt(boardValues[i * 9 + j]);
					var mark = switch (c) {
						case 4 -> Mark.X;
						case 2 -> Mark.O;
						default -> Mark.EMPTY;
					};

					board.play(m, mark);
				}
			}

			if (board.nextPlayer() != this.player) {
				readServer();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to connect to the server");
		}

	}

	/**
	 * Transmit the play to the server.
	 * This method is blocking as the server needs time answer
	 * One can assume that once the method finish and return the board has been
	 * updated with the opponents reaction
	 * @param move where in the board the move is play
	 */
	public void play(final Move move) {
		board.play(move, player);
		var msg = move.toString();

		try {
			output.write(msg.getBytes(), 0, msg.length());
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Connection with the server was lost");
		}
		readServer();
	}

	public Board getBoard() {
		return new Board(board);
	}

	public Mark getPlayer() {
		return player;
	}

	public Move getLastPlay() {
		return lastServerPlay;
	}

	/**
	 * read the server's response.
	 * this call is blocking
	 * the board may be updated depending of the server's answer
	 */
	public void readServer() {
		System.out.println("Waiting on the server");
		byte[] bbuf = new byte[1024];
		try {
			input.read(bbuf, 0, bbuf.length);
			String s = new String(bbuf).trim();
			String[] ans = s.split(" ");
			switch (Integer.parseInt(ans[0])) {
				case 3 -> {
					//the opponent played and the server is waiting
					var pos = Board.strToInd(ans[1]);
					var mark = player.other();
					var move = new Move(pos[0], pos[1]);
					lastServerPlay = move;
					board.play(move, mark);
					System.out.println("server has player, you're up");
				}
				case 4 -> {
					// our last play was invalid, this should never happen
					throw new RuntimeException("Invalid move");
				}
				default -> {
					throw new RuntimeException("Unexpected answer");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Connection with the server was lost");
		}
	}

	/**
	 * Getter method of the singleton.
	 * @return a Link to the server
	 */
	public static Link getInstance() {
		//return (instance == null) ? (instance = new Link()) : instance;
		if (instance == null) {
			instance = new Link();
		}
		return instance;
	}

	/**
	 * Getter that the address and port
	 * before initializing the connection
	 * @param the address of the server
	 * @param the port of the server
	 * @return a Link to the server
	 */
	public static Link getInstance(String address, Integer port) {
		ADDRESS = address;
		PORT = port;
		if (instance == null) {
			instance = new Link();
		}
		return instance;
	}
	
	/**
	 * Getter that the address
	 * before initializing the connection
	 * port is assumed to be the default port
	 * @param the address of the server
	 * @return a Link to the server
	 */
	public static Link getInstance(String address) {
		ADDRESS = address;
		if (instance == null) {
			instance = new Link();
		}
		return instance;
	}
}
