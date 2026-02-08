package com.academic.planning.service;

// NOTE: For standalone testing, EJB annotations are commented out
// Uncomment when deploying to Java EE server

// import javax.ejb.EJB;
// import javax.ejb.Schedule;
// import javax.ejb.Stateless;
import java.time.LocalDateTime;

/**
 * EJB Timer Service - Automated Scheduling Layer (Standalone Version)
 * 
 * This version has EJB annotations commented out for standalone testing.
 * For Java EE deployment, uncomment all @Stateless, @Schedule, and @EJB
 * annotations.
 */
// @Stateless
public class TimerService {

    // @EJB
    private AcademicService academicService = new AcademicService();

    /**
     * Automatic Timer Method
     * In Java EE, this would execute daily at 9:00 AM via @Schedule annotation
     * For standalone testing, call manually from Main.java
     */
    // @Schedule(hour = "9", minute = "0", persistent = false)
    public void performDailyCheck() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  TIMER TRIGGERED AT: " + LocalDateTime.now());
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        try {
            academicService.performDailyExamCheck();
            System.out.println("✓ Daily check completed successfully");
        } catch (Exception e) {
            System.err.println("✗ Error during daily check: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void triggerManualCheck() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║  MANUAL CHECK TRIGGERED AT: " + LocalDateTime.now());
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        academicService.performDailyExamCheck();
    }

    public void displayTimerInfo() {
        System.out.println("\n========== EJB TIMER CONFIGURATION ==========");
        System.out.println("Schedule: Every day at 9:00 AM");
        System.out.println("Type: Automatic (@Schedule)");
        System.out.println("Persistent: false (non-persistent)");
        System.out.println("Note: Annotations commented for standalone testing");
        System.out.println("=============================================\n");
    }
}
