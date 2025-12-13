package calendar.commandobject;

import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.Event;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper method class to process the string command and extract the important details.
 *
 */
public class ExtractCommandHelper {


  /**
   * Method to get new property value from command. Parses the edit calendar command
   * to extract the new value for a property being edited.
   *
   * @param command user command in format "edit calendar
   *                --name &lt;name&gt; --property &lt;prop&gt; &lt;value&gt;"
   * @return new property value as a String, or null if command format is incorrect
   */
  public static String getPropertyValueFromEditCalendarCommand(String command) {

    String[] parts = command.split("--property ");

    if (parts.length > 1) {

      String[] propertyValue = parts[1].split(" ");
      return propertyValue[1].trim();
    }

    return null;

  }

  /**
   * Method to get calendar property from command. Extracts the property name
   * that is being edited from the edit calendar command.
   *
   * @param command user command in format "edit calendar
   *                --property &lt;property-name&gt; &lt;value&gt;"
   * @return calendar property name as a String, or null if command format is incorrect
   */
  public static String getPropertyFromEditCalendarCommand(String command) {

    String[] parts = command.split("--property ");


    if (parts.length > 1) {
      String[] property = parts[1].split(" ");
      return property[0].trim();
    }

    return null;
  }

  /**
   * Method to get timezone from command. Extracts the timezone specification
   * from a create calendar command.
   *
   * @param command user command in format "create calendar
   *                --name &lt;name&gt; --timezone &lt;timezone&gt;"
   * @return calendar timezone as a String (e.g., "America/New_York"), or null if not found
   */
  public static String getTimezoneFromCalendarCommand(String command) {

    String[] parts = command.split("--timezone ");

    if (parts.length > 1) {
      return parts[1].trim();
    }


    return null;

  }

  /**
   * Method to get calendar Name from command. Extracts the calendar name
   * from various calendar-related commands.
   *
   * @param command user command containing "--name &lt;calendar-name&gt;"
   * @return calendar name as a String, or null if not found
   */
  public static String getCalendarNameFromCommand(String command) {

    String[] parts = command.split("--name ");

    if (parts.length > 1) {
      return parts[1].split("--")[0].trim();
    }

    return null;

  }


  /**
   * Retrieves event subject from the command string. Extracts the event subject/title
   * from a create event command, handling both quoted and unquoted subjects.
   *
   * @param commands user input in format "create event &lt;subject&gt; from/on &lt;date&gt;"
   * @return subject string with quotes removed, or empty string if extraction fails
   */
  public static String getSubjectFromCommand(String commands) {
    String keyword = commands.contains(" from ") ? "from" : "on";

    String subject = commands.substring(commands.indexOf("event") + "event".length(),
        commands.indexOf(" " + keyword + " ")).trim();

    if (subject.length() > 1) {
      subject = subject.replace("\"", "");
      return subject;
    }

    return subject;
  }


  /**
   * Retrieves date time from command. Parses a datetime value from the command
   * based on the specified keyword position.
   *
   * @param commands user input containing datetime in ISO format
   * @param keyword  either "from" or "to" to indicate which datetime to extract
   * @return LocalDateTime object parsed from the command string
   */
  public static LocalDateTime getDateTimeFromCommand(String commands, String keyword) {
    String keywordWithSpaces = " " + keyword + " ";
    int startIndex = commands.indexOf(keywordWithSpaces) + keywordWithSpaces.length();
    int endIndex;

    if (keyword.equals("from")) {
      endIndex = commands.indexOf(" to ");
    } else {
      endIndex = commands.length();
    }

    String dateString = commands.substring(startIndex, endIndex).trim();
    return LocalDateTime.parse(dateString);
  }


  /**
   * Retrieves date from command. Extracts a date value from commands
   * that specify events on a specific date.
   *
   * @param commands user input containing " on &lt;date&gt;"
   * @return LocalDate object parsed from the command string
   */
  public static LocalDate getDateFromCommand(String commands) {
    int startIndex = commands.indexOf(" on ") + " on ".length();
    String dateString = commands.substring(startIndex).trim();
    return LocalDate.parse(dateString);
  }


