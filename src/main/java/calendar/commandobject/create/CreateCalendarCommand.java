package calendar.commandobject.create;

import static calendar.commandobject.ExtractCommandHelper.getCalendarNameFromCommand;
import static calendar.commandobject.ExtractCommandHelper.getTimezoneFromCalendarCommand;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;

/**
 * Class to execute create calendar command.
 * Class implements Command interface
 */

public class CreateCalendarCommand implements Command {
  private String command;
  private CalendarManager calendarManager;

  /**
   * Constructor to construct command object using:.
   *
   * @param command         user input
   * @param calendarManager calendar manager object
   */

  public CreateCalendarCommand(String command, CalendarManager calendarManager) {
    if (calendarManager == null || command == null) {}
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {

    String name = getCalendarNameFromCommand(command);
    String timezone = getTimezoneFromCalendarCommand(command);

    if (timezone==null){
      System.out.println("Invalid Command");
      return;

    }

    String pattern = "^[A-Z][a-zA-Z]+([-_/][A-Z]?[a-zA-Z]+)*$";

    if (!timezone.matches(pattern)) {
      System.out.println("Time zone must be in 'area/location'");
      return;
    }
    else{

      try {

        Calendar calendar = calendarManager.createCalendar(name, timezone);

        calendarManager.saveCalendar(calendar);
        System.out.println("Successfully saved calendar: " + calendar.toString());

      } catch (Exception e) {
        System.out.println(e.getMessage());
        return;
      }

    }
  }
}
