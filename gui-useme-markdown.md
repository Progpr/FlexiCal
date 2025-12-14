# USEME.md - Calendar Application GUI Guide

## Running the Program

The main method is located in `src/main/java/CalendarRunner.java`

### Build the jar file first (Requires Java 11)
```bash
./gradlew jar
```

## GUI Mode (Default)
Opens the graphical user interface for interactive calendar management.

```bash
java -jar build/libs/calendar-1.0.jar
```
Or simply double-click the JAR file.

## GUI Operations Guide

### Calendar Management

<img width="1918" height="1016" alt="Image" src="https://github.com/user-attachments/assets/86df9cc1-2277-4bde-bb48-6d5c40c75639" />

#### Creating a New Calendar
1. Click the **"Navigate calendars"** button in the toolbar
2. Click the **"+ Create New Calendar"** button in the left panel
3. In the dialog box:
   - Enter a calendar name (e.g., "Work Calendar")
   - Enter a timezone (e.g., "America/New_York", "Europe/London")
   - Click **"Create"** to save or **"Cancel"** to close

<img width="1918" height="1020" alt="Image" src="https://github.com/user-attachments/assets/5d7dd853-1e01-438f-905b-398f86fef26f" />
  


#### Navigating Between Calendars
1. Click the **"Navigate calendars"** button in the toolbar
2. All available calendars will appear in the left panel
3. The current calendar is displayed in the toolbar as "Current: [Calendar Name] (Timezone)"

<img width="1918" height="1015" alt="Image" src="https://github.com/user-attachments/assets/91ae9f2c-13d8-411c-9e00-60b82f3a346f" />


#### Using a Calendar
1. Click on any calendar name in the left panel
2. Select **"Use Calendar"** from the popup menu
3. This calendar becomes the active calendar for all operations

<img width="1918" height="1017" alt="Image" src="https://github.com/user-attachments/assets/ecffdff7-50af-4d62-88f5-04f3118da294" />


#### Editing Calendar Name
1. Click on a calendar name in the left panel
2. Select **"Edit Calendar Name"** from the popup menu
3. Enter the new name in the dialog box
4. Click **OK** to save changes

<img width="1918" height="1017" alt="Image" src="https://github.com/user-attachments/assets/40e1f3b3-3cfe-41e4-b350-b4f1a2a702de" />



#### Editing Calendar Timezone
1. Click on a calendar name in the left panel
2. Select **"Edit Calendar Timezone"** from the popup menu
3. Enter the new timezone (e.g., "Asia/Tokyo")
4. Click **OK** to save changes

<img width="1918" height="1017" alt="Image" src="https://github.com/user-attachments/assets/4a4a3c27-7114-4759-b005-d0b36d47353e" />



### Event Management

#### Viewing Events for a Specific Day
1. Click on any date in the calendar grid
2. The selected date will be highlighted in blue
3. All events for that date will appear in the left panel
4. Today's date is automatically highlighted when the application starts

<img width="1918" height="1017" alt="Image" src="https://github.com/user-attachments/assets/96cc43fe-09b2-4987-85d0-26d0c15f5078" />



#### Creating a New Event
1. Select a date by clicking on it in the calendar
2. Click the **"+ Create New Event"** button in the left panel
3. In the Create Event dialog:
   - **Event Name**: Enter the event title
   - **Status**: Enter event status (e.g., "Confirmed", "Tentative")
   - **Start Date**: Automatically filled with selected date
   - **End Date**: Enter end date (YYYY-MM-DD format)
   - **Start Time**: Enter start time (HH:mm format, e.g., "14:30")
   - **End Time**: Enter end time (HH:mm format)
   - **Location**: Enter event location
   - **Description**: Enter event description
4. Click **"Create Event"** to save or **"Cancel"** to close

<img width="1918" height="1017" alt="Image" src="https://github.com/user-attachments/assets/5e84799b-8628-4231-aa79-224425232adc" />



#### Creating a Recurring Event
1. Follow the steps for creating a new event
2. Check the **"Recurring Event"** checkbox
3. Additional fields will appear:
   - **Repeats**: Enter pattern (e.g., "Daily", "Weekly", "Mon,Wed,Fri")
   - Choose either:
     - **Until**: Select end date for recurrence
     - **No. of times**: Enter number of occurrences
4. Click **"Create Event"** to save the series

<img width="1918" height="1017" alt="Image" src="https://github.com/user-attachments/assets/0f612f75-3d9d-419a-8a3d-e1452b138719" />



#### Editing an Event
1. Select a date to view its events
2. Click on the event button in the left panel
3. The Edit Event dialog will open with current event details
4. Choose the field you want to modify from dropdown and enter new value
5. For recurring events, choose to edit:
   - All events in the series
   - This and all future events
   - (Radio buttons will be available for selection)
6. Click **"Save Changes"** to update

<img width="1512" height="945" alt="Image" src="https://github.com/user-attachments/assets/9fe13be2-9ab9-4187-b325-c213bb58318d" />



### Navigation Features

#### Navigating Between Months
- Click **"Previous month"** button to go to the previous month
- Click **"Next month"** button to go to the next month
- Current month and year are displayed in the center of the toolbar

#### Searching for Events
1. Enter search terms in the **Search** field in the toolbar (top-right)
2. Press **Enter** to search
3. Search results will appear in the left panel
4. Click **"Clear Search"** to return to the normal view

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

- **Multiple Calendars**: Create separate calendars for different purposes (Work, Personal, School)
- **Timezone Management**: Each calendar maintains its own timezone for accurate scheduling
- **Quick Navigation**: Use the month navigation buttons to quickly browse future/past events
- **Search Function**: Use the search bar to quickly find specific events by name
- **Visual Feedback**: Hover over buttons to see visual feedback (color changes)
- **Recurring Events**: Use the recurrence feature for regular meetings or appointments

## Error Handling

- **Invalid Timezone**: If you enter an invalid timezone, an error dialog will appear with suggestions
- **Duplicate Calendar Names**: The system prevents creating calendars with duplicate names
- **Invalid Date/Time**: Error messages guide you to correct format (YYYY-MM-DD for dates, HH:mm for times)
- **Empty Required Fields**: The system prompts you to fill in required fields before saving

## File Structure

The application expects to be run from the project root directory. All file paths are platform-independent and will work on Windows, macOS, and Linux systems.
