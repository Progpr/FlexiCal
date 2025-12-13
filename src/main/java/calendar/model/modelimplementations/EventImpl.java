package calendar.model.modelimplementations;


import calendar.model.modelinterfaces.Event;
import calendar.model.modelutility.Location;
import calendar.model.modelutility.Status;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * This class represents an event in a calendar.
 * It is an implementation of the Event interface
 * It can be either part of a series or not.
 *
 */

public class EventImpl implements Event {

  protected String subject;
  protected LocalDate startDate;
  protected LocalDate endDate;
  protected LocalTime startTime;
  protected LocalTime endTime;
  protected String description;
  protected Location location;
  protected Status status;
  String eventSeriesId;


  /**
   * Constructs the event object.
   *
   */

  EventImpl(String subject, LocalDate startDate, LocalDate endDate,
            LocalTime startTime, LocalTime endTime, String eventSeriesId, Status status,
            String description, Location location) {

    this.subject = subject;
    this.startDate = startDate;
    this.endDate = endDate;
    this.startTime = startTime;
    this.endTime = endTime;
    this.eventSeriesId = eventSeriesId;
    this.status = status;
    this.description = description;
    this.location = location;

  }

  /**
   * Constructs to create a copy of the event.
   *
   * @param other the event of which we need to make a copy of
   */

  public EventImpl(Event other) {
    this.subject = other.getSubject();
    this.startDate = other.getStartDate();
    this.endDate = other.getEndDate();
    this.startTime = other.getStartTime();
    this.endTime = other.getEndTime();
    this.eventSeriesId = other.getEventSeriesId();
    this.status = Status.valueOf(other.getStatus());
    this.description = other.getDescription();
    this.location = Location.valueOf(other.getLocation());
  }

  /**
   * Method to return the builder event builder object which will be used to create an event.
   *
   * @return a new event builder object
   */

  public static EventBuilder getEventBuilder() {
    return new EventBuilder();
  }

  private static Location validateLocation(String input) {

    if (input.equalsIgnoreCase(Location.Online.name())) {
      return Location.Online;
    } else if (input.equalsIgnoreCase(Location.Physical.name())) {
      return Location.Physical;
    }
    System.out.println("Invalid Location");
    return null;
  }

  private static Status validateStatus(String input) {

    if (input.equalsIgnoreCase((Status.Private.name()))) {
      return Status.Private;
    } else if (input.equalsIgnoreCase(Status.Public.name())) {
      return Status.Public;
    }
    System.out.println("Invalid Status");
    return null;
  }


  @Override
  public String getSubject() {
    return this.subject;
  }

  @Override
  public LocalTime getStartTime() {
    return this.startTime;
  }

  @Override
  public LocalTime getEndTime() {
    return this.endTime;
  }

  @Override
  public LocalDate getStartDate() {
    return this.startDate;
  }

  @Override
  public LocalDate getEndDate() {
    return this.endDate;
  }

  @Override
  public String getDescription() {
    return this.description;
  }

  @Override
  public String getStatus() {
    return this.status.name();
  }

  @Override
  public String getLocation() {
    return this.location.name();
  }

  @Override
  public String getEventSeriesId() {
    return this.eventSeriesId;
  }

  @Override
  public Event modifySubject(String subject) {
    if (subject.equals(this.subject)) {
      return null;
    }

    this.subject = subject;
    return this;
  }

  @Override
  public Event modifySeriesId(String eventSeriesId) {

    this.eventSeriesId = eventSeriesId;
    return this;
  }

  @Override
  public Event modifyStartDate(LocalDate startDate) {
    if (startDate.equals(this.startDate)) {
      return null;
    }

    this.startDate = startDate;
    return this;
  }

  @Override
  public Event modifyEndDate(LocalDate endDate) {
    if (endDate.equals(this.endDate)) {
      return null;
    }

    this.endDate = endDate;
    return this;
  }

  @Override
  public Event modifyStartTime(LocalTime startTime) {
    if (startTime.equals(this.startTime)) {
      return null;
    }

    this.startTime = startTime;
    return this;
  }

  @Override
  public Event modifyEndTime(LocalTime endTime) {
    if (endTime.equals(this.endTime)) {
      return null;
    }
    this.endTime = endTime;
    return this;
  }

  @Override
  public Event modifyDescription(String description) {
    if (description.equals(this.description)) {
      return null;
    }
    this.description = description;
    return this;
  }

