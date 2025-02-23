import java.util.*;

public class Board {
    private final int rows, cols;
    private final char[][] board;
    private final Map<Character, String> colorMap = new HashMap<>();
    
    // ANSI Colors untuk memberikan warna pada piece
    private static final String[] colors = {
        "\u001B[31m", "\u001B[32m", 
        "\u001B[33m", "\u001B[34m", 
        "\u001B[35m", "\u001B[36m",
        "\u001B[91m", "\u001B[92m",
        "\u001B[93m", "\u001B[94m",
        "\u001B[95m", "\u001B[96m",
        "\u001B[97m", "\u001B[90m",
        "\u001B[37m", "\u001B[30m",
        "\u001B[41m", "\u001B[42m",
        "\u001B[43m", "\u001B[44m",
        "\u001B[45m", "\u001B[46m",
        "\u001B[47m", "\u001B[100m",
        "\u001B[101m", "\u001B[102m",
        "\u001B[103m"
    };

    private static final String reset = "\u001B[0m";

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.board = new char[rows][cols];
        for (char[] row : board) {
            for (int i = 0; i < row.length; i++) {
                row[i] = '.';
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public char[][] getBoard() {
        return board;
    }

    public boolean canPlacePiece(Piece piece, int row, int col) {
        char[][] shape = piece.getPiece();
        int pieceRows = shape.length;
        int pieceCols = shape[0].length;

        if (row + pieceRows > rows || col + pieceCols > cols) {
            return false;
        }

        for (int r = 0; r < pieceRows; r++) {
            for (int c = 0; c < pieceCols; c++) {
                if (shape[r][c] != ' ' && board[row + r][col + c] != '.') {
                    return false;
                }
            }
        }

        for (int r = 0; r < pieceRows; r++) {
            for (int c = 0; c < pieceCols; c++) {
                board[row + r][col + c] = shape[r][c];
                if (!colorMap.containsKey(shape[r][c])) {
                    colorMap.put(shape[r][c], colors[colorMap.size()]);
                }
            }
        }
        return true;
    }

    public void removePiece(Piece piece, int row, int col) {
        char[][] shape = piece.getPiece();
        int pieceRows = shape.length;
        int pieceCols = shape[0].length;

        for (int r = 0; r < pieceRows; r++) {
            for (int c = 0; c < pieceCols; c++) {
                if (shape[r][c] != ' ') {
                    board[row + r][col + c] = '.';
                }
            }
        }
    }

    public boolean isFull() {
        for (char[] row : board) {
            for (char cell : row) {
                if (cell == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    public String getBoardString(boolean withColor) {
        StringBuilder sb = new StringBuilder();
        for (char[] row : board) {
            for (char cell : row) {
                if (withColor && cell != '.' && colorMap.containsKey(cell)) {
                    sb.append(colorMap.get(cell)).append(cell).append(' ').append(reset);
                } else {
                    sb.append(cell).append(' ');
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}