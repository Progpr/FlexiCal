package calendar.commandobject.create;

import static calendar.commandobject.ExtractCommandHelper.extractDayOfEvent;
import static calendar.commandobject.ExtractCommandHelper.extractDaysOfWeekOfEvent;
import static calendar.commandobject.ExtractCommandHelper.extractEndDateTime;
import static calendar.commandobject.ExtractCommandHelper.extractRepeatTimes;
import static calendar.commandobject.ExtractCommandHelper.extractStartDateTime;
import static calendar.commandobject.ExtractCommandHelper.extractSubject;
import static calendar.commandobject.ExtractCommandHelper.extractTillLastEventDay;
import static calendar.utility.GenerateSeriesId.generateSeriesId;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Command object which processes the create series command.
 * It calls the create series functionalities from model.
 *
 */
public class CreateSeriesCommand implements Command {
  private static final int DEFAULT_START_HOUR = 8;
  private static final int DEFAULT_END_HOUR = 17;
  private static final int DEFAULT_MINUTES = 0;

  private CalendarManager calendarManager;
  String command;

  /**
   * constructor to create series command object.
   *
   * @param command         command in string format.
   * @param calendarManager calendarManager object.
   */
  public CreateSeriesCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {
    Calendar calendar = calendarManager.getCalendar(calendarManager.getCurrentCalendarName());

    try {
      if (command.contains("times")) {
        handleRepeatTimesCommand(calendar);
      } else if (command.contains("until")) {
        handleRepeatUntilCommand(calendar);
      } else {
        System.out.println("Invalid command. Could not create event series.");
      }
    } catch (Exception e) {
      System.out.println("Error creating event series: " + e.getMessage());
    }
  }

  /**
   * Handles the create series command when creating series with number of occurrences.
   *
   * @param calendar calendar in which we create series
   */

  private void handleRepeatTimesCommand(Calendar calendar) {
    String subject = extractSubject(command);
    List<DayOfWeek> daysOfWeekOfEvent = extractDaysOfWeekOfEvent(command);
    int repeatTimes = extractRepeatTimes(command);

    DateTimePair dateTimePair = extractDateTimePair();

    createAndSaveSeries(calendar, subject, dateTimePair.startDateTime,
        dateTimePair.endDateTime, daysOfWeekOfEvent,
        repeatTimes, null);
  }

  /**
   * Handles the create series command when creating series till a particular day.
   *
   * @param calendar calendar in which we create series
   */

  private void handleRepeatUntilCommand(Calendar calendar) {
    String subject = extractSubject(command);
    List<DayOfWeek> daysOfWeekOfEvent = extractDaysOfWeekOfEvent(command);
    LocalDate tillDay = extractTillLastEventDay(command);

    DateTimePair dateTimePair = extractDateTimePair();

    createAndSaveSeries(calendar, subject, dateTimePair.startDateTime,
        dateTimePair.endDateTime, daysOfWeekOfEvent,
        0, tillDay);
  }

  /**
   * extracts the date start date time and end date time in date time pair,
   * based on if it is an all day event or has start time and end time.
   *
   * @return Date time pair of start date time and end date time
   */

  private DateTimePair extractDateTimePair() {
    LocalDateTime startDateTime;
    LocalDateTime endDateTime;

    if (command.contains("on")) {
      LocalDate eventDate = extractDayOfEvent(command);
      startDateTime = createDefaultStartDateTime(eventDate);
      endDateTime = createDefaultEndDateTime(eventDate);
    } else if (command.contains("to")) {
      startDateTime = extractStartDateTime(command);
      endDateTime = extractEndDateTime(command);
      validateSameDayEvent(startDateTime, endDateTime);
    } else {
      throw new IllegalArgumentException("Command must contain 'on' "
          + "or 'to' for time specification");
    }

    return new DateTimePair(startDateTime, endDateTime);
  }

  /**
   * create default start time for start date.
   *
   * @param eventDate the start date
   * @return default date time for start date
   */

  private LocalDateTime createDefaultStartDateTime(LocalDate eventDate) {
    return eventDate.atTime(DEFAULT_START_HOUR, DEFAULT_MINUTES);
  }

  /**
   * create default end time for end date.
   *
   * @param eventDate the end date
   * @return default date time for end date
   */

  private LocalDateTime createDefaultEndDateTime(LocalDate eventDate) {
    return eventDate.atTime(DEFAULT_END_HOUR, DEFAULT_MINUTES);
  }

  /**
   * Validates the start date and end date of the event is on same day.
   * Because the events in series can not be of multiple days.
   *
   * @param startDateTime start date time of the event
   * @param endDateTime end date time of the event
   */

  private void validateSameDayEvent(LocalDateTime startDateTime, LocalDateTime endDateTime) {
    if (!startDateTime.toLocalDate().equals(endDateTime.toLocalDate())) {
      throw new IllegalArgumentException("Events in a series must start and end on the same day");
    }
  }

  /**
   * Helper method which creates and saves the series.
   *
   * @param calendar calendar in which the series is to be saved
   * @param subject subject of the series events
   * @param startDateTime start date time of events
   * @param endDateTime end date time of events
   * @param daysOfWeekOfEvent Days of week on which the event will recur
   * @param repeatTimes number of occurrences of recurring series
   * @param tillDay day till which the series should recur
   */

  private void createAndSaveSeries(Calendar calendar, String subject,
                                   LocalDateTime startDateTime, LocalDateTime endDateTime,
                                   List<DayOfWeek> daysOfWeekOfEvent,
                                   int repeatTimes, LocalDate tillDay) {
    String seriesId = generateSeriesId();

    Event firstOccOfEvent = calendar.createEvent(subject, startDateTime, endDateTime,
        seriesId, null, null, null);
    calendar.saveEvent(firstOccOfEvent);
    calendar.createSeries(firstOccOfEvent, repeatTimes, daysOfWeekOfEvent, tillDay);
    System.out.println("Event Series created.");
  }

  /**
   * static class whose object stores start date time and end date time as a pair.
   */

  private static class DateTimePair {
    final LocalDateTime startDateTime;
    final LocalDateTime endDateTime;

    /**
     * Constructs the date time pair object.
     *
     * @param startDateTime Start date time
     * @param endDateTime End date time
     */

    DateTimePair(LocalDateTime startDateTime, LocalDateTime endDateTime) {
      this.startDateTime = startDateTime;
      this.endDateTime = endDateTime;
    }
  }
}