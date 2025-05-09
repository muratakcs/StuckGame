package game;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int size;
    private final int[][] grid;
    private final boolean[][] visited;
    private int visitedCount;
    private int playerRow;
    private int playerCol;
    private int score;
    private int stepCount; // ✅ Track actual moves made

    public Board(int size, int[][] grid, int startRow, int startCol) {
        this.size = size;
        this.grid = grid;
        this.visited = new boolean[size][size];
        this.visitedCount = 1;
        this.playerRow = startRow;
        this.playerCol = startCol;
        this.score = 1;
        this.stepCount = 0;

        visited[startRow][startCol] = true;
        grid[startRow][startCol] = 0;
    }

    public Board(Board other) {
        this.size = other.size;
        this.grid = new int[size][size];
        this.visited = new boolean[size][size];
        this.playerRow = other.playerRow;
        this.playerCol = other.playerCol;
        this.score = other.score;
        this.visitedCount = other.visitedCount;
        this.stepCount = other.stepCount;

        for (int r = 0; r < size; r++) {
            System.arraycopy(other.grid[r], 0, this.grid[r], 0, size);
            System.arraycopy(other.visited[r], 0, this.visited[r], 0, size);
        }
    }

    public int getSize() {
        return size;
    }

    public int getScore() {
        return visitedCount;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getValueAt(int row, int col) {
        return grid[row][col];
    }

    public boolean isGameOver() {
        return getPossibleMoves().isEmpty();
    }

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public boolean isVisited(int row, int col) {
        return visited[row][col];
    }

    public List<Move> getPossibleMoves() {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] dir : directions) {
            int dRow = dir[0];
            int dCol = dir[1];
            if (canMoveInDirection(dRow, dCol)) {
                moves.add(new Move(dRow, dCol));
            }
        }
        return moves;
    }

    private boolean canMoveInDirection(int dRow, int dCol) {
        int newRow = playerRow + dRow;
        int newCol = playerCol + dCol;

        if (!isInBounds(newRow, newCol) || visited[newRow][newCol]) {
            return false;
        }

        int stepSize = grid[newRow][newCol];
        int targetRow = playerRow + dRow * stepSize;
        int targetCol = playerCol + dCol * stepSize;

        if (!isInBounds(targetRow, targetCol) || visited[targetRow][targetCol]) {
            return false;
        }

        int tempRow = playerRow, tempCol = playerCol;
        for (int i = 1; i <= stepSize; i++) {
            tempRow += dRow;
            tempCol += dCol;
            if (!isInBounds(tempRow, tempCol) || visited[tempRow][tempCol]) {
                return false;
            }
        }
        return true;
    }

    public boolean applyMove(Move move) {
        int dRow = move.getDRow();
        int dCol = move.getDCol();

        int newRow = playerRow + dRow;
        int newCol = playerCol + dCol;

        if (!isInBounds(newRow, newCol) || visited[newRow][newCol]) {
            return false;
        }

        int stepSize = grid[newRow][newCol];
        int targetRow = playerRow + dRow * stepSize;
        int targetCol = playerCol + dCol * stepSize;

        if (!canMoveInDirection(dRow, dCol)) {
            return false;
        }

        for (int i = 1; i <= stepSize; i++) {
            playerRow += dRow;
            playerCol += dCol;
            visited[playerRow][playerCol] = true;
            grid[playerRow][playerCol] = 0;
            visitedCount++;
        }

        stepCount++;
        score++;
        return true;
    }

    private boolean isPathClear(int startRow, int startCol, int targetRow, int targetCol, int rowStep, int colStep) {
        int row = startRow + rowStep;
        int col = startCol + colStep;
        while (row != targetRow || col != targetCol) {
            if (!isInBounds(row, col) || visited[row][col]) {
                return false;
            }
            row += rowStep;
            col += colStep;
        }
        return true;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < getSize() && col >= 0 && col < getSize();
    }

    public boolean isValidMove(int row, int col) {
        return isInBounds(row, col) && !visited[row][col];
    }

    private boolean isInBounds(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    public int[][] copyGrid() {
        int[][] copy = new int[getSize()][getSize()];
        for (int i = 0; i < getSize(); i++) {
            for (int j = 0; j < getSize(); j++) {
                copy[i][j] = getValueAt(i, j);
            }
        }
        return copy;
    }

    public double getCoveragePercentage() {
        int visitedCount = 0;
        int totalCells = size * size;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (visited[r][c]) {
                    visitedCount++;
                }
            }
        }
        return (100.0 * visitedCount) / totalCells;
    }

    public void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == playerRow && j == playerCol) {
                    System.out.print(" * ");
                } else if (visited[i][j]) {
                    System.out.print("   ");
                } else {
                    System.out.printf(" %d ", grid[i][j]);
                }
            }
            System.out.println();
        }
    }
}
