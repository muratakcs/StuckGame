package game;

import java.util.*;

public class GameLogger {
    private List<int[][]> boardHistory = new ArrayList<>();
    private List<int[]> moveHistory = new ArrayList<>(); // Stores (dRow, dCol) instead of absolute positions
    private Board board;

    public GameLogger(Board board) {
        this.board = board;
        saveState();
    }

    public void logMove(int dRow, int dCol) {
        moveHistory.add(new int[]{dRow, dCol});
        saveState();
        printBoardState(dRow, dCol);
    }

    private void saveState() {
        int[][] snapshot = new int[board.getSize()][board.getSize()];
        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                snapshot[r][c] = board.getValueAt(r, c);
            }
        }
        boardHistory.add(snapshot);
    }

    private void printBoardState(int dRow, int dCol) {
        System.out.println("\nStep " + moveHistory.size() + " - Player moved direction: (" + dRow + ", " + dCol + ")");

        int[][] latestBoard = boardHistory.get(boardHistory.size() - 1);
        for (int r = 0; r < latestBoard.length; r++) {
            for (int c = 0; c < latestBoard[r].length; c++) {
                if (r == board.getPlayerRow() && c == board.getPlayerCol()) {
                    System.out.print(" P ");
                } else if (board.isVisited(r, c)) {
                    System.out.print(" . ");
                } else {
                    System.out.print(" " + latestBoard[r][c] + " ");
                }
            }
            System.out.println();
        }

        System.out.println("Total Moves Made: " + board.getStepCount());

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
