import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class PuzzleSolverGUI {
    private JFrame frame;
    private JButton loadButton, solveButton, saveTxtButton, savePngButton;
    private JPanel mainPanel, boardContainer, boardPanel;
    private JLabel executionTimeLabel, attemptCountLabel;
    private Board board;
    private List<Piece> pieces;
    private Solver solver;
    private int N, M;
    private Map<Character, Color> colorMap;

    public PuzzleSolverGUI() {
        frame = new JFrame("IQ Puzzler Pro Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        loadButton = new JButton("Load File");
        solveButton = new JButton("Solve");
        saveTxtButton = new JButton("Save To TXT");
        savePngButton = new JButton("Save To PNG");

        topPanel.add(loadButton);
        topPanel.add(solveButton);
        topPanel.add(saveTxtButton);
        topPanel.add(savePngButton);

        frame.add(topPanel, BorderLayout.NORTH);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        frame.add(mainPanel, BorderLayout.CENTER);

        boardContainer = new JPanel();
        boardContainer.setPreferredSize(new Dimension(300, 300));
        boardContainer.setLayout(new BorderLayout());
        
        boardPanel = new JPanel();
        boardPanel.setPreferredSize(new Dimension(300, 300));
        boardContainer.add(boardPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        executionTimeLabel = new JLabel("Waktu Pencarian: -");
        attemptCountLabel = new JLabel("Banyak kasus yang ditinjau: -");
        infoPanel.add(executionTimeLabel);
        infoPanel.add(attemptCountLabel);

        mainPanel.add(boardContainer, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(_ -> loadFile());
        solveButton.addActionListener(_ -> solvePuzzle());
        saveTxtButton.addActionListener(_ -> saveToTxt());
        savePngButton.addActionListener(_ -> saveToPng());

        frame.setVisible(true);
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser("../test/input");
        int returnValue = fileChooser.showOpenDialog(frame);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Scanner scanner = new Scanner(selectedFile);
                N = scanner.nextInt();
                M = scanner.nextInt();
                int P = scanner.nextInt();
                scanner.nextLine();
                
                if (!scanner.nextLine().equals("DEFAULT")) {
                    JOptionPane.showMessageDialog(frame, "Jenis kasus yang tersedia hanya DEFAULT", "Error", JOptionPane.ERROR_MESSAGE);
                    scanner.close();
                    return;
                }
                
                pieces = new ArrayList<>();
                List<String> currentBlock = new ArrayList<>();
                char lastLetter = ' ';
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.isEmpty()) continue;
                    
                    int firstNonSpaceIndex = 0;
                    while (firstNonSpaceIndex < line.length() && line.charAt(firstNonSpaceIndex) == ' ') {
                        firstNonSpaceIndex++;
                    }
                    if (firstNonSpaceIndex == line.length()) continue;
                    
                    char firstChar = line.charAt(firstNonSpaceIndex);
                    if (lastLetter != ' ' && firstChar != lastLetter) {
                        if (!currentBlock.isEmpty()) {
                            pieces.add(new Piece(currentBlock));
                            currentBlock.clear();
                        }
                    }
                    
                    lastLetter = firstChar;
                    currentBlock.add(line);
                }
                
                if (!currentBlock.isEmpty()) {
                    pieces.add(new Piece(currentBlock));
                }
                
                scanner.close();
                
                if (pieces.size() != P) {
                    JOptionPane.showMessageDialog(frame, "Jumlah blok terdata tidak sesuai dengan input", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                board = new Board(N, M);
                solver = new Solver(board, pieces);
                JOptionPane.showMessageDialog(frame, "File berhasil dimuat!", "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "File tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }    

    private void solvePuzzle() {
        if (board == null || solver == null) {
            JOptionPane.showMessageDialog(frame, "Load File terlebih dahulu!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        long startTime = System.currentTimeMillis();
        boolean solve = solver.runSolver();
        long endTime = System.currentTimeMillis();
        
        executionTimeLabel.setText("Waktu Pencarian: " + (endTime - startTime) + " ms");
        attemptCountLabel.setText("Banyak kasus yang ditinjau: " + solver.getAttemptCount());
        
        if (solve) {
            drawBoard();
        } else {
            JOptionPane.showMessageDialog(frame, "Tidak ada solusi!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void drawBoard() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(N, M));
        boardPanel.setPreferredSize(new Dimension(300, 300));
        char[][] grid = board.getBoard();
        
        colorMap = new HashMap<>();
        Random rand = new Random();
        
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < M; c++) {
                char block = grid[r][c];
                JPanel cell = new JPanel(new BorderLayout());
                JLabel label = new JLabel(String.valueOf(block), SwingConstants.CENTER);
                
                if (block != ' ') {
                    colorMap.putIfAbsent(block, new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));
                    cell.setBackground(colorMap.get(block));
                } else {
                    cell.setBackground(Color.WHITE);
                }
                
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cell.add(label, BorderLayout.CENTER);
                boardPanel.add(cell);
            }
        }
        
        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void saveToTxt() {
        JFileChooser fileChooser = new JFileChooser("../test/output");
        fileChooser.setDialogTitle("Simpan sebagai");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int returnValue = fileChooser.showSaveDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getAbsolutePath();
            
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }
    
            try (PrintWriter writer = new PrintWriter(fileName)) {
                writer.print(board.getBoardString(false));
                writer.println();
                writer.println(executionTimeLabel.getText());
                writer.println();
                writer.println(attemptCountLabel.getText());
                JOptionPane.showMessageDialog(frame, "Hasil disimpan ke " + fileName, "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(frame, "Gagal menyimpan file!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }    
  
    private void saveToPng() {
        BufferedImage image = new BufferedImage(boardContainer.getWidth(), boardContainer.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        boardContainer.printAll(g2d);
        g2d.dispose();
        
        JFileChooser fileChooser = new JFileChooser("../test/output");
        fileChooser.setDialogTitle("Simpan sebagai");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int returnValue = fileChooser.showSaveDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getAbsolutePath();
            
            if (!fileName.endsWith(".png")) {
                fileName += ".png";
            }
    
            try {
                ImageIO.write(image, "png", new File(fileName));
                JOptionPane.showMessageDialog(frame, "Gambar disimpan ke " + fileName, "Info", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(frame, "Gagal menyimpan gambar!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PuzzleSolverGUI::new);
    }
}