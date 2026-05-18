package org.example;

import java.util.Scanner;

public class HumanPlayer implements Player {

    private final Scanner scanner;

    public HumanPlayer(Scanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public String getLabel() {
        return "Human";
    }

    @Override
    public int chooseMove(char[] board, char myMark, char opponentMark, int moveNumber) {
        while (true) {
            System.out.print("Enter a cell (1-9): ");
            String input = scanner.nextLine();
            int index = validate(input, board);
            if (index >= 0) {
                return index;
            }
        }
    }


    static int validate(String input, char[] board) {
        if (input == null || input.trim().isEmpty()) {
            System.out.println("  ⚠ No input detected. Please enter a number between 1 and 9.\n");
            return -1;
        }
        input = input.trim();
        if (input.contains(".")) {
            System.out.println("  ⚠ Decimal numbers are not allowed. Please enter a whole number between 1 and 9.\n");
            return -1;
        }
        int cell;
        try {
            cell = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("  ⚠ \"" + input + "\" is not a valid number. Please enter a number between 1 and 9.\n");
            return -1;
        }
        if (cell < 1 || cell > 9) {
            System.out.println("  ⚠ " + cell + " is out of range. Please enter a number between 1 and 9.\n");
            return -1;
        }
        int index = cell - 1;
        if (board[index] != TicTacToe.EMPTY) {
            System.out.println("  ⚠ Cell " + cell + " is already taken. Please choose an empty cell.\n");
            return -1;
        }
        return index;
    }
}
