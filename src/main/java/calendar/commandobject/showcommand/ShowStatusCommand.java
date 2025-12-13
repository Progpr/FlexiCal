package calendar.commandobject.showcommand;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;

/**
 * Command object handles show status command.
 *
 */

public class ShowStatusCommand implements Command {
  private String command;
  private CalendarManager calendarManager;

  /**
   * Constructs show status command object.
   *
   * @param command         user input
   * @param calendarManager calendar manager object
   *
   */

  public ShowStatusCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {

    Calendar currentCalendar =
        calendarManager.getCalendar(calendarManager.getCurrentCalendarName());
    String dateTimeString =
        command.substring(command.indexOf(" on ") + " on ".length());
    String status = currentCalendar.showStatus(dateTimeString);
    System.out.println(status);
  }

}
