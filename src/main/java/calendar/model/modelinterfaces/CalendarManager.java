package calendar.model.modelinterfaces;

import java.util.Map;

/**
 * Interface to represent all operations that can be performed on calendars.
 * Operations include creating, storing, editing  and quering calendars
 */

public interface CalendarManager {

  /**
   * Method to get the data structure where all calendars are stored.
   *
   * @return hash map of known calendars
   */

  public Map<String, calendar.model.modelinterfaces.Calendar> getCalendarManagerStore();

  /**
   * Method to create a calendar object using name and timezone.
   *
   * @param calendarName Name of the calendar
   * @param timeZone     timezone of calendar in area/location format
   * @return calendar object
   */

  public calendar.model.modelinterfaces.Calendar createCalendar(String calendarName,
                                                                String timeZone);

  /**
   * Method to get the calendar using its name.
   *
   * @param calendarName name to search by
   * @return calendar object
   */

  public calendar.model.modelinterfaces.Calendar getCalendar(String calendarName);

  /**
   * Method to save the calendar to the Calendar Manager store.
   *
   * @param calendar calendar object
   */
  public void saveCalendar(calendar.model.modelinterfaces.Calendar calendar);


  /**
   * Method to set current calendar to an existing calendar.
   *
   * @param calendarName name of existing calendar
   */

  public void setCurrentCalendarNameAs(String calendarName);

  /**
   * Method to get the name of the current calendar.
   *
   * @return name of current calendar.\
   */

  public String getCurrentCalendarName();


}
