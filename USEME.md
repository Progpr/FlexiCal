# USEME.md - Calendar Application User Guide

## Running the Program

The main method is located in `src/main/java/CalendarRunner.java`

### Three Execution Modes

#### 1. GUI Mode (Default)
Opens the graphical user interface for interactive calendar management.
```bash
java -jar calendar-1.0.jar
```
Or simply double-click the JAR file.

#### 2. Interactive Text Mode
Opens a command-line interface for entering commands one at a time.
```bash
java -jar calendar-1.0.jar --mode interactive
```
Example session:
```
> create event Meeting from 2025-05-15T10:00 to 2025-05-15T11:00
Saved event: Meeting
> print events on 2025-05-15
- Meeting starting at 10:00, ending at 11:00
> exit
```

#### 3. Headless Mode (Script Execution)
Executes commands from a script file without user interaction.
```bash
java -jar calendar-1.0.jar --mode headless commands.txt
```
Where `commands.txt` contains:
```
create event "Team Meeting" from 2025-05-15T10:00 to 2025-05-15T11:00
print events on 2025-05-15
export cal output.csv
```

## GUI Operations Guide

### Calendar Management

#### Creating a New Calendar
- Click the **"Navigate calendars"** button in the toolbar
- Click the **"+ Create New Calendar"** button in the left panel
- In the dialog box:
    - Enter a calendar name (e.g., "Work Calendar")
    - Enter a timezone (e.g., "America/New_York", "Europe/London")
    - Click **"Create"** to save or **"Cancel"** to close

#### Navigating Between Calendars
- Click the **"Navigate calendars"** button in the toolbar
- All available calendars will appear in the left panel
- The current calendar is displayed in the toolbar as "Current: [Calendar Name] (Timezone)"

#### Using a Calendar
- Click on any calendar name in the left panel
- Select **"Use Calendar"** from the popup menu
- This calendar becomes the active calendar for all operations

#### Editing Calendar Name
- Click on a calendar name in the left panel
- Select **"Edit Calendar Name"** from the popup menu
- Enter the new name in the dialog box
- Click **OK** to save changes

#### Editing Calendar Timezone
- Click on a calendar name in the left panel
- Select **"Edit Calendar Timezone"** from the popup menu
- Enter the new timezone (e.g., "Asia/Tokyo")
- Click **OK** to save changes

### Event Management

#### Viewing Events for a Specific Day
- Click on any date in the calendar grid
- The selected date will be highlighted in blue
- All events for that date will appear in the left panel
- Today's date is automatically highlighted when the application starts

#### Creating a New Event
- Select a date by clicking on it in the calendar
- Click the **"+ Create New Event"** button in the left panel
- In the Create Event dialog:
    - **Event Name**: Enter the event title
    - **Status**: Enter event status (e.g., "Confirmed", "Tentative")
    - **Start Date**: Automatically filled with selected date
    - **End Date**: Enter end date (YYYY-MM-DD format)
    - **Start Time**: Enter start time (HH:mm format, e.g., "14:30")
    - **End Time**: Enter end time (HH:mm format)
    - **Location**: Enter event location
    - **Description**: Enter event description
- Click **"Create Event"** to save or **"Cancel"** to close

#### Creating a Recurring Event
- Follow the steps for creating a new event
- Check the **"Recurring Event"** checkbox
- Additional fields will appear:
    - **Repeats**: Enter pattern (e.g., "Daily", "Weekly", "Mon,Wed,Fri")
    - Choose either:
        - **Until**: Select end date for recurrence
        - **No. of times**: Enter number of occurrences
- Click **"Create Event"** to save the series

#### Editing an Event
- Select a date to view its events
- Click on the event button in the left panel
- The Edit Event dialog will open with current event details based on if its part of a series or single event
- Choose the field you want to modify from drop down and enter new value you want to set
- For recurring events, choose to edit:
    - All events in the series
    - This and all future events
- There will be radio button to chose from above options
- Click **"Save Changes"** to update

### Navigation Features

#### Navigating Between Months
- Click **"Previous month"** button to go to the previous month
- Click **"Next month"** button to go to the next month
- Current month and year are displayed in the center of the toolbar

#### Searching for Events
- Enter search terms in the **Search** field in the toolbar (top-right)
- Press **Enter** to search
- Search results will appear in the left panel
- Click **"Clear Search"** to return to the normal view

### Calendar Display Features

- **Current Date**: Automatically highlighted in light blue
- **Selected Date**: Highlighted in blue when clicked
- **Month View**: Shows all days of the current month in a grid format
- **Day Headers**: Sunday through Saturday labels at the top
- **Empty Days**: Disabled and shown in white for days outside the current month

## Default Behavior

- On startup, a default calendar is created in your system's timezone
- The current date is automatically selected
- Events for the current date are displayed in the left panel
- You can immediately start creating events without creating a new calendar

## Tips for Best Use

1. **Multiple Calendars**: Create separate calendars for different purposes (Work, Personal, School)
2. **Timezone Management**: Each calendar maintains its own timezone for accurate scheduling
3. **Quick Navigation**: Use the month navigation buttons to quickly browse future/past events
4. **Search Function**: Use the search bar to quickly find specific events by name
5. **Visual Feedback**: Hover over buttons to see visual feedback (color changes)
6. **Recurring Events**: Use the recurrence feature for regular meetings or appointments

## Error Handling

- **Invalid Timezone**: If you enter an invalid timezone, an error dialog will appear with suggestions
- **Duplicate Calendar Names**: The system prevents creating calendars with duplicate names
- **Invalid Date/Time**: Error messages guide you to correct format (YYYY-MM-DD for dates, HH:mm for times)
- **Empty Required Fields**: The system prompts you to fill in required fields before saving

## File Structure

The application expects to be run from the project root directory. All file paths are platform-independent and will work on Windows, macOS, and Linux systems.