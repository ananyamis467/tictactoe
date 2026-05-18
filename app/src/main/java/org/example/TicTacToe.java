package org.example;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

//tests
public class TicTacToe {

    static final char EMPTY    = ' ';
    static final char PLAYER_X = 'X';
    static final char PLAYER_O = 'O';
    static final String LOG_FILE = "game.txt";

    char[] board = new char[9];
    char currentPlayer;
    private final Scanner scanner;

    // Players assigned at the start of each session
    Player playerX;   // controls X
    Player playerO;   // controls O

    // Game log counters
    int winsX = 0;
    int winsO = 0;
    int ties  = 0;

    // Result of the most recently completed round:
    // PLAYER_X or PLAYER_O = that player lost; EMPTY = draw
    char lastRoundResult = EMPTY;


    // Constructors

    public TicTacToe() {
        this.scanner = new Scanner(System.in);
        initBoard();
        currentPlayer = PLAYER_X;
    }

    /** Package-private constructor for testing with an injected Scanner. */
    TicTacToe(Scanner scanner) {
        this.scanner = scanner;
        initBoard();
        currentPlayer = PLAYER_X;
    }

    // Top-level flow

    void run() {
        System.out.println("║   Welcome to Tic-Tac-Toe!    ║");
        System.out.println();

        selectGameMode();

        do {
            playGame();
            printGameLog();
        } while (askPlayAgain());

        saveGameLog();
        System.out.println("\nThanks for playing! Goodbye!");
        scanner.close();
    }

    // Game mode selection

    void selectGameMode() {
        System.out.println("What kind of game would you like to play?");
        System.out.println("  1. Human vs. Human");
        System.out.println("  2. Human vs. Computer  (computer goes second / plays O)");
        System.out.println("  3. Computer vs. Human  (computer goes first  / plays X)");
        System.out.println();

        while (true) {
            System.out.print("Enter your selection (1, 2, or 3): ");
            String input = scanner.nextLine();
            int choice = validateMenuInput(input, 1, 3);
            if (choice < 0) {
                System.out.println("  ⚠ Invalid selection. Please enter 1, 2, or 3.\n");
                continue;
            }

            switch (choice) {
                case 1:
                    playerX = new HumanPlayer(scanner);
                    playerO = new HumanPlayer(scanner);
                    System.out.println("\nGreat! Two human players it is.\n");
                    break;
                case 2:
                    playerX = new HumanPlayer(scanner);
                    playerO = new ComputerPlayer();
                    System.out.println("\nGreat! The computer will play as O (second).\n");
                    break;
                case 3:
                    playerX = new ComputerPlayer();
                    playerO = new HumanPlayer(scanner);
                    System.out.println("\nGreat! The computer will play as X (first).\n");
                    break;
            }
            return;
        }
    }