  /**
   * Gets subject from an edit event command. Extracts the event subject
   * from edit commands by parsing tokens before the "from" keyword.
   *
   * @param command edit event command string
   * @return subject string with quotes removed
   */
  public static String getSubjectFromCommandToEdit(String command) {
    int fromIndex = command.indexOf(" from ");
    String beforeFrom = command.substring(0, fromIndex);

    String[] tokens = beforeFrom.split(" ");
    StringBuilder subject = new StringBuilder();
    for (int i = 3; i < tokens.length; i++) {
      subject.append(tokens[i]).append(" ");
    }

    String result = subject.toString().trim();
    return result.replace("\"", "");
  }

  /**
   * Gets date time from command for editing events. Extracts datetime values
   * from edit commands based on keyword position.
   *
   * @param commands edit event command string
   * @param keyword  either "from" or "to" indicating which datetime to extract
   * @return LocalDateTime object parsed from the command
   */
  public static LocalDateTime getDateTimeFromCommandToEdit(String commands, String keyword) {
    int startIndex = commands.indexOf(keyword) + keyword.length();
    int endIndex;

    if (keyword.equals("from")) {
      endIndex = commands.indexOf(" to ");
    } else {
      endIndex = commands.indexOf(" with ");
    }

    String dateString = commands.substring(startIndex, endIndex).trim();
    return LocalDateTime.parse(dateString);
  }

  /**
   * Gets property value from the subject. Extracts the new value specified
   * after the "with" keyword in edit commands.
   *
   * @param command edit command containing " with &lt;value&gt;"
   * @return property value string with quotes removed
   */
  public static String getPropertyValue(String command) {
    return command.substring(command.indexOf(" with ") + " with ".length())
        .trim().replace("\"", "");
  }

  /**
   * Gets which type of property it is from command. Extracts the property name
   * being edited from the third token of the command.
   *
   * @param command edit command string
   * @return property name string
   */
  public static String getPropertyFromCommand(String command) {
    String[] tokens = command.split(" ");
    return tokens[2];
  }

  /**
   * Extracts the subject from the command using regex. Handles both quoted
   * and unquoted event subjects in create event commands.
   *
   * @param command create event command string
   * @return extracted subject string
   * @throws IllegalArgumentException if subject cannot be extracted
   */
  public static String extractSubject(String command) {
    Pattern quotedPattern = Pattern.compile("event\\s+\"([^\"]+)\"");
    Matcher quotedMatcher = quotedPattern.matcher(command);

    if (quotedMatcher.find()) {
      return quotedMatcher.group(1);
    }

    Pattern singleWordPattern = Pattern.compile("event\\s+(\\S+)\\s+(?:from|on)");
    Matcher singleWordMatcher = singleWordPattern.matcher(command);

    if (singleWordMatcher.find()) {
      return singleWordMatcher.group(1);
    }

    throw new IllegalArgumentException("Could not extract subject from command: " + command);
  }

  /**
   * Extracts start date from command. Uses regex to find and parse
   * the start datetime following the "from" keyword.
   *
   * @param command command string containing "from &lt;datetime&gt;"
   * @return LocalDateTime representing the start time
   * @throws IllegalArgumentException if start datetime cannot be extracted
   */
  public static LocalDateTime extractStartDateTime(String command) {
    Pattern pattern = Pattern.compile("from\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})");
    Matcher matcher = pattern.matcher(command);

    if (matcher.find()) {
      String dateTimeStr = matcher.group(1);
      return LocalDateTime.parse(dateTimeStr);
    }

    throw new IllegalArgumentException("Could not extract start datetime from command: " + command);
  }

  /**
   * Extracts end date from command. Uses regex to find and parse
   * the end datetime between "to" and "repeats" keywords.
   *
   * @param command command string containing "to &lt;datetime&gt; repeats"
   * @return LocalDateTime representing the end time
   * @throws IllegalArgumentException if end datetime cannot be extracted
   */
  public static LocalDateTime extractEndDateTime(String command) {
    Pattern pattern = Pattern.compile("to\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})\\s+repeats");
    Matcher matcher = pattern.matcher(command);

    if (matcher.find()) {
      String dateTimeStr = matcher.group(1);
      return LocalDateTime.parse(dateTimeStr);
    }

    throw new IllegalArgumentException("Could not extract end datetime from command: " + command);
  }

