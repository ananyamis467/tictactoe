package org.example;
import java.util.Scanner; 

import java.util.Scanner;

public class TicTacToe {

    static final char EMPTY = ' ';
    static final char PLAYER_X = 'X';
    static final char PLAYER_O = 'O';

    char[] board = new char[9];
    char currentPlayer;
    private Scanner scanner;

    public TicTacToe() {
        this.scanner = new Scanner(System.in);
        initBoard();
        currentPlayer = PLAYER_X;
    }

    // Constructor for testing with injected Scanner
    TicTacToe(Scanner scanner) {
        this.scanner = scanner;
        initBoard();
        currentPlayer = PLAYER_X;
    }

    public static void main(String[] args) {
        TicTacToe game = new TicTacToe();
        game.run();
    }

    void run() {
        System.out.println("╔══════════════════════════════╗");
        System.out.println("║   Welcome to Tic-Tac-Toe!    ║");
        System.out.println("║  Player 1 = X  Player 2 = O  ║");
        System.out.println("╚══════════════════════════════╝");
        System.out.println();

        do {
            playGame();
        } while (askPlayAgain());

        System.out.println("\nThanks for playing! Goodbye!");
        scanner.close();
    }

    void playGame() {
        initBoard();
        currentPlayer = PLAYER_X;

        while (true) {
            printBoard();
            int move = getMove();
            board[move] = currentPlayer;

            if (checkWin()) {
                printBoard();
                int playerNum = (currentPlayer == PLAYER_X) ? 1 : 2;
                System.out.println("🎉 Player " + playerNum + " (" + currentPlayer + ") wins!\n");
                return;
            }

            if (checkDraw()) {
                printBoard();
                System.out.println("🤝 It's a draw!\n");
                return;
            }

            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
    }

    void initBoard() {
        for (int i = 0; i < 9; i++) {
            board[i] = EMPTY;
        }
    }

    void printBoard() {
        System.out.println();
        System.out.println("  Cell numbers:       Current board:");
        System.out.println("  +---------+          +---------+");
        for (int row = 0; row < 3; row++) {
            int base = row * 3;
            System.out.printf("  | %d  %d  %d |          | %s  %s  %s |%n",
                    base + 1, base + 2, base + 3,
                    cellDisplay(base), cellDisplay(base + 1), cellDisplay(base + 2));
            if (row < 2) {
                System.out.println("  |---------|          |---------|");
            }
        }
        System.out.println("  +---------+          +---------+");
        System.out.println();
    }

    String cellDisplay(int index) {
        return board[index] == EMPTY ? "." : String.valueOf(board[index]);
    }

    String lastMoveError = "";

    /**
     * Validates a raw string input for a cell move.
     * Returns the 0-based board index on success, or -1 on any validation failure.
     * Sets lastMoveError with a human-readable message on failure.
     */
    int validateMoveInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            lastMoveError = "No input detected. Please enter a number between 1 and 9.";
            return -1;
        }

        input = input.trim();

        if (input.contains(".")) {
            lastMoveError = "Decimal numbers are not allowed. Please enter a whole number between 1 and 9.";
            return -1;
        }

        int cell;
        try {
            cell = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            lastMoveError = "\"" + input + "\" is not a valid number. Please enter a number between 1 and 9.";
            return -1;
        }

        if (cell < 1 || cell > 9) {
            lastMoveError = cell + " is out of range. Please enter a number between 1 and 9.";
            return -1;
        }

        int index = cell - 1;

        if (board[index] != EMPTY) {
            lastMoveError = "Cell " + cell + " is already taken. Please choose an empty cell.";
            return -1;
        }

        return index;
    }

    int getMove() {
        int playerNum = (currentPlayer == PLAYER_X) ? 1 : 2;
        while (true) {
            System.out.print("Player " + playerNum + " (" + currentPlayer + "), enter a cell (1-9): ");
            String input = scanner.nextLine();
            int index = validateMoveInput(input);
            if (index >= 0) {
                return index;
            }
            System.out.println("  ⚠ Invalid input: " + lastMoveError + "\n");
        }
    }

    boolean checkWin() {
        int[][] lines = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
            {0, 4, 8}, {2, 4, 6}              // diagonals
        };
        for (int[] line : lines) {
            if (board[line[0]] != EMPTY &&
                board[line[0]] == board[line[1]] &&
                board[line[1]] == board[line[2]]) {
                return true;
            }
        }
        return false;
    }

    boolean checkDraw() {
        for (char c : board) {
            if (c == EMPTY) return false;
        }
        return true;
    }

    /**
     * Validates a raw string input for the play-again prompt.
     * Returns: 1 = yes, 0 = no, -1 = invalid
     */
    int validatePlayAgainInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return -1;
        }
        String answer = input.trim().toLowerCase();
        if (answer.equals("yes") || answer.equals("y")) return 1;
        if (answer.equals("no")  || answer.equals("n"))  return 0;
        return -1;
    }

    boolean askPlayAgain() {
        while (true) {
            System.out.print("Would you like to play again? (yes/no): ");
            String input = scanner.nextLine();
            int result = validatePlayAgainInput(input);
            if (result == 1) {
                System.out.println();
                return true;
            } else if (result == 0) {
                return false;
            } else {
                String display = (input == null) ? "" : input.trim();
                System.out.println("  ⚠ Invalid input: \"" + display + "\" is not recognized. Please enter 'yes' or 'no'.\n");
            }
        }
    }
}