    /**
     * Validates a menu selection input string.
     * Returns the integer value if it is within [min, max], or -1 on any error.
     */
    int validateMenuInput(String input, int min, int max) {
        if (input == null || input.trim().isEmpty()) return -1;
        input = input.trim();
        if (input.contains(".")) return -1;
        try {
            int val = Integer.parseInt(input);
            return (val >= min && val <= max) ? val : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Core game loop

    void playGame() {
        initBoard();

        // Determine who starts: loser of the last round goes first;
        // first game or a draw → X starts.
        if (lastRoundResult == PLAYER_X) {
            currentPlayer = PLAYER_X;
        } else if (lastRoundResult == PLAYER_O) {
            currentPlayer = PLAYER_O;
        } else {
            currentPlayer = PLAYER_X;
        }

        int moveNumber = 0; // total marks placed so far this game

        while (true) {
            printBoard();

            int move = getMove(moveNumber);
            board[move] = currentPlayer;
            moveNumber++;

            if (checkWin()) {
                printBoard();
                announceWinner();
                return;
            }

            if (checkDraw()) {
                printBoard();
                System.out.println("🤝 It's a draw!\n");
                ties++;
                lastRoundResult = EMPTY;
                return;
            }

            currentPlayer = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
        }
    }


    int getMove(int moveNumber) {
        Player activePlayer = (currentPlayer == PLAYER_X) ? playerX : playerO;
        int playerNum = (currentPlayer == PLAYER_X) ? 1 : 2;

        if (activePlayer instanceof HumanPlayer) {
            System.out.print("Player " + playerNum + " (" + currentPlayer + ") [Human], enter a cell (1-9): ");
            while (true) {
                String input = scanner.nextLine();
                int index = HumanPlayer.validate(input, board);
                if (index >= 0) return index;
                System.out.print("Player " + playerNum + " (" + currentPlayer + ") [Human], enter a cell (1-9): ");
            }
        } else {
            // Computer player: determine opponent mark then choose
            char opponentMark = (currentPlayer == PLAYER_X) ? PLAYER_O : PLAYER_X;
            int index = activePlayer.chooseMove(board, currentPlayer, opponentMark, moveNumber);
            System.out.println("Player " + playerNum + " (" + currentPlayer + ") [Computer] chose cell " + (index + 1) + ".");
            return index;
        }
    }

    // Win announcement (extracted to keep playGame readable)

    private void announceWinner() {
        Player winner = (currentPlayer == PLAYER_X) ? playerX : playerO;
        int playerNum  = (currentPlayer == PLAYER_X) ? 1 : 2;
        String label   = winner.getLabel();
        System.out.println("🎉 Player " + playerNum + " (" + currentPlayer + ") [" + label + "] wins!\n");
        if (currentPlayer == PLAYER_X) {
            winsX++;
            lastRoundResult = PLAYER_O; // O lost → O goes first next round
        } else {
            winsO++;
            lastRoundResult = PLAYER_X; // X lost → X goes first next round
        }
    }

    // Board helpers

    void initBoard() {
        for (int i = 0; i < 9; i++) board[i] = EMPTY;
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
            if (row < 2) System.out.println("  |---------|          |---------|");
        }
        System.out.println("  +---------+          +---------+");
        System.out.println();
    }

    String cellDisplay(int index) {
        return board[index] == EMPTY ? "." : String.valueOf(board[index]);
    }

    // Move validation (retained for backward-compat and direct unit testing)

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


    boolean checkWin() {
        int[][] lines = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
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
        for (char c : board) if (c == EMPTY) return false;
        return true;
    }

    // Game log — display and persist

    void printGameLog() {
        System.out.println("┌─────────────────────────┐");
        System.out.println("│         Game Log         │");
        System.out.println("├─────────────────────────┤");
        System.out.printf( "│  Player X Wins  : %-4d  │%n", winsX);
        System.out.printf( "│  Player O Wins  : %-4d  │%n", winsO);
        System.out.printf( "│  Ties           : %-4d  │%n", ties);
        System.out.println();
    }

    void saveGameLog() {
        System.out.println("\nWriting the game log to disk. Please see " + LOG_FILE + " for the final statistics!");
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE))) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.println("╔══════════════════════════════╗");
            pw.println("║     Tic-Tac-Toe  Game Log    ║");
            pw.println("╚══════════════════════════════╝");
            pw.println();
            pw.println("Session ended  : " + timestamp);
            pw.println("Total rounds   : " + (winsX + winsO + ties));
            pw.println();
            pw.println("┌─────────────────────────┐");
            pw.println("│       Final Results      │");
            pw.println("├─────────────────────────┤");
            pw.printf( "│  Player X Wins  : %-4d  │%n", winsX);
            pw.printf( "│  Player O Wins  : %-4d  │%n", winsO);
            pw.printf( "│  Ties           : %-4d  │%n", ties);
        } catch (IOException e) {
            System.out.println("  ⚠ Warning: could not write game log — " + e.getMessage());
        }
    }


    // Play-again prompt

    /** Returns 1 = yes, 0 = no, -1 = invalid. */
    int validatePlayAgainInput(String input) {
        if (input == null || input.trim().isEmpty()) return -1;
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
                System.out.println("  ⚠ \"" + display + "\" is not recognized. Please enter 'yes' or 'no'.\n");
            }
        }
    }
}
