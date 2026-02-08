# Academic Planning System - Architecture Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Why XML?](#why-xml)
3. [Why EJB Timer?](#why-ejb-timer)
4. [How the Scheduled Task Works](#how-the-scheduled-task-works)
5. [Component Details](#component-details)
6. [Data Flow](#data-flow)
7. [Design Decisions](#design-decisions)

---

## Architecture Overview

The Academic Planning System follows a **three-tier architecture** adapted for educational purposes:

```
┌─────────────────────────────────────────────────────┐
│              PRESENTATION LAYER                     │
│  (Console Output / Scheduled Reports)               │
│  • TimerService.java - Scheduling interface         │
│  • Main.java - Standalone testing interface         │
└────────────────┬────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────┐
│              BUSINESS LOGIC LAYER                   │
│  (Enterprise JavaBeans)                             │
│  • AcademicService.java - Exam check logic          │
│  • TimerService.java - Scheduling coordination      │
└────────────────┬────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────┐
│              DATA ACCESS LAYER                      │
│  (XML Parsing)                                      │
│  • XMLParser.java - DOM-based XML reading           │
└────────────────┬────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────┐
│              DATA STORAGE LAYER                     │
│  (XML Files)                                        │
│  • planning.xml - Course and exam data              │
│  • config.xml - System configuration                │
└─────────────────────────────────────────────────────┘
```

### Component Interaction

```
Timer Trigger (9:00 AM)
    │
    ├──> TimerService.performDailyCheck()
    │       │
    │       ├──> AcademicService.performDailyExamCheck()
    │       │       │
    │       │       ├──> getAllCourses()
    │       │       │       │
    │       │       │       └──> XMLParser.loadCourses()
    │       │       │               │
    │       │       │               └──> Reads planning.xml
    │       │       │
    │       │       ├──> getCoursesNeedingReminders()
    │       │       │       │
    │       │       │       └──> Filters courses by date logic
    │       │       │
    │       │       └──> Display formatted output
    │       │
    │       └──> Catches and logs any errors
    │
    └──> Waits until next day at 9:00 AM
```

---

## Why XML?

### Rationale for Using XML as Data Source

XML (eXtensible Markup Language) was chosen for this academic planning system for several compelling reasons:

#### 1. **Externalized Data**
- Course information can be updated without recompiling Java code
- Non-programmers (administrators, professors) can edit course data
- Changes take effect immediately on next execution
- No need for code deployment for data updates

#### 2. **Human-Readable Format**
```xml
<course>
    <code>CS101</code>
    <name>Introduction to Programming</name>
    <examDate>2026-03-15</examDate>
</course>
```
- Self-documenting structure
- Easy to understand and validate visually
- Can be edited in any text editor

#### 3. **Standardized and Platform-Independent**
- XML is a W3C standard
- Works across all platforms (Windows, Mac, Linux)
- Many tools available for validation and editing
- Can be processed by multiple programming languages

#### 4. **Educational Benefits**
- Demonstrates XML parsing in Java
- Shows alternative to database for small-scale applications
- Teaches configuration management patterns
- Relevant for enterprise Java curriculum

#### 5. **No Database Complexity**
For this academic project scope:
- No database installation required
- No SQL learning curve
- No connection pooling or persistence layer
- Perfect for focused study of scheduling concepts

### XML vs. Database Trade-offs

| Aspect | XML | Database |
|--------|-----|----------|
| **Setup Complexity** | ✅ None (just files) | ❌ Installation, configuration |
| **Query Performance** | ❌ Slow for large data | ✅ Optimized indexing |
| **Concurrent Access** | ❌ File locking issues | ✅ Built-in concurrency |
| **Human Readability** | ✅ Easy to read/edit | ❌ Requires SQL/tools |
| **Suitable Data Size** | ✅ Small (<1000 records) | ✅ Any size |
| **Versioning** | ✅ Git-friendly | ❌ Requires migrations |
| **Educational Focus** | ✅ Focuses on scheduling | ❌ Adds database complexity |

**For This Project:** XML is perfect because we have:
- Small dataset (5-20 courses typical)
- Read-heavy operations (daily reads, infrequent updates)
- Educational focus on EJB Timer, not databases
- Need for human-editable configuration

---

## Why EJB Timer?

### The Problem EJB Timer Solves

**Challenge:** How do we run code automatically at scheduled times without manual intervention?

**Traditional Approaches:**
1. Manual execution - requires someone to run program daily ❌
2. Operating system cron jobs - platform-specific, outside Java ❌
3. Java Timer/ScheduledExecutorService - requires manual thread management ❌
4. While loop with sleep - inefficient, error-prone ❌

**EJB Timer Solution:** Container-managed, declarative scheduling ✅

### Benefits of EJB Timer

#### 1. **Container-Managed Lifecycle**
```java
@Schedule(hour = "9", minute = "0", persistent = false)
public void performDailyCheck() {
    // Container automatically calls this at 9 AM daily
}
```
- Developer just declares when to run
- Container handles all the scheduling mechanics
- No manual thread creation or management

#### 2. **Declarative Configuration**
- Annotation-based (readable, maintainable)
- Cron-like expressions for flexibility
- Changes visible in code, not buried in configuration files

#### 3. **Enterprise Features**
- **Transaction Support:** Timer methods can participate in transactions
- **Security Integration:** Can enforce role-based access
- **Clustering:** Can coordinate across multiple servers
- **Error Recovery:** Container handles exceptions gracefully

#### 4. **Reliability**
- Container guarantees execution (when server is running)
- Automatic retry on transient failures
- Persistent timers survive server restarts (if configured)

#### 5. **Educational Value**
- Demonstrates enterprise Java patterns
- Shows dependency injection (@EJB)
- Teaches container services concept
- Relevant for Java EE certification

### EJB Timer vs. Alternatives

| Feature | EJB Timer | Quartz Scheduler | ScheduledExecutorService | Cron Job |
|---------|-----------|------------------|--------------------------|----------|
| **Java EE Integration** | ✅ Native | ❌ External library | ⚠️ Java SE only | ❌ OS-level |
| **Setup Complexity** | ✅ Simple | ❌ Complex config | ✅ Simple | ⚠️ Platform-specific |
| **Transaction Support** | ✅ Built-in | ⚠️ Manual | ❌ Manual | ❌ None |
| **Clustering** | ✅ Supported | ✅ Supported | ❌ Not built-in | ❌ No |
| **Persistence** | ✅ Optional | ✅ Yes | ❌ No | ⚠️ OS-dependent |
| **Educational Fit** | ✅ Perfect for Java EE | ❌ Overkill | ⚠️ Too basic | ❌ Wrong focus |

**For This Project:** EJB Timer is ideal because:
- Built into Java EE (no extra dependencies)
- Simple annotation-based setup
- Teaches enterprise concepts
- Production-ready pattern

---

## How the Scheduled Task Works

### Step-by-Step Execution Flow

```
┌──────────────────────────────────────────────────────────────┐
│  1. APPLICATION DEPLOYMENT                                   │
│     • WAR/EAR deployed to Java EE server (WildFly/TomEE)     │
│     • Container scans classes for @Schedule annotations      │
│     • Timer registered in container's scheduler              │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  2. TIMER INITIALIZATION                                     │
│     • Container creates timer metadata                       │
│     • Schedule: "Every day at 9:00 AM"                       │
│     • Persistent: false (memory-only)                        │
│     • Status: ACTIVE                                         │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  3. WAITING FOR SCHEDULED TIME                               │
│     • Container monitors system clock                        │
│     • Timer sleeps in background                             │
│     • No CPU usage while waiting                             │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼ (9:00 AM arrives)
┌──────────────────────────────────────────────────────────────┐
│  4. TIMER TRIGGER                                            │
│     • Container detects scheduled time reached               │
│     • Gets TimerService instance from EJB pool               │
│     • Injects dependencies (AcademicService via @EJB)        │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  5. METHOD EXECUTION: TimerService.performDailyCheck()       │
│     • Logs execution timestamp                               │
│     • Calls AcademicService.performDailyExamCheck()          │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  6. BUSINESS LOGIC: AcademicService.performDailyExamCheck()  │
│     ┌──────────────────────────────────────────────────┐    │
│     │ a) Load courses from planning.xml                │    │
│     │    • XMLParser.loadCourses()                      │    │
│     │    • DOM parsing of course elements               │    │
│     └──────────────────────────────────────────────────┘    │
│     ┌──────────────────────────────────────────────────┐    │
│     │ b) Calculate days until each exam                │    │
│     │    • LocalDate.now() vs. examDate                │    │
│     │    • ChronoUnit.DAYS.between()                    │    │
│     └──────────────────────────────────────────────────┘    │
│     ┌──────────────────────────────────────────────────┐    │
│     │ c) Filter courses needing reminders              │    │
│     │    • Check Course.needsReminder()                │    │
│     │    • daysUntil <= reminderDaysBefore             │    │
│     └──────────────────────────────────────────────────┘    │
│     ┌──────────────────────────────────────────────────┐    │
│     │ d) Generate formatted output                     │    │
│     │    • Display exam details                        │    │
│     │    • Urgency indicators (🔴🟠🟡🟢)               │    │
│     │    • Summary statistics                          │    │
│     └──────────────────────────────────────────────────┘    │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  7. OUTPUT TO CONSOLE/LOGS                                   │
│     ════════════════════════════════════════════             │
│     DAILY ACADEMIC PLANNING CHECK - 2026-02-08               │
│     ════════════════════════════════════════════             │
│     ⚠️ EXAM REMINDERS (2 courses):                           │
│     🟡 CS101 - Intro to Programming (5 days)                 │
│     🟢 CS201 - Data Structures (10 days)                     │
│     ════════════════════════════════════════════             │
└──────────────────────┬───────────────────────────────────────┘
                       │
                       ▼
┌──────────────────────────────────────────────────────────────┐
│  8. CLEANUP AND RESCHEDULE                                   │
│     • Method execution completes                             │
│     • TimerService instance returned to EJB pool             │
│     • Container reschedules timer for tomorrow 9:00 AM       │
│     • Cycle repeats                                          │
└──────────────────────────────────────────────────────────────┘
```

### Reminder Calculation Logic

```java
// For each course, determine if reminder is needed
public boolean needsReminder() {
    long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), examDate);
    
    // Reminder needed if:
    // 1. Exam is in the future (daysUntil >= 0)
    // 2. Within reminder window (daysUntil <= reminderDaysBefore)
    return daysUntil >= 0 && daysUntil <= reminderDaysBefore;
}
```

**Example:**
- Today: February 8, 2026
- Exam Date: February 15, 2026
- Days Until: 7 days
- Reminder Days Before: 7
- **Result:** ✅ Reminder triggered (7 <= 7)

---

## Component Details

### 1. Course.java (Domain Model)
**Purpose:** Represents a single academic course

**Key Responsibilities:**
- Store course data (code, name, instructor, exam date, reminder settings)
- Calculate days until exam
- Determine if reminder is needed
- Format display information

**Design Pattern:** Plain Old Java Object (POJO) / Value Object

### 2. XMLParser.java (Data Access)
**Purpose:** Read and parse XML files

**Key Responsibilities:**
- Load planning.xml and convert to Course objects
- Load config.xml and extract settings
- Handle XML parsing errors gracefully
- Provide utility methods for DOM navigation

**Technology:** DOM (Document Object Model) parser
- Loads entire XML into memory as tree structure
- Good for small files (<1 MB)
- Easy to navigate and extract data

### 3. AcademicService.java (Business Logic)
**Purpose:** Core business logic for exam management

**Key Responsibilities:**
- Retrieve all courses from XML
- Filter courses needing reminders
- Perform daily exam check
- Generate formatted reports
- Calculate exam statistics

**EJB Type:** @Stateless Session Bean
- Pool of instances managed by container
- Thread-safe (each client gets own instance)
- No conversational state between calls

### 4. TimerService.java (Scheduler)
**Purpose:** Automated scheduling coordination

**Key Responsibilities:**
- Define schedule via @Schedule annotation
- Trigger daily exam checks automatically
- Delegate business logic to AcademicService
- Handle scheduling errors
- Provide manual trigger for testing

**EJB Type:** @Stateless Session Bean with @Schedule
- Timer automatically created on deployment
- Executes at specified times
- Non-persistent (timer data not saved to database)

### 5. Main.java (Entry Point)
**Purpose:** Standalone testing and demonstration

**Key Responsibilities:**
- Load configuration and data
- Manually execute exam check logic
- Display comprehensive test output
- Simulate what timer would do

**Usage:** Development and demonstration only (not used in production)

---

## Data Flow

### Read Flow (Daily Check)

```
User/Timer
    │
    ├──> TimerService.performDailyCheck()
    │       │
    │       └──> AcademicService.performDailyExamCheck()
    │               │
    │               ├──> getAllCourses()
    │               │       │
    │               │       └──> XMLParser.loadCourses()
    │               │               │
    │               │               ├──> Read planning.xml from resources
    │               │               ├──> Parse XML with DOM
    │               │               ├──> Extract <course> elements
    │               │               ├──> Create Course objects
    │               │               └──> Return List<Course>
    │               │
    │               ├──> getCoursesNeedingReminders()
    │               │       │
    │               │       └──> Stream filter: course.needsReminder()
    │               │               │
    │               │               └──> Course.getDaysUntilExam()
    │               │
    │               └──> Display formatted output
    │
    └──> Console/Logs
```

### Write Flow (Configuration Update)

```
Administrator
    │
    ├──> Edit planning.xml in text editor
    │       │
    │       ├──> Change exam dates
    │       ├──> Add new courses
    │       ├──> Update reminder settings
    │       └──> Save file
    │
    └──> Next daily check reads updated data automatically
```

---

## Design Decisions

### 1. Why Stateless EJBs?
- **Scalability:** Container pools instances efficiently
- **Thread Safety:** No shared state between calls
- **Simplicity:** Don't need conversational state for this use case

### 2. Why Non-Persistent Timer?
- **Simplicity:** No database setup required
- **Academic Fit:** Focus on scheduling concepts, not persistence
- **Trade-off Accepted:** Missing checks during server downtime is acceptable for this use case

### 3. Why DOM Parser (not SAX)?
- **Ease of Use:** Simple navigation of XML tree
- **Small Data:** Our XML files are small enough for memory
- **Clarity:** Code is easier to understand for students

### 4. Why Java 8 LocalDate (not Date/Calendar)?
- **Modern API:** LocalDate is current best practice
- **Immutable:** Thread-safe and less error-prone
- **Clear:** Better API than legacy Date class

### 5. Why Console Output (not Database Logging)?
- **Visibility:** Immediate feedback during development
- **Simplicity:** No logging framework configuration
- **Educational:** Clear demonstration of functionality

### 6. Separation of Concerns
- **Model Layer:** Course.java - data structure only
- **Data Access:** XMLParser.java - knows how to read XML
- **Business Logic:** AcademicService.java - exam checking rules
- **Scheduling:** TimerService.java - when to execute
- **Each layer has single responsibility**

---

## Summary

This Academic Planning System demonstrates enterprise Java best practices through:

1. **XML for Data Management**
   - Externalized, human-readable configuration
   - Appropriate for small-scale academic data
   - Demonstrates alternative to databases

2. **EJB Timer for Automation**
   - Container-managed scheduling
   - Declarative configuration
   - Enterprise-grade reliability

3. **Layered Architecture**
   - Clear separation of concerns
   - Maintainable and testable
   - Industry-standard patterns

4. **Educational Value**
   - Comprehensive code comments
   - Clear documentation
   - Demonstrates Java EE concepts
   - Production-ready patterns in simple form
