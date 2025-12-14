# USEME.md - Calendar Application CLI Guide

## Running the Program

The main method is located in `src/main/java/CalendarRunner.java`

### Build the jar file first (Requires Java 11)
```bash
./gradlew jar
```

## Execution Modes

### 1. Interactive Text Mode
Opens a command-line interface for entering commands one at a time.

```bash
java -jar build/libs/calendar-1.0.jar --mode interactive
```

**Example session:**
```
> create calendar --name Personal --timezone America/New_York
Successfully created calendar: Personal
> use calendar --name Personal
Now using calendar: Personal
> create event Meeting from 2025-05-15T10:00 to 2025-05-15T11:00
Saved event: Meeting
> print events on 2025-05-15
- Meeting starting at 10:00, ending at 11:00
> exit
```

### 2. Headless Mode (Script Execution)
Executes commands from a script file without user interaction.

```bash
java -jar build/libs/calendar-1.0.jar --mode headless commands.txt
```

Where `commands.txt` contains:
```
create event "Team Meeting" from 2025-05-15T10:00 to 2025-05-15T11:00
print events on 2025-05-15
export cal output.csv
exit
```

## Command Reference

### Calendar Management

#### Create a new calendar
```
create calendar --name <calendarName> --timezone <area/location>
```
Examples:
```
create calendar --name Personal --timezone America/New_York
create calendar --name Work --timezone Europe/London
create calendar --name School --timezone Asia/Tokyo
```
Supported timezones follow IANA format (e.g., America/New_York, Europe/Paris, Asia/Kolkata, Australia/Sydney)

#### Select a calendar to work with
```
use calendar --name <calendarName>
```
Example:
```
use calendar --name Personal
```
**Note:** You must select a calendar before creating or managing events.

#### Edit calendar properties
```
edit calendar --name <calendarName> --property <propertyName> <newValue>
```
Examples:
```
edit calendar --name Work --property timezone America/Chicago
edit calendar --name School --property name University
```
Properties: `name`, `timezone`

### Creating Events

#### Create event with specific times
```
create event <subject> from <YYYY-MM-DDThh:mm> to <YYYY-MM-DDThh:mm>
```
Examples:
```
create event Meeting from 2025-05-15T10:00 to 2025-05-15T11:00
create event "Team Standup" from 2025-05-16T09:00 to 2025-05-16T09:30
```

#### Create all-day event
```
create event <subject> on <YYYY-MM-DD>
```
Examples:
```
create event Lunch on 2025-05-15
create event "All Day Conference" on 2025-05-20
```
**Note:** All-day events default to 8:00 AM - 5:00 PM

### Creating Recurring Events (Series)

#### Create series with number of occurrences
```
create event <subject> from <YYYY-MM-DDThh:mm> to <YYYY-MM-DDThh:mm> repeats <days> for <n> times
```
Example:
```
create event "Weekly Standup" from 2025-05-19T09:00 to 2025-05-19T09:30 repeats MW for 5 times
```

#### Create series with end date
```
create event <subject> from <YYYY-MM-DDThh:mm> to <YYYY-MM-DDThh:mm> repeats <days> until <YYYY-MM-DD>
```
Example:
```
create event "Daily Scrum" from 2025-05-20T10:00 to 2025-05-20T10:15 repeats MTWRF until 2025-06-15
```

#### Create all-day series
```
create event <subject> on <YYYY-MM-DD> repeats <days> for <n> times
create event <subject> on <YYYY-MM-DD> repeats <days> until <YYYY-MM-DD>
```
Examples:
```
create event "Gym Session" on 2025-05-21 repeats MWF for 8 times
create event "Team Lunch" on 2025-05-22 repeats F until 2025-06-30
```

**Day codes:** M=Monday, T=Tuesday, W=Wednesday, R=Thursday, F=Friday, S=Saturday, U=Sunday

### Editing Events

#### Edit single event
```
edit event <property> <subject> from <YYYY-MM-DDThh:mm> with <newValue>
```

#### Edit multiple events in a series (current and future)
```
edit events <property> <subject> from <YYYY-MM-DDThh:mm> with <newValue>
```

#### Edit entire series
```
edit series <property> <subject> from <YYYY-MM-DDThh:mm> with <newValue>
```

