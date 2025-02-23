import java.util.*;
public class Solver {
    private final Board board;
    private final List<Piece> pieces;
    private long attemptCount = 0;

    public Solver(Board board, List<Piece> pieces) {
        this.board = board;
        this.pieces = pieces;
    }

    public long getAttemptCount() {
        return attemptCount;
    }

    public boolean runSolver() {
        return solve(0);
    }

    private boolean solve(int index) {
        if (index == pieces.size()) {
            return board.isFull();
        }

        Piece piece = pieces.get(index);
        List<Piece> variations = piece.generateVariations();

        for (Piece variant : variations) {
            for (int r = 0; r < board.getRows(); r++) {
                for (int c = 0; c < board.getCols(); c++) {
                    attemptCount++;
                    if (board.canPlacePiece(variant, r, c)) {
                        if (solve(index+1)) {
                            return true;
                        }
                        board.removePiece(variant, r, c);
                    }
                }
            }
        }
        return false;
    }
}