  /**
   * Extracts day of the event for all-day events. Parses the date
   * following the "on" keyword for all-day event commands.
   *
   * @param command command string containing "on &lt;date&gt;"
   * @return LocalDate representing the day of the event
   * @throws IllegalArgumentException if date cannot be extracted
   */
  public static LocalDate extractDayOfEvent(String command) {
    Pattern pattern = Pattern.compile("on\\s+(\\d{4}-\\d{2}-\\d{2})");
    Matcher matcher = pattern.matcher(command);

    if (matcher.find()) {
      String dateStr = matcher.group(1);
      return LocalDate.parse(dateStr);
    }

    throw new IllegalArgumentException("Could not extract day of event from command: " + command);
  }

  /**
   * Extracts days of the week when event repeats. Parses the weekday
   * characters following "repeats" keyword for recurring events.
   *
   * @param command command string containing "repeats &lt;weekdays&gt;"
   * @return List of DayOfWeek enums representing recurring days
   * @throws IllegalArgumentException if weekdays cannot be extracted
   */
  public static List<DayOfWeek> extractDaysOfWeekOfEvent(String command) {
    Pattern pattern = Pattern.compile("repeats\\s+([MTWRFSU]+)");
    Matcher matcher = pattern.matcher(command);

    if (matcher.find()) {
      String weekdaysStr = matcher.group(1);
      return parseWeekdays(weekdaysStr);
    }

    throw new IllegalArgumentException("Could not extract days of week from command: " + command);
  }

  /**
   * Helper method to parse days of week from string. Converts single-character
   * weekday codes to DayOfWeek enums (M=Monday, T=Tuesday, W=Wednesday,
   * R=Thursday, F=Friday, S=Saturday, U=Sunday).
   *
   * @param weekdaysStr string containing weekday characters (e.g., "MWF")
   * @return List of corresponding DayOfWeek enums
   * @throws IllegalArgumentException if invalid weekday character is encountered
   */
  public static List<DayOfWeek> parseWeekdays(String weekdaysStr) {
    List<DayOfWeek> days = new ArrayList<>();

    for (char c : weekdaysStr.toCharArray()) {
      switch (c) {
        case 'M':
          days.add(DayOfWeek.MONDAY);
          break;
        case 'T':
          days.add(DayOfWeek.TUESDAY);
          break;
        case 'W':
          days.add(DayOfWeek.WEDNESDAY);
          break;
        case 'R':
          days.add(DayOfWeek.THURSDAY);
          break;
        case 'F':
          days.add(DayOfWeek.FRIDAY);
          break;
        case 'S':
          days.add(DayOfWeek.SATURDAY);
          break;
        case 'U':
          days.add(DayOfWeek.SUNDAY);
          break;
        default:
          throw new IllegalArgumentException("Invalid weekday character: " + c);
      }
    }

    return days;
  }

  /**
   * Extracts number of times the event repeats from command. Parses
   * the repeat count from "for &lt;n&gt; times" pattern in recurring events.
   *
   * @param command command string containing "for &lt;number&gt; times"
   * @return integer representing number of repetitions
   * @throws IllegalArgumentException if repeat times cannot be extracted
   */
  public static int extractRepeatTimes(String command) {
    Pattern pattern = Pattern.compile("for\\s+(\\d+)\\s+times");
    Matcher matcher = pattern.matcher(command);

    if (matcher.find()) {
      String timesStr = matcher.group(1);
      return Integer.parseInt(timesStr);
    }

    throw new IllegalArgumentException("Could not extract repeat times from command: " + command);
  }

  /**
   * Extracts last day until the event should repeat. Parses the end date
   * for recurring events from "until &lt;date&gt;" pattern.
   *
   * @param command command string containing "until &lt;date&gt;"
   * @return LocalDate representing the last day of recurrence
   * @throws IllegalArgumentException if until date cannot be extracted
   */
  public static LocalDate extractTillLastEventDay(String command) {
    Pattern pattern = Pattern.compile("until\\s+(\\d{4}-\\d{2}-\\d{2})");
    Matcher matcher = pattern.matcher(command);

    if (matcher.find()) {
      String dateStr = matcher.group(1);
      return LocalDate.parse(dateStr);
    }

    throw new IllegalArgumentException("Could not extract until date from command: " + command);
  }

