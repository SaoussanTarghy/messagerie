package com.academic.planning.service;

// NOTE: For standalone testing, EJB annotations are commented out
// Uncomment when deploying to Java EE server

// import javax.ejb.Stateless;
import com.academic.planning.model.Course;
import com.academic.planning.parser.XMLParser;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Academic Service - Business Logic Layer (Standalone Version)
 * 
 * This version has @Stateless annotation commented for standalone testing.
 * For Java EE deployment, uncomment the @Stateless annotation.
 */
// @Stateless
public class AcademicService {

    public List<Course> getAllCourses() {
        try {
            return XMLParser.loadCourses();
        } catch (Exception e) {
            System.err.println("Error loading courses in service layer: " + e.getMessage());
            return List.of();
        }
    }

    public List<Course> getCoursesNeedingReminders() {
        List<Course> allCourses = getAllCourses();
        return allCourses.stream()
                .filter(Course::needsReminder)
                .collect(Collectors.toList());
    }

    public List<Course> getCoursesWithinDays(int maxDays) {
        List<Course> allCourses = getAllCourses();
        return allCourses.stream()
                .filter(course -> {
                    long days = course.getDaysUntilExam();
                    return days >= 0 && days <= maxDays;
                })
                .collect(Collectors.toList());
    }

    public void performDailyExamCheck() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("  DAILY ACADEMIC PLANNING CHECK - " + LocalDate.now());
        System.out.println("=".repeat(70) + "\n");

        List<Course> reminders = getCoursesNeedingReminders();

        if (reminders.isEmpty()) {
            System.out.println("✓ No upcoming exams requiring reminders today.");
            System.out.println("  All clear! Enjoy your day.\n");
        } else {
            System.out.println("⚠️  EXAM REMINDERS (" + reminders.size() + " course(s)):");
            System.out.println("-".repeat(70));

            for (Course course : reminders) {
                long daysUntil = course.getDaysUntilExam();
                String urgency = getUrgencyIndicator(daysUntil);

                System.out.println("\n" + urgency + " " + course.getCode() + " - " + course.getName());
                System.out.println("   Instructor: " + course.getInstructor());
                System.out.println("   Exam Date: " + course.getExamDate());
                System.out.println("   Days Until Exam: " + daysUntil + " day(s)");

                if (daysUntil == 0) {
                    System.out.println("   >>> EXAM IS TODAY! <<<");
                } else if (daysUntil == 1) {
                    System.out.println("   >>> Exam is TOMORROW! Final review recommended.");
                } else if (daysUntil <= 3) {
                    System.out.println("   >>> Very soon! Ensure you're well prepared.");
                } else {
                    System.out.println("   >>> Start reviewing soon.");
                }
            }

            System.out.println("\n" + "-".repeat(70));
        }

        displaySummary();
        System.out.println("=".repeat(70) + "\n");
    }

    private String getUrgencyIndicator(long daysUntil) {
        if (daysUntil == 0)
            return "🔴";
        if (daysUntil <= 2)
            return "🟠";
        if (daysUntil <= 5)
            return "🟡";
        return "🟢";
    }

    private void displaySummary() {
        List<Course> allCourses = getAllCourses();
        List<Course> nextWeek = getCoursesWithinDays(7);
        List<Course> nextMonth = getCoursesWithinDays(30);

        System.out.println("\n📊 SUMMARY:");
        System.out.println("   Total Courses: " + allCourses.size());
        System.out.println("   Exams in Next 7 Days: " + nextWeek.size());
        System.out.println("   Exams in Next 30 Days: " + nextMonth.size());
    }

    public void displayAllCourses() {
        List<Course> courses = getAllCourses();

        System.out.println("\n" + "=".repeat(70));
        System.out.println("  ALL COURSES IN ACADEMIC PLAN");
        System.out.println("=".repeat(70) + "\n");

        for (Course course : courses) {
            System.out.println(course.getDisplayInfo());
        }

        System.out.println("\n" + "=".repeat(70) + "\n");
    }
}
