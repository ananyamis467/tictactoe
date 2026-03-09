package org.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.Scanner;

//tests
class TicTacToeTest {

    private TicTacToe game;

    @BeforeEach
    void setUp(){
        //use a dummy scanner; individual tests supply their own wherever needed
        game = new TicTacToe(new Scanner(""));
    }

    //board initialization
    @Test
    void testBoardInitializedEmpty(){
        for (char cell : game.board) {
            assertEquals(TicTacToe.EMPTY, cell, "Every cell should be EMPTY after init");
        }
    }

    @Test
    void testInitBoardResetsBoard(){
        game.board[0] = TicTacToe.PLAYER_X;
        game.board[4] = TicTacToe.PLAYER_O;
        game.initBoard();
        for (char cell : game.board) {
            assertEquals(TicTacToe.EMPTY, cell);
        }
    }

    @Test

    void testFirstPlayerIsX(){
        assertEquals(TicTacToe.PLAYER_X, game.currentPlayer);
    }

    //cellDisplay
    @Test
    void testCellDisplayEmpty(){
        assertEquals(".", game.cellDisplay(0));
    }

    @Test
    void testCellDisplayX(){
        game.board[3] = TicTacToe.PLAYER_X;
        assertEquals("X", game.cellDisplay(3));
    }

    @Test
    void testCellDisplayO(){
        game.board[8] = TicTacToe.PLAYER_O;
        assertEquals("O", game.cellDisplay(8));
    }

    // validateMoveInput – valid inputs
    @Test
    void testValidMoveCell1(){
        assertEquals(0, game.validateMoveInput("1"));
    }

    @Test
    void testValidMoveCell9(){
        assertEquals(8, game.validateMoveInput("9"));
    }

    @Test
    void testValidMoveCellMiddle(){
        assertEquals(4, game.validateMoveInput("5"));
    }

    @Test
    void testValidMoveWithWhitespace(){
        assertEquals(2, game.validateMoveInput("  3  "));
    }

    //validateMoveInput – invalid inputs
    @Test
    void testInvalidMoveEmptyString(){
        assertEquals(-1, game.validateMoveInput(""));
        assertTrue(game.lastMoveError.contains("No input"));
    }

    @Test
    void testInvalidMoveWhitespaceOnly(){
        assertEquals(-1, game.validateMoveInput("   "));
        assertTrue(game.lastMoveError.contains("No input"));
    }

    @Test
    void testInvalidMoveNull(){
        assertEquals(-1, game.validateMoveInput(null));
        assertTrue(game.lastMoveError.contains("No input"));
    }

    @Test
    void testInvalidMoveDecimal(){
        assertEquals(-1, game.validateMoveInput("1.5"));
        assertTrue(game.lastMoveError.contains("Decimal"));
    }

    @Test
    void testInvalidMoveDecimalZero(){
        assertEquals(-1, game.validateMoveInput("3.0"));
        assertTrue(game.lastMoveError.contains("Decimal"));
    }

    @Test
    void testInvalidMoveZero(){
        assertEquals(-1, game.validateMoveInput("0"));
        assertTrue(game.lastMoveError.contains("out of range"));
    }

    @Test
    void testInvalidMoveTen(){
        assertEquals(-1, game.validateMoveInput("10"));
        assertTrue(game.lastMoveError.contains("out of range"));
    }

    @Test
    void testInvalidMoveNegative(){
        assertEquals(-1, game.validateMoveInput("-1"));
        assertTrue(game.lastMoveError.contains("out of range"));
    }

    @Test
    void testInvalidMoveAlreadyTaken(){
        game.board[0] = TicTacToe.PLAYER_X; // cell 1 is taken
        assertEquals(-1, game.validateMoveInput("1"));
        assertTrue(game.lastMoveError.contains("already taken"));
    }

    @Test
    void testValidateMoveDoesNotMutateBoard(){
        int index = game.validateMoveInput("5");
        assertEquals(4, index);
        assertEquals(TicTacToe.EMPTY, game.board[4], "validateMoveInput must not write to the board");
    }


    //checkWin – all 8 winning lines
    private void fillLine(char player, int... indices){
        for (int i : indices) game.board[i] = player;
    }

