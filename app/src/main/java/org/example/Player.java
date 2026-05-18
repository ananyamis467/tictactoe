package org.example;


public interface Player {

    // Returns a human-readable label: "Human" or "Computer"
    String getLabel();

    int chooseMove(char[] board, char myMark, char opponentMark, int moveNumber);
}
