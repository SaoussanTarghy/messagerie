package com.academic.planning.parser;

import com.academic.planning.model.Course;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * XML Parser Utility
 * 
 * This class handles parsing of XML configuration and data files using DOM
 * (Document Object Model).
 * 
 * Why XML Parsing?
 * - Allows external data storage without database complexity
 * - Easy to update course information without recompiling
 * - Standard format readable by humans and machines
 * - Good for academic projects with moderate data volumes
 * 
 * Why DOM Parser?
 * - Loads entire XML into memory as a tree structure
 * - Easy to navigate and extract data
 * - Suitable for small to medium XML files
 * - Alternative: SAX (event-based, more memory-efficient for large files)
 * 
 * Educational Purpose:
 * - Demonstrates XML parsing in Java
 * - Shows error handling for file I/O operations
 * - Illustrates resource management with try-with-resources
 * 
 * @author Academic Planning System
 * @version 1.0
 */
public class XMLParser {

    /**
     * Loads all courses from the planning.xml file.
     * 
     * Process:
     * 1. Locate XML file in classpath resources
     * 2. Parse XML into DOM Document
     * 3. Extract course elements
     * 4. Convert each XML element to Course object
     * 5. Return list of courses
     * 
     * @return List of Course objects parsed from XML
     * @throws Exception if XML parsing fails or file not found
     */
    public static List<Course> loadCourses() throws Exception {
        List<Course> courses = new ArrayList<>();

        try {
            // Step 1: Get XML file from resources
            // Using ClassLoader to load from src/main/resources
            InputStream inputStream = XMLParser.class.getClassLoader()
                    .getResourceAsStream("planning.xml");

            if (inputStream == null) {
                throw new Exception("planning.xml not found in resources folder");
            }

            // Step 2: Create DOM parser
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Step 3: Parse XML into Document object
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            System.out.println("✓ Successfully loaded planning.xml");
            System.out.println("Root element: " + document.getDocumentElement().getNodeName());

            // Step 4: Get all <course> elements
            NodeList courseNodes = document.getElementsByTagName("course");
            System.out.println("Found " + courseNodes.getLength() + " courses in XML");

            // Step 5: Iterate through each course element
            for (int i = 0; i < courseNodes.getLength(); i++) {
                Node courseNode = courseNodes.item(i);

                // Only process ELEMENT nodes (skip text nodes, comments, etc.)
                if (courseNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element courseElement = (Element) courseNode;

                    // Extract course data from XML
                    Course course = parseCourseElement(courseElement);
                    courses.add(course);
                }
            }

            System.out.println("✓ Parsed " + courses.size() + " courses successfully\n");

        } catch (Exception e) {
            System.err.println("✗ Error loading courses from XML: " + e.getMessage());
            throw e; // Re-throw to caller
        }

        return courses;
    }

    /**
     * Parses a single <course> XML element into a Course object.
     * 
     * XML Structure Expected:
     * <course>
     * <code>CS101</code>
     * <name>Course Name</name>
     * <instructor>Prof. Name</instructor>
     * <examDate>2026-03-15</examDate>
     * <reminderDaysBefore>7</reminderDaysBefore>
     * </course>
     * 
     * @param element The course XML element
     * @return Course object with data from XML
     */
    private static Course parseCourseElement(Element element) {
        Course course = new Course();

        // Extract text content from child elements
        course.setCode(getElementText(element, "code"));
        course.setName(getElementText(element, "name"));
        course.setInstructor(getElementText(element, "instructor"));

        // Parse date string to LocalDate
        String examDateStr = getElementText(element, "examDate");
        course.setExamDate(LocalDate.parse(examDateStr)); // Format: YYYY-MM-DD

        // Parse reminder days as integer
        String reminderDaysStr = getElementText(element, "reminderDaysBefore");
        course.setReminderDaysBefore(Integer.parseInt(reminderDaysStr));

        return course;
    }

    /**
     * Helper method to extract text content from a child element.
     * 
     * @param parent  Parent XML element
     * @param tagName Name of child element to extract
     * @return Text content of the child element, or empty string if not found
     */
    private static String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent().trim();
        }
        return "";
    }

    /**
     * Loads system configuration from config.xml file.
     * 
     * Returns a Map with configuration key-value pairs:
     * - checkHour: Hour to run daily check
     * - checkMinute: Minute to run daily check
     * - enabled: Whether timer is enabled
     * - timeZone: System time zone
     * - etc.
     * 
     * @return Map of configuration settings
     * @throws Exception if XML parsing fails
     */
    public static Map<String, String> loadConfig() throws Exception {
        Map<String, String> config = new HashMap<>();

        try {
            // Load config.xml from resources
            InputStream inputStream = XMLParser.class.getClassLoader()
                    .getResourceAsStream("config.xml");

            if (inputStream == null) {
                throw new Exception("config.xml not found in resources folder");
            }

            // Parse XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            document.getDocumentElement().normalize();

            System.out.println("✓ Successfully loaded config.xml");

            // Extract timer settings
            Element timerSettings = (Element) document.getElementsByTagName("timerSettings").item(0);
            if (timerSettings != null) {
                config.put("checkHour", getElementText(timerSettings, "checkHour"));
                config.put("checkMinute", getElementText(timerSettings, "checkMinute"));
                config.put("enabled", getElementText(timerSettings, "enabled"));
            }

            // Extract notification settings
            Element notificationSettings = (Element) document.getElementsByTagName("notificationSettings").item(0);
            if (notificationSettings != null) {
                config.put("consoleNotificationsEnabled",
                        getElementText(notificationSettings, "consoleNotificationsEnabled"));
                config.put("showDetailedInfo",
                        getElementText(notificationSettings, "showDetailedInfo"));
            }

            // Extract system settings
            Element systemSettings = (Element) document.getElementsByTagName("systemSettings").item(0);
            if (systemSettings != null) {
                config.put("timeZone", getElementText(systemSettings, "timeZone"));
                config.put("dateFormat", getElementText(systemSettings, "dateFormat"));
                config.put("minDaysToReport", getElementText(systemSettings, "minDaysToReport"));
                config.put("maxDaysToReport", getElementText(systemSettings, "maxDaysToReport"));
            }

            System.out.println("✓ Loaded " + config.size() + " configuration settings\n");

        } catch (Exception e) {
            System.err.println("✗ Error loading configuration: " + e.getMessage());
            throw e;
        }

        return config;
    }

    /**
     * Displays loaded configuration in a readable format.
     * Useful for debugging and verification.
     * 
     * @param config Configuration map to display
     */
    public static void displayConfig(Map<String, String> config) {
        System.out.println("========== System Configuration ==========");
        config.forEach((key, value) -> System.out.printf("  %-30s: %s%n", key, value));
        System.out.println("==========================================\n");
    }
}