  /**
   * Helper method to edit a single event. Modifies the specified property
   * of an event with the new value provided.
   *
   * @param event    the Event object to be modified
   * @param property the property name to modify (subject, start,
   *                 end, description, status, Location)
   * @param newValue the new value to set for the property
   */
  public static void editSingleEvent(Event event, String property, String newValue) {
    String message = "new property value cannot be the same as old property value";
    switch (property) {
      case "subject":
        if (event.modifySubject(newValue)==null){
          System.out.println(message);


        } else {
          System.out.println("Edited Event:" + event.toString());
        }

        break;
      case "start":
        LocalDateTime dateTime = LocalDateTime.parse(newValue);
        if(event.modifyStartDate(dateTime.toLocalDate())==null){
          System.out.println(message);

        }

        if (event.modifyStartTime(dateTime.toLocalTime())==null){
          System.out.println(message);

        }
        else {
          System.out.println("Edited Event: " + event.toString());
        }
        break;

      case "end":
        LocalDateTime dateTime2 = LocalDateTime.parse(newValue);
        if(event.modifyEndDate(dateTime2.toLocalDate())==null){
          System.out.println(message);

        }
        if(event.modifyEndTime(dateTime2.toLocalTime())==null){
          System.out.println(message);

        }
        else {
          System.out.println("Edited Event " + event.toString());
        }


        break;

      case "description":
        if (event.modifyDescription(newValue)==null){
          System.out.println(message);

        }
        else {
          System.out.println("Edited Event " + event.toString());

        }

        break;
      case "status":
        if (newValue.equalsIgnoreCase("Public") || newValue.equalsIgnoreCase("Private")) {
          event.modifyStatus(newValue);
          System.out.println("Edited Event " + event.toString());
        } else {
          System.out.println("Status can only be either Public or Private");

        }

        break;

      case "location":
        if(newValue.equalsIgnoreCase("Physical") || newValue.equalsIgnoreCase("Online")) {
          event.modifyLocation(newValue);
          System.out.println("Edited Event " + event.toString());
        } else {
          System.out.println("Location can only be either Physical, Online");
        }

        break;

      default:
        System.out.println("Invalid property");
    }

  }

  /**
   * Helper method to edit a single event in a series. Modifies the specified property
   * of an event with the new value provided.
   *
   * @param event    the Event object to be modified
   * @param property the property name to modify (subject, start,
   *                 end, description, status, Location)
   * @param newValue the new value to set for the property
   */
  public static void editSingleEventInSeries(Event event, String property, String newValue) {
    switch (property) {
      case "subject":
        event.modifySubject(newValue);
        System.out.println("Edited Event:" + event.toString());

        break;
      case "start":
        LocalDateTime dateTime = LocalDateTime.parse(newValue);
        event.modifyStartTime(dateTime.toLocalTime());

        System.out.println("Edited Event: " + event.toString());

        break;
      case "end":
        LocalDateTime dateTime2 = LocalDateTime.parse(newValue);
        event.modifyEndTime(dateTime2.toLocalTime());

        System.out.println("Edited Event " + event.toString());
        break;

      case "description":
        event.modifyDescription(newValue);
        System.out.println("Edited Event " + event.toString());

        break;
      case "status":
        event.modifyStatus(newValue);
        System.out.println("Edited Event " + event.toString());

        break;

      case "location":
        event.modifyLocation(newValue);
        System.out.println("Edited Event " + event.toString());

        break;

      default:
        System.out.println("Invalid property");
    }

  }

