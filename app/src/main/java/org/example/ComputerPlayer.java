package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ComputerPlayer implements Player {

    private static final int CENTER = 4;
    private static final int[] CORNERS = {0, 2, 6, 8};
    private static final int[][] WIN_LINES = {
        {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
        {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
        {0, 4, 8}, {2, 4, 6}
    };

    private final Random random;

    public ComputerPlayer() {
        this.random = new Random();
    }

    /** Package-private constructor for seeded testing */
    ComputerPlayer(long seed) {
        this.random = new Random(seed);
    }

    @Override
    public String getLabel() {
        return "Computer";
    }

    @Override
    public int chooseMove(char[] board, char myMark, char opponentMark, int moveNumber) {
        // Rule 1: first move of the game → random corner
        if (moveNumber == 0) {
            return randomCorner(board);
        }

        // Rule 2: computer's first individual move AND center is free → take center
        // moveNumber counts total marks on the board; if it's 1, exactly one mark exists.
        if (moveNumber == 1 && board[CENTER] == TicTacToe.EMPTY) {
            return CENTER;
        }

        // Rule 3: take a winning move if one exists
        int win = findThreat(board, myMark);
        if (win >= 0) return win;

        // Rule 4: block opponent's winning move
        int block = findThreat(board, opponentMark);
        if (block >= 0) return block;

        // Rule 5: random empty cell
        return randomEmpty(board);
    }


    int findThreat(char[] board, char mark) {
        for (int[] line : WIN_LINES) {
            int markCount  = 0;
            int emptyIndex = -1;
            for (int idx : line) {
                if (board[idx] == mark) {
                    markCount++;
                } else if (board[idx] == TicTacToe.EMPTY) {
                    emptyIndex = idx;
                }
            }
            if (markCount == 2 && emptyIndex >= 0) {
                return emptyIndex;
            }
        }
        return -1;
    }

    // Returns the index of a random available corner, or -1 if none are free
    int randomCorner(char[] board) {
        List<Integer> free = new ArrayList<>();
        for (int c : CORNERS) {
            if (board[c] == TicTacToe.EMPTY) free.add(c);
        }
        if (free.isEmpty()) return randomEmpty(board);
        return free.get(random.nextInt(free.size()));
    }

    // Returns the index of a uniformly random empty cell
    int randomEmpty(char[] board) {
        List<Integer> empty = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            if (board[i] == TicTacToe.EMPTY) empty.add(i);
        }
        if (empty.isEmpty()) {
            throw new IllegalStateException("No empty cells available.");
        }
        return empty.get(random.nextInt(empty.size()));
    }
}
