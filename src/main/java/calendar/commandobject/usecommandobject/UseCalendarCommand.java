package calendar.commandobject.usecommandobject;

import static calendar.commandobject.ExtractCommandHelper.getCalendarNameFromCommand;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;

/**
 * Class to carry out use calendar command operation.
 * It sets the current calendar to an existing calendar given by user
 */

public class UseCalendarCommand implements Command {
  private final CalendarManager calendarManager;
  private final String command;

  /**
   * Constructor to construct the usecalendarcommand class.
   *
   * @param command         user input
   * @param calendarManager calendarManagerObject
   */

  public UseCalendarCommand(String command, CalendarManager calendarManager) {
    this.calendarManager = calendarManager;
    this.command = command;
  }

  @Override
  public void execute() {

    String name = getCalendarNameFromCommand(command);
    Calendar calendar = calendarManager.getCalendar(name);

    if (calendar == null) {
      System.out.println("Error in command: " + command + " No calendar found with this name");
      System.out.println("You can create a new calendar" + System.lineSeparator());
    } else {
      this.calendarManager.setCurrentCalendarNameAs(calendar.getCalendarName());
      System.out.println("Using calendar: " + calendar);
    }

  }
}