  /**
   * Validates if the edit would create a duplicate event. Checks whether
   * modifying an event's property would result in a duplicate event in the calendar.
   *
   * @param originalEvent the event being edited
   * @param property      the property being modified
   * @param newValue      the new value for the property
   * @param calendar      the calendar containing all events
   * @return true if the edit would create a duplicate, false otherwise
   */
  public static boolean wouldCreateDuplicate(Event originalEvent, String property,
                                             String newValue, Calendar calendar) {
    String newSubject = originalEvent.getSubject();
    LocalDate newStartDate = originalEvent.getStartDate();
    LocalDate newEndDate = originalEvent.getEndDate();
    LocalTime newStartTime = originalEvent.getStartTime();
    LocalTime newEndTime = originalEvent.getEndTime();

    switch (property) {
      case "subject":
        newSubject = newValue;
        break;
      case "start":
        LocalDateTime startDateTime = LocalDateTime.parse(newValue);
        newStartDate = startDateTime.toLocalDate();
        newStartTime = startDateTime.toLocalTime();
        break;
      case "end":
        LocalDateTime endDateTime = LocalDateTime.parse(newValue);
        newEndDate = endDateTime.toLocalDate();
        newEndTime = endDateTime.toLocalTime();
        break;
      default:
        System.out.println("Unknown property: " + property);
        break;
    }

    for (Event existingEvent : calendar.getCalendarStore().values()) {
      if (existingEvent != originalEvent
          && existingEvent.getSubject().equals(newSubject)
          && existingEvent.getStartDate().equals(newStartDate)
          && existingEvent.getEndDate().equals(newEndDate)
          && existingEvent.getStartTime().equals(newStartTime)
          && existingEvent.getEndTime().equals(newEndTime)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Retrieves property name from command. Extracts the property to be edited
   * from edit series or edit events commands using regex.
   *
   * @param command edit series/events command string
   * @return property name (subject, start, end, description, location, or status)
   * @throws IllegalArgumentException if property cannot be extracted or is invalid
   */
  public static String extractProperty(String command) {
    Pattern pattern = null;
    if (command.contains("series")) {
      pattern = Pattern.compile("edit\\s+series\\s+(\\w+)\\s+");
    } else if (command.contains("events")) {
      pattern = Pattern.compile("edit\\s+events\\s+(\\w+)\\s+");
    }

    if (pattern == null) {
      throw new IllegalArgumentException("Could not extract property from command: " + command);
    }

    Matcher matcher = pattern.matcher(command);
    if (matcher.find()) {
      String property = matcher.group(1).toLowerCase();

      if (property.equals("subject") || property.equals("start")
          || property.equals("end") || property.equals("description")
          || property.equals("location") || property.equals("status")) {
        return property;
      } else {
        throw new IllegalArgumentException("Invalid property: " + property);
      }
    }

    throw new IllegalArgumentException("Could not extract property from command: " + command);
  }

  /**
   * Retrieves the subject from command. Extracts event subject from
   * edit series/events commands, handling both quoted and unquoted subjects.
   *
   * @param command edit series/events command string
   * @return event subject string
   * @throws IllegalArgumentException if subject cannot be extracted
   */
  public static String extractEventSubject(String command) {
    Pattern propertyPattern = null;
    if (command.contains("series")) {
      propertyPattern = Pattern.compile("edit\\s+series\\s+\\w+\\s+");
    } else if (command.contains("events")) {
      propertyPattern = Pattern.compile("edit\\s+events\\s+\\w+\\s+");
    }

    if (propertyPattern == null) {
      throw new IllegalArgumentException("Could not extract subject from command: " + command);
    }

    Matcher propertyMatcher = propertyPattern.matcher(command);

    if (!propertyMatcher.find()) {
      throw new IllegalArgumentException("Invalid command format: " + command);
    }

    int afterPropertyIndex = propertyMatcher.end();
    String remainingCommand = command.substring(afterPropertyIndex);

    Pattern quotedPattern = Pattern.compile("^\"([^\"]+)\"\\s+from");
    Matcher quotedMatcher = quotedPattern.matcher(remainingCommand);

    if (quotedMatcher.find()) {
      return quotedMatcher.group(1);
    }

    Pattern singleWordPattern = Pattern.compile("^(\\S+)\\s+from");
    Matcher singleWordMatcher = singleWordPattern.matcher(remainingCommand);

    if (singleWordMatcher.find()) {
      return singleWordMatcher.group(1);
    }

    throw new IllegalArgumentException("Could not extract event subject from command: " + command);
  }

  /**
   * Retrieves date time from command. Extracts datetime string
   * following the "from" keyword using regex.
   *
   * @param command command containing "from &lt;datetime&gt;"
   * @return datetime string in ISO format (yyyy-MM-ddTHH:mm)
   * @throws IllegalArgumentException if datetime cannot be extracted
   */
  public static String extractDateTimeString(String command) {
    Pattern pattern = Pattern.compile("from\\s+(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})");
    Matcher matcher = pattern.matcher(command);

    if (matcher.find()) {
      return matcher.group(1);
    }

    throw new IllegalArgumentException("Could not extract datetime from command: " + command);
  }

  /**
   * Extracts the new property value. Parses the new value for a property
   * from the "with" clause, handling different property types appropriately.
   *
   * @param command  command containing " with &lt;value&gt;"
   * @param property the property type being edited
   * @return extracted value string appropriate for the property type
   * @throws IllegalArgumentException if value cannot be extracted or is invalid for property type
   */
  public static String extractNewPropertyValue(String command, String property) {
    int withIndex = command.indexOf(" with ");

    if (withIndex == -1) {
      throw new IllegalArgumentException("Could not find 'with' clause in command: " + command);
    }

    String valueStr = command.substring(withIndex + 6).trim();

    switch (property) {
      case "start":
      case "end":
        Pattern dateTimePattern = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})");
        Matcher dateTimeMatcher = dateTimePattern.matcher(valueStr);
        if (dateTimeMatcher.find()) {
          return dateTimeMatcher.group(1);
        } else {
          throw new IllegalArgumentException("Invalid datetime format for "
              + property + ": " + valueStr);
        }

      case "subject":
      case "description":
      case "location":
      case "status":
        if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
          return valueStr.substring(1, valueStr.length() - 1);
        } else if (valueStr.startsWith("\"")) {
          int closingQuoteIndex = valueStr.indexOf("\"", 1);
          if (closingQuoteIndex != -1) {
            return valueStr.substring(1, closingQuoteIndex);
          }
        }
        return valueStr.split("\\s+")[0];

      default:
        throw new IllegalArgumentException("Unknown property type: " + property);
    }
  }

