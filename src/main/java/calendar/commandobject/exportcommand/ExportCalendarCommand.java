package calendar.commandobject.exportcommand;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelutility.ExportCalendar;

/**
 * Command object to handle export calendar command.
 * It calls the ExportCalendar Utility class
 *
 */
public class ExportCalendarCommand implements Command {
  private CalendarManager calendarManager;
  private String command;

  /**
   * Construct export calendar command object.
   *
   * @param command         user input
   * @param calendarManager calendar Manager object
   */

  public ExportCalendarCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {

    Calendar currentCalendar =
        calendarManager.getCalendar(calendarManager.getCurrentCalendarName());
    String filename =
        command.substring(command.indexOf("cal") + "cal".length()).trim();

    ExportCalendar export = new ExportCalendar(filename, currentCalendar);

    export.exportCalendar();

  }

}
