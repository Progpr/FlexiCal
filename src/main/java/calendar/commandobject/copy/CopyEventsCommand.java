package calendar.commandobject.copy;

import static calendar.commandobject.ExtractCommandHelper.extractCalendarNameFromCopyEventsBetween;
import static calendar.commandobject.ExtractCommandHelper.extractCalendarNameFromCopyEventsOn;
import static calendar.commandobject.ExtractCommandHelper.extractSourceDate;
import static calendar.commandobject.ExtractCommandHelper.extractSourceEndDate;
import static calendar.commandobject.ExtractCommandHelper.extractSourceStartDate;
import static calendar.commandobject.ExtractCommandHelper.extractTargetDateBetween;
import static calendar.commandobject.ExtractCommandHelper.extractTargetDateOn;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import java.time.LocalDate;

/**
 * Class to copy multiple events.
 * from one calendar to another
 */

public class CopyEventsCommand implements Command {
  private final String command;
  private final CalendarManager calendarManager;

  /**
   * Constructor to create edit event command object.
   *
   * @param command         user command
   * @param calendarManager calendar manager object
   */

  public CopyEventsCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {
    Calendar currentCalendar =
        calendarManager.getCalendar(calendarManager.getCurrentCalendarName());

    if (command.contains("on")) {
      LocalDate sourceDate = extractSourceDate(command);
      String calendarName = extractCalendarNameFromCopyEventsOn(command);
      LocalDate targetDate = extractTargetDateOn(command);

      Calendar targetCalendar = calendarManager.getCalendar(calendarName);

      currentCalendar.copyEventsOnDate(sourceDate, targetCalendar, targetDate);

    } else if (command.contains("between")) {
      LocalDate sourceStartDate = extractSourceStartDate(command);
      LocalDate sourceEndDate = extractSourceEndDate(command);
      String calendarName = extractCalendarNameFromCopyEventsBetween(command);
      LocalDate targetDate = extractTargetDateBetween(command);

      Calendar targetCalendar = calendarManager.getCalendar(calendarName);

      currentCalendar.copyEventsBetween(sourceStartDate, sourceEndDate, targetCalendar, targetDate);

    }
  }
}