  @Override
  public Event modifyStatus(String status) {
    if (status.equals(this.status.name())) {
      return null;
    }
    this.status = validateStatus(status);
    return this;
  }

  @Override
  public Event modifyLocation(String location) {
    if (location.equals(this.location.name())) {
      return null;
    }
    this.location = validateLocation(location);
    return this;
  }

  @Override
  public String toString() {
    DateTimeFormatter formatter12 = DateTimeFormatter.ofPattern("hh:mm a");

    LocalTime startTime = getStartTime();
    startTime.format(formatter12);

    LocalTime endTime = getEndTime();
    endTime.format(formatter12);

    return getSubject()
        +
        " starting on " + getStartDate()
        +
        " at "
        +
        getStartTime()
        +
        ", ending on " + getEndDate() + " at " + getEndTime()
        +
        ", description: " + getDescription() + ", status "
        +
        getStatus()
        +
        ", location " + getLocation();

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (!(o instanceof EventImpl)) {
      return false;
    }

    EventImpl otherEvent = (EventImpl) o;

    return this.getSubject().equals(otherEvent.getSubject())
        && this.getStartTime().equals(otherEvent.getStartTime())
        && this.getEndTime().equals(otherEvent.getEndTime())
        && this.getStartDate().equals(otherEvent.getStartDate())
        && this.getEndDate().equals(otherEvent.getEndDate());
  }

  @Override
  public int compareTo(Event o) {

    int compareStartDate = this.getStartDate().compareTo(o.getStartDate());
    if (compareStartDate != 0) {
      return compareStartDate;
    }

    int compareEndDate = this.getEndDate().compareTo(o.getEndDate());
    if (compareEndDate != 0) {
      return compareEndDate;
    }

    int compareStartTime = this.getStartTime().compareTo(o.getStartTime());
    if (compareStartTime != 0) {
      return compareStartTime;
    }

    return this.getEndTime().compareTo(o.getEndTime());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getSubject(), getStartDate(), getEndDate(), getStartTime(), getEndTime());
  }

  /**
   * A builder class to build event.
   *
   */

  public static class EventBuilder {

    protected String subject;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected LocalTime startTime;
    protected LocalTime endTime;
    String eventSeriesId;
    protected String description;
    protected Location location;
    protected Status status;

    /**
     * Constructs event builder object.
     *
     */
    public EventBuilder() {

      LocalTime startTime = LocalTime.of(8, 0);
      LocalTime endTime = LocalTime.of(17, 0);
      LocalDate currentDate = LocalDate.now();
      this.startTime = startTime;
      this.endTime = endTime;
      this.startDate = currentDate;
      this.endDate = currentDate;
      this.eventSeriesId = null;
      this.description = " ";
      this.status = Status.Private;
      this.location = Location.Online;

    }

    /**
     * Sets the start date.
     *
     */

    public EventBuilder setStartDate(LocalDate startDate) throws IllegalArgumentException {
      if (startDate == null) {
        throw new IllegalArgumentException("startDate cannot be null");
      }

      this.startDate = startDate;
      return this;
    }

    /**
     * Sets end date.
     *
     */

    public EventBuilder setEndDate(LocalDate endDate) {
      this.endDate = endDate;
      return this;
    }

    /**
     * Sets start time.
     *
     */

    public EventBuilder setStartTime(LocalTime startTime) throws IllegalArgumentException {
      if (startTime == null) {
        throw new IllegalArgumentException("startTime cannot be null");
      }

      this.startTime = startTime;
      return this;
    }

    /**
     * Sets end time.
     *
     */

    public EventBuilder setEndTime(LocalTime endTime) {
      this.endTime = endTime;
      return this;
    }

    /**
     * Sets the subject.
     *
     */

    public EventBuilder setSubject(String subject) throws IllegalArgumentException {
      if (subject == null) {
        throw new IllegalArgumentException("subject cannot be null");
      }

      this.subject = subject;
      return this;
    }

    /**
     * Sets event series ID.
     *
     */

    public EventBuilder setEventSeriesId(String eventSeriesId) {
      this.eventSeriesId = eventSeriesId;
      return this;
    }



    /**
     * builder build method which calls the main object constructor.
     *
     */

    public Event build() {
      return new EventImpl(this.subject, this.startDate, this.endDate,
          this.startTime, this.endTime, this.eventSeriesId, this.status, this.description,
          this.location);
    }

  }

}
