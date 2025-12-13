package calendar.controller;

import calendar.view.ViewInterface;
import java.util.Map;

/**
 * Controller interface for GUI calendar.
 */

public interface GuiControllerFeatures {

  /**
   * Connects the controller to view.
   *
   * @param v view object
   */

  public void setView(ViewInterface v);

  /**
   * Handles the action of 'Navigate calendars' button in toolbar.
   * populates the left panel with available calendars.
   *
   */
  void handleNavigateCalendarClicked();

  /**
   * Handles the action when a date on the calendar is clicked.
   * refreshes and populates the left panel with events on that date.
   *
   */

  void handleDateClicked();

  /**
   * Handles the action create event button is clicked.
   * Shows the create event dialog box.
   *
   */

  void handleCreateEventClicked();

  /**
   * Handles the action of create event button clicked from create event dialog box.
   * Functionality to create and save event or event series.
   *
   * @param map Map of input parameter fields is passed
   */

  void handleDialogBoxCreateEvent(Map<String, Object> map);

  /**
   * handle edit event.
   *
   * @param newParameters parameter
   */
  void handleEditEventClicked(Map<String, Object> newParameters);

  /**
   * handle edit series.
   *
   * @param map field map
   */
  void handleEditSeriesClicked(Map<String, Object> map);

  /**
   * handles the action create calendar button clicked.
   * Shows a create calendar dialog box.
   *
   */

  void handleCreateCalendarClicked();

  /**
   * handles the action, use calendar option from option menu of a calendar is clicked.
   * The calendar name from that panel is set as current calendar in context.
   *
   * @param calendarName calendar name which is being set up as calendar in context.
   */

  void handleUseCalendarClicked(String calendarName);

  /**
   * gets the current calendar name in context.
   *
   * @return calendar name in string format
   */

  public String getCurrentCalendarName();

  /**
   * gets the current calendars time zone.
   *
   * @return time zone in string format
   */

  String getCurrentCalendarTimezone();

  /**
   * gets the current calendars time zone with name.
   *
   * @param calendarName name of calendar
   * @return time zone in string format
   */

  String getCalendarTimezone(String calendarName);

  /**
   * Handles the action, create calendar button from create calendar dialog box is clicked.
   *
   * @param calendarName calendar name from input field to create calendar
   * @param timeZone     calendar time zone from input field to create calendar
   */

  public void handleCreateCalendarDialogBoxClicked(String calendarName, String timeZone);

  /**
   * Handles the action when a calendar is clicked from left panel.
   * It pops up the option menu with operations of the calendar.
   *
   * @param calendarName calendar name text from the panel
   */

  public void handleCalendarClicked(String calendarName);

  /**
   * Handles the action, Edit calendar button is clicked from the edit calendar dialog box.
   * Edits the calendar with new name.
   *
   * @param oldName old name of the calendar
   * @param newName new name of the calendar from input field
   */

  void handleEditCalendarNameClicked(String oldName, String newName);

  /**
   * Handles the action, Edit calendar button is clicked from the edit calendar dialog box.
   * Edits the calendar with new timezone.
   *
   * @param calendarName calendar name of for which the time zone needs to be updated
   * @param newTimezone  new time zone value
   */

  void handleEditCalendarTimezoneClicked(String calendarName, String newTimezone);

  /**
   * Handles the action, when search field is entered a value an enter is clicked.
   *
   * @param searchTerm the input filed in search text box
   */

  void handleSearchEvents(String searchTerm);
}