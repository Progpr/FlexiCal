package calendar.commandobject.copy;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.CalendarManager;

/**
 * Class to execute the edit command based on.
 * whether it is single event or multiple events
 *
 */

public class ExecutableCopyCommand implements Command {
  private String command;
  private CalendarManager calendarManager;

  /**
   * constructor to build theExecutableCopyCommand class.
   *
   * @param command         user command
   * @param calendarManager calendar manager object
   */

  public ExecutableCopyCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {
    if (command.contains(" event ")) {
      CopyEventCommand copyEventCommand = new CopyEventCommand(command, calendarManager);
      copyEventCommand.execute();

    } else if (command.contains(" events ")) {
      CopyEventsCommand copyEventsCommand = new CopyEventsCommand(command, calendarManager);
      copyEventsCommand.execute();

    } else {
      System.out.println("Invalid command");
    }
  }
}
