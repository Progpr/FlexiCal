package calendar.view;

import calendar.controller.GuiControllerFeatures;
import calendar.model.modelinterfaces.Event;
import java.time.LocalDate;
import java.util.List;

/**
 * View interface for the GUI calendar.
 */
public interface ViewInterface {

  /**
   * Shows all the available calenders in the left panel when navigate calendar button is clicked.
   *
   * @param calendars list of available calendars is passed
   */

  public void showCalendars(List<String> calendars);

  /**
   * Connects the view to controller, and initializes some action listeners.
   *
   * @param guiHandlers GUI controller
   */

  public void addHandlers(GuiControllerFeatures guiHandlers);

  /**
   * Displays create calendar dialog box, when create calendar button is clicked.
   *
   */

  public void showCreateCalendarDialogBox();

  /**
   * Shows events on that day, triggers when the date is clicked.
   *
   * @param events list of events on that day
   */

  public void showEventsOfTheDay(List<Event> events);

  /**
   * gets the current date.
   *
   * @return current date
   */

  public LocalDate getCurrentDate();

  /**
   * Displays the edit event dialog box.
   */
  public void showEditEventDialogBox(Event event);


  /**
   * Displays the create event dialog box, triggered when create event button is called.
   */

  public void showCreateEventDialogBox();

  /**
   * Displays the calendar operations in option menu,
   * triggered when a calendar name from left panel is clicked.
   *
   * @param calendarName the calendar name which is clicked
   * @param x            the position identifier at which the option menu should be shown
   * @param y            the position identifier at which the option menu should be shown
   */

  public void showCalendarOptionsMenu(String calendarName, int x, int y);

  /**
   * Updates/refreshes the current calendar label on toolbar.
   *
   * @param calendarName current calendar name to be displayed on label
   * @param timezone     current calendar time zone to be displayed on label
   */

  public void updateCurrentCalendarLabel(String calendarName, String timezone);

  /**
   * Displays the search results in left panel, triggered when enter is clicked in search text box.
   *
   * @param searchResults List of events as search results which need to be displayed
   * @param searchTerm    Search term with which search was called
   */
  public void showSearchResults(List<Event> searchResults, String searchTerm);

  /**
   * Displays error window when a calendar name is trying to be set which already exists.
   *
   * @param calendarName Name of the calendar
   * @param isEditing    flag to check if previous window was edit or create
   * @param oldName      old name of the calendar
   */

  void showCalendarNameExistsError(String calendarName, boolean isEditing, String oldName);

  /**
   * throws error window when event already exists.
   *
   * @param event       event
   * @param isEditing   is it in edit mode or not
   * @param eventExists if event exist or not
   */

  void showEventExistsError(Event event, boolean isEditing, boolean eventExists);

  /**
   * edit series dialog box create.
   *
   * @param event event
   */

  public void showEditSeriesDialogBox(Event event);
}
