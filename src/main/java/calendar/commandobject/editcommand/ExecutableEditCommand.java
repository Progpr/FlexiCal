package calendar.commandobject.editcommand;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;

/**
 * The class calls whichever edit command object based on the command.
 *
 */

public class ExecutableEditCommand implements Command {

  private String command;
  private CalendarManager calendarManager;

  /**
   * Constructs ExecutableEditCommand object.
   *
   * @param command         command in string format.
   * @param calendarManager calendarManager object.
   */

  public ExecutableEditCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {

    Calendar calendar = calendarManager.getCalendar(calendarManager.getCurrentCalendarName());

    if (calendar == null) {
      System.out.println("Calendar Doesn't Exist");
      return;
    }

    String input = command.split(" ")[1];

    switch (input) {
      case "event":
        Command editSingleEventCmd = new EditEventCommand(command, calendarManager);
        editSingleEventCmd.execute();
        break;

      case "events":
        Command editMultipleEventsCmd = new EditEventsCommand(command, calendarManager);
        editMultipleEventsCmd.execute();
        break;

      case "series":
        Command editSeriesCmd = new EditSeriesCommand(command, calendarManager);
        editSeriesCmd.execute();
        break;

      case "calendar":
        Command editCalendarCmd = new EditCalendarCommand(command, calendarManager);
        editCalendarCmd.execute();
        break;

      default:
        System.out.println("Error in " + command + ": Command not found");
        break;
    }
  }
}
