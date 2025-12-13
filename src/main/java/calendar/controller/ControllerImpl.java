package calendar.controller;

import static calendar.commandobject.ExtractCommandHelper.getCalendarNameFromCommand;

import calendar.commandobject.Command;
import calendar.commandobject.copy.ExecutableCopyCommand;
import calendar.commandobject.create.ExecutableCreateCommand;
import calendar.commandobject.editcommand.ExecutableEditCommand;
import calendar.commandobject.exportcommand.ExportCalendarCommand;
import calendar.commandobject.showcommand.PrintEventsCommand;
import calendar.commandobject.showcommand.ShowStatusCommand;
import calendar.commandobject.usecommandobject.UseCalendarCommand;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.view.ViewInterface;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiFunction;


/**
 * Controller class implementation.
 * Controller processes the commands and calls respective method for execution.
 *
 */

public class ControllerImpl implements CalendarController {

  CalendarManager calendarManager;
  Readable in;
  Appendable out;
  String mode;
  String commandFile;
  public Calendar calendar = null;
  public Map<String, BiFunction<String, CalendarManager, Command>> knownCommands;



  /**
   * Instantiates a controller object.
   *
   * @param calendarManager calendar manager object
   * @param in              readable object (user input)
   * @param out             appendable object (output)
   * @param mode            headless or interactive
   * @param commandFile     txt file of commands for headless
   * @throws IllegalArgumentException if either of the parameters are null
   */

  public ControllerImpl(CalendarManager calendarManager, Readable in,
                        Appendable out, String mode, String commandFile)
      throws IllegalArgumentException {
    if (calendarManager == null || in == null || out == null) {
      throw new IllegalArgumentException("Calendar or Readable or Appendable cannot be null");
    }
    this.calendarManager = calendarManager;
    this.in = in;
    this.out = out;
    this.mode = mode;
    this.commandFile = commandFile;
    this.knownCommands = new HashMap<>();
  }


