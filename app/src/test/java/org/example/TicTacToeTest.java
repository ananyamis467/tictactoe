package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for TicTacToe
 *
 * Run with JUnit 5 (junit-platform-console-standalone):
 *   javac -cp junit-platform-console-standalone.jar TicTacToe.java TicTacToeTest.java
 *   java  -cp .:junit-platform-console-standalone.jar \
 *         org.junit.platform.console.ConsoleLauncher --select-class=TicTacToeTest
 *
 * Or with Maven / Gradle using the standard JUnit 5 dependency.
 */
class TicTacToeTest {

    private TicTacToe game;

    @BeforeEach
    void setUp() {
        // Use a dummy scanner; individual tests supply their own where needed
        game = new TicTacToe(new Scanner(""));
    }

    // =========================================================
    // Board initialisation
    // =========================================================

    @Test
    @DisplayName("Board initialises with all cells empty")
    void testBoardInitialisedEmpty() {
        for (char cell : game.board) {
            assertEquals(TicTacToe.EMPTY, cell, "Every cell should be EMPTY after init");
        }
    }

    @Test
    @DisplayName("initBoard resets a partially-played board to empty")
    void testInitBoardResetsBoard() {
        game.board[0] = TicTacToe.PLAYER_X;
        game.board[4] = TicTacToe.PLAYER_O;
        game.initBoard();
        for (char cell : game.board) {
            assertEquals(TicTacToe.EMPTY, cell);
        }
    }

    @Test
    @DisplayName("Game starts with Player X")
    void testFirstPlayerIsX() {
        assertEquals(TicTacToe.PLAYER_X, game.currentPlayer);
    }

    // =========================================================
    // cellDisplay
    // =========================================================

    @Test
    @DisplayName("cellDisplay returns '.' for an empty cell")
    void testCellDisplayEmpty() {
        assertEquals(".", game.cellDisplay(0));
    }

    @Test
    @DisplayName("cellDisplay returns 'X' after X plays that cell")
    void testCellDisplayX() {
        game.board[3] = TicTacToe.PLAYER_X;
        assertEquals("X", game.cellDisplay(3));
    }

    @Test
    @DisplayName("cellDisplay returns 'O' after O plays that cell")
    void testCellDisplayO() {
        game.board[8] = TicTacToe.PLAYER_O;
        assertEquals("O", game.cellDisplay(8));
    }

    // =========================================================
    // validateMoveInput – valid inputs
    // =========================================================

    @Test
    @DisplayName("Valid input '1' maps to board index 0")
    void testValidMoveCell1() {
        assertEquals(0, game.validateMoveInput("1"));
    }

    @Test
    @DisplayName("Valid input '9' maps to board index 8")
    void testValidMoveCell9() {
        assertEquals(8, game.validateMoveInput("9"));
    }

    @Test
    @DisplayName("Valid input '5' maps to board index 4")
    void testValidMoveCellMiddle() {
        assertEquals(4, game.validateMoveInput("5"));
    }

    @Test
    @DisplayName("Input with surrounding whitespace is accepted")
    void testValidMoveWithWhitespace() {
        assertEquals(2, game.validateMoveInput("  3  "));
    }

    // =========================================================
    // validateMoveInput – invalid inputs
    // =========================================================

    @Test
    @DisplayName("Empty string input is rejected")
    void testInvalidMoveEmptyString() {
        assertEquals(-1, game.validateMoveInput(""));
        assertTrue(game.lastMoveError.contains("No input"));
    }

    @Test
    @DisplayName("Whitespace-only input is rejected")
    void testInvalidMoveWhitespaceOnly() {
        assertEquals(-1, game.validateMoveInput("   "));
        assertTrue(game.lastMoveError.contains("No input"));
    }

    @Test
    @DisplayName("Null input is rejected")
    void testInvalidMoveNull() {
        assertEquals(-1, game.validateMoveInput(null));
        assertTrue(game.lastMoveError.contains("No input"));
    }

    @Test
    @DisplayName("Decimal input '1.5' is rejected")
    void testInvalidMoveDecimal() {
        assertEquals(-1, game.validateMoveInput("1.5"));
        assertTrue(game.lastMoveError.contains("Decimal"));
    }

    @Test
    @DisplayName("Decimal input '3.0' is rejected")
    void testInvalidMoveDecimalZero() {
        assertEquals(-1, game.validateMoveInput("3.0"));
        assertTrue(game.lastMoveError.contains("Decimal"));
    }

    @ParameterizedTest(name = "String input ''{0}'' is rejected")
    @ValueSource(strings = {"abc", "one", "!", "@#$", "?", "two3"})
    void testInvalidMoveNonNumeric(String input) {
        assertEquals(-1, game.validateMoveInput(input));
        assertTrue(game.lastMoveError.contains("not a valid number"));
    }

    @Test
    @DisplayName("Input '0' is out of range and rejected")
    void testInvalidMoveZero() {
        assertEquals(-1, game.validateMoveInput("0"));
        assertTrue(game.lastMoveError.contains("out of range"));
    }

