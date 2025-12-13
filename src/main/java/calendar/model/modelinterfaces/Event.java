package calendar.model.modelinterfaces;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Event Interface, represents an event in calendar.
 *
 */

public interface Event extends Comparable<Event> {

  /**
   * Method to edit the seriesID of the given Event.
   *
   * @param seriesId new seriesID
   * @return the edited event
   */

  public Event modifySeriesId(String seriesId);

  /**
   * Getter method for series ID.
   *
   * @return returns the Event series ID
   */

  public String getEventSeriesId();

  /**
   * Method to get the subject of the event.
   *
   * @return the present subject
   */

  public String getSubject();

  /**
   * Method to a get the start time of an event.
   *
   * @return the present start time
   */

  public LocalTime getStartTime();

  /**
   * Method to get the end time of the event.
   *
   * @return the present end time
   */

  public LocalTime getEndTime();

  /**
   * Method to get the start date of the event.
   *
   * @return the present start date
   */

  public LocalDate getStartDate();

  /**
   * Method to get the end date of the event.
   *
   * @return the present end date
   */

  public LocalDate getEndDate();

  /**
   * Method to get the description of the event.
   *
   * @return description of the event
   */

  public String getDescription();

  /**
   * Method to get the status of the event.
   *
   * @return private or public
   */

  public String getStatus();

  /**
   * Method to get the location of the event.
   *
   * @return physical or online
   */

  public String getLocation();

  /**
   * Method to edit the subject of the given Event.
   *
   * @param subject new subject
   * @return the edited event
   */

  public Event modifySubject(String subject);

  /**
   * Method to edit the start date of the given event.
   *
   * @param startDate new start date
   * @return the edited event
   */

  public Event modifyStartDate(LocalDate startDate);

  /**
   * Method to edit the end date of the given event.
   *
   * @param endDate new end date
   * @return the edited event
   */

  public Event modifyEndDate(LocalDate endDate);

  /**
   * Method to edit the start time of the event.
   *
   * @param startTime new start time
   * @return the edited event
   */

  public Event modifyStartTime(LocalTime startTime);

  /**
   * Method to edit the end time of the event.
   *
   * @param endTime new end time
   * @return the edited event
   */

  public Event modifyEndTime(LocalTime endTime);

  /**
   * Method to edit the description of the event.
   *
   * @param description new description
   * @return the edited event
   */

  public Event modifyDescription(String description);

  /**
   * Method to edit the status of the event.
   *
   * @param status new status
   * @return edited event object
   */

  public Event modifyStatus(String status);

  /**
   * Method to edit the location of the event.
   *
   * @param location new location
   * @return edited event object
   */

  public Event modifyLocation(String location);


}
