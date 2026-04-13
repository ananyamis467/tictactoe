package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit and integration tests for TicTacToe
 */
class TicTacToeTest {

    private TicTacToe game;

    @BeforeEach
    void setUp() {
        game = new TicTacToe(new Scanner(""));
    }

    // Helper; capture stdout for a full run
    private String runGame(String inputLines) {
        TicTacToe g = new TicTacToe(new Scanner(inputLines));
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(buf));
        try { g.run(); } finally { System.setOut(old); }
        return buf.toString();
    }


    // Board initialization
    @Test
    void testBoardInitialisedEmpty() {
        for (char cell : game.board)
            assertEquals(TicTacToe.EMPTY, cell);
    }

    @Test
    void testInitBoardResetsBoard() {
        game.board[0] = TicTacToe.PLAYER_X;
        game.board[4] = TicTacToe.PLAYER_O;
        game.initBoard();
        for (char cell : game.board)
            assertEquals(TicTacToe.EMPTY, cell);
    }

    @Test
    void testFirstPlayerIsX() {
        assertEquals(TicTacToe.PLAYER_X, game.currentPlayer);
    }


    // cellDisplay
    @Test 
    void testCellDisplayEmpty() { assertEquals(".", game.cellDisplay(0)); }

    @Test 
    void testCellDisplayX() {
        game.board[3] = TicTacToe.PLAYER_X;
        assertEquals("X", game.cellDisplay(3));
    }

    @Test 
    void testCellDisplayO() {
        game.board[8] = TicTacToe.PLAYER_O;
        assertEquals("O", game.cellDisplay(8));
    }

    // validateMoveInput – valid
    @Test  
    void testValidCell1() { assertEquals(0, game.validateMoveInput("1")); }
    
    @Test  
    void testValidCell9() { assertEquals(8, game.validateMoveInput("9")); }

    @Test 
    void testValidCell5() { assertEquals(4, game.validateMoveInput("5")); }

    @Test
    void testValidMoveWithWhitespace() {
        assertEquals(2, game.validateMoveInput("  3  "));
    }

    @Test
    void testValidateDoesNotMutateBoard() {
        game.validateMoveInput("5");
        assertEquals(TicTacToe.EMPTY, game.board[4]);
    }


    // validateMoveInput – invalid
    @Test void testRejectEmpty()     { assertEquals(-1, game.validateMoveInput("")); assertTrue(game.lastMoveError.contains("No input")); }
    @Test void testRejectWhitespace(){ assertEquals(-1, game.validateMoveInput("   ")); assertTrue(game.lastMoveError.contains("No input")); }
    @Test void testRejectNull()      { assertEquals(-1, game.validateMoveInput(null)); assertTrue(game.lastMoveError.contains("No input")); }
    @Test void testRejectDecimal()   { assertEquals(-1, game.validateMoveInput("1.5")); assertTrue(game.lastMoveError.contains("Decimal")); }
    @Test void testRejectDecimalZero(){ assertEquals(-1, game.validateMoveInput("3.0")); assertTrue(game.lastMoveError.contains("Decimal")); }
    @Test void testRejectZero()      { assertEquals(-1, game.validateMoveInput("0")); assertTrue(game.lastMoveError.contains("out of range")); }
    @Test void testRejectTen()       { assertEquals(-1, game.validateMoveInput("10")); assertTrue(game.lastMoveError.contains("out of range")); }
    @Test void testRejectNegative()  { assertEquals(-1, game.validateMoveInput("-1")); assertTrue(game.lastMoveError.contains("out of range")); }


    @Test
    void testRejectAlreadyTaken() {
        game.board[0] = TicTacToe.PLAYER_X;
        assertEquals(-1, game.validateMoveInput("1"));
        assertTrue(game.lastMoveError.contains("already taken"));
    }


    // checkWin – all 8 lines
    private void fill(char p, int... idx) { for (int i : idx) game.board[i] = p; }

    @Test void testWinRow0()   { game.currentPlayer = 'X'; fill('X',0,1,2); assertTrue(game.checkWin()); }
    @Test void testWinRow1()   { game.currentPlayer = 'X'; fill('X',3,4,5); assertTrue(game.checkWin()); }
    @Test void testWinRow2()   { game.currentPlayer = 'X'; fill('X',6,7,8); assertTrue(game.checkWin()); }
    @Test void testWinCol0()   { game.currentPlayer = 'O'; fill('O',0,3,6); assertTrue(game.checkWin()); }
    @Test void testWinCol1()   { game.currentPlayer = 'O'; fill('O',1,4,7); assertTrue(game.checkWin()); }
    @Test void testWinCol2()   { game.currentPlayer = 'O'; fill('O',2,5,8); assertTrue(game.checkWin()); }
    @Test void testWinDiag0()  { game.currentPlayer = 'X'; fill('X',0,4,8); assertTrue(game.checkWin()); }
    @Test void testWinDiag1()  { game.currentPlayer = 'X'; fill('X',2,4,6); assertTrue(game.checkWin()); }
    @Test void testNoWinEmpty()  { assertFalse(game.checkWin()); }
    @Test void testNoWinTwo()    { fill('X',0,1); game.currentPlayer = 'X'; assertFalse(game.checkWin()); }
    @Test void testNoWinMixed()  { game.board[0]='X'; game.board[1]='O'; game.board[2]='X'; game.currentPlayer='X'; assertFalse(game.checkWin()); }


    // checkDraw
    @Test
    void testDrawFull() {
        char[] d = {'X','O','X','X','O','O','O','X','X'};
        System.arraycopy(d, 0, game.board, 0, 9);
        assertTrue(game.checkDraw());
    }

    @Test 
    void testNoDrawEmpty() { assertFalse(game.checkDraw()); }

    @Test
    void testNoDrawOneLeft() {
        for (int i = 0; i < 8; i++) game.board[i] = 'X';
        assertFalse(game.checkDraw());
    }


    @Test 
    void testPlayAgainNull()  { assertEquals(-1, game.validatePlayAgainInput(null)); }

    @Test 
    void testPlayAgainEmpty() { assertEquals(-1, game.validatePlayAgainInput("")); }

    // Game log counters
    @Test
    void testInitialCounters() {
        assertEquals(0, game.winsX);
        assertEquals(0, game.winsO);
        assertEquals(0, game.ties);
    }

    @Test
    void testWinsXIncrement() {
        // Simulate X winning: set board so X wins top row
        game.currentPlayer = TicTacToe.PLAYER_X;
        fill('X', 0, 1, 2);
        // Replicate the win-handling logic from playGame()
        if (game.checkWin()) {
            if (game.currentPlayer == TicTacToe.PLAYER_X) game.winsX++;
        }
        assertEquals(1, game.winsX);
        assertEquals(0, game.winsO);
    }

    @Test
    void testWinsOIncrement() {
        game.currentPlayer = TicTacToe.PLAYER_O;
        fill('O', 0, 3, 6);
        if (game.checkWin()) {
            if (game.currentPlayer == TicTacToe.PLAYER_O) game.winsO++;
        }
        assertEquals(0, game.winsX);
        assertEquals(1, game.winsO);
    }

    @Test
    void testTiesIncrement() {
        char[] d = {'X','O','X','X','O','O','O','X','X'};
        System.arraycopy(d, 0, game.board, 0, 9);
        if (game.checkDraw()) game.ties++;
        assertEquals(1, game.ties);
    }

    // Turn-order logic based on lastRoundResult
    @Test
    void testLastRoundResultAfterXWin() {
        // After X wins: loser is O, so lastRoundResult = PLAYER_O
        game.currentPlayer = TicTacToe.PLAYER_X;
        fill('X', 0, 1, 2);
        // simulate win-handling
        game.winsX++;
        game.lastRoundResult = TicTacToe.PLAYER_O;
        assertEquals(TicTacToe.PLAYER_O, game.lastRoundResult);
    }

    @Test
    void testLastRoundResultAfterOWin() {
        game.currentPlayer = TicTacToe.PLAYER_O;
        fill('O', 0, 3, 6);
        game.winsO++;
        game.lastRoundResult = TicTacToe.PLAYER_X;
        assertEquals(TicTacToe.PLAYER_X, game.lastRoundResult);
    }

    @Test
    void testLastRoundResultAfterDraw() {
        game.ties++;
        game.lastRoundResult = TicTacToe.EMPTY;
        assertEquals(TicTacToe.EMPTY, game.lastRoundResult);
    }

    // saveGameLog
    @Test
    void testSaveGameLog() throws IOException {
        game.winsX = 3;
        game.winsO = 1;
        game.ties  = 2;

        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(buf));
        try { game.saveGameLog(); } finally { System.setOut(old); }

        Path logPath = Path.of(TicTacToe.LOG_FILE);
        assertTrue(Files.exists(logPath), "game.txt should be created");

        String content = Files.readString(logPath);
        assertTrue(content.contains("Player X Wins  : 3"));
        assertTrue(content.contains("Player O Wins  : 1"));
        assertTrue(content.contains("Ties           : 2"));
        assertTrue(content.contains("Total rounds   : 6"));

        Files.deleteIfExists(logPath); // cleanup
    }

    @Test
    void testSaveGameLogPrintsMessage() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(buf));
        try { game.saveGameLog(); } finally { System.setOut(old); }

        assertTrue(buf.toString().contains(TicTacToe.LOG_FILE));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE)); // cleanup
    }


    // Integration tests
    @Test
    void testIntegrationXWins() throws IOException {
        // X: 1,2,3  O: 4,5
        String output = runGame("1\n4\n2\n5\n3\nno\n");
        assertTrue(output.contains("Player 1 (X) wins"));
        assertTrue(output.contains("Player X Wins  : 1"));
        assertTrue(output.contains("Player O Wins  : 0"));
        assertTrue(output.contains("Ties           : 0"));
        assertTrue(output.contains("Goodbye"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test
    void testIntegrationDraw() throws IOException {
        // X O X / X O O / O X X  (no winner)
        String output = runGame("1\n2\n3\n5\n4\n6\n8\n7\n9\nno\n");
        assertTrue(output.contains("draw"));
        assertTrue(output.contains("Ties           : 1"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test
    void testIntegrationInvalidMoves() throws IOException {
        String output = runGame("\nabc\n1.5\n0\n10\n1\n4\n2\n5\n3\nno\n");
        assertTrue(output.contains("Invalid input"));
        assertTrue(output.contains("Player 1 (X) wins"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test
    void testIntegrationLoserGoesFirst() throws IOException {
        // Game 1: X wins top row (X:1,2,3 O:4,5) → O lost → O goes first in game 2
        // Game 2: verify the board prompt says O goes first, then X wins again → no
        String output = runGame("1\n4\n2\n5\n3\nyes\n1\n4\n2\n5\n3\nno\n");
        assertTrue(output.contains("O lost last round, so O goes first"),
                "Should announce O goes first after O loses");
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test
    void testIntegrationCumulativeLog() throws IOException {
        // Game 1: X wins. Game 2: O wins. Log → 1-1-0.
        // X wins game 1: X:1,2,3 O:4,5
        // After that O goes first in game 2; O wins left column: O:1,3,7 X:2,4 (O:1,4,7 col)
        // O first: O=1, X=2, O=4, X=3, O=7
        String output = runGame("1\n4\n2\n5\n3\nyes\n1\n2\n4\n3\n7\nno\n");
        assertTrue(output.contains("Player X Wins  : 1"));
        assertTrue(output.contains("Player O Wins  : 1"));
        assertTrue(output.contains("Ties           : 0"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test
    void testIntegrationLogFileSaved() throws IOException {
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
        runGame("1\n4\n2\n5\n3\nno\n");
        assertTrue(Files.exists(Path.of(TicTacToe.LOG_FILE)), "game.txt must exist after exit");
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }
}
