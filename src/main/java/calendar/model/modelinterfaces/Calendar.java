package calendar.model.modelinterfaces;

import calendar.model.modelutility.EventKey;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;


/**
 * An interface to define functionalities of a calendar.
 * Functionalities include saving an event, saving an event series,
 * exporting the calendar, showing status for an event and querying the calendar
 */

public interface Calendar {

  /**
   * Method to create an event from the following properties.
   *
   * @param subject       subject of the event
   * @param startDateTime start date of the event
   * @param endDateTime   end date of the event
   * @param eventSeriesId series id is passed if the event is part of a series
   * @return the event created
   */

  public Event createEvent(String subject, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, String eventSeriesId,
                           String location, String status, String description);

  /**
   * Method to get the CalendarStore, a hashmap which stores the calendar events.
   *
   * @return the Calendar store
   */

  public Map<EventKey, Event> getCalendarStore();

  /**
   * Method to create a series in the calendar from the recurring event.
   *
   * @param event       Event object
   * @param repeatTimes how many times does the event repeat for
   * @param repeatDays  which days it repeats for example, "MT" means Mondays and Tuesdays
   * @param lastDate    dummy des
   */

  public void createSeries(Event event, int repeatTimes, List<DayOfWeek> repeatDays,
                           LocalDate lastDate);

  /**
   * Method to save the passed event in the calendar.
   *
   * @param event that represents the event object
   */

  public void saveEvent(Event event);


  /**
   * Method prints either "Busy" if events are scheduled otherwise "Available".
   *
   * @param dateTime the start date and time string
   * @return "Busy" or "Available"
   */

  public String showStatus(String dateTime);

  /**
   * Method to get the schedule within any range of dates.
   *
   * @param startDateTime start date and time of the schedule range
   * @param endDateTime   end date and time of the schedule range
   * @return a String List of the events in the schedule.
   */

  public List<Event> getSchedule(String startDateTime, String endDateTime);

  /**
   * Method to get an event on the given date and time.
   *
   * @param endTime   end time of the event
   * @param startTime start time of the event
   * @param subject   subject of the event
   * @param startDate start date of the event
   * @param endDate   end date of the event
   * @return the event object
   */

  public Event getEvent(String subject, LocalDate startDate, LocalDate endDate,
                        LocalTime startTime, LocalTime endTime);

  /**
   * Method to get event/s on only the given date.
   *
   * @param date datetime to check
   * @return String List of events on that date
   */

  public List<Event> getEventsForDate(LocalDate date);

  /**
   * Method to get event/s on only the given date.
   *
   * @param oldEvent     old event keys
   * @param updatedEvent new updated event
   */

  public void updateEventKey(Event oldEvent, Event updatedEvent);

  /**
   * Gets the calendar name.
   *
   * @return the name of the calendar
   */

  public String getCalendarName();

  /**
   * Gets the time zone of the calendar.
   *
   * @return the time zone of calendar
   */

  public ZoneId getCalendarTimeZone();

  /**
   * Method to edit the calendar name.
   *
   * @param newName new calendar name
   * @return the edited calendar
   */
  public Calendar modifyName(String newName);

  /**
   * Method to edit the calendar timezone.
   *
   * @param newTimezone new calendar timezone
   * @return the edited calendar
   */
  public Calendar modifyTimezone(ZoneId newTimezone);


  /**
   * Method to check for conflict between date times of newly added event with existing events.
   *
   * @param newEvent new event which is being added.
   * @return boolean true is there is conflict or else false.
   */

  public boolean hasConflict(Event newEvent);

  /**
   * Method which copies the events from source calendar to target calendar
   * within the date range.
   * source calendar is current object from which the method is being called.
   *
   * @param startDate events to be copied from start date.
   * @param endDate events to be copied will end date.
   * @param targetCalendar target calendar.
   * @param targetStartDate target start date from which the events will start copying.
   * @return List of events copied.
   */

  public List<Event> copyEventsBetween(LocalDate startDate, LocalDate endDate,
                                       Calendar targetCalendar, LocalDate targetStartDate);

  /**
   * Method copies events on that date to target calendar from source calendar.
   * source calendar is calendar from which the method is being called.
   *
   * @param sourceDate date of which the events need to be copied.
   * @param targetCalendar calendar to which the events need to copied.
   * @param targetDate target calendar date on which the event would be copied.
   * @return return the list of events copied.
   */
  public List<Event> copyEventsOnDate(LocalDate sourceDate,
                                      Calendar targetCalendar, LocalDate targetDate);

  /**
   * Method to copy an event from source calendar to target calendar when
   * copy on date or between dates is called.
   * source calendar is calendar from which the method is being called.
   *
   * @param event event to be copied.
   * @param targetCalendar target calendar.
   * @param targetDateTime target calendar date on which the event needs to be copied.
   * @return the copied event.
   */

  public Event copyEvent(Event event, Calendar targetCalendar, LocalDateTime targetDateTime);

  /**
   * Method which copies a single event, and gets called when a single event only needs to be copied
   * to target calendar.
   *
   * @param event the event which needs to copied to new calendar
   * @param targetCalendar target calendar to which the event will be copied
   * @param targetDateTime target date time to which the event will be copied
   * @return copied event
   */

  public Event copyEventSingle(Event event, Calendar targetCalendar, LocalDateTime targetDateTime);

}