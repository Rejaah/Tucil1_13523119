import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.print("\nMasukkan file input: ");
        String inputFile = userInput.nextLine();

        try {
            File file = new File(inputFile);
            Scanner input = new Scanner(file);
            int N = input.nextInt();
            int M = input.nextInt();
            int P = input.nextInt();
            input.nextLine();

            String caseType = input.nextLine();
            if (!caseType.equals("DEFAULT")) {
                System.out.println("Jenis kasus yang tersedia hanya DEFAULT");
                input.close();
                return;
            }

            List<Piece> pieces = new ArrayList<>();
            List<String> block = new ArrayList<>();
            char lastLetter = ' ';
            while (input.hasNextLine()) {
                String line = input.nextLine();
                int firstIndex = 0;
                while (firstIndex < line.length() && line.charAt(firstIndex) == ' ') {
                    firstIndex++;
                }
                if (firstIndex == line.length()) {
                    continue;
                }
                char firstChar = line.charAt(firstIndex);
                if (lastLetter != ' ' && firstChar != lastLetter) {
                    if (!block.isEmpty()) {
                        pieces.add(new Piece(block));
                        block.clear();
                    }
                }
                lastLetter = firstChar;
                block.add(line);
            }
            if (!block.isEmpty()) {
                pieces.add(new Piece(block));
            }
            input.close();

            if (pieces.size() != P) {
                System.out.println("Jumlah blok terdata tidak sesuai dengan input");
                return;
            }
            
            Board board = new Board(N, M);
            Solver solver = new Solver(board, pieces);
            long startTime = System.currentTimeMillis();
            boolean solve = solver.runSolver();
            long endTIme = System.currentTimeMillis();
            long executionTime = endTIme - startTime;
            StringBuilder result = new StringBuilder();

            if (solve) {
                result.append("Solusi ditemukan!\n");
                result.append(board.getBoardString(false)).append("\n");

                System.out.println("\nSolusi ditemukan!");
                System.out.println(board.getBoardString(true));
            } else {
                result.append("Solusi tidak ditemukan.\n");
                System.out.println("Solusi tidak ditemukan.");
            }

            result.append("Waktu pencarian: ").append(executionTime).append(" ms\n");
            result.append("Banyak kasus yang ditinjau: ").append(solver.getAttemptCount()).append("\n");
            System.out.println("Waktu pencarian: " + executionTime + " ms\n");
            System.out.println("Banyak kasus yang ditinjau: " + solver.getAttemptCount() + "\n");

            System.out.println("Apakah anda ingin menyimpan solusi? (y/n)");
            String save = userInput.nextLine().trim().toLowerCase();

            if (save.equals("y")) {
                System.out.println("\nMasukkan nama file output: ");
                String outputFile = userInput.nextLine();
                try (PrintWriter writer = new PrintWriter(outputFile)) {
                    writer.println(result);
                    System.out.println("\nHasil telah disimpan di " + outputFile + "\n");
                } catch (IOException e) {
                    System.out.println("Gagal menyimpan file output: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("\nFile tidak ditemukan\n");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println();
        userInput.close();
    }
}
