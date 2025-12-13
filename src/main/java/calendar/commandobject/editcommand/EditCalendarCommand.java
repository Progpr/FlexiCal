package calendar.commandobject.editcommand;

import static calendar.commandobject.ExtractCommandHelper.getCalendarNameFromCommand;
import static calendar.commandobject.ExtractCommandHelper.getPropertyFromEditCalendarCommand;
import static calendar.commandobject.ExtractCommandHelper.getPropertyValueFromEditCalendarCommand;

import calendar.commandobject.Command;
import calendar.model.modelimplementations.CalendarImpl;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import java.time.ZoneId;
import java.util.Objects;

/**
 * Class to execute Edit calendar command.
 * Class implements command interface
 *
 */

public class EditCalendarCommand implements Command {

  private final String command;
  private final CalendarManager calendarManager;

  /**
   * Constructor to construct edit calendar command object using:.
   *
   * @param command         user input
   * @param calendarManager calendar manager object
   */

  public EditCalendarCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {

    String property = getPropertyFromEditCalendarCommand(command);
    String newTimezone = getPropertyValueFromEditCalendarCommand(command);
    String newName = getPropertyValueFromEditCalendarCommand(command);
    String name = getCalendarNameFromCommand(command);

    Calendar calendar = calendarManager.getCalendar(name);
    if (calendar == null) {
      System.out.println("Calendar not found");

    } else {

      switch (property) {
        case "name":
          String newCalendarName = calendar.modifyName(newName).toString();
          Calendar entry = new CalendarImpl(calendar);
          calendarManager.getCalendarManagerStore().remove(name);
          calendarManager.getCalendarManagerStore().put(newName, entry);
          System.out.println("Edited Calendar: "
              + newCalendarName);
          break;
        case "timezone":
          System.out.println("Edited Calendar: "
              + calendar.modifyTimezone(ZoneId.of(newTimezone)));
          break;

        default:
          System.out.println("Invalid property");
          break;
      }
    }
  }
}
