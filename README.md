# Calendar Application

A full-featured calendar application with support for multiple calendars, timezone management, recurring events, and Google Calendar integration through CSV/iCal export functionality.

## Features

- **Multiple Calendar Support** - Create and manage multiple calendars with different timezones
- **Event Management** - Create, edit, and delete single or recurring events
- **Timezone Handling** - Each calendar maintains its own timezone with automatic conversion
- **Recurring Events** - Support for complex recurrence patterns with flexible editing
- **Import/Export** - Export to CSV and iCal formats compatible with Google Calendar
- **Multiple Interfaces** - GUI (Java Swing), Command Line Interface, and Headless mode

## Prerequisites

- Java 11 or higher
- Gradle (for building)

## Building the Application

```bash
./gradlew jar
```

This will create a JAR file in `build/libs/calendar-1.0.jar`

## Quick Start

### GUI Mode (Default)
```bash
java -jar build/libs/calendar-1.0.jar
```

### Command Line Mode
```bash
java -jar build/libs/calendar-1.0.jar --mode interactive
```

### Batch Processing Mode
```bash
java -jar build/libs/calendar-1.0.jar --mode headless commands.txt
```

## Documentation

### 📖 User Guides

- **[GUI User Guide](gui-useme-markdown.md)** - Complete guide for using the graphical interface
  - Calendar creation and management
  - Event scheduling and editing
  - Visual navigation and search features
  - Step-by-step instructions with UI element descriptions

- **[CLI User Guide](cli-useme-markdown.md)** - Comprehensive command-line interface documentation
  - Interactive mode usage
  - Headless/batch mode with script files
  - Complete command reference
  - Examples and workflows



## Key Features Overview

### Calendar Management
- Create multiple calendars with unique names
- Set different timezones for each calendar
- Switch between calendars seamlessly

### Event Types
- **Single Events** - One-time occurrences with start/end times
- **All-Day Events** - Default 8 AM to 5 PM duration
- **Recurring Events** - Daily, weekly, or custom patterns
- **Event Series** - Edit single instances or entire series

### Export Options
- **CSV Format** - Compatible with Google Calendar import
- **iCal Format** - Universal calendar format (.ical)

## Technology Stack

- **Language**: Java 11
- **GUI Framework**: Java Swing
- **Build Tool**: Gradle
- **Architecture**: MVC (Model-View-Controller)
- **Testing**: JUnit

