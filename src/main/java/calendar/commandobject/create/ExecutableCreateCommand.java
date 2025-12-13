package calendar.commandobject.create;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.CalendarManager;


/**
 * The class calls whichever create command object based on the command.
 *
 */

public class ExecutableCreateCommand implements Command {
  private final String command;
  private final CalendarManager calendarManager;


  /**
   * Constructs the ExecutableCreateCommand object.
   *
   * @param command         command in string format.
   * @param calendarManager calendarManager object.
   */

  public ExecutableCreateCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {

    if (command.contains(" from ")) {
      if (command.contains(" to ") && !command.contains(" repeats ")) {
        Command createEventCmd = new CreateEventCommand(command, calendarManager);
        createEventCmd.execute();
      } else {
        Command createSeriesCmd = new CreateSeriesCommand(command, calendarManager);
        createSeriesCmd.execute();
      }
    } else if (command.contains(" on ")) {
      if (!command.contains(" repeats ")) {
        Command createAllDayEvent = new CreateEventCommand(command, calendarManager);
        createAllDayEvent.execute();
      } else {
        Command createSingleDaySeries = new CreateSeriesCommand(command, calendarManager);
        createSingleDaySeries.execute();
      }

    } else {

      Command createCalendarCmd = new CreateCalendarCommand(command, calendarManager);
      createCalendarCmd.execute();

    }

  }
}
