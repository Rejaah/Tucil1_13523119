import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        System.out.print("Masukkan file input: ");
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
            }
        } catch (FileNotFoundException e) {
            System.out.println("File tidak ditemukan");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        userInput.close();
    }
}
