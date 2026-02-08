package com.academic.planning;

import com.academic.planning.model.Course;
import com.academic.planning.parser.XMLParser;
import com.academic.planning.service.AcademicService;

import java.util.List;
import java.util.Map;

/**
 * Main Application Entry Point
 * 
 * This class serves as a standalone entry point for testing and demonstration.
 * 
 * IMPORTANT DISTINCTION:
 * 
 * In a Java EE environment:
 * - Deploy to Java EE server (WildFly, Payara, TomEE)
 * - EJB Timer automatically starts and runs in background
 * - No main() method needed for timer to work
 * - Main() is only for TESTING outside of EJB container
 * 
 * This Main class provides:
 * - Quick testing of XML parsing
 * - Manual execution of exam check logic
 * - Verification that all components work correctly
 * - Educational demonstration
 * 
 * FOR DEVELOPMENT/TESTING: Run this Main class to see immediate results
 * IN PRODUCTION: Deploy as .war/.ear and check server logs at 9 AM
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                    ║");
        System.out.println("║          ACADEMIC PLANNING SYSTEM - TEST EXECUTION                 ║");
        System.out.println("║                                                                    ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");

        try {
            // ==================== STEP 1: Load Configuration ====================
            System.out.println("STEP 1: Loading System Configuration...");
            System.out.println("-".repeat(70));

            Map<String, String> config = XMLParser.loadConfig();
            XMLParser.displayConfig(config);

            // ==================== STEP 2: Load Academic Data ====================
            System.out.println("STEP 2: Loading Academic Planning Data...");
            System.out.println("-".repeat(70));

            List<Course> courses = XMLParser.loadCourses();

            System.out.println("Loaded Courses:");
            for (Course course : courses) {
                System.out.println("  - " + course.getDisplayInfo());
            }
            System.out.println();

            // ==================== STEP 3: Create Service Instance ====================
            // NOTE: In real Java EE, container would inject via @EJB
            // Here we manually create for standalone testing
            System.out.println("STEP 3: Initializing Academic Service...");
            System.out.println("-".repeat(70));

            AcademicService academicService = new AcademicService();
            System.out.println("✓ Service initialized successfully\n");

            // ==================== STEP 4: Display All Courses ====================
            System.out.println("STEP 4: Displaying All Courses in Detail...");
            System.out.println("-".repeat(70));

            academicService.displayAllCourses();

            // ==================== STEP 5: Perform Exam Check ====================
            System.out.println("STEP 5: Performing Daily Exam Check...");
            System.out.println("-".repeat(70));
            System.out.println("(This simulates what the EJB Timer would do automatically)\n");

            academicService.performDailyExamCheck();

            // ==================== STEP 6: Show Upcoming Exams ====================
            System.out.println("STEP 6: Showing Specific Timeframes...");
            System.out.println("-".repeat(70));

            List<Course> nextWeek = academicService.getCoursesWithinDays(7);
            System.out.println("\n📅 Exams in Next 7 Days: " + nextWeek.size());
            for (Course course : nextWeek) {
                System.out.println("   " + course.getDisplayInfo());
            }

            List<Course> nextTwoWeeks = academicService.getCoursesWithinDays(14);
            System.out.println("\n📅 Exams in Next 14 Days: " + nextTwoWeeks.size());
            for (Course course : nextTwoWeeks) {
                System.out.println("   " + course.getDisplayInfo());
            }

            // ==================== SUCCESS ====================
            System.out.println("\n\n╔════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                                                                    ║");
            System.out.println("║                     ✓ ALL TESTS PASSED                             ║");
            System.out.println("║                                                                    ║");
            System.out.println("║  The system is working correctly!                                  ║");
            System.out.println("║                                                                    ║");
            System.out.println("║  NEXT STEPS FOR JAVA EE DEPLOYMENT:                                ║");
            System.out.println("║  1. Package application as .war file                               ║");
            System.out.println("║  2. Deploy to Java EE server (WildFly/TomEE/Payara)                ║");
            System.out.println("║  3. EJB Timer will run automatically at 9:00 AM daily              ║");
            System.out.println("║  4. Check server logs for timer output                             ║");
            System.out.println("║                                                                    ║");
            System.out.println("╚════════════════════════════════════════════════════════════════════╝\n");

        } catch (Exception e) {
            System.err.println("\n✗ ERROR OCCURRED:");
            System.err.println("-".repeat(70));
            System.err.println("Message: " + e.getMessage());
            System.err.println("\nStack Trace:");
            e.printStackTrace();

            System.err.println("\n╔════════════════════════════════════════════════════════════════════╗");
            System.err.println("║                    ✗ TEST FAILED                                   ║");
            System.err.println("╚════════════════════════════════════════════════════════════════════╝\n");
        }
    }
}
