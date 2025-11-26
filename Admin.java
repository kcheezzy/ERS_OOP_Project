import java.util.*;

public class Admin {

    private static final String ADMIN_PASS = "admin123";
    private static final String APPLICANT_MASTER_FILE = "data/applicants.txt";
    private static final String STUDENT_MASTER_FILE = "data/students.txt";

    public static void login() {
        String pass = Utils.input("Enter Admin Password: ");
        if (!pass.equals(ADMIN_PASS)) {
            System.out.println("Wrong password! Access denied.");
            return;
        }
        System.out.println("Admin logged in successfully.\n");
        adminMenu();
    }

    private static void adminMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View Courses");
            System.out.println("2. Select Course -> List Applicants");
            System.out.println("3. Select Course -> Student Grades");
            System.out.println("4. Generate Course Schedule");
            System.out.println("5. Log Out");
            String choice = Utils.input("Choice: ");

            switch (choice) {
                case "1":
                    viewCourses();
                    break;
                case "2":
                    selectCourseApplicants();
                    break; // Added missing break
                case "3":
                    selectCourseStudentGrades();
                    break;
                case "4":
                    adminGenerateCourseSchedule();
                    break;
                case "5":
                    exit = true;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void adminGenerateCourseSchedule() {
        System.out.println("\nGENERATE COURSE SCHEDULE");
        System.out.println("1. BSCS");
        System.out.println("2. BSIS");
        System.out.println("3. BSIT");
        System.out.println("4. BASALT");
        System.out.println("5. BSES");

        String choice = Utils.input("Choose course: ");

        String course = "";
        switch (choice) {
            case "1":
                course = "BSCS";
                break;
            case "2":
                course = "BSIS";
                break;
            case "3":
                course = "BSIT";
                break;
            case "4":
                course = "BASALT";
                break;
            case "5":
                course = "BSES";
                break;
            default:
                System.out.println("Invalid course.");
                return;
        }

        Student s = new Student("ADMIN", "Admin", "admin", course);

        s.generateSchedule(course);

        System.out.println("\nSchedule generated for: " + course);
        Utils.input("Press ENTER to return to Admin Menu...");
    }

    private static void viewCourses() {
        // Hardcoded for simplicity; can also read from courses.txt
        System.out.println("\nAvailable Courses:");
        System.out.println("1. BSCS");
        System.out.println("2. BSIS");
        System.out.println("3. BSIT");
        System.out.println("4. BASALT");
        System.out.println("5. BSES");

    }

    private static void selectCourseApplicants() {
        String course = Utils.input("Enter course name: ");
        List<String> master = Utils.readFile(APPLICANT_MASTER_FILE);
        if (master.isEmpty()) {
            System.out.println("No applicants yet.");
            return;
        }

        List<String> validIds = new ArrayList<>();
        System.out.println("\nApplicants for " + course + ":");
        for (String line : master) {
            String[] parts = line.split("\\|");
            String applicantId = parts[0];
            List<String> info = Utils.readFile("data/" + applicantId + ".txt");
            if (info.size() > 6 && info.get(6).equalsIgnoreCase(course)) {
                String fullName = info.get(1); // First Name
                if (!info.get(2).isEmpty())
                    fullName += " " + info.get(2); // Middle Name
                if (!info.get(0).isEmpty())
                    fullName += " " + info.get(0); // Last Name
                System.out.println(applicantId + " | " + fullName + " | Status: " + info.get(10));
                validIds.add(applicantId);
            }
        }

        String appNo = Utils.input("Enter Applicant ID to approve/reject (0 to go back): ");
        if (appNo.equals("0"))
            return;

        if (!validIds.contains(appNo)) {
            System.out.println("Invalid Applicant ID.");
            return;
        }

        String action = Utils.input("Approve (A) / Reject (R): ").toUpperCase();
        List<String> info = Utils.readFile("data/" + appNo + ".txt");
        switch (action) {
            case "A":
                info.set(10, "Passed");
                Utils.writeFile("data/" + appNo + ".txt", info);
                Applicant.transferToStudent(info, appNo);
                break;
            case "R":
                info.set(10, "Failed");
                Utils.writeFile("data/" + appNo + ".txt", info);
                break;
            default:
                System.out.println("Invalid action!");
        }

    }

    private static void selectCourseStudentGrades() {
        String course = Utils.input("Enter course name: ");
        List<String> master = Utils.readFile(STUDENT_MASTER_FILE);
        if (master.isEmpty()) {
            System.out.println("No students yet.");
            return;
        }

        List<String> validIds = new ArrayList<>();
        System.out.println("\nStudents for " + course + ":");
        for (String line : master) {
            String[] parts = line.split("\\|");
            String studentId = parts[0];
            List<String> info = Utils.readFile("data/" + studentId + ".txt");
            if (info.size() > 10 && info.get(6).equalsIgnoreCase(course) && info.get(10).equalsIgnoreCase("Passed")) {
                String fullName = info.get(1); // First Name
                if (!info.get(2).isEmpty())
                    fullName += " " + info.get(2); // Middle Name
                if (!info.get(0).isEmpty())
                    fullName += " " + info.get(0); // Last Name
                System.out.println(studentId + " | " + fullName);
                validIds.add(studentId);
            }
        }

        if (validIds.isEmpty()) {
            System.out.println("No students in this course.");
            return;
        }

        String studId = Utils.input("Enter Student ID to add grades (0 to go back): ");
        if (studId.equals("0"))
            return;

        if (!validIds.contains(studId)) {
            System.out.println("Invalid Student ID.");
            return;
        }

        List<String> info = Utils.readFile("data/" + studId + ".txt");
        double sum = 0;
        for (int i = 1; i <= 5; i++) {
            boolean validGrade = false;
            while (!validGrade) {
                String gradeStr = Utils.input("Enter grade for Subject#" + i + ": ");
                try {
                    double grade = Double.parseDouble(gradeStr);
                    sum += grade;
                    // Store grade in info list (indices 11-15 for subjects 1-5)
                    if (info.size() <= 10 + i) {
                        info.add(gradeStr);
                    } else {
                        info.set(10 + i, gradeStr);
                    }
                    validGrade = true;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid grade. Please enter a number.");
                }
            }
        }
        double average = sum / 5;
        String avgStr = String.valueOf(average);
        // Store average in info list (index 16)
        if (info.size() <= 16) {
            info.add(avgStr);
        } else {
            info.set(16, avgStr);
        }
        Utils.writeFile("data/" + studId + ".txt", info);
        System.out.println("Grades added successfully. Average: " + average);
    }

    // Optional: List all pending applicants only
    private static void listPendingApplicants(String course) {
        List<String> master = Utils.readFile(APPLICANT_MASTER_FILE);
        for (String line : master) {
            String[] parts = line.split("\\|");
            String applicantId = parts[0];
            List<String> info = Utils.readFile("data/" + applicantId + ".txt");
            if (info.size() > 6 && info.get(6).equalsIgnoreCase(course) && info.get(10).equalsIgnoreCase("Pending")) {
                System.out.println(applicantId + " | " + info.get(1) + " " + info.get(2));
            }
        }
    }

    // Optional: helper for course validation
    private static boolean courseExists(String course) {
        List<String> courses = Arrays.asList("BSCS", "BSIS", "BSIT", "BASALT", "BSES");
        return courses.contains(course);
    }

}