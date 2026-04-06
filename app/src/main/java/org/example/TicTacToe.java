package org.example;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TicTacToe {

    static final char EMPTY    = ' ';
    static final char PLAYER_X = 'X';
    static final char PLAYER_O = 'O';
    static final String LOG_FILE = "game.txt";

    char[] board = new char[9];
    char currentPlayer;
    private Scanner scanner;

    // Game log counters
    int winsX  = 0;
    int winsO  = 0;
    int ties   = 0;

    // Result of the most recently completed round:
    // PLAYER_X, PLAYER_O, or EMPTY (draw)
    char lastRoundResult = EMPTY;

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


    // Top-level flow
    void run() {
        System.out.println("║   Welcome to Tic-Tac-Toe!    ║");
        System.out.println("║  Player 1 = X  Player 2 = O  ║");
        System.out.println();

        do {
            playGame();
            printGameLog();
        } while (askPlayAgain());

        saveGameLog();
        System.out.println("\nThanks for playing! Goodbye!");
        scanner.close();
    }

    // Core game loop
    void playGame() {
        initBoard();
        // The loser of the last round goes first; on the very first game X starts.
        if (lastRoundResult == PLAYER_X) {
            // X lost last round → X goes first
            currentPlayer = PLAYER_X;
        } else if (lastRoundResult == PLAYER_O) {
            // O lost last round → O goes first
            currentPlayer = PLAYER_O;
        } else {
            // Draw or first game → default to X
            currentPlayer = PLAYER_X;
        }

        while (true) {
            printBoard();
            int move = getMove();
            board[move] = currentPlayer;

            if (checkWin()) {
                printBoard();
                int playerNum = (currentPlayer == PLAYER_X) ? 1 : 2;
                System.out.println("🎉 Player " + playerNum + " (" + currentPlayer + ") wins!\n");
                if (currentPlayer == PLAYER_X) {
                    winsX++;
                    lastRoundResult = PLAYER_O; // O lost → O goes first next
                } else {
                    winsO++;
                    lastRoundResult = PLAYER_X; // X lost → X goes first next
                }
                return;
            }

            if (checkDraw()) {
                printBoard();
                System.out.println("🤝 It's a draw!\n");
                ties++;
                lastRoundResult = EMPTY; // No loser → default to X next
                return;
            }

            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
    }


    // Board helpers
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


    // Input validation
    String lastMoveError = "";

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


    // Win / draw detection
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


    // Game log — display and save
    void printGameLog() {
        System.out.println("┌─────────────────────────┐");
        System.out.println("│       Game Log           │");
        System.out.println("├─────────────────────────┤");
        System.out.printf( "│  Player X Wins  : %-4d  │%n", winsX);
        System.out.printf( "│  Player O Wins  : %-4d  │%n", winsO);
        System.out.printf( "│  Ties           : %-4d  │%n", ties);
        System.out.println("└─────────────────────────┘");
        System.out.println();
    }

    //Writes the final game log to LOG_FILE and informs the user
    void saveGameLog() {
        System.out.println("\nWriting the game log to disk. Please see " + LOG_FILE + " for the final statistics!");

        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE))) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            pw.println("╔══════════════════════════════╗");
            pw.println("║     Tic-Tac-Toe  Game Log    ║");
            pw.println("╚══════════════════════════════╝");
            pw.println();
            pw.println("Session ended : " + timestamp);
            pw.println("Total rounds  : " + (winsX + winsO + ties));
            pw.println();
            pw.println("┌─────────────────────────┐");
            pw.println("│       Final Results      │");
            pw.println("├─────────────────────────┤");
            pw.printf( "│  Player X Wins  : %-4d  │%n", winsX);
            pw.printf( "│  Player O Wins  : %-4d  │%n", winsO);
            pw.printf( "│  Ties           : %-4d  │%n", ties);
            pw.println("└─────────────────────────┘");
        } catch (IOException e) {
            System.out.println("  ⚠ Warning: could not write game log — " + e.getMessage());
        }
    }


    // Play-again prompt
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
                // Tell the user who goes first in the next round
                if (lastRoundResult == PLAYER_X) {
                    System.out.println("Great! X lost last round, so X goes first!\n");
                } else if (lastRoundResult == PLAYER_O) {
                    System.out.println("Great! O lost last round, so O goes first!\n");
                } else {
                    System.out.println("Great! It was a draw, so X goes first!\n");
                }
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
