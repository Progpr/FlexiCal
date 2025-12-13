package calendar.model.modelutility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Immutable key representing a unique event identity.
 * It is composite key of subject, startDate, endDate, startTime, endTime.
 * Creates an object which will be stored in calendar hash map as a key.
 *
 */
public final class EventKey {
  private final String subject;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final LocalTime startTime;
  private final LocalTime endTime;


  /**
   * Constructor which creates an object for event key.
   *
   * @param subject   subject of the event
   * @param startDate start date of the event
   * @param endDate   end date of the event
   * @param startTime time on which the event starts
   * @param endTime   time on which the event ends
   */

  public EventKey(String subject, LocalDate startDate, LocalDate endDate,
                  LocalTime startTime, LocalTime endTime) {
    this.subject = subject;
    this.startDate = startDate;
    this.endDate = endDate;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  /**
   * getter to get the subject part of the key.
   *
   * @return the subject part of the key
   */

  public String getSubject() {
    return subject;
  }

  /**
   * getter to get the start date part of the key.
   *
   * @return the start date part of the key
   */

  public LocalDate getStartDate() {
    return startDate;
  }

  /**
   * getter to get the end date part of the key.
   *
   * @return the end date part of the key
   */

  public LocalDate getEndDate() {
    return endDate;
  }

  /**
   * getter to get the start time part of the key.
   *
   * @return the start time part of the key
   */

  public LocalTime getStartTime() {
    return startTime;
  }

  /**
   * getter to get the end time part of the key.
   *
   * @return the end time part of the key
   */

  public LocalTime getEndTime() {
    return endTime;
  }

  /**
   * override the equals method to compare two event keys properly.
   *
   * @param o the reference object with which to compare.
   * @return boolean result of equal or not.
   */

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof EventKey)) {
      return false;
    }
    EventKey that = (EventKey) o;
    return Objects.equals(subject, that.subject)
        && Objects.equals(startDate, that.startDate)
        && Objects.equals(endDate, that.endDate)
        && Objects.equals(startTime, that.startTime)
        && Objects.equals(endTime, that.endTime);
  }

  /**
   * override the hashcode method, returns the object hashcode.
   *
   * @return returns the hashcode.
   */

  @Override
  public int hashCode() {
    return Objects.hash(subject, startDate, endDate, startTime, endTime);
  }

  /**
   * Override the toString  method.
   *
   * @return returns the event details in string format.
   */

  @Override
  public String toString() {
    return "EventKey{"
        + "subject='" + subject + '\''
        + ", startDate=" + startDate
        + ", endDate=" + endDate
        + ", startTime=" + startTime
        + ", endTime=" + endTime
        + '}';
  }
}
