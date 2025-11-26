import java.io.*;
import java.util.*;
import java.security.MessageDigest;

public class Utils {

    private static Scanner sc = new Scanner(System.in);

    // Read input as string
    public static String input(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    // Read input as integer
    public static int inputInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number, try again.");
            }
        }
    }

    // Read file into list of strings
    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        File f = new File(filename);
        if (!f.exists())
            return lines; // return empty if file doesn't exist
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lines;
    }

    // Write list of strings to file (overwrite)
    public static void writeFile(String filename, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new File(filename))) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Simple hash function for password
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return password; // fallback
        }
    }
}