package com.javamastery.grades;

import com.javamastery.grades.model.*;
import com.javamastery.grades.service.*;
import com.javamastery.grades.util.*;
import com.javamastery.grades.io.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main application class for the Student Grade Management System.
 * Demonstrates comprehensive usage of Java Collections Framework, Generics,
 * and advanced data manipulation techniques.
 */
public class GradeManagementApp {

    private final StudentManager studentManager;
    private final GradeCalculator gradeCalculator;
    private final ReportGenerator reportGenerator;
    private final FileManager fileManager;
    private final Scanner scanner;
    private final Map<String, Subject> subjectMap;

    public GradeManagementApp() {
        this.studentManager = new StudentManager();
        this.gradeCalculator = new GradeCalculator();
        this.reportGenerator = new ReportGenerator(gradeCalculator);
        this.fileManager = new FileManager("data");
        this.scanner = new Scanner(System.in);
        this.subjectMap = fileManager.loadSampleSubjects();
        
        // Initialize with sample data
        initializeSampleData();
    }

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("        🎓 STUDENT GRADE MANAGEMENT SYSTEM 🎓");
        System.out.println("    Demonstrating Java Collections Framework Mastery");
        System.out.println("=".repeat(80));
        
        GradeManagementApp app = new GradeManagementApp();
        app.run();
    }

    public void run() {
        boolean running = true;
        
        while (running) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            try {
                switch (choice) {
                    case "1" -> manageStudents();
                    case "2" -> manageGrades();
                    case "3" -> viewReports();
                    case "4" -> performDataAnalysis();
                    case "5" -> demonstrateCollections();
                    case "6" -> fileOperations();
                    case "7" -> demonstrateComparators();
                    case "8" -> viewSystemStatistics();
                    case "0" -> {
                        System.out.println("\n👋 Thank you for using the Student Grade Management System!");
                        running = false;
                    }
                    default -> System.out.println("\n❌ Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("\n❌ Error: " + e.getMessage());
            }
            
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
    }

    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    📋 MAIN MENU");
        System.out.println("=".repeat(60));
        System.out.println("1. 👥 Student Management");
        System.out.println("2. 📊 Grade Management");
        System.out.println("3. 📈 Reports");
        System.out.println("4. 🔍 Data Analysis");
        System.out.println("5. 🧩 Collections Framework Demo");
        System.out.println("6. 📁 File Operations");
        System.out.println("7. ⚖️  Comparator Demonstrations");
        System.out.println("8. 📊 System Statistics");
        System.out.println("0. 🚪 Exit");
        System.out.println("=".repeat(60));
        System.out.print("Enter your choice (0-8): ");
    }

    private void manageStudents() {
        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("            👥 STUDENT MANAGEMENT");
            System.out.println("=".repeat(50));
            System.out.println("1. Add Student");
            System.out.println("2. Update Student");
            System.out.println("3. Remove Student");
            System.out.println("4. Search Students");
            System.out.println("5. View All Students");
            System.out.println("6. View Students by Major");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> addStudent();
                case "2" -> updateStudent();
                case "3" -> removeStudent();
                case "4" -> searchStudents();
                case "5" -> viewAllStudents();
                case "6" -> viewStudentsByMajor();
                case "0" -> { return; }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void addStudent() {
        System.out.println("\n➕ Add New Student");
        System.out.print("Student ID (e.g., ST123456): ");
        String id = scanner.nextLine().trim();
        
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        
        System.out.print("Major: ");
        String major = scanner.nextLine().trim();
        
        System.out.print("Enrollment Year: ");
        int year = Integer.parseInt(scanner.nextLine().trim());
        
        try {
            Student student = new Student(id, name, major, year);
            List<String> errors = DataValidator.validateStudent(student);
            
            if (!errors.isEmpty()) {
                System.out.println("❌ Validation errors:");
                errors.forEach(error -> System.out.println("  - " + error));
                return;
            }
            
            if (studentManager.addStudent(student)) {
                System.out.println("✅ Student added successfully!");
            } else {
                System.out.println("❌ Student with this ID already exists.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void updateStudent() {
        System.out.println("\n✏️ Update Student");
        System.out.print("Student ID: ");
        String id = scanner.nextLine().trim();
        
        Student student = studentManager.getStudent(id);
        if (student == null) {
            System.out.println("❌ Student not found.");
            return;
        }
        
        System.out.println("Current: " + student);
        System.out.print("New name (press Enter to skip): ");
        String name = scanner.nextLine().trim();
        
        System.out.print("New major (press Enter to skip): ");
        String major = scanner.nextLine().trim();
        
        System.out.print("New enrollment year (press Enter to skip): ");
        String yearStr = scanner.nextLine().trim();
        
        Integer year = yearStr.isEmpty() ? null : Integer.parseInt(yearStr);
        
        if (studentManager.updateStudent(id, 
                name.isEmpty() ? null : name,
                major.isEmpty() ? null : major,
                year)) {
            System.out.println("✅ Student updated successfully!");
        } else {
            System.out.println("❌ Failed to update student.");
        }
    }

    private void removeStudent() {
        System.out.println("\n🗑️ Remove Student");
        System.out.print("Student ID: ");
        String id = scanner.nextLine().trim();
        
        if (studentManager.removeStudent(id)) {
            System.out.println("✅ Student removed successfully!");
        } else {
            System.out.println("❌ Student not found.");
        }
    }

    private void searchStudents() {
        System.out.println("\n🔍 Search Students");
        System.out.print("Enter name to search: ");
        String query = scanner.nextLine().trim();
        
        List<Student> results = studentManager.searchStudentsByName(query);
        
        if (results.isEmpty()) {
            System.out.println("❌ No students found.");
        } else {
            System.out.println("✅ Found " + results.size() + " student(s):");
            results.forEach(student -> System.out.println("  " + student));
        }
    }

    private void viewAllStudents() {
        System.out.println("\n👥 All Students (Ranked by GPA):");
        List<Student> students = studentManager.getStudentRankings();
        
        if (students.isEmpty()) {
            System.out.println("❌ No students found.");
        } else {
            students.forEach(student -> System.out.println("  " + student));
        }
    }

    private void viewStudentsByMajor() {
        System.out.println("\n📚 View Students by Major");
        System.out.print("Enter major: ");
        String major = scanner.nextLine().trim();
        
        List<Student> students = studentManager.getStudentsByMajor(major);
        
        if (students.isEmpty()) {
            System.out.println("❌ No students found for major: " + major);
        } else {
            System.out.println("✅ Students in " + major + ":");
            students.forEach(student -> System.out.println("  " + student));
        }
    }

    private void manageGrades() {
        System.out.println("\n📊 Grade Management");
        System.out.print("Student ID: ");
        String id = scanner.nextLine().trim();
        
        Student student = studentManager.getStudent(id);
        if (student == null) {
            System.out.println("❌ Student not found.");
            return;
        }
        
        System.out.println("Available subjects:");
        subjectMap.values().forEach(subject -> System.out.println("  " + subject));
        
        System.out.print("Subject code: ");
        String subjectCode = scanner.nextLine().trim();
        
        Subject subject = subjectMap.get(subjectCode);
        if (subject == null) {
            System.out.println("❌ Invalid subject code.");
            return;
        }
        
        System.out.println("Grade types: " + Arrays.toString(GradeType.values()));
        System.out.print("Grade type: ");
        String gradeTypeStr = scanner.nextLine().trim();
        
        try {
            GradeType gradeType = GradeType.valueOf(gradeTypeStr.toUpperCase());
            
            System.out.print("Grade value: ");
            double value = Double.parseDouble(scanner.nextLine().trim());
            
            System.out.print("Max value: ");
            double maxValue = Double.parseDouble(scanner.nextLine().trim());
            
            System.out.print("Description (optional): ");
            String description = scanner.nextLine().trim();
            
            Grade<Double> grade = new Grade<>(value, maxValue, subject, gradeType, description);
            
            if (studentManager.addGradeToStudent(id, grade)) {
                System.out.println("✅ Grade added successfully!");
                System.out.println("Grade: " + grade);
            } else {
                System.out.println("❌ Failed to add grade.");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void viewReports() {
        while (true) {
            System.out.println("\n📈 Reports");
            System.out.println("1. Class Performance Report");
            System.out.println("2. Subject Report");
            System.out.println("3. Student Report");
            System.out.println("4. Academic Alert Report");
            System.out.println("5. Grade Trend Analysis");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> generateClassReport();
                case "2" -> generateSubjectReport();
                case "3" -> generateStudentReport();
                case "4" -> generateAcademicAlertReport();
                case "5" -> generateTrendReport();
                case "0" -> { return; }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void generateClassReport() {
        System.out.println("\n📊 Generating Class Performance Report...");
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        Set<Subject> subjects = studentManager.getAllSubjects();
        
        LinkedHashMap<String, Object> report = reportGenerator.generateClassPerformanceReport(students, subjects);
        String reportText = reportGenerator.formatReportAsString(report);
        
        System.out.println(reportText);
        
        System.out.print("\nSave to file? (y/n): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("y")) {
            String filename = "class_report_" + System.currentTimeMillis() + ".txt";
            if (fileManager.exportReportToFile(report, filename)) {
                System.out.println("✅ Report saved to: " + filename);
            }
        }
    }

    private void generateSubjectReport() {
        System.out.println("\n📚 Subject Report");
        System.out.println("Available subjects:");
        subjectMap.values().forEach(subject -> System.out.println("  " + subject.getCode() + " - " + subject.getName()));
        
        System.out.print("Subject code: ");
        String code = scanner.nextLine().trim();
        
        Subject subject = subjectMap.get(code);
        if (subject == null) {
            System.out.println("❌ Invalid subject code.");
            return;
        }
        
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        LinkedHashMap<String, Object> report = reportGenerator.generateSubjectReport(students, subject);
        String reportText = reportGenerator.formatReportAsString(report);
        
        System.out.println(reportText);
    }

    private void generateStudentReport() {
        System.out.println("\n🎓 Individual Student Report");
        System.out.print("Student ID: ");
        String id = scanner.nextLine().trim();
        
        Student student = studentManager.getStudent(id);
        if (student == null) {
            System.out.println("❌ Student not found.");
            return;
        }
        
        LinkedHashMap<String, Object> report = reportGenerator.generateStudentReport(student);
        String reportText = reportGenerator.formatReportAsString(report);
        
        System.out.println(reportText);
    }

    private void generateAcademicAlertReport() {
        System.out.println("\n🚨 Academic Alert Report");
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        LinkedHashMap<String, Object> report = reportGenerator.generateAcademicAlertReport(students);
        String reportText = reportGenerator.formatReportAsString(report);
        
        System.out.println(reportText);
    }

    private void generateTrendReport() {
        System.out.println("\n📈 Grade Trend Analysis");
        System.out.println("Available subjects:");
        subjectMap.values().forEach(subject -> System.out.println("  " + subject.getCode() + " - " + subject.getName()));
        
        System.out.print("Subject code: ");
        String code = scanner.nextLine().trim();
        
        Subject subject = subjectMap.get(code);
        if (subject == null) {
            System.out.println("❌ Invalid subject code.");
            return;
        }
        
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        LinkedHashMap<String, Object> report = reportGenerator.generateTrendAnalysisReport(students, subject);
        String reportText = reportGenerator.formatReportAsString(report);
        
        System.out.println(reportText);
    }

    private void performDataAnalysis() {
        while (true) {
            System.out.println("\n🔍 Data Analysis");
            System.out.println("1. Top Performers");
            System.out.println("2. Students Needing Support");
            System.out.println("3. Grade Distribution");
            System.out.println("4. Statistical Analysis");
            System.out.println("5. Performance Groups");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> showTopPerformers();
                case "2" -> showStudentsNeedingSupport();
                case "3" -> showGradeDistribution();
                case "4" -> performStatisticalAnalysis();
                case "5" -> showPerformanceGroups();
                case "0" -> { return; }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void showTopPerformers() {
        System.out.println("\n🏆 Top Performers");
        List<Student> topStudents = studentManager.getTopStudents(10);
        
        if (topStudents.isEmpty()) {
            System.out.println("❌ No students found.");
        } else {
            System.out.println("Top 10 Students by GPA:");
            for (int i = 0; i < topStudents.size(); i++) {
                Student student = topStudents.get(i);
                System.out.printf("%2d. %s (GPA: %.2f)\n", i + 1, student.getName(), student.calculateGPA());
            }
        }
    }

    private void showStudentsNeedingSupport() {
        System.out.println("\n🆘 Students Needing Academic Support");
        List<Student> students = studentManager.getStudentsNeedingSupport();
        
        if (students.isEmpty()) {
            System.out.println("✅ No students currently need academic support.");
        } else {
            System.out.println("Students with GPA < 2.0:");
            students.forEach(student -> 
                System.out.printf("  %s - GPA: %.2f\n", student.getName(), student.calculateGPA()));
        }
    }

    private void showGradeDistribution() {
        System.out.println("\n📊 Grade Distribution");
        System.out.println("Available subjects:");
        subjectMap.values().forEach(subject -> System.out.println("  " + subject.getCode() + " - " + subject.getName()));
        
        System.out.print("Subject code: ");
        String code = scanner.nextLine().trim();
        
        Subject subject = subjectMap.get(code);
        if (subject == null) {
            System.out.println("❌ Invalid subject code.");
            return;
        }
        
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        TreeMap<String, Integer> distribution = gradeCalculator.calculateGradeDistribution(students, subject);
        
        System.out.println("\nGrade distribution for " + subject.getName() + ":");
        distribution.forEach((grade, count) -> 
            System.out.printf("  %-12s: %3d students %s\n", 
                grade, count, "█".repeat(Math.max(1, count * 2))));
    }

    private void performStatisticalAnalysis() {
        System.out.println("\n📊 Statistical Analysis");
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        
        if (students.isEmpty()) {
            System.out.println("❌ No students found.");
            return;
        }
        
        List<Double> gpas = students.stream()
                .filter(s -> s.getTotalGradeCount() > 0)
                .mapToDouble(Student::calculateGPA)
                .boxed()
                .collect(Collectors.toList());
        
        if (gpas.isEmpty()) {
            System.out.println("❌ No GPA data available.");
            return;
        }
        
        Map<String, Double> stats = StatisticsCalculator.calculateDescriptiveStatistics(gpas);
        
        System.out.println("GPA Statistics:");
        stats.forEach((key, value) -> 
            System.out.printf("  %-20s: %.3f\n", 
                key.replaceAll("([a-z])([A-Z])", "$1 $2").toLowerCase(), value));
        
        // Show outliers
        List<Double> outliers = StatisticsCalculator.detectOutliers(gpas);
        if (!outliers.isEmpty()) {
            System.out.println("\nOutliers detected:");
            outliers.forEach(gpa -> System.out.printf("  %.3f\n", gpa));
        }
    }

    private void showPerformanceGroups() {
        System.out.println("\n📈 Performance Groups");
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        Map<String, List<Student>> groups = gradeCalculator.groupStudentsByPerformance(students);
        
        groups.forEach((level, studentList) -> {
            System.out.println("\n" + level + " (" + studentList.size() + " students):");
            studentList.forEach(student -> 
                System.out.printf("  %s - GPA: %.2f\n", student.getName(), student.calculateGPA()));
        });
    }

    private void demonstrateCollections() {
        System.out.println("\n🧩 Collections Framework Demonstration");
        System.out.println("This system demonstrates the following collections:");
        
        System.out.println("\n1. 🗺️  HashMap - O(1) student lookups by ID");
        System.out.println("   Total students indexed: " + studentManager.getTotalStudentCount());
        
        System.out.println("\n2. 🌳 TreeSet - Sorted student rankings by GPA");
        List<Student> rankings = studentManager.getStudentRankings();
        System.out.println("   Top 3 students:");
        rankings.stream().limit(3).forEach(s -> 
            System.out.printf("     %s (GPA: %.2f)\n", s.getName(), s.calculateGPA()));
        
        System.out.println("\n3. 🔗 LinkedList - Chronological grade history");
        if (!rankings.isEmpty()) {
            Student student = rankings.get(0);
            List<Grade<?>> history = student.getGradeHistory();
            System.out.printf("   %s has %d grades in chronological order\n", 
                student.getName(), history.size());
        }
        
        System.out.println("\n4. 🎯 PriorityQueue - Academic alerts (lowest GPA first)");
        Student nextAlert = studentManager.getNextStudentForAcademicAlert();
        if (nextAlert != null) {
            System.out.printf("   Next student needing attention: %s (GPA: %.2f)\n", 
                nextAlert.getName(), nextAlert.calculateGPA());
        } else {
            System.out.println("   No students currently need attention");
        }
        
        System.out.println("\n5. 🔗 LinkedHashSet - Preserves subject order");
        Set<Subject> subjects = studentManager.getAllSubjects();
        System.out.println("   Subjects in order: " + 
            subjects.stream().map(Subject::getCode).collect(Collectors.joining(", ")));
    }

    private void fileOperations() {
        while (true) {
            System.out.println("\n📁 File Operations");
            System.out.println("1. Export Students to CSV");
            System.out.println("2. Import Students from CSV");
            System.out.println("3. Export Grades to CSV");
            System.out.println("4. Create Backup");
            System.out.println("5. List Backups");
            System.out.println("6. Create Sample Files");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1" -> exportStudents();
                case "2" -> importStudents();
                case "3" -> exportGrades();
                case "4" -> createBackup();
                case "5" -> listBackups();
                case "6" -> createSampleFiles();
                case "0" -> { return; }
                default -> System.out.println("❌ Invalid choice.");
            }
        }
    }

    private void exportStudents() {
        System.out.print("Filename (e.g., students.csv): ");
        String filename = scanner.nextLine().trim();
        
        if (fileManager.exportStudentsToCSV(studentManager.getAllStudents(), filename)) {
            System.out.println("✅ Students exported successfully to: " + filename);
        } else {
            System.out.println("❌ Export failed.");
        }
    }

    private void importStudents() {
        System.out.print("Filename: ");
        String filename = scanner.nextLine().trim();
        
        List<Student> students = fileManager.importStudentsFromCSV(filename);
        if (!students.isEmpty()) {
            students.forEach(studentManager::addStudent);
            System.out.println("✅ Imported " + students.size() + " students.");
        } else {
            System.out.println("❌ Import failed or no valid students found.");
        }
    }

    private void exportGrades() {
        System.out.print("Filename (e.g., grades.csv): ");
        String filename = scanner.nextLine().trim();
        
        if (fileManager.exportGradesToCSV(studentManager.getAllStudents(), filename)) {
            System.out.println("✅ Grades exported successfully to: " + filename);
        } else {
            System.out.println("❌ Export failed.");
        }
    }

    private void createBackup() {
        if (fileManager.createBackup(studentManager)) {
            System.out.println("✅ Backup created successfully!");
        } else {
            System.out.println("❌ Backup failed.");
        }
    }

    private void listBackups() {
        List<String> backups = fileManager.listBackups();
        if (backups.isEmpty()) {
            System.out.println("❌ No backups found.");
        } else {
            System.out.println("Available backups:");
            backups.forEach(backup -> System.out.println("  " + backup));
        }
    }

    private void createSampleFiles() {
        try {
            CsvProcessor csvProcessor = new CsvProcessor();
            csvProcessor.createSampleStudentCSV("data/sample_students.csv");
            csvProcessor.createSampleGradeCSV("data/sample_grades.csv");
            System.out.println("✅ Sample files created in data/ directory");
        } catch (Exception e) {
            System.out.println("❌ Error creating sample files: " + e.getMessage());
        }
    }

    private void demonstrateComparators() {
        System.out.println("\n⚖️ Comparator Demonstrations");
        List<Student> students = new ArrayList<>(studentManager.getAllStudents());
        
        if (students.isEmpty()) {
            System.out.println("❌ No students available for comparison.");
            return;
        }
        
        System.out.println("\n1. Natural ordering (by GPA, descending):");
        students.stream().sorted().limit(5).forEach(s -> 
            System.out.printf("   %s - GPA: %.2f\n", s.getName(), s.calculateGPA()));
        
        System.out.println("\n2. By name (alphabetical):");
        students.stream().sorted(StudentComparators.BY_NAME).limit(5).forEach(s -> 
            System.out.println("   " + s.getName()));
        
        System.out.println("\n3. By enrollment year (newest first):");
        students.stream().sorted(StudentComparators.BY_ENROLLMENT_YEAR).limit(5).forEach(s -> 
            System.out.printf("   %s (%d)\n", s.getName(), s.getEnrollmentYear()));
        
        System.out.println("\n4. Comprehensive sort (major → year → GPA → name):");
        students.stream().sorted(StudentComparators.COMPREHENSIVE_SORT).limit(5).forEach(s -> 
            System.out.printf("   %s - %s, %d, GPA: %.2f\n", 
                s.getName(), s.getMajor(), s.getEnrollmentYear(), s.calculateGPA()));
    }

    private void viewSystemStatistics() {
        System.out.println("\n📊 System Statistics");
        Map<String, Double> stats = studentManager.getPerformanceStatistics();
        
        System.out.println("Overall System Metrics:");
        stats.forEach((key, value) -> {
            String formattedKey = key.replaceAll("([a-z])([A-Z])", "$1 $2")
                                    .toLowerCase()
                                    .replace("gpa", "GPA");
            if (key.contains("GPA") || key.contains("average") || key.contains("median")) {
                System.out.printf("  %-25s: %.3f\n", formattedKey, value);
            } else {
                System.out.printf("  %-25s: %.0f\n", formattedKey, value);
            }
        });
        
        System.out.println("\nCollection Usage Summary:");
        System.out.println("  HashMap (student lookups)    : " + studentManager.getTotalStudentCount() + " entries");
        System.out.println("  TreeSet (student rankings)   : " + studentManager.getStudentRankings().size() + " entries");
        System.out.println("  LinkedHashSet (subjects)     : " + studentManager.getAllSubjects().size() + " unique subjects");
        System.out.println("  PriorityQueue (alerts)       : " + studentManager.getStudentsNeedingSupport().size() + " students");
    }

    private void initializeSampleData() {
        System.out.println("🚀 Initializing sample data...");
        
        // Create sample students
        List<Student> sampleStudents = Arrays.asList(
            new Student("ST001234", "Alice Johnson", "Computer Science", 2023),
            new Student("ST002345", "Bob Smith", "Mathematics", 2022),
            new Student("ST003456", "Carol Davis", "Physics", 2023),
            new Student("ST004567", "David Wilson", "Computer Science", 2021),
            new Student("ST005678", "Eva Brown", "Chemistry", 2022),
            new Student("ST006789", "Frank Miller", "Mathematics", 2023),
            new Student("ST007890", "Grace Lee", "Physics", 2022),
            new Student("ST008901", "Henry Taylor", "Computer Science", 2021),
            new Student("ST009012", "Ivy Chen", "Chemistry", 2023),
            new Student("ST010123", "Jack Anderson", "Mathematics", 2022)
        );
        
        // Add students to manager
        for (Student student : sampleStudents) {
            studentManager.addStudent(student);
        }
        
        // Add sample grades
        Random random = new Random(42); // Fixed seed for consistent results
        List<Subject> subjects = new ArrayList<>(subjectMap.values());
        
        for (Student student : sampleStudents) {
            int numGrades = 8 + random.nextInt(7); // 8-14 grades per student
            
            for (int i = 0; i < numGrades; i++) {
                Subject subject = subjects.get(random.nextInt(subjects.size()));
                GradeType[] gradeTypes = GradeType.values();
                GradeType gradeType = gradeTypes[random.nextInt(gradeTypes.length)];
                
                double maxValue = 100.0;
                double baseScore = 60 + random.nextDouble() * 35; // Base score 60-95
                
                // Add some variation based on student and grade type
                if (student.getName().startsWith("A") || student.getName().startsWith("E")) {
                    baseScore += 5; // Boost for "good" students
                }
                if (gradeType == GradeType.HOMEWORK) {
                    baseScore += 5; // Homework tends to be easier
                }
                
                double value = Math.min(maxValue, baseScore + (random.nextGaussian() * 8));
                value = Math.max(0, value);
                
                Grade<Double> grade = new Grade<>(value, maxValue, subject, gradeType, 
                    gradeType.getDisplayName() + " " + (i + 1));
                
                studentManager.addGradeToStudent(student.getStudentId(), grade);
            }
        }
        
        System.out.println("✅ Sample data initialized:");
        System.out.println("   - " + sampleStudents.size() + " students");
        System.out.println("   - " + subjects.size() + " subjects");
        System.out.println("   - Multiple grades per student");
        System.out.println("   - Various grade types and distributions");
    }
}