package game;

import java.io.*;
import java.util.concurrent.*;

public class Referee {
    private static final int INIT_TIMEOUT_SECONDS = 2;  // ⏳ Max time for player constructor
    private static final int MOVE_TIMEOUT_SECONDS = 1;  // ⏳ Max time per move

    public static int playGame(Player player, String studentID, String boardFile, boolean enableSnapshots) {
        int boardNumber = extractBoardNumber(boardFile);
        File snapshotFile = new File(String.format("snapshots/Player%s_Board%d.txt", studentID, boardNumber));

        ExecutorService executor = Executors.newSingleThreadExecutor();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(snapshotFile))) {
            if (enableSnapshots) {
                saveSnapshot(player.board, writer, 0, true);
            }

            while (!player.board.isGameOver()) {
                Future<Move> futureMove = executor.submit(player::nextMove);
                Move move;

                try {
                    move = futureMove.get(MOVE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    System.err.println("⏳ Timeout: Player " + studentID + " took too long to move! Ending game.");
                    break;
                } catch (Exception e) {
                    System.err.println("❌ Runtime error in nextMove() for Player " + studentID + ": " + e.getMessage());
                    break;
                }

                if (move == null) break;
                if (!player.board.applyMove(move)) {
                    System.err.println("🚫 Invalid move by Player " + studentID + ": " + move);
                    break;
                }

                if (enableSnapshots) {
                    saveSnapshot(player.board, writer, player.board.getStepCount(), false);
                }
            }
        } catch (IOException e) {
            System.err.println("📍 Error writing snapshot for Player " + studentID + ": " + e.getMessage());
        } finally {
            executor.shutdownNow();
        }

        return player.board.getStepCount();
    }

    public static Player initializePlayer(String studentID, Board board) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Player> future = executor.submit(() -> {
            try {
                Class<?> playerClass = Class.forName("players.Player" + studentID);
                return (Player) playerClass.getDeclaredConstructor(Board.class).newInstance(board);
            } catch (Exception e) {
                return null;
            }
        });

        try {
            return future.get(INIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            System.err.println("⏳ Timeout: Player " + studentID + " took too long to initialize!");
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error initializing player " + studentID + ": " + e.getMessage());
            return null;
        } finally {
            executor.shutdownNow();
        }
    }

    private static int extractBoardNumber(String boardFile) {
        try {
            String name = new File(boardFile).getName();
            String digits = name.replaceAll("\\D+", "");
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void saveSnapshot(Board board, BufferedWriter writer, int step, boolean isInitial) throws IOException {
        writer.write((isInitial ? "Initial Board:\n" : "Step " + step + ":\n"));
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (i == board.getPlayerRow() && j == board.getPlayerCol()) {
                    writer.write("* ");
                } else if (board.isVisited(i, j)) {
                    writer.write("  ");
                } else {
                    writer.write(board.getValueAt(i, j) + " ");
                }
            }
            writer.newLine();
        }
        writer.newLine();
    }
}