    @Test 
    void testWinTopRow(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 0, 1, 2);
        assertTrue(game.checkWin());
    }

    @Test 
    void testWinMiddleRow(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 3, 4, 5);
        assertTrue(game.checkWin());
    }

    @Test 
    void testWinBottomRow(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 6, 7, 8);
        assertTrue(game.checkWin());
    }

    @Test 
    void testWinLeftColumn(){
        game.currentPlayer = TicTacToe.PLAYER_O;
        fillLine(TicTacToe.PLAYER_O, 0, 3, 6);
        assertTrue(game.checkWin());
    }

    @Test 
    void testWinMiddleColumn(){
        game.currentPlayer = TicTacToe.PLAYER_O;
        fillLine(TicTacToe.PLAYER_O, 1, 4, 7);
        assertTrue(game.checkWin());
    }

    @Test 
    void testWinRightColumn(){
        game.currentPlayer = TicTacToe.PLAYER_O;
        fillLine(TicTacToe.PLAYER_O, 2, 5, 8);
        assertTrue(game.checkWin());
    }

    @Test 
    void testWinDiagonalMain(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 0, 4, 8);
        assertTrue(game.checkWin());
    }

    @Test 
    void testWinDiagonalAnti(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 2, 4, 6);
        assertTrue(game.checkWin());
    }

    @Test 
    void testNoWinOnEmptyBoard(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        assertFalse(game.checkWin());
    }

    @Test 
    void testNoWinTwoInRow(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        fillLine(TicTacToe.PLAYER_X, 0, 1);
        assertFalse(game.checkWin());
    }

    @Test 
    void testNoWinMixedLine(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        game.board[0] = TicTacToe.PLAYER_X;
        game.board[1] = TicTacToe.PLAYER_O;
        game.board[2] = TicTacToe.PLAYER_X;
        assertFalse(game.checkWin());
    }

    //checkDraw
    @Test
    void testDrawAllFilled(){
        //a [known] drawn board
        char[] drawn = {
            'X','O','X',
            'X','O','O',
            'O','X','X'
        };
        System.arraycopy(drawn, 0, game.board, 0, 9);
        assertTrue(game.checkDraw());
    }

    @Test
    void testNoDraw(){
        assertFalse(game.checkDraw());
    }

    @Test
    void testNoDrawOneEmptyCell(){
        for (int i = 0; i < 8; i++) game.board[i] = TicTacToe.PLAYER_X;
        // board[8] stays EMPTY
        assertFalse(game.checkDraw());
    }

    //validatePlayAgainInput
    @Test
    void testPlayAgainNull(){
        assertEquals(-1, game.validatePlayAgainInput(null));
    }

    @Test
    void testPlayAgainEmpty(){
        assertEquals(-1, game.validatePlayAgainInput(""));
    }

    //player turn alternation
    @Test
    void testTurnSwitchesXtoO(){
        game.currentPlayer = TicTacToe.PLAYER_X;
        game.board[0] = TicTacToe.PLAYER_X;
        // Simulate turn switch logic
        game.currentPlayer = (game.currentPlayer == TicTacToe.PLAYER_X)
                ? TicTacToe.PLAYER_O : TicTacToe.PLAYER_X;
        assertEquals(TicTacToe.PLAYER_O, game.currentPlayer);
    }

    @Test
    void testTurnSwitchesOtoX(){
        game.currentPlayer = TicTacToe.PLAYER_O;
        game.currentPlayer = (game.currentPlayer == TicTacToe.PLAYER_X)
                ? TicTacToe.PLAYER_O : TicTacToe.PLAYER_X;
        assertEquals(TicTacToe.PLAYER_X, game.currentPlayer);
    }

    //full-game integration tests (Scanner-driven)
    @Test
    void testIntegrationXWinsTopRow(){
        //X: 1,2,3 & O: 4,5 → X wins after move 3; then "no" to play again
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
    void testIntegrationDraw(){
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
    void testIntegrationInvalidMovesRejected(){
        //first attempt invalid inputs, then play a complete X-wins game
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
    void testIntegrationPlayAgain(){
        //game 1: X wins top row; game 2: X wins top row again, then "no"
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
        //X wins twice
        long winCount = output.lines()
                .filter(l -> l.contains("Player 1 (X) wins"))
                .count();
        assertEquals(2, winCount, "X should win exactly twice across both games");
    }
}
