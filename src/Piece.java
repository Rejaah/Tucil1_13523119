import java.util.*;

public class Piece {
    private final char[][] piece;
    private final char label;

    public Piece(List<String> lines) {
        this.label = lines.get(0).trim().charAt(0);

        int maxCols = 0;
        for (String line : lines) {
            int lastIdx = line.lastIndexOf(line.replaceAll(" ","").charAt(line.replaceAll(" ", "").length() - 1));
            if (lastIdx + 1 > maxCols) {
                maxCols = lastIdx + 1;
            }
        }

        int rows = lines.size();
        piece  = new char[rows][maxCols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < maxCols; j++) {
                piece[i][j] = ' ';
            }
        }

        for (int i = 0; i < rows; i++) {
            String line = lines.get(i);
            for (int j = 0; j < line.length(); j++) {
                piece[i][j] = line.charAt(j);
            }
        }
    }

    public Piece(char[][] piece, char label) {
        this.piece = piece;
        this.label = label;
    }

    public char[][] getPiece() {
        return piece;
    }

    public char getLabel() {
        return label;
    }

    private char[][] trimShape(char[][] piece) {
        int rows = piece.length;
        int cols = piece[0].length;
        int top = rows, bottom = 0, left = cols, right = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (piece[i][j] != ' ') {
                    top = Math.min(top, i);
                    bottom = Math.max(bottom, i);
                    left = Math.min(left, j);
                    right = Math.max(right, j);
                }
            }
        }
        char[][] trimmed = new char[bottom - top + 1][right - left + 1];
        for (int i = top; i <= bottom; i++) {
            for (int j = left; j <= right; j++) {
                trimmed[i - top][j - left] = piece[i][j];
            }
        }
        return trimmed;
    }

    private char[][] rotate(char[][] piece) {
        int rows = piece.length;
        int cols = piece[0].length;
        char[][] rotated = new char[cols][rows];
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                rotated[i][j] = piece[rows - j - 1][i];
            }
        }
        return trimShape(rotated);
    }

    private char[][] flipHorizontal(char[][] piece) {
        int rows = piece.length;
        int cols = piece[0].length;
        char[][] flipped = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flipped[i][j] = piece[i][cols - j - 1];
            }
        }
        return trimShape(flipped);
    }

    private char[][] flipVertical(char[][] piece) {
        int rows = piece.length;
        int cols = piece[0].length;
        char[][] flipped = new char[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flipped[i][j] = piece[rows - i - 1][j];
            }
        }
        return trimShape(flipped);
    }    

    public List<Piece> generateVariations() {
        Set<String> uniqueVariations = new HashSet<>();
        List<Piece> variations = new ArrayList<>();
    
        char[][] currentPiece = piece;
    
        for (int i = 0; i < 4; i++) {
            for (char[][] variation : new char[][][]{currentPiece, flipHorizontal(currentPiece), flipVertical(currentPiece)}) {
                String key = Arrays.deepToString(variation);
                if (uniqueVariations.add(key)) { 
                    variations.add(new Piece(variation, label));
                }
            }
            currentPiece = rotate(currentPiece);
        }
    
        return variations;
    }    
}