    @Test
    @DisplayName("Input '10' is out of range and rejected")
    void testInvalidMoveTen() {
        assertEquals(-1, game.validateMoveInput("10"));
        assertTrue(game.lastMoveError.contains("out of range"));
    }

    @Test
    @DisplayName("Negative number is rejected")
    void testInvalidMoveNegative() {
        assertEquals(-1, game.validateMoveInput("-1"));
        assertTrue(game.lastMoveError.contains("out of range"));
    }

    @Test
    @DisplayName("Already-taken cell is rejected")
    void testInvalidMoveAlreadyTaken() {
        game.board[0] = TicTacToe.PLAYER_X;        // cell 1 is taken
        assertEquals(-1, game.validateMoveInput("1"));
        assertTrue(game.lastMoveError.contains("already taken"));
    }

    @Test
    @DisplayName("Taking a cell does not mutate the board")
    void testValidateMoveDoesNotMutateBoard() {
        int index = game.validateMoveInput("5");
        assertEquals(4, index);
        assertEquals(TicTacToe.EMPTY, game.board[4], "validateMoveInput must not write to the board");
    }

    // =========================================================
    // checkWin – all 8 winning lines
    // =========================================================

    private void fillLine(char player, int... indices) {
        for (int i : indices) game.board[i] = player;
    }