**Available Properties:**
- `subject` - The event title (any string)
- `start` - Start date/time (YYYY-MM-DDThh:mm format)
- `end` - End date/time (YYYY-MM-DDThh:mm format)
- `description` - Event description (any string)
- `location` - Must be either `Physical` or `Online`
- `status` - Must be either `Private` or `Public`

Examples:
```
edit event subject Meeting from 2025-05-15T10:00 with "Important Meeting"
edit event description Meeting from 2025-05-15T10:00 with "Quarterly planning discussion"
edit event location Meeting from 2025-05-15T10:00 with Physical
edit event status Meeting from 2025-05-15T10:00 with Private
edit events start "Daily Scrum" from 2025-05-20T10:00 with 2025-05-20T09:30
edit series subject "Gym Session" from 2025-05-21T08:00 with "Morning Workout"
```

### Copying Events

#### Copy single event to another calendar
```
copy event <eventName> on <dateStringTtimeString> --target <calendarName> to <dateStringTtimeString>
```
Example:
```
copy event Meeting on 2025-05-15T10:00 --target Work to 2025-06-05T14:00
```

#### Copy all events on a specific day
```
copy events on <dateString> --target <calendarName> to <dateString>
```
Example:
```
copy events on 2025-05-15 --target Work to 2025-06-20
```

#### Copy events within a date range
```
copy events between <dateString> and <dateString> --target <calendarName> to <dateString>
```
Example:
```
copy events between 2025-05-19 and 2025-05-21 --target School to 2025-07-01
```

### Querying Events

#### Print events on a specific date
```
print events on <YYYY-MM-DD>
```
Example:
```
print events on 2025-05-15
```

#### Print events in a date/time range
```
print events from <YYYY-MM-DDThh:mm> to <YYYY-MM-DDThh:mm>
```
Example:
```
print events from 2025-05-15T00:00 to 2025-05-20T23:59
```

#### Check availability status
```
show status on <YYYY-MM-DDThh:mm>
```
Example:
```
show status on 2025-05-15T10:00
```
Returns: `Busy` or `Available`

### Export Calendar

#### Export to CSV format
```
export cal <filename.csv>
```
Example:
```
export cal calendar_export.csv
```

#### Export to iCal format
```
export cal <filename.ical>
```
Example:
```
export cal calendar_export.ical
```

The export format is automatically detected based on file extension.
The absolute path of the exported file will be displayed after successful export.

### Exit Application
```
exit
```

## Date and Time Formats

- **Date**: `YYYY-MM-DD` (e.g., `2025-05-15`)
- **Time**: `hh:mm` in 24-hour format (e.g., `14:30`)
- **DateTime**: `YYYY-MM-DDThh:mm` (e.g., `2025-05-15T14:30`)
- **Timezone**: IANA format `area/location` (e.g., `America/New_York`)

## Important Notes

1. **Calendar context required**: You must create and select a calendar using `use calendar --name` before creating or managing events.

2. **Unique calendar names**: No two calendars can have the same name.

3. **Multi-word subjects** must be enclosed in double quotes:
   ```
   create event "Team Standup" from 2025-05-15T10:00 to 2025-05-15T11:00
   ```

4. **No duplicate events**: Two events cannot have the same subject, start date/time, and end date/time.

5. **Invalid commands** will display an error message indicating what went wrong.

## Complete Example Workflow

### commands.txt
```
create calendar --name Personal --timezone America/New_York
create calendar --name Work --timezone America/Los_Angeles
use calendar --name Personal
create event Meeting from 2025-05-15T10:00 to 2025-05-15T11:00
edit event description Meeting from 2025-05-15T10:00 with "Budget review meeting"
edit event location Meeting from 2025-05-15T10:00 with Physical
create event "Team Standup" from 2025-05-16T09:00 to 2025-05-16T09:30 repeats MWF for 10 times
create event Lunch on 2025-05-15
print events on 2025-05-15
use calendar --name Work
create event "Quarterly Review" from 2025-06-01T14:00 to 2025-06-01T16:00
copy event Meeting on 2025-05-15T10:00 --target Work to 2025-06-05T14:00
export cal personal_calendar.csv
export cal work_calendar.ical
exit
```

Run with:
```bash
java -jar build/libs/calendar-1.0.jar --mode headless commands.txt
```