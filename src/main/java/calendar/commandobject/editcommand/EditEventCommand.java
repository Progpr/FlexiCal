package calendar.commandobject.editcommand;

import static calendar.commandobject.ExtractCommandHelper.editSingleEvent;
import static calendar.commandobject.ExtractCommandHelper.getDateTimeFromCommandToEdit;
import static calendar.commandobject.ExtractCommandHelper.getPropertyFromCommand;
import static calendar.commandobject.ExtractCommandHelper.getPropertyValue;
import static calendar.commandobject.ExtractCommandHelper.getSubjectFromCommandToEdit;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Command object for processing edit of a single event.
 */
public class EditEventCommand implements Command {
  private final CalendarManager calendarManager;
  private final String command;


  /**
   * Constructor to create edit event command object.
   *
   * @param command         user input
   * @param calendarManager calendar manager object
   */

  public EditEventCommand(String command, CalendarManager calendarManager) {
    this.calendarManager = calendarManager;
    this.command = command;
  }

  @Override
  public void execute() {
    String subject = getSubjectFromCommandToEdit(command);
    String property = getPropertyFromCommand(command);
    String newPropertyValue = getPropertyValue(command);
    LocalDateTime startDateTime = getDateTimeFromCommandToEdit(command, "from");
    LocalDateTime endDateTime = getDateTimeFromCommandToEdit(command, "to");

    LocalDate startDate = startDateTime.toLocalDate();
    LocalDate endDate = endDateTime.toLocalDate();
    LocalTime startTime = startDateTime.toLocalTime();
    LocalTime endTime = endDateTime.toLocalTime();

    Calendar calendar = calendarManager.getCalendar(
        calendarManager.getCurrentCalendarName());

    if (calendar == null) {
      System.out.println("Calendar Doesn't Exist");
    }

    Event event = calendar.getEvent(subject, startDate, endDate, startTime, endTime);
    if (event == null) {
      System.out.println("Event not found");
      return;
    }

    Event oldSnapshot = calendar.createEvent(
        event.getSubject(),
        LocalDateTime.of(event.getStartDate(), event.getStartTime()),
        LocalDateTime.of(event.getEndDate(), event.getEndTime()),
        event.getEventSeriesId(),
        null,
        null,
        null
    );

    editSingleEvent(event, property, newPropertyValue);

    calendar.updateEventKey(oldSnapshot, event);
  }
}
