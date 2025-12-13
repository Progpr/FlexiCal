package calendar.commandobject.create;

import static calendar.commandobject.ExtractCommandHelper.getCalendarNameFromCommand;
import static calendar.commandobject.ExtractCommandHelper.getDateFromCommand;
import static calendar.commandobject.ExtractCommandHelper.getDateTimeFromCommand;
import static calendar.commandobject.ExtractCommandHelper.getSubjectFromCommand;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Command object to execute create single event operation.
 *
 */
public class CreateEventCommand implements Command {
  private String command;
  private CalendarManager calendarManager;

  /**
   * Constructs command object.
   *
   * @param calendarManager calendar manager object
   * @param command         user input
   *
   */

  public CreateEventCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  /**
   * Checks if it is an all day event.
   *
   */
  private boolean isAllDayEvent() {
    return command.contains(" on ") && !command.contains(" from ");
  }

  /**
   * Execute method which executes the operation by calling model method.
   *
   */
  @Override
  public void execute() {
    Calendar currentCalendar =
        calendarManager.getCalendar(calendarManager.getCurrentCalendarName());
    String subject = getSubjectFromCommand(command);

    try {

      if (isAllDayEvent()) {

        LocalDate eventDate = getDateFromCommand(command);

        LocalDateTime startDateTime = LocalDateTime.of(eventDate, LocalTime.of(8, 0));
        LocalDateTime endDateTime = LocalDateTime.of(eventDate, LocalTime.of(17, 0));

        Event event =
            currentCalendar.createEvent(subject, startDateTime, endDateTime,
                null, null, null, null
            );
        currentCalendar.saveEvent(event);
        System.out.println("Created event: " + event);
      } else {

        LocalDateTime startDateTime = getDateTimeFromCommand(command, "from");
        LocalDateTime endDateTime = getDateTimeFromCommand(command, "to");

        Event event =
            currentCalendar.createEvent(subject, startDateTime, endDateTime, null, null, null, null);
        currentCalendar.saveEvent(event);

      }
    } catch (Exception e) {
      System.out.println("Invalid command: " + command);
    }
  }
}