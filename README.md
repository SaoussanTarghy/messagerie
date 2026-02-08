# Academic Planning System

A Java EE application that manages academic planning (courses, exams, deadlines) using XML files for data storage and EJB Timer for automated exam reminders.

## 📋 Project Overview

This system demonstrates enterprise Java concepts including:
- **XML-based data management** (planning.xml, config.xml)
- **EJB Timer** with `@Schedule` annotation for automated scheduling
- **Stateless Session Beans** for business logic
- **DOM parsing** for XML processing
- **Java 8+ date/time API** for date calculations

## 🎯 Features

- ✅ Store academic course information in XML format
- ✅ Configure system settings via XML
- ✅ Automatic daily exam checks at scheduled times
- ✅ Smart reminder system based on days before exam
- ✅ Console-based output with formatted reports
- ✅ No database required

## 📁 Project Structure

```
academic-planning-system/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── academic/
│       │           └── planning/
│       │               ├── model/
│       │               │   └── Course.java          # Domain model
│       │               ├── parser/
│       │               │   └── XMLParser.java       # XML parsing utilities
│       │               ├── service/
│       │               │   ├── AcademicService.java # Business logic
│       │               │   └── TimerService.java    # EJB Timer service
│       │               └── Main.java                # Standalone entry point
│       └── resources/
│           ├── planning.xml                         # Academic data
│           └── config.xml                           # System configuration
├── README.md
└── ARCHITECTURE.md
```

## 🔧 Prerequisites

### For Standalone Testing (via Main.java):
- **Java Development Kit (JDK)**: Version 8 or higher
- No additional dependencies required

### For Full Java EE Deployment:
- **Java EE Application Server**: One of:
  - WildFly 10+ (recommended)
  - Apache TomEE 7+
  - Payara Server 5+
- **JDK**: Version 8 or higher

## 🚀 How to Run

### Option 1: Standalone Mode (Testing)

Perfect for quick testing and demonstration:

```bash
# Navigate to project directory
cd academic-planning-system

# Compile Java files
javac -d bin src/main/java/com/academic/planning/**/*.java

# Run the application
java -cp bin:src/main/resources com.academic.planning.Main
```

**What happens:**
- Loads and displays configuration from `config.xml`
- Loads all courses from `planning.xml`
- Performs a manual exam check (simulating timer execution)
- Shows upcoming exams in different timeframes

**Limitation:** EJB Timer won't run automatically; Main() simulates the timer logic.

### Option 2: Java EE Deployment (Production)

For automatic scheduling:

1. **Package as WAR file** (requires Maven/Gradle setup)
2. **Deploy to application server:**
   - **WildFly:** Copy `.war` to `standalone/deployments/`
   - **TomEE:** Copy `.war` to `webapps/`
3. **Start server**
4. **Check logs** at 9:00 AM daily for automated output

## 📝 How to Modify Data

### Adding/Editing Courses

Edit `src/main/resources/planning.xml`:

```xml
<course>
    <code>CS402</code>
    <name>Your New Course</name>
    <instructor>Dr. Your Name</instructor>
    <examDate>2026-04-15</examDate>  <!-- Format: YYYY-MM-DD -->
    <reminderDaysBefore>7</reminderDaysBefore>
</course>
```

### Changing Timer Schedule

Edit `src/main/java/com/academic/planning/service/TimerService.java`:

```java
@Schedule(
    hour = "14",    // Change to 2:00 PM
    minute = "30",  // Change to 2:30 PM
    persistent = false
)
```

### Changing System Settings

Edit `src/main/resources/config.xml`:

```xml
<timerSettings>
    <checkHour>10</checkHour>        <!-- Run at 10 AM instead -->
    <checkMinute>0</checkMinute>
    <enabled>true</enabled>
</timerSettings>
```

## 📊 Expected Output

When the system runs, you'll see:

```
======================================================================
  DAILY ACADEMIC PLANNING CHECK - 2026-02-08
======================================================================

⚠️  EXAM REMINDERS (2 course(s)):
----------------------------------------------------------------------

🟡 CS101 - Introduction to Programming
   Instructor: Dr. Sarah Smith
   Exam Date: 2026-03-15
   Days Until Exam: 5 day(s)
   >>> Very soon! Ensure you're well prepared.

🟢 CS201 - Data Structures and Algorithms
   Instructor: Prof. John Johnson
   Exam Date: 2026-03-20
   Days Until Exam: 10 day(s)
   >>> Start reviewing soon.

----------------------------------------------------------------------

📊 SUMMARY:
   Total Courses: 5
   Exams in Next 7 Days: 2
   Exams in Next 30 Days: 4

======================================================================
```

## 🧪 Testing the Timer

To test immediately without waiting:

1. Edit `TimerService.java`
2. Change schedule to current time + 2 minutes:
   ```java
   @Schedule(hour = "*", minute = "*/2", persistent = false)  // Every 2 minutes
   ```
3. Redeploy to server
4. Watch console output

## 🎓 Educational Value

This project demonstrates:

1. **XML as Data Source**
   - Human-readable format
   - Easy to modify without recompilation
   - Standard enterprise practice

2. **EJB Timer Benefits**
   - Container-managed scheduling
   - No manual thread management
   - Automatic execution
   - Enterprise-grade reliability

3. **Separation of Concerns**
   - Model: Data structure (Course.java)
   - Parser: Data access (XMLParser.java)
   - Service: Business logic (AcademicService.java)
   - Timer: Scheduling (TimerService.java)

4. **Java Best Practices**
   - Comprehensive documentation
   - Error handling
   - Modern Java 8+ APIs (LocalDate, Streams)
   - Design patterns (Service Layer, Separation of Concerns)

## 📚 Further Reading

- [Java EE Tutorial - Using EJB Timers](https://docs.oracle.com/javaee/7/tutorial/ejb-basicexamples003.htm)
- [DOM Parser Guide](https://docs.oracle.com/javase/tutorial/jaxp/dom/readingXML.html)
- [Java Time API](https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html)

## 📄 Related Documentation

See `ARCHITECTURE.md` for detailed technical documentation explaining:
- System architecture
- Role of XML
- Role of EJB Timer
- Complete execution flow

## 👨‍💻 Author

Academic Planning System - Educational Project
Version 1.0

## 📜 License

This is an academic project for educational purposes.
