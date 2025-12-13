package calendar.controller;

import calendar.model.modelinterfaces.CalendarManager;
import calendar.view.ViewInterface;

/**
 * Controller object interface. Used to process input.
 *
 */

public interface CalendarController {

  /**
   * Method to relinquish control of the application to the controller.
   *
   */

  public void go();


  /**
   * Processes the command, by parsing the command based on keywords
   * and calls respective execute method.
   *
   * @param command the command which needs to be executed.
   */

  public void processCommands(String command);


  /**
   * Writes a message on user screen.
   *
   * @param message message
   */

  public void writeMessage(String message);

  /**
   * Prints the first welcome message on user screen.
   *
   */
  public void printWelcomeMessage();

  /**
   * Prints the rules of the application on screen.
   *
   */

  public void printRules();

  /**
   * Asks for instruction.
   *
   */

  public void askForInstruction();


  /**
   * Prints error message if system encounters one.
   *
   * @param command     user command
   * @param errorReason reason for error
   */

  public void printErrorMessage(String command, String errorReason);

  /**
   * Farewell message saying indicating that the system is being closed.
   *
   */

  public void printFarewellMessage();


  /**
   * Method to set the current calendar based on input calendar name.
   *
   * @param command         the use command
   * @param calendarManager Calendar Manager object
   */

  public void setCalendar(String command, CalendarManager calendarManager);


  /**
   * Method to populate the HashMap of commands and function objects.
   *
   */

  public void initializeCommands();


}
