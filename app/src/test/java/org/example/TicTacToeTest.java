package org.example;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;


class TicTacToeTest {

    private TicTacToe game;

    @BeforeEach
    void setUp() {
        game = new TicTacToe(new Scanner(""));
        // Default to two human players so legacy tests compile without mode selection
        game.playerX = new HumanPlayer(new Scanner(""));
        game.playerO = new HumanPlayer(new Scanner(""));
    }

    // ── helper ────────────────────────────────────────────────────────────────
    private String runGame(String inputLines) {
        TicTacToe g = new TicTacToe(new Scanner(inputLines));
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(buf));
        try { g.run(); } finally { System.setOut(old); }
        return buf.toString();
    }

    private void suppressOutput(Runnable r) {
        PrintStream old = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        try { r.run(); } finally { System.setOut(old); }
    }

    // Board initialization

    @Test @DisplayName("Board initialises with all cells empty")
    void testBoardInitialisedEmpty() {
        for (char c : game.board) assertEquals(TicTacToe.EMPTY, c);
    }

    @Test @DisplayName("initBoard resets a partially-played board")
    void testInitBoardResets() {
        game.board[0] = 'X'; game.board[4] = 'O';
        game.initBoard();
        for (char c : game.board) assertEquals(TicTacToe.EMPTY, c);
    }

    @Test @DisplayName("Game starts with Player X")
    void testFirstPlayerIsX() { assertEquals(TicTacToe.PLAYER_X, game.currentPlayer); }


    // cellDisplay

    @Test void testCellDisplayEmpty() { assertEquals(".", game.cellDisplay(0)); }
    @Test void testCellDisplayX()     { game.board[3]='X'; assertEquals("X", game.cellDisplay(3)); }
    @Test void testCellDisplayO()     { game.board[8]='O'; assertEquals("O", game.cellDisplay(8)); }


    // validateMoveInput

    @Test void testValidCell1()    { assertEquals(0, game.validateMoveInput("1")); }
    @Test void testValidCell9()    { assertEquals(8, game.validateMoveInput("9")); }
    @Test void testValidCell5()    { assertEquals(4, game.validateMoveInput("5")); }
    @Test void testValidWhitespace(){ assertEquals(2, game.validateMoveInput("  3  ")); }
    @Test void testValidateNoMutation(){ game.validateMoveInput("5"); assertEquals(TicTacToe.EMPTY, game.board[4]); }

    @Test void testRejectEmpty()    { assertEquals(-1, game.validateMoveInput("")); assertTrue(game.lastMoveError.contains("No input")); }
    @Test void testRejectWhitespace(){ assertEquals(-1, game.validateMoveInput("   ")); }
    @Test void testRejectNull()     { assertEquals(-1, game.validateMoveInput(null)); }
    @Test void testRejectDecimal()  { assertEquals(-1, game.validateMoveInput("1.5")); assertTrue(game.lastMoveError.contains("Decimal")); }
    @Test void testRejectZero()     { assertEquals(-1, game.validateMoveInput("0")); assertTrue(game.lastMoveError.contains("out of range")); }
    @Test void testRejectTen()      { assertEquals(-1, game.validateMoveInput("10")); }
    @Test void testRejectNegative() { assertEquals(-1, game.validateMoveInput("-1")); }
    @Test void testRejectTaken()    { game.board[0]='X'; assertEquals(-1, game.validateMoveInput("1")); assertTrue(game.lastMoveError.contains("already taken")); }

    void testRejectNonNumeric(String s){ assertEquals(-1, game.validateMoveInput(s)); assertTrue(game.lastMoveError.contains("not a valid number")); }


    // checkWin — all 8 lines

    private void fill(char p, int... idx){ for(int i:idx) game.board[i]=p; }

    @Test void testWinRow0()  { game.currentPlayer='X'; fill('X',0,1,2); assertTrue(game.checkWin()); }
    @Test void testWinRow1()  { game.currentPlayer='X'; fill('X',3,4,5); assertTrue(game.checkWin()); }
    @Test void testWinRow2()  { game.currentPlayer='X'; fill('X',6,7,8); assertTrue(game.checkWin()); }
    @Test void testWinCol0()  { game.currentPlayer='O'; fill('O',0,3,6); assertTrue(game.checkWin()); }
    @Test void testWinCol1()  { game.currentPlayer='O'; fill('O',1,4,7); assertTrue(game.checkWin()); }
    @Test void testWinCol2()  { game.currentPlayer='O'; fill('O',2,5,8); assertTrue(game.checkWin()); }
    @Test void testWinDiag0() { game.currentPlayer='X'; fill('X',0,4,8); assertTrue(game.checkWin()); }
    @Test void testWinDiag1() { game.currentPlayer='X'; fill('X',2,4,6); assertTrue(game.checkWin()); }
    @Test void testNoWinEmpty(){ assertFalse(game.checkWin()); }
    @Test void testNoWinTwo()  { fill('X',0,1); game.currentPlayer='X'; assertFalse(game.checkWin()); }
    @Test void testNoWinMixed(){ game.board[0]='X'; game.board[1]='O'; game.board[2]='X'; game.currentPlayer='X'; assertFalse(game.checkWin()); }

    // checkDraw

    @Test void testDrawFull() {
        char[] d={'X','O','X','X','O','O','O','X','X'};
        System.arraycopy(d,0,game.board,0,9);
        assertTrue(game.checkDraw());
    }
    @Test void testNoDrawEmpty()  { assertFalse(game.checkDraw()); }
    @Test void testNoDrawOneLeft(){ for(int i=0;i<8;i++) game.board[i]='X'; assertFalse(game.checkDraw()); }

    // validatePlayAgainInput



    void testPlayAgainYes(String s){ assertEquals(1, game.validatePlayAgainInput(s)); }


    void testPlayAgainNo(String s) { assertEquals(0, game.validatePlayAgainInput(s)); }


    void testPlayAgainInvalid(String s){ assertEquals(-1, game.validatePlayAgainInput(s)); }

    @Test void testPlayAgainNull()  { assertEquals(-1, game.validatePlayAgainInput(null)); }
    @Test void testPlayAgainEmpty() { assertEquals(-1, game.validatePlayAgainInput("")); }


    // validateMenuInput

    @Test void testMenuValid1()    { assertEquals(1, game.validateMenuInput("1", 1, 3)); }
    @Test void testMenuValid3()    { assertEquals(3, game.validateMenuInput("3", 1, 3)); }
    @Test void testMenuValid2()    { assertEquals(2, game.validateMenuInput(" 2 ", 1, 3)); }
    @Test void testMenuTooLow()    { assertEquals(-1, game.validateMenuInput("0", 1, 3)); }
    @Test void testMenuTooHigh()   { assertEquals(-1, game.validateMenuInput("4", 1, 3)); }
    @Test void testMenuDecimal()   { assertEquals(-1, game.validateMenuInput("1.0", 1, 3)); }
    @Test void testMenuAlpha()     { assertEquals(-1, game.validateMenuInput("abc", 1, 3)); }
    @Test void testMenuEmpty()     { assertEquals(-1, game.validateMenuInput("", 1, 3)); }
    @Test void testMenuNull()      { assertEquals(-1, game.validateMenuInput(null, 1, 3)); }


    // Game log counters


    @Test void testInitialCounters(){ assertEquals(0,game.winsX); assertEquals(0,game.winsO); assertEquals(0,game.ties); }


    // saveGameLog

    @Test @DisplayName("saveGameLog creates game.txt with correct stats")
    void testSaveGameLog() throws IOException {
        game.winsX=3; game.winsO=1; game.ties=2;
        suppressOutput(() -> game.saveGameLog());
        Path p = Path.of(TicTacToe.LOG_FILE);
        assertTrue(Files.exists(p));
        String c = Files.readString(p);
        assertTrue(c.contains("Player X Wins  : 3"));
        assertTrue(c.contains("Player O Wins  : 1"));
        assertTrue(c.contains("Ties           : 2"));
        assertTrue(c.contains("Total rounds   : 6"));
        Files.deleteIfExists(p);
    }


    // HumanPlayer.validate (static helper)

    @Test void testHumanValidateGood()    { assertEquals(4, HumanPlayer.validate("5", game.board)); }
    @Test void testHumanValidateEmpty()   {
        ByteArrayOutputStream b=new ByteArrayOutputStream(); System.setOut(new PrintStream(b));
        try{ assertEquals(-1, HumanPlayer.validate("", game.board)); } finally{ System.setOut(System.out); }
    }
    @Test void testHumanValidateTaken()   {
        game.board[0]='X';
        ByteArrayOutputStream b=new ByteArrayOutputStream(); System.setOut(new PrintStream(b));
        try{ assertEquals(-1, HumanPlayer.validate("1", game.board)); } finally{ System.setOut(System.out); }
    }


    // ComputerPlayer — unit tests

    private ComputerPlayer cpu;
    private char[] emptyBoard;

    @BeforeEach
    void setUpCpu() {
        cpu = new ComputerPlayer(42L); // seeded for determinism
        emptyBoard = new char[9];
        for (int i = 0; i < 9; i++) emptyBoard[i] = TicTacToe.EMPTY;
    }

    @Test @DisplayName("Computer picks a corner on empty board (moveNumber=0)")
    void testCpuFirstMoveIsCorner() {
        int move = cpu.chooseMove(emptyBoard, 'X', 'O', 0);
        assertTrue(move == 0 || move == 2 || move == 6 || move == 8,
                   "First move must be a corner; got " + move);
    }

    @Test @DisplayName("Computer takes center on move 1 when center is free")
    void testCpuTakesCenterOnMoveTwoIfFree() {
        emptyBoard[8] = 'O'; // one mark already placed (opponent went first)
        int move = cpu.chooseMove(emptyBoard, 'X', 'O', 1);
        assertEquals(4, move, "Computer should take center (index 4) on moveNumber=1");
    }

    @Test @DisplayName("Computer does NOT take center on move 1 when center is occupied")
    void testCpuSkipsCenterWhenTaken() {
        emptyBoard[4] = 'O'; // center already taken
        emptyBoard[0] = 'O'; // one other mark
        int move = cpu.chooseMove(emptyBoard, 'X', 'O', 1);
        assertNotEquals(4, move);
        assertEquals(TicTacToe.EMPTY, emptyBoard[move]); // must be empty
    }

    @Test @DisplayName("Computer takes winning move (rule 3)")
    void testCpuTakesWin() {
        // X has top row with one gap: X _ X — computer should complete it
        emptyBoard[0] = 'X';
        emptyBoard[2] = 'X';
        // moveNumber=4 so rules 1 & 2 are skipped
        int move = cpu.chooseMove(emptyBoard, 'X', 'O', 4);
        assertEquals(1, move, "Computer must complete the winning row at index 1");
    }

    @Test @DisplayName("Computer blocks opponent winning move (rule 4)")
    void testCpuBlocks() {
        // O threatens top row: O O _
        emptyBoard[0] = 'O';
        emptyBoard[1] = 'O';
        // X has no winning move
        emptyBoard[5] = 'X';
        int move = cpu.chooseMove(emptyBoard, 'X', 'O', 4);
        assertEquals(2, move, "Computer must block O at index 2");
    }

    @Test @DisplayName("Win takes priority over block (rule 3 before rule 4)")
    void testCpuWinBeforeBlock() {
        // X can win at index 2; O threatens column 0 (index 6 missing)
        emptyBoard[0] = 'X'; emptyBoard[1] = 'X'; // X wins at index 2
        emptyBoard[3] = 'O'; emptyBoard[6] = TicTacToe.EMPTY;
        emptyBoard[0] = 'O'; // reuse — let's set up cleanly:

        char[] b = new char[9];
        for (int i=0;i<9;i++) b[i]=TicTacToe.EMPTY;
        b[3]='X'; b[4]='X'; // X wins at index 5
        b[0]='O'; b[1]='O'; // O wins at index 2 — but X win check runs first
        int move = cpu.chooseMove(b, 'X', 'O', 4);
        assertEquals(5, move, "Computer should take its own win at index 5");
    }

    @Test @DisplayName("findThreat returns correct index for two-in-a-row")
    void testFindThreatBasic() {
        emptyBoard[0] = 'X';
        emptyBoard[1] = 'X';
        assertEquals(2, cpu.findThreat(emptyBoard, 'X'));
    }

    @Test @DisplayName("findThreat returns -1 when no threat exists")
    void testFindThreatNone() {
        assertEquals(-1, cpu.findThreat(emptyBoard, 'X'));
    }

    @Test @DisplayName("randomEmpty returns an empty cell")
    void testRandomEmpty() {
        for (int i=0; i<8; i++) emptyBoard[i]='X'; // only index 8 is free
        assertEquals(8, cpu.randomEmpty(emptyBoard));
    }

    @Test @DisplayName("randomCorner returns a corner index")
    void testRandomCorner() {
        int c = cpu.randomCorner(emptyBoard);
        assertTrue(c==0||c==2||c==6||c==8);
    }

    @Test @DisplayName("Computer label is 'Computer'")
    void testCpuLabel() { assertEquals("Computer", cpu.getLabel()); }

    @Test @DisplayName("Human label is 'Human'")
    void testHumanLabel() { assertEquals("Human", new HumanPlayer(new Scanner("")).getLabel()); }

    
    // Integration tests

    @Test @DisplayName("Integration: mode 1 (HvH) — X wins, log shows 1-0-0")
    void testIntegrationHvHXWins() throws IOException {
        // Mode 1, X:1,2,3  O:4,5 → X wins; then no
        String out = runGame("1\n1\n4\n2\n5\n3\nno\n");
        assertTrue(out.contains("Player 1 (X) [Human] wins"));
        assertTrue(out.contains("Player X Wins  : 1"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test @DisplayName("Integration: mode 1 (HvH) — draw")
    void testIntegrationHvHDraw() throws IOException {
        // X O X / X O O / O X X
        String out = runGame("1\n1\n2\n3\n5\n4\n6\n8\n7\n9\nno\n");
        assertTrue(out.contains("draw"));
        assertTrue(out.contains("Ties           : 1"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test @DisplayName("Integration: invalid menu selection is re-prompted")
    void testIntegrationInvalidMenuRejected() throws IOException {
        // bad inputs before valid "1"
        String out = runGame("\nabc\n5\n1\n1\n4\n2\n5\n3\nno\n");
        assertTrue(out.contains("Invalid selection"));
        assertTrue(out.contains("Player 1 (X) [Human] wins"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test @DisplayName("Integration: mode 2 — computer plays O, game completes")
    void testIntegrationHvCComputer() throws IOException {
        // Mode 2: human=X, computer=O
        // Human plays a quick game; computer will respond automatically
        // X:1,2,3 wins before O gets 3 in a row
        String out = runGame("2\n1\n2\n3\nno\n");
        assertTrue(out.contains("Computer") || out.contains("Human"));
        // Game must end (win or draw)
        assertTrue(out.contains("wins") || out.contains("draw"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test @DisplayName("Integration: mode 3 — computer plays X (goes first), game completes")
    void testIntegrationCvHComputer() throws IOException {
        // Mode 3: computer=X, human=O; human provides enough moves to finish
        String out = runGame("3\n5\n1\n2\n3\nno\n");
        assertTrue(out.contains("Computer") || out.contains("Human"));
        assertTrue(out.contains("wins") || out.contains("draw"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test @DisplayName("Integration: game.txt is written on exit")
    void testIntegrationLogFileSaved() throws IOException {
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
        runGame("1\n1\n4\n2\n5\n3\nno\n");
        assertTrue(Files.exists(Path.of(TicTacToe.LOG_FILE)));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }

    @Test @DisplayName("Integration: loser goes first in next round (HvH)")
    void testIntegrationLoserGoesFirst() throws IOException {
        // Game 1 X wins → O goes first game 2
        String out = runGame("1\n1\n4\n2\n5\n3\nyes\n1\n4\n2\n5\n3\nno\n");
        assertTrue(out.contains("O lost last round, so O goes first"));
        Files.deleteIfExists(Path.of(TicTacToe.LOG_FILE));
    }
}