  /**
   * Extracts subject from copy event command. Parses the event subject
   * from a copy event command.
   *
   * @param command copy event command string
   * @return event subject string
   * @throws IllegalArgumentException if command format is invalid
   */
  public static String extractSubjectFromCopyEvent(String command) {
    String[] parts = command.split(" on ");
    if (parts.length < 2) {
      throw new IllegalArgumentException("Invalid copy event command format");
    }
    String eventPart = parts[0].replace("copy event", "").trim();
    return eventPart;
  }

  /**
   * Extracts start datetime from copy event command. Parses the source
   * event's datetime from a copy event command.
   *
   * @param command copy event command containing source datetime
   * @return LocalDateTime of the source event
   * @throws IllegalArgumentException if command format is invalid
   */
  public static LocalDateTime extractStartDateTimeFromCopyEvent(String command) {
    int onIndex = command.indexOf(" on ");
    int targetIndex = command.indexOf(" --target ");
    if (onIndex == -1 || targetIndex == -1) {
      throw new IllegalArgumentException("Invalid copy event command format");
    }
    String dateTimeStr = command.substring(onIndex + 4, targetIndex).trim();
    return LocalDateTime.parse(dateTimeStr);
  }

  /**
   * Extracts calendar name from copy event command. Parses the target
   * calendar name from a copy event command.
   *
   * @param command copy event command containing "--target &lt;calendar&gt;"
   * @return target calendar name string
   * @throws IllegalArgumentException if command format is invalid
   */
  public static String extractCalendarNameFromCopyEvent(String command) {
    int targetIndex = command.indexOf(" --target ");
    int toIndex = command.lastIndexOf(" to ");
    if (targetIndex == -1 || toIndex == -1) {
      throw new IllegalArgumentException("Invalid copy event command format");
    }
    String calendarName = command.substring(targetIndex + 10, toIndex).trim();
    return calendarName;
  }

  /**
   * Extracts target datetime from copy event command. Parses the target
   * datetime where the event should be copied to.
   *
   * @param command copy event command containing " to &lt;datetime&gt;"
   * @return LocalDateTime for the copied event
   * @throws IllegalArgumentException if command format is invalid
   */
  public static LocalDateTime extractTargetDateTimeFromCopyEvent(String command) {
    int toIndex = command.lastIndexOf(" to ");
    if (toIndex == -1) {
      throw new IllegalArgumentException("Invalid copy event command format");
    }
    String targetDateTimeStr = command.substring(toIndex + 4).trim();
    return LocalDateTime.parse(targetDateTimeStr);
  }

  /**
   * Extracts source date for copy events on command. Parses the source
   * date from which events should be copied.
   *
   * @param command copy events command containing " on &lt;date&gt;"
   * @return LocalDate of source events
   * @throws IllegalArgumentException if command format is invalid
   */
  public static LocalDate extractSourceDate(String command) {
    int onIndex = command.indexOf(" on ");
    int targetIndex = command.indexOf(" --target ");
    if (onIndex == -1 || targetIndex == -1) {
      throw new IllegalArgumentException("Invalid copy events command format");
    }
    String dateStr = command.substring(onIndex + 4, targetIndex).trim();
    return LocalDate.parse(dateStr);
  }

