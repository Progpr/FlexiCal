package calendar.commandobject.showcommand;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Command object to handle print events command.
 *
 */

public class PrintEventsCommand implements Command {
  private String command;
  private CalendarManager calendarManager;

  /**
   * Constructs print event command object.
   *
   * @param command         user input
   * @param calendarManager calendar manager object
   *
   */
  public PrintEventsCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  /**
   * Display's the event.
   *
   */

  private void displayEvents(List<Event> eventList) {
    if (eventList.isEmpty()) {
      System.out.println("No events found.");
    } else {
      for (Event event : eventList) {
        System.out.println("• " + event.toString());
      }
    }
  }

  @Override
  public void execute() {
    Calendar currentCalendar =
        calendarManager.getCalendar(calendarManager.getCurrentCalendarName());
    if (command.contains(" on ")) {
      String dateString = command.substring(command.indexOf(" on ") + " on ".length()).trim();
      LocalDate localDate = LocalDate.parse(dateString);

      List<Event> eventList = currentCalendar.getEventsForDate(localDate);
      displayEvents(eventList);

    } else if (command.contains(" from ") && command.contains(" to ")) {
      String fromPart = command.substring(command.indexOf(" from ") + " from ".length(),
          command.indexOf(" to ")).trim();
      String toPart = command.substring(command.indexOf(" to ") + " to ".length()).trim();

      LocalDateTime startDateTime = LocalDateTime.parse(fromPart);
      LocalDateTime endDateTime = LocalDateTime.parse(toPart);

      List<Event> eventList = currentCalendar.getSchedule(startDateTime.toString(),
          endDateTime.toString());
      displayEvents(eventList);
    }
  }
}