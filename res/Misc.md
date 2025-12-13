# Misc.md

## List of Changes to the Design

### 1. **Addition of GUI View Layer (JFrameView)**
- **Change**: Created a new `JFrameView` class implementing `ViewInterface` for the graphical user interface
- **Justification**: Separates presentation logic from business logic, adhering to MVC principles. The GUI view operates independently from the existing text-based view, allowing users to choose their preferred interface mode

### 2. **Separate GUI Controller (GuiControllerHandlers)**
- **Change**: Implemented `GuiControllerHandlers` class as a dedicated controller for GUI operations
- **Justification**: Maintains separation of concerns by having distinct controllers for different view modes (GUI vs command-line). This prevents coupling between different UI paradigms and allows each controller to be optimized for its specific interface

### 3. **Controller-View Communication Pattern**
- **Change**: Established bidirectional communication where the controller is passed to the view via `addHandlers()` method, and the view is set in the controller via `setView()`
- **Justification**: Enables loose coupling between view and controller. The view doesn't directly know about the controller implementation, only the interface (`GuiControllerFeatures`), promoting flexibility and testability

### 4. **Event-Driven Architecture for GUI**
- **Change**: All user interactions trigger controller methods through action listeners centralized in the view's `addHandlers()` method
- **Justification**: Follows standard GUI design patterns and keeps all event handling logic organized in one place, making the code more maintainable and debuggable

### 5. **No Modifications to Existing Model**
- **Change**: The existing model layer (`CalendarManager`, `Calendar`, `Event`) remains unchanged
- **Justification**: Demonstrates proper MVC separation - the model is view-agnostic and can work with both command-line and GUI interfaces without modification

### 6. **No Modifications to Command-Line Controller**
- **Change**: The existing command-line controller remains untouched
- **Justification**: Maintains backward compatibility and allows the application to run in multiple modes (headless, interactive, GUI) without interference between different control flows

## Features Status

### Working Features:
- **Calendar Management**
    - Create new calendars with custom timezones
    - Navigate between multiple calendars
    - Edit calendar names
    - Edit calendar timezones
    - Set current/active calendar
    - Default calendar creation on startup

- **Event Management**
    - Create single events with all attributes (name, date, time, location, description, status)
    - Create recurring events with customizable patterns
    - Specify recurrence end conditions (until date or number of occurrences)
    - View events for selected dates
    - Edit existing events (single and recurring)
    - Edit all events in a series or individual events or that event and all the events after it
    - Search events by subject/name

- **User Interface**
    - Month view calendar display
    - Navigate between months (previous/next)
    - Day selection with visual feedback (highlighting)
    - Current date highlighting
    - Left panel for displaying calendars/events dynamically
    - Toolbar with calendar navigation and search functionality
    - Dialog boxes for creating/editing calendars and events

- **Program Modes**
    - GUI mode (java -jar JARNAME.jar)
    - Interactive text mode (java -jar JARNAME.jar --mode interactive)
    - Headless/script mode (java -jar JARNAME.jar --mode headless script-file)

### Features Not Working:
- None - All required features are functional

## Additional Information

### Architecture Highlights:
- The application successfully maintains MVC separation with distinct Model, View, and Controller layers
- The GUI implementation uses Java Swing exclusively (no external libraries)
- Error handling is implemented with user-friendly dialog boxes that don't expose implementation details
- The application handles invalid timezone inputs gracefully with appropriate error messages

### Design Decisions:
- Used `JSplitPane` to create a resizable layout between the calendar grid and the side panel
- Implemented a dynamic left panel that switches context between showing calendars and events based on user actions
- Color coding and visual feedback (hover effects, selection highlighting) improve user experience
- All file paths are platform-independent as required

### Testing Considerations:
- Controller logic is separated from view interactions, making it testable without GUI components
- Model remains unchanged and retains all existing test coverage
- The `GuiControllerHandlers` methods can be tested independently by mocking the view interface