  /**
   * Extracts calendar name from copy events on command. Parses the target
   * calendar name for copying events on a specific date.
   *
   * @param command copy events command containing "--target &lt;calendar&gt;"
   * @return target calendar name string
   * @throws IllegalArgumentException if command format is invalid
   */
  public static String extractCalendarNameFromCopyEventsOn(String command) {
    int targetIndex = command.indexOf(" --target ");
    int toIndex = command.lastIndexOf(" to ");
    if (targetIndex == -1 || toIndex == -1) {
      throw new IllegalArgumentException("Invalid copy events command format");
    }
    String calendarName = command.substring(targetIndex + 10, toIndex).trim();
    return calendarName;
  }

  /**
   * Extracts target date for copy events on command. Parses the target
   * date where events should be copied to.
   *
   * @param command copy events command containing " to &lt;date&gt;"
   * @return LocalDate for the copied events
   * @throws IllegalArgumentException if command format is invalid
   */
  public static LocalDate extractTargetDateOn(String command) {
    int toIndex = command.lastIndexOf(" to ");
    if (toIndex == -1) {
      throw new IllegalArgumentException("Invalid copy events command format");
    }
    String targetDateStr = command.substring(toIndex + 4).trim();
    return LocalDate.parse(targetDateStr);
  }

  /**
   * Extracts source start date for copy events between command. Parses
   * the start date of the range from which events should be copied.
   *
   * @param command copy events command containing " between &lt;date&gt;"
   * @return LocalDate marking start of source range
   * @throws IllegalArgumentException if command format is invalid
   */
  public static LocalDate extractSourceStartDate(String command) {
    int betweenIndex = command.indexOf(" between ");
    int andIndex = command.indexOf(" and ");
    if (betweenIndex == -1 || andIndex == -1) {
      throw new IllegalArgumentException("Invalid copy events between command format");
    }
    String startDateStr = command.substring(betweenIndex + 9, andIndex).trim();
    return LocalDate.parse(startDateStr);
  }

  /**
   * Extracts source end date for copy events between command. Parses
   * the end date of the range from which events should be copied.
   *
   * @param command copy events command containing " and &lt;date&gt;"
   * @return LocalDate marking end of source range
   * @throws IllegalArgumentException if command format is invalid
   */
  public static LocalDate extractSourceEndDate(String command) {
    int andIndex = command.indexOf(" and ");
    int targetIndex = command.indexOf(" --target ");
    if (andIndex == -1 || targetIndex == -1) {
      throw new IllegalArgumentException("Invalid copy events between command format");
    }
    String endDateStr = command.substring(andIndex + 5, targetIndex).trim();
    return LocalDate.parse(endDateStr);
  }

  /**
   * Extracts calendar name from copy events between command. Parses the
   * target calendar name for copying events in a date range.
   *
   * @param command copy events command containing "--target &lt;calendar&gt;"
   * @return target calendar name string
   * @throws IllegalArgumentException if command format is invalid
   */
  public static String extractCalendarNameFromCopyEventsBetween(String command) {
    int targetIndex = command.indexOf(" --target ");
    int toIndex = command.lastIndexOf(" to ");
    if (targetIndex == -1 || toIndex == -1) {
      throw new IllegalArgumentException("Invalid copy events between command format");
    }
    String calendarName = command.substring(targetIndex + 10, toIndex).trim();
    return calendarName;
  }

  /**
   * Extracts target date for copy events between command. Parses the
   * target date where events from a date range should be copied to.
   *
   * @param command copy events command containing " to &lt;date&gt;"
   * @return LocalDate for the copied events
   * @throws IllegalArgumentException if command format is invalid
   */
  public static LocalDate extractTargetDateBetween(String command) {
    int toIndex = command.lastIndexOf(" to ");
    if (toIndex == -1) {
      throw new IllegalArgumentException("Invalid copy events between command format");
    }
    String targetDateStr = command.substring(toIndex + 4).trim();
    return LocalDate.parse(targetDateStr);
  }
}