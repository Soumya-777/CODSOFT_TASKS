import java.sql.*;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n===== STUDENT COURSE REGISTRATION =====");
            System.out.println("1. Add Student");
            System.out.println("2. Add Course");
            System.out.println("3. View Courses");
            System.out.println("4. Register for Course");
            System.out.println("5. Drop Course");
            System.out.println("6. View Student Courses");
            System.out.println("7. Exit");

            choice = getInt("Enter your choice: ");

            switch (choice) {

                case 1:
                    addStudent();
                    break;

                case 2:
                    addCourse();
                    break;

                case 3:
                    viewCourses();
                    break;

                case 4:
                    registerCourse();
                    break;

                case 5:
                    dropCourse();
                    break;

                case 6:
                    viewStudentCourses();
                    break;

                case 7:
                    System.out.println("Thank you for using the system.");
                    break;

                default:
                    System.out.println("Invalid choice! Please enter 1-7.");
            }

        } while (choice != 7);

        sc.close();
    }

    // Add a new student
    public static void addStudent() {

        int id = getInt("Enter student ID: ");

        System.out.print("Enter student name: ");
        String name = sc.nextLine();

        if (name.isEmpty()) {
            System.out.println("Student name cannot be empty.");
            return;
        }

        String sql = "INSERT INTO students(student_id, name) VALUES (?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, name);

            ps.executeUpdate();

            System.out.println("Student added successfully.");

        } catch (SQLException e) {
            System.out.println("Could not add student.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    // Add a new course
    public static void addCourse() {

        System.out.print("Enter course code: ");
        String code = sc.nextLine();

        System.out.print("Enter course title: ");
        String title = sc.nextLine();

        System.out.print("Enter course description: ");
        String description = sc.nextLine();

        int capacity = getInt("Enter course capacity: ");

        if (capacity <= 0) {
            System.out.println("Capacity must be greater than 0.");
            return;
        }

        System.out.print("Enter course schedule: ");
        String schedule = sc.nextLine();

        String sql = "INSERT INTO courses " +
                "(course_code, title, description, capacity, schedule) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, title);
            ps.setString(3, description);
            ps.setInt(4, capacity);
            ps.setString(5, schedule);

            ps.executeUpdate();

            System.out.println("Course added successfully.");

        } catch (SQLException e) {
            System.out.println("Could not add course.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    // Display all courses
    public static void viewCourses() {

        String sql = "SELECT c.course_code, c.title, c.description, " +
                "c.capacity, c.schedule, " +
                "COUNT(r.student_id) AS registered " +
                "FROM courses c " +
                "LEFT JOIN registrations r " +
                "ON c.course_code = r.course_code " +
                "GROUP BY c.course_code, c.title, c.description, " +
                "c.capacity, c.schedule";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n========== AVAILABLE COURSES ==========");

            boolean found = false;

            while (rs.next()) {

                found = true;

                int capacity = rs.getInt("capacity");
                int registered = rs.getInt("registered");
                int available = capacity - registered;

                System.out.println("---------------------------------------");
                System.out.println("Course Code : " + rs.getString("course_code"));
                System.out.println("Title       : " + rs.getString("title"));
                System.out.println("Description : " + rs.getString("description"));
                System.out.println("Schedule    : " + rs.getString("schedule"));
                System.out.println("Capacity    : " + capacity);
                System.out.println("Registered  : " + registered);
                System.out.println("Available   : " + available);
            }

            if (!found) {
                System.out.println("No courses available.");
            }

        } catch (SQLException e) {
            System.out.println("Could not display courses.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    // Register student for a course
    public static void registerCourse() {

        int studentId = getInt("Enter student ID: ");

        System.out.print("Enter course code: ");
        String courseCode = sc.nextLine();

        // Check student
        String studentSql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement studentPs = con.prepareStatement(studentSql)) {

            studentPs.setInt(1, studentId);

            ResultSet studentRs = studentPs.executeQuery();

            if (!studentRs.next()) {
                System.out.println("Student not found.");
                return;
            }

            // Check course
            String courseSql = "SELECT capacity FROM courses WHERE course_code = ?";

            try (PreparedStatement coursePs = con.prepareStatement(courseSql)) {

                coursePs.setString(1, courseCode);

                ResultSet courseRs = coursePs.executeQuery();

                if (!courseRs.next()) {
                    System.out.println("Course not found.");
                    return;
                }

                int capacity = courseRs.getInt("capacity");

                // Check if already registered
                String checkSql =
                        "SELECT * FROM registrations " +
                        "WHERE student_id = ? AND course_code = ?";

                try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {

                    checkPs.setInt(1, studentId);
                    checkPs.setString(2, courseCode);

                    ResultSet checkRs = checkPs.executeQuery();

                    if (checkRs.next()) {
                        System.out.println("Student is already registered for this course.");
                        return;
                    }
                }

                // Check available seats
                String countSql =
                        "SELECT COUNT(*) FROM registrations WHERE course_code = ?";

                try (PreparedStatement countPs = con.prepareStatement(countSql)) {

                    countPs.setString(1, courseCode);

                    ResultSet countRs = countPs.executeQuery();
                    countRs.next();

                    int registered = countRs.getInt(1);

                    if (registered >= capacity) {
                        System.out.println("Sorry! This course is full.");
                        return;
                    }
                }

                // Register student
                String insertSql =
                        "INSERT INTO registrations(student_id, course_code) " +
                        "VALUES (?, ?)";

                try (PreparedStatement insertPs =
                             con.prepareStatement(insertSql)) {

                    insertPs.setInt(1, studentId);
                    insertPs.setString(2, courseCode);

                    insertPs.executeUpdate();

                    System.out.println("Course registration successful.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Registration failed.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    // Drop a course
    public static void dropCourse() {

        int studentId = getInt("Enter student ID: ");

        System.out.print("Enter course code: ");
        String courseCode = sc.nextLine();

        String sql =
                "DELETE FROM registrations " +
                "WHERE student_id = ? AND course_code = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, courseCode);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Course dropped successfully.");
            } else {
                System.out.println("Registration not found.");
            }

        } catch (SQLException e) {
            System.out.println("Could not drop course.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    // View courses registered by a student
    public static void viewStudentCourses() {

        int studentId = getInt("Enter student ID: ");

        String sql =
                "SELECT c.course_code, c.title, c.schedule " +
                "FROM registrations r " +
                "JOIN courses c " +
                "ON r.course_code = c.course_code " +
                "WHERE r.student_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== REGISTERED COURSES =====");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println("--------------------------------");
                System.out.println("Course Code : " + rs.getString("course_code"));
                System.out.println("Title       : " + rs.getString("title"));
                System.out.println("Schedule    : " + rs.getString("schedule"));
            }

            if (!found) {
                System.out.println("No courses registered.");
            }

        } catch (SQLException e) {
            System.out.println("Could not display registered courses.");
            System.out.println("Reason: " + e.getMessage());
        }
    }

    // Input validation for integers
    public static int getInt(String message) {

        while (true) {

            System.out.print(message);

            String input = sc.nextLine();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
}