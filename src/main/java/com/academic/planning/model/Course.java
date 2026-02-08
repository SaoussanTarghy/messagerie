package com.academic.planning.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Domain Model - Course Entity
 * 
 * This class represents a single academic course with its associated exam information.
 * It's a Plain Old Java Object (POJO) that encapsulates course data.
 * 
 * Educational Purpose:
 * - Demonstrates object-oriented design principles (encapsulation)
 * - Shows proper use of Java 8+ date/time API (LocalDate)
 * - Illustrates domain modeling in enterprise applications
 * 
 * @author Academic Planning System
 * @version 1.0
 */
public class Course {
    
    // ========== Attributes ==========
    
    /** Unique course code (e.g., "CS101") */
    private String code;
    
    /** Full name of the course */
    private String name;
    
    /** Name of the instructor teaching this course */
    private String instructor;
    
    /** Date of the exam (using Java 8 LocalDate for better date handling) */
    private LocalDate examDate;
    
    /** Number of days before the exam to trigger a reminder */
    private int reminderDaysBefore;
    
    
    // ========== Constructors ==========
    
    /**
     * Default no-argument constructor.
     * Required for frameworks like JAXB that use reflection.
     */
    public Course() {
        // Empty constructor for framework compatibility
    }
    
    /**
     * Full constructor with all fields.
     * Useful for creating Course objects programmatically.
     * 
     * @param code Unique course identifier
     * @param name Full course name
     * @param instructor Instructor's name
     * @param examDate Date of the exam
     * @param reminderDaysBefore Days before exam to send reminder
     */
    public Course(String code, String name, String instructor, 
                  LocalDate examDate, int reminderDaysBefore) {
        this.code = code;
        this.name = name;
        this.instructor = instructor;
        this.examDate = examDate;
        this.reminderDaysBefore = reminderDaysBefore;
    }
    
    
    // ========== Getters and Setters ==========
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getInstructor() {
        return instructor;
    }
    
    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }
    
    public LocalDate getExamDate() {
        return examDate;
    }
    
    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }
    
    public int getReminderDaysBefore() {
        return reminderDaysBefore;
    }
    
    public void setReminderDaysBefore(int reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }
    
    
    // ========== Business Logic Methods ==========
    
    /**
     * Calculates the number of days between today and the exam date.
     * 
     * Educational Note:
     * - Uses Java 8 ChronoUnit for precise date calculations
     * - Returns negative value if exam is in the past
     * 
     * @return Number of days until exam (negative if exam has passed)
     */
    public long getDaysUntilExam() {
        return ChronoUnit.DAYS.between(LocalDate.now(), examDate);
    }
    
    /**
     * Checks if a reminder should be sent for this course based on current date.
     * 
     * Logic:
     * - Reminder is needed if we're within the reminder window
     * - AND the exam hasn't passed yet
     * 
     * @return true if reminder should be sent, false otherwise
     */
    public boolean needsReminder() {
        long daysUntil = getDaysUntilExam();
        // Reminder needed if: exam is in future AND within reminder window
        return daysUntil >= 0 && daysUntil <= reminderDaysBefore;
    }
    
    /**
     * Formats course information for display.
     * Useful for console output and logging.
     * 
     * @return Human-readable course information
     */
    public String getDisplayInfo() {
        return String.format("[%s] %s - Instructor: %s, Exam: %s (%d days)",
                code, name, instructor, examDate, getDaysUntilExam());
    }
    
    
    // ========== Object Methods ==========
    
    /**
     * String representation of the Course object.
     * Used for debugging and logging.
     */
    @Override
    public String toString() {
        return "Course{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", instructor='" + instructor + '\'' +
                ", examDate=" + examDate +
                ", reminderDaysBefore=" + reminderDaysBefore +
                '}';
    }
    
    /**
     * Equals method for comparing Course objects.
     * Two courses are equal if they have the same code.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return Objects.equals(code, course.code);
    }
    
    /**
     * Hash code based on course code.
     * Ensures consistency with equals() method.
     */
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
