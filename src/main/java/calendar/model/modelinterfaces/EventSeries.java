package calendar.model.modelinterfaces;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * This is an interface for Event series implementation.
 * It stores the metadata of the event series.
 *
 */

public interface EventSeries {

  /**
   * Gets the event series ID.
   *
   * @return the Event series ID
   */
  public String getEventSeriesId();


  /**
   * Method to get the number of occurrences of the event.
   *
   * @return the number of occurrences of the recurring event
   */

  public int getOccurrences();

  /**
   * Method gets the day of the week on which the series repeats.
   *
   * @return days on which the event repeats.
   */

  public List<DayOfWeek> getRepeatDays();

  /**
   * Method to get the subject of the event series.
   *
   * @return the present subject
   */

  public String getSubject();

  /**
   * Method to get the start date of the event series.
   *
   * @return the present start date
   */

  public LocalDate getFirstDate();

  /**
   * Method to get the end date of the event series.
   *
   * @return the present end date
   */

  public LocalDate getLastDate();

  /**
   * construct a series of events which are part of the series.
   *
   * @param event first occurrence of the event.
   * @param calendar calendar in which we create the event series.
   */

  public void constructSeriesEvents(Event event, Calendar calendar);

}