  @Override
  public void go() {
    initializeCommands();
    if (mode.equals("interactive")) {
      printWelcomeMessage();
      printRules();

      Scanner scanner = new Scanner(in);
      askForInstruction();
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.equals("exit")) {
          break;
        }
        processCommands(line);
      }
    } else if (mode.equals("headless")) {

      try (BufferedReader fileReader = new BufferedReader(new FileReader(commandFile))) {
        String line;
        while ((line = fileReader.readLine()) != null) {
          if (line.equals("exit")) {
            break;
          }
          processCommands(line);
        }
      } catch (IOException e) {
        System.out.println("Error reading file: " + e.getMessage());
      }
    }

    printFarewellMessage();
  }


  @Override
  public void processCommands(String command) {

    String commandType = command.split(" ")[0].trim();

    BiFunction<String, CalendarManager, Command> commandOb = knownCommands.get(commandType);

    if (commandOb == null) {
      printErrorMessage(command, "Invalid command");
      return;
    }

    if (commandType.equals("create") || commandType.equals("use")) {
      Command c = commandOb.apply(command, calendarManager);
      c.execute();

      if (commandType.equals("use")) {
        String calendarName = getCalendarNameFromCommand(command);
        Calendar selectedCalendar = calendarManager.getCalendar(calendarName);
        if (selectedCalendar != null) {
          this.calendar = selectedCalendar;
        }
      }
      return;
    }

    if (this.calendar == null) {
      printErrorMessage(command, "No calendar is "
          +
          "currently selected. Please follow these steps:");
      writeMessage(
          "1. Create a calendar "
              +
              System.lineSeparator());
      writeMessage("2. Select it using"
          +
          System.lineSeparator());
      writeMessage("3. Then you can run other commands"
          +
          System.lineSeparator());

      return;
    }


    Command c = commandOb.apply(command, calendarManager);
    c.execute();
  }


  @Override
  public void writeMessage(String message) {
    try {

      out.append(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void printWelcomeMessage() {
    writeMessage("Welcome to the Edith calendar!" + System.lineSeparator());
    printRules();

  }

  @Override
  public void printRules() {

    writeMessage("Calendar Application Rules" + System.lineSeparator());
    writeMessage(System.lineSeparator());

    writeMessage("Event Requirements:" + System.lineSeparator());
    writeMessage("Every event MUST have: subject, start date and time"
        + System.lineSeparator());
    writeMessage("Optional properties: description, end date/time, location, "
        + "status (public/private)"
        + System.lineSeparator());
    writeMessage("NO two events can have identical: subject + start date/time + end "
        + "date/time"
        + System.lineSeparator());
    writeMessage(System.lineSeparator());

    writeMessage("Single Events:" + System.lineSeparator());
    writeMessage("Events without end date/time are \"All Day Events\" (8am - 5pm)"
        + System.lineSeparator());
    writeMessage("Single events CAN span multiple days" + System.lineSeparator());
    writeMessage(System.lineSeparator());

    writeMessage("Event Series (Recurring Events):" + System.lineSeparator());
    writeMessage("Must specify days of week to repeat (M/T/W/R/F/S/U)"
        + System.lineSeparator());
    writeMessage("Must specify EITHER: number of occurrences OR end date"
        + System.lineSeparator());
    writeMessage("ALL events in a series must have the SAME start time"
        + System.lineSeparator());
    writeMessage("Each occurrence can ONLY span one day (start and end same day)"
        + System.lineSeparator());
    writeMessage("Weekday codes: M=Monday, T=Tuesday, W=Wednesday, R=Thursday, F=Friday, "
        + "S=Saturday, U=Sunday"
        + System.lineSeparator());
    writeMessage(System.lineSeparator());

    writeMessage("Editing Rules:" + System.lineSeparator());
    writeMessage("Must uniquely identify event (if multiple matches, edit fails)"
        + System.lineSeparator());
    writeMessage("edit event = modify SINGLE instance only" + System.lineSeparator());
    writeMessage("edit events = modify identified event + all FUTURE events in series"
        + System.lineSeparator());
    writeMessage("edit series = modify ALL events in the series" + System.lineSeparator());
    writeMessage("WARNING: Changing start time splits a series into separate series"
        + System.lineSeparator());
    writeMessage(System.lineSeparator());

    writeMessage("Query Commands:" + System.lineSeparator());
    writeMessage("• print events on <dateString> - Lists all events on specified date with "
        + "times and location"
        + System.lineSeparator());
    writeMessage("• print events from <dateTimeString> to <dateTimeString> - Lists events "
        + "in date/time range"
        + System.lineSeparator());
    writeMessage("• show status on <dateTimeString> - Shows if you are busy or available at "
        + "specific time"
        + System.lineSeparator());
    writeMessage(System.lineSeparator());

    writeMessage("Miscellaneous Commands:" + System.lineSeparator());
    writeMessage("• export cal <fileName.csv> - Exports calendar to Google Calendar "
        + "compatible CSV file"
        + System.lineSeparator());
    writeMessage("• exit - Stops listening for further commands and quits the application"
        + System.lineSeparator());
    writeMessage(System.lineSeparator());

    writeMessage("Important Constraints:" + System.lineSeparator());
    writeMessage("• Date format: YYYY-MM-DD (e.g., 2025-05-15)" + System.lineSeparator());
    writeMessage("• Time format: hh:mm in 24-hour format (e.g., 14:30)"
        + System.lineSeparator());
    writeMessage("• DateTime format: YYYY-MM-DDThh:mm (e.g., 2025-05-15T14:30)"
        + System.lineSeparator());
    writeMessage("• Multi-word subjects MUST be in \"double quotes\"" + System.lineSeparator());
    writeMessage("• Edits that create duplicate events will be REJECTED"
        + System.lineSeparator());
    writeMessage(System.lineSeparator());

    writeMessage("Editable Properties:" + System.lineSeparator());
    writeMessage("• subject, start, end, description, location, status"
        + System.lineSeparator());
    writeMessage("Enter 'exit' to quit the application" + System.lineSeparator());
  }

  @Override
  public void askForInstruction() {
    writeMessage("Type Instruction: " + System.lineSeparator());

  }

  @Override
  public void printErrorMessage(String command, String errorReason) {
    writeMessage("Invalid Command: " + command + System.lineSeparator());
    writeMessage(errorReason + System.lineSeparator());
  }

  @Override
  public void printFarewellMessage() {
    writeMessage("Thank you for using the Edith calendar!" + System.lineSeparator());

  }

  @Override
  public void setCalendar(String command, CalendarManager calendarManager) {
    String name = getCalendarNameFromCommand(command);
    Calendar calendar = calendarManager.getCalendar(name);
    if (calendar == null) {
      printErrorMessage(command, "No calendar found with this name");
      writeMessage("You can create a new calendar" + System.lineSeparator());
    } else {
      this.calendar = calendar;
      this.calendarManager.setCurrentCalendarNameAs(calendar.getCalendarName());
      System.out.println("Using calendar: " + this.calendar.toString());
    }
  }

  @Override
  public void initializeCommands() {

    knownCommands.put("create", (command, obj) ->
        new ExecutableCreateCommand(command, calendarManager));

    knownCommands.put("edit", (command, obj) ->
        new ExecutableEditCommand(command, calendarManager));

    knownCommands.put("print", (command, obj) ->
        new PrintEventsCommand(command, calendarManager));

    knownCommands.put("show", (command, obj) ->
        new ShowStatusCommand(command, calendarManager));

    knownCommands.put("export", (command, obj) ->
        new ExportCalendarCommand(command, calendarManager));

    knownCommands.put("copy", (command, obj) ->
        new ExecutableCopyCommand(command, calendarManager));

    knownCommands.put("use", (command, obj) ->
        new UseCalendarCommand(command, calendarManager));

  }
}
