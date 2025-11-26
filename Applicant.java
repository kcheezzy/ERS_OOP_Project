import java.util.*;

public class Applicant {

    private static final String MASTER_FILE = "data/applicants.txt";
    private static final String STUDENT_MASTER_FILE = "data/students.txt";

    // -------------------- SIGN UP --------------------
    public static String signup() {
        System.out.println("\n--- APPLICANT SIGN UP ---");

        String lastName = Utils.input("Last Name: ");
        String firstName = Utils.input("First Name: ");
        String middleName = Utils.input("Middle Name: ");
        String extension = Utils.input("Extension Name (if any): ");
        int age = Utils.inputInt("Age: ");
        String birthdate = Utils.input("Birthdate (MM/DD/YYYY): ");
        String course = Utils.input("Course Applying: ");
        String address = Utils.input("Address: ");
        String father = Utils.input("Father's Name: ");
        String mother = Utils.input("Mother's Name: ");
        String password = Utils.hashPassword(Utils.input("Password: "));

        String applicantId = "APP" + (1000 + new Random().nextInt(9000));
        String status = "Pending";

        // Save login credentials
        List<String> master = Utils.readFile(MASTER_FILE);
        master.add(applicantId + "|" + password);
        Utils.writeFile(MASTER_FILE, master);

        // Save personal information
        List<String> info = new ArrayList<>();
        info.add(lastName);
        info.add(firstName);
        info.add(middleName);
        info.add(extension);
        info.add(String.valueOf(age));
        info.add(birthdate);
        info.add(course);
        info.add(address);
        info.add(father);
        info.add(mother);
        info.add(status);
        info.add(""); // Student ID

        Utils.writeFile("data/" + applicantId + ".txt", info);

        System.out.println("\nSign up successful! Your Applicant ID: " + applicantId);
        return applicantId;
    }

    // -------------------- LOGIN --------------------
    public static String login() {
        System.out.println("\n--- APPLICANT LOGIN ---");
        String id = Utils.input("Applicant ID: ");
        String pass = Utils.hashPassword(Utils.input("Password: "));

        List<String> master = Utils.readFile(MASTER_FILE);

        for (String line : master) {
            String[] parts = line.split("\\|");
            if (parts[0].equals(id) && parts[1].equals(pass)) {
                System.out.println("\nLogin successful!");
                return id;
            }
        }

        System.out.println("Invalid ID or password.");
        return null;
    }

    // -------------------- DASHBOARD --------------------
    public static void menu(String applicantId) {
        while (true) {
            System.out.println("\n===== APPLICANT DASHBOARD =====");
            System.out.println("Applicant ID: " + applicantId);

            System.out.println("1. View Information");
            System.out.println("2. View Permit");
            System.out.println("3. View Status");
            System.out.println("4. Logout");

            int choice = Utils.inputInt("Enter choice: ");

            switch (choice) {
                case 1 -> viewInfo(applicantId);
                case 2 -> viewPermit(applicantId);
                case 3 -> viewStatus(applicantId);
                case 4 -> {
                    System.out.println("Logged out.");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // -------------------- VIEW INFORMATION --------------------
    public static void viewInfo(String applicantId) {
        List<String> info = Utils.readFile("data/" + applicantId + ".txt");

        String[] labels = {
            "Last Name", "First Name", "Middle Name", "Extension",
            "Age", "Birthdate", "Course Applying",
            "Address", "Father", "Mother", "Status"
        };

        for (int i = 0; i <= 10; i++) {
            System.out.println(labels[i] + ": " + info.get(i));
        }

        if (!info.get(11).isEmpty()) {
            System.out.println("Student ID: " + info.get(11));
        }
    }

    // -------------------- VIEW PERMIT --------------------
    public static void viewPermit(String applicantId) {
        List<String> info = Utils.readFile("data/" + applicantId + ".txt");

        System.out.println("\n--- EXAM PERMIT ---");
        System.out.println("Applicant: " + info.get(1) + " " + info.get(0));
        System.out.println("Exam Date: 2025-12-15");
        System.out.println("Time: 9:00 AM");
        System.out.println("Room: Room 105");
    }

    // -------------------- VIEW STATUS --------------------
    public static void viewStatus(String applicantId) {
        List<String> info = Utils.readFile("data/" + applicantId + ".txt");
        String status = info.get(10);

        System.out.println("\n--- APPLICATION STATUS ---");
        System.out.println("Status: " + status);

        // If PASSED → Ask Proceed or Reject
        if (status.equalsIgnoreCase("Passed")) {

            // Already enrolled
            if (!info.get(11).isEmpty()) {
                System.out.println("You are already enrolled as a Student.");
                System.out.println("Student ID: " + info.get(11));
                return;
            }

            System.out.println("\n1. Proceed with Enrollment");
            System.out.println("2. Reject / Not Continue");
            int choice = Utils.inputInt("Choose: ");

            if (choice == 1) {
                proceedEnrollment(info, applicantId);
            } else {
                System.out.println("You chose not to continue.");
            }
        }
    }

    // -------------------- PROCEED TO ENROLLMENT --------------------
    private static void proceedEnrollment(List<String> info, String applicantId) {

        List<String> master = Utils.readFile(STUDENT_MASTER_FILE);

        String newId = "ID-0001";

        if (!master.isEmpty()) {
            String last = master.get(master.size() - 1).split("\\|")[0];
            int num = Integer.parseInt(last.replace("ID-", ""));
            newId = String.format("ID-%04d", num + 1);
        }

        String defaultPass = generateDefaultPassword(info);

        master.add(newId + "|" + Utils.hashPassword(defaultPass));
        Utils.writeFile(STUDENT_MASTER_FILE, master);

        info.set(11, newId); 
        Utils.writeFile("data/" + applicantId + ".txt", info);

        System.out.println("\nEnrollment Successful!");
        System.out.println("Your Student ID: " + newId);
        System.out.println("Default Password: " + defaultPass);
    }

    // -------------------- DEFAULT PASSWORD --------------------
    private static String generateDefaultPassword(List<String> info) {
        String[] birth = info.get(5).split("/");
        return info.get(0).toUpperCase() + birth[0] + birth[1];
    }
}