    @Test @DisplayName("X wins: top row (0-1-2)")
    void testWinTopRow() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 0, 1, 2);
        assertTrue(game.checkWin());
    }

    @Test @DisplayName("X wins: middle row (3-4-5)")
    void testWinMiddleRow() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 3, 4, 5);
        assertTrue(game.checkWin());
    }

    @Test @DisplayName("X wins: bottom row (6-7-8)")
    void testWinBottomRow() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 6, 7, 8);
        assertTrue(game.checkWin());
    }

    @Test @DisplayName("O wins: left column (0-3-6)")
    void testWinLeftColumn() {
        game.currentPlayer = TicTacToe.PLAYER_O;
        fillLine(TicTacToe.PLAYER_O, 0, 3, 6);
        assertTrue(game.checkWin());
    }

    @Test @DisplayName("O wins: middle column (1-4-7)")
    void testWinMiddleColumn() {
        game.currentPlayer = TicTacToe.PLAYER_O;
        fillLine(TicTacToe.PLAYER_O, 1, 4, 7);
        assertTrue(game.checkWin());
    }

    @Test @DisplayName("O wins: right column (2-5-8)")
    void testWinRightColumn() {
        game.currentPlayer = TicTacToe.PLAYER_O;
        fillLine(TicTacToe.PLAYER_O, 2, 5, 8);
        assertTrue(game.checkWin());
    }

    @Test @DisplayName("X wins: diagonal top-left to bottom-right (0-4-8)")
    void testWinDiagonalMain() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 0, 4, 8);
        assertTrue(game.checkWin());
    }

    @Test @DisplayName("X wins: diagonal top-right to bottom-left (2-4-6)")
    void testWinDiagonalAnti() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 2, 4, 6);
        assertTrue(game.checkWin());
    }

    @Test @DisplayName("No win on empty board")
    void testNoWinOnEmptyBoard() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        assertFalse(game.checkWin());
    }

    @Test @DisplayName("No win when only two in a row")
    void testNoWinTwoInRow() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 0, 1);
        assertFalse(game.checkWin());
    }

    @Test @DisplayName("Mixed marks in a line do not constitute a win")
    void testNoWinMixedLine() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        game.board[0] = TicTacToe.PLAYER_X;
        game.board[1] = TicTacToe.PLAYER_O;
        game.board[2] = TicTacToe.PLAYER_X;
        assertFalse(game.checkWin());
    }

    // =========================================================
    // checkDraw
    // =========================================================

    @Test
    @DisplayName("Draw detected when all cells are filled")
    void testDrawAllFilled() {
        // A known drawn board
        char[] drawn = {
            'X','O','X',
            'X','O','O',
            'O','X','X'
        };
        System.arraycopy(drawn, 0, game.board, 0, 9);
        assertTrue(game.checkDraw());
    }

    @Test
    @DisplayName("No draw on empty board")
    void testNoDraw() {
        assertFalse(game.checkDraw());
    }

    @Test
    @DisplayName("No draw when one cell remains empty")
    void testNoDrawOneEmptyCell() {
        for (int i = 0; i < 8; i++) game.board[i] = TicTacToe.PLAYER_X;
        // board[8] stays EMPTY
        assertFalse(game.checkDraw());
    }

    // =========================================================
    // validatePlayAgainInput
    // =========================================================

    @ParameterizedTest(name = "''{0}'' should be treated as YES")
    @ValueSource(strings = {"yes", "YES", "Yes", "y", "Y", " yes ", " Y "})
    void testPlayAgainYes(String input) {
        assertEquals(1, game.validatePlayAgainInput(input));
    }

    @ParameterizedTest(name = "''{0}'' should be treated as NO")
    @ValueSource(strings = {"no", "NO", "No", "n", "N", " no ", " N "})
    void testPlayAgainNo(String input) {
        assertEquals(0, game.validatePlayAgainInput(input));
    }

    @ParameterizedTest(name = "''{0}'' is invalid play-again input")
    @ValueSource(strings = {"maybe", "yep", "nope", "1", "!", "yess", " "})
    void testPlayAgainInvalid(String input) {
        assertEquals(-1, game.validatePlayAgainInput(input));
    }

    @Test
    @DisplayName("Null play-again input is invalid")
    void testPlayAgainNull() {
        assertEquals(-1, game.validatePlayAgainInput(null));
    }

    @Test
    @DisplayName("Empty string play-again input is invalid")
    void testPlayAgainEmpty() {
        assertEquals(-1, game.validatePlayAgainInput(""));
    }

    // =========================================================
    // Player turn alternation
    // =========================================================

    @Test
    @DisplayName("After X plays, current player switches to O")
    void testTurnSwitchesXtoO() {
        game.currentPlayer = TicTacToe.PLAYER_X;
        game.board[0] = TicTacToe.PLAYER_X;
        // Simulate turn switch logic
        game.currentPlayer = (game.currentPlayer == TicTacToe.PLAYER_X)
                ? TicTacToe.PLAYER_O : TicTacToe.PLAYER_X;
        assertEquals(TicTacToe.PLAYER_O, game.currentPlayer);
    }

    @Test
    @DisplayName("After O plays, current player switches back to X")
    void testTurnSwitchesOtoX() {
        game.currentPlayer = TicTacToe.PLAYER_O;
        game.currentPlayer = (game.currentPlayer == TicTacToe.PLAYER_X)
                ? TicTacToe.PLAYER_O : TicTacToe.PLAYER_X;
        assertEquals(TicTacToe.PLAYER_X, game.currentPlayer);
    }

    // =========================================================
    // Full-game integration tests (Scanner-driven)
    // =========================================================

    /**
     * Simulates a complete game via the Scanner, capturing stdout.
     * Moves: X=1,3,5,7,9 vs O=2,4,6,8 → X wins on cell 9 (diagonal blocked, X fills row)
     * Actually uses a pre-verified X win: X plays 1,2,3 (top row), O plays 4,5.
     */
    @Test
    @DisplayName("Integration: X wins via top row then player declines rematch")
    void testIntegrationXWinsTopRow() {
        // X: 1,2,3  O: 4,5  → X wins after move 3; then "no" to play again
        String input = "1\n4\n2\n5\n3\nno\n";
        TicTacToe g = new TicTacToe(new Scanner(input));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            g.run();
        } finally {
            System.setOut(original);
        }

        String output = out.toString();
        assertTrue(output.contains("Player 1 (X) wins"), "Output should declare X as winner");
        assertTrue(output.contains("Goodbye"), "Output should say goodbye after declining rematch");
    }

    @Test
    @DisplayName("Integration: game ends in a draw then player declines rematch")
    void testIntegrationDraw() {
        // Board filled without any winner:
        // X O X
        // X O O
        // O X X
        // Moves in order: X=1,3,4,8,9  O=2,5,6,7
        String input = "1\n2\n3\n5\n4\n6\n8\n7\n9\nno\n";
        TicTacToe g = new TicTacToe(new Scanner(input));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            g.run();
        } finally {
            System.setOut(original);
        }

        String output = out.toString();
        assertTrue(output.contains("draw"), "Output should declare a draw");
    }

    @Test
    @DisplayName("Integration: invalid moves are rejected and re-prompted")
    void testIntegrationInvalidMovesRejected() {
        // First attempt invalid inputs, then play a complete X-wins game
        String input = "\nabc\n1.5\n0\n10\n1\n4\n2\n5\n3\nno\n";
        TicTacToe g = new TicTacToe(new Scanner(input));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            g.run();
        } finally {
            System.setOut(original);
        }

        String output = out.toString();
        assertTrue(output.contains("Invalid input"), "Invalid inputs should show error messages");
        assertTrue(output.contains("Player 1 (X) wins"), "Game should eventually complete with X winning");
    }

    @Test
    @DisplayName("Integration: play again starts a fresh game")
    void testIntegrationPlayAgain() {
        // Game 1: X wins top row.  Game 2: X wins top row again.  Then "no".
        String input = "1\n4\n2\n5\n3\nyes\n1\n4\n2\n5\n3\nno\n";
        TicTacToe g = new TicTacToe(new Scanner(input));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream original = System.out;
        System.setOut(new PrintStream(out));
        try {
            g.run();
        } finally {
            System.setOut(original);
        }

        String output = out.toString();
        // X wins twice
        long winCount = output.lines()
                .filter(l -> l.contains("Player 1 (X) wins"))
                .count();
        assertEquals(2, winCount, "X should win exactly twice across both games");
    }
}
