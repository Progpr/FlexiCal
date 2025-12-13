package calendar.commandobject.copy;

import static calendar.commandobject.ExtractCommandHelper.extractCalendarNameFromCopyEvent;
import static calendar.commandobject.ExtractCommandHelper.extractStartDateTimeFromCopyEvent;
import static calendar.commandobject.ExtractCommandHelper.extractSubjectFromCopyEvent;
import static calendar.commandobject.ExtractCommandHelper.extractTargetDateTimeFromCopyEvent;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import java.time.LocalDateTime;

/**
 * class to handle the copying of singular event.
 * from one calendar to another.
 *
 */

public class CopyEventCommand implements Command {

  private final String command;
  private final CalendarManager calendarManager;


  /**
   * Constructor to create edit event command object.
   *
   * @param command         user command
   * @param calendarManager calendar manager object
   */

  public CopyEventCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {

    String subject = extractSubjectFromCopyEvent(command);
    LocalDateTime dateTime = extractStartDateTimeFromCopyEvent(command);
    String calendarName = extractCalendarNameFromCopyEvent(command);
    LocalDateTime targetDateTime = extractTargetDateTimeFromCopyEvent(command);

    Calendar currentCalendar =
        calendarManager.getCalendar(calendarManager.getCurrentCalendarName());

    try {
      Event eventToCopy = getEvent(subject, dateTime, currentCalendar);
      calendar.model.modelinterfaces.Calendar targetCalendar =
          calendarManager.getCalendar(calendarName);

      if (!(currentCalendar.copyEventSingle(eventToCopy, targetCalendar, targetDateTime) == null)) {
        System.out.println("Event has been copied successfully!");
      }

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Searches and gets the event from calendar with provided subject and start date time.
   *
   * @param subject         search term subject
   * @param dateTime        search term date time
   * @param currentCalendar current calendar to search
   * @return Searched event
   * @throws IllegalArgumentException When the event is not present in the calendar
   */

  private Event getEvent(String subject, LocalDateTime dateTime,
                         calendar.model.modelinterfaces.Calendar currentCalendar) throws
      IllegalArgumentException {
    for (Event event : currentCalendar.getCalendarStore().values()) {
      if (event.getSubject().equals(subject)
          && event.getStartDate().equals(dateTime.toLocalDate())
          && event.getStartTime().equals(dateTime.toLocalTime())) {
        return event;
      }
    }
    throw new IllegalArgumentException("Event not found: " + subject + " at " + dateTime);
  }
}
