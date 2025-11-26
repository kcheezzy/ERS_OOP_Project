import java.util.*;

public class Applicant {

    private static final String MASTER_FILE = "data/applicants.txt";
    private static final String STUDENT_MASTER_FILE = "data/students.txt";

    // -------------------- SIGN UP --------------------
    public static void signup() {
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

        // Add to applicants master file
        List<String> master = Utils.readFile(MASTER_FILE);
        master.add(applicantId + "|" + password);
        Utils.writeFile(MASTER_FILE, master);

        // Create applicant file
        List<String> info = new ArrayList<>();
        info.add(lastName); // 0
        info.add(firstName); // 1
        info.add(middleName); // 2
        info.add(extension); // 3
        info.add(String.valueOf(age)); // 4
        info.add(birthdate); // 5
        info.add(course); // 6
        info.add(address); // 7
        info.add(father); // 8
        info.add(mother); // 9
        info.add(status); // 10
        info.add(""); // 11 → student ID placeholder
        Utils.writeFile("data/" + applicantId + ".txt", info);

        System.out.println("Sign up successful. Your Applicant ID: " + applicantId);
    }

    // -------------------- LOGIN --------------------
    public static String login() {
        String id = Utils.input("Applicant ID: ");
        String pass = Utils.hashPassword(Utils.input("Password: "));

        List<String> master = Utils.readFile(MASTER_FILE);
        boolean found = false;
        for (String line : master) {
            String[] parts = line.split("\\|");
            if (parts[0].equals(id) && parts[1].equals(pass)) {
                found = true;
                break;
            }
        }
        if (!found) {
            System.out.println("Invalid ID or password.");
            return null;
        }

        List<String> info = Utils.readFile("data/" + id + ".txt");

        // Fix old/wrong student ID if it exists (like 99)
        if (info.size() <= 11) {
            while (info.size() <= 11)
                info.add("");
            Utils.writeFile("data/" + id + ".txt", info);
        } else if (info.get(11).equals("99")) {
            info.set(11, "");
            Utils.writeFile("data/" + id + ".txt", info);
        }

        System.out.println("\nWelcome [" + id + ": " + info.get(1) + "]");
        return id;
    }

    // -------------------- MENU --------------------
    public static void menu(String applicantId) {
        while (true) {
            System.out.println("\n--- APPLICANT MENU ---");
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
                    System.out.println("Logged out.\n");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // -------------------- VIEW INFO --------------------
    public static void viewInfo(String applicantId) {
        List<String> info = Utils.readFile("data/" + applicantId + ".txt");
        String[] labels = { "Last Name", "First Name", "Middle Name", "Extension Name",
                "Age", "Birthdate", "Course Applying", "Address", "Father", "Mother", "Status" };
        for (int i = 0; i <= 10; i++)
            System.out.println(labels[i] + ": " + info.get(i));

        if (!info.get(11).isEmpty())
            System.out.println("Student ID: " + info.get(11));
    }

    // -------------------- VIEW PERMIT --------------------
    public static void viewPermit(String applicantId) {
        List<String> info = Utils.readFile("data/" + applicantId + ".txt");
        String status = info.get(10);

        String date = "2025-12-" + (10 + new Random().nextInt(10));
        String time = (8 + new Random().nextInt(3)) + ":00 AM";
        String room = "Room " + (100 + new Random().nextInt(50));

        System.out.println("\n--- Exam Permit ---");
        System.out.println("Exam Date: " + date);
        System.out.println("Time: " + time);
        System.out.println("Room: " + room);
        System.out.println("Status: " + status);
    }

    // -------------------- VIEW STATUS --------------------
    public static void viewStatus(String applicantId) {
        List<String> info = Utils.readFile("data/" + applicantId + ".txt");
        String status = info.get(10);
        System.out.println("\n--- APPLICATION STATUS ---");
        System.out.println("Status: " + status);

        if (status.equals("Passed")) {
            String studentId = info.get(11);
            if (studentId.isEmpty()) {
                // auto-transfer if not transferred yet
                transferToStudent(info, applicantId);
                info = Utils.readFile("data/" + applicantId + ".txt"); // read updated info
                studentId = info.get(11);
            }
            System.out.println("Congratulations, you are now a student!");
            System.out.println("Student ID: " + studentId);
            System.out.println("Default Password: " + generateDefaultPassword(info));

        }
    }

    // -------------------- TRANSFER TO STUDENT --------------------
    public static void transferToStudent(List<String> info, String applicantId) {
        List<String> studentsMaster = Utils.readFile(STUDENT_MASTER_FILE);
        studentsMaster.removeIf(line -> line.isEmpty() || !line.startsWith("ID-"));

        // Generate next student ID
        String studentId = "ID-0001";
        if (!studentsMaster.isEmpty()) {
            String lastLine = studentsMaster.get(studentsMaster.size() - 1);
            String lastId = lastLine.split("\\|")[0];
            int lastNum = Integer.parseInt(lastId.split("-")[1]);
            studentId = String.format("ID-%04d", lastNum + 1);
        }

        String defaultPass = generateDefaultPassword(info);
        studentsMaster.add(studentId + "|" + Utils.hashPassword(defaultPass));
        Utils.writeFile(STUDENT_MASTER_FILE, studentsMaster);

        // Copy applicant info to student file
        List<String> studentInfo = new ArrayList<>(info);
        while (studentInfo.size() <= 22)
            studentInfo.add("");
        Utils.writeFile("data/" + studentId + ".txt", studentInfo);

        // Save student ID to applicant file
        while (info.size() <= 11)
            info.add("");
        info.set(11, studentId);
        Utils.writeFile("data/" + applicantId + ".txt", info);
    }

    // -------------------- DEFAULT PASSWORD --------------------
    static String generateDefaultPassword(List<String> info) {
        String[] birthParts = info.get(5).split("/"); // MM/DD/YYYY
        return info.get(0).toUpperCase().replaceAll(" ", "") + birthParts[0] + birthParts[1];
    }
    // -------------------- connect to GUI --------------------
    public static String guiSignup(
        String lastName, String firstName, String middleName, String extension,
        int age, String birthdate, String course, String address,
        String father, String mother, String password) {

        String applicantId = "APP" + (1000 + new Random().nextInt(9000));
        String hashedPass = Utils.hashPassword(password);

        // Save sa master file
        List<String> master = Utils.readFile(MASTER_FILE);
        master.add(applicantId + "|" + hashedPass);
        Utils.writeFile(MASTER_FILE, master);

        // Save applicant file
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
        info.add("Pending"); // status
        info.add(""); // student ID
        Utils.writeFile("data/" + applicantId + ".txt", info);

        return applicantId;
    }
}