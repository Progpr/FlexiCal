package calendar.model.modelimplementations;

import static calendar.model.modelimplementations.EventImpl.getEventBuilder;
import static calendar.utility.GenerateSeriesId.generateSeriesId;

import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.Event;
import calendar.model.modelinterfaces.EventSeries;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.management.openmbean.KeyAlreadyExistsException;

/**
 * Implementation of Event series object class.
 * It stores the metadata of the series events.
 *
 */

public class EventSeriesImpl implements EventSeries {
  String eventSeriesId;
  int occurrences;
  LocalDate startDate;
  LocalDate endDate;
  List<DayOfWeek> repeatDays;


  /**
   * Constructs an Event series object.
   *
   * @param eventSeriesId generated series id from series builder is passed.
   * @param occurrences   number of occurrences of the event in series are passed.
   * @param startDate     start date of the event series.
   * @param endDate       till date of the event series.
   * @param repeatDays    days of the week on which the event is repeating in a series.
   */
  public EventSeriesImpl(String eventSeriesId, int occurrences, LocalDate startDate,
                         LocalDate endDate, List<DayOfWeek> repeatDays) {
    this.eventSeriesId = eventSeriesId;
    this.occurrences = occurrences;
    this.startDate = startDate;
    this.endDate = endDate;
    this.repeatDays = repeatDays;
  }

  @Override
  public int getOccurrences() {
    return occurrences;
  }

  @Override
  public String getEventSeriesId() {
    return this.eventSeriesId;
  }

  @Override
  public String getSubject() {
    return "";
  }

  @Override
  public LocalDate getFirstDate() {
    return this.startDate;
  }

  @Override
  public LocalDate getLastDate() {
    return this.endDate;
  }

  @Override
  public List<DayOfWeek> getRepeatDays() {
    return this.repeatDays;
  }

  @Override
  public void constructSeriesEvents(Event event, Calendar calendar) {
    int createdCount = 1;
    LocalDate current = event.getStartDate();

    if (this.getOccurrences() > 0) {
      current = current.plusDays(1);

      while (createdCount < this.getOccurrences()) {
        if (this.repeatDays.contains(current.getDayOfWeek())) {
          Event newEvent = createCopyOfEventOnDate(event, current);
          try {
            calendar.saveEvent(newEvent);
            createdCount++;
          } catch (KeyAlreadyExistsException e) {
            System.out.println("Skipping duplicate event on " + current);
          }
        }
        current = current.plusDays(1);
      }
    } else if (this.getLastDate() != null) {
      current = current.plusDays(1);

      while (!current.isAfter(this.getLastDate())) {
        if (this.repeatDays.contains(current.getDayOfWeek())) {
          Event newEvent = createCopyOfEventOnDate(event, current);
          try {
            calendar.saveEvent(newEvent);
          } catch (KeyAlreadyExistsException e) {
            System.out.println("Skipping duplicate event on " + current);
          }
        }
        current = current.plusDays(1);
      }
    }
  }


  /**
   * Builder is being used to create event series object, this method gets the builder object.
   *
   * @return event series builder object.
   */

  public static EventSeriesBuilder getEventSeriesBuilder() {
    return new EventSeriesBuilder();
  }


  /**
   * A helper method which creates a copy of the event that is passed on specific date.
   * since it is a helper method for creating multiple objects of event for a series,
   * it modifies the subject slightly.
   *
   * @param event the event of which we need to make the copy of.
   * @param date  the date on which the copy needs to be created.
   */

  public static Event createCopyOfEventOnDate(Event event, LocalDate date) {
    EventImpl.EventBuilder eventBuilder = getEventBuilder();

    if (event.getSubject() != null) {
      eventBuilder.setSubject(event.getSubject());
    }
    if (date != null) {
      eventBuilder.setStartDate(date);
    }
    if (date != null) {
      eventBuilder.setEndDate(date);
    }
    if (event.getStartTime() != null) {
      eventBuilder.setStartTime(event.getStartTime());
    }
    if (event.getEndTime() != null) {
      eventBuilder.setEndTime(event.getEndTime());
    }
    if (event.getEventSeriesId() != null) {
      eventBuilder.setEventSeriesId(event.getEventSeriesId());
    }

    return eventBuilder.build();
  }


  /**
   * An event series is being built using a builder.
   * An event series builder class which helps build an event series.
   *
   */

  public static class EventSeriesBuilder {
    public String eventSeriesId;
    public int occurrences;
    public LocalDate startDate;
    public LocalDate endDate;
    public List<DayOfWeek> repeatDays;

    /**
     * Constructs the event series builder object.
     *
     */

    public EventSeriesBuilder() {
      this.eventSeriesId = generateSeriesId();
      this.occurrences = 0;
      this.startDate = null;
      this.endDate = null;
      this.repeatDays = new ArrayList<>();
    }


    /**
     * sets the number of occurrences in builder object.
     *
     * @param occurrences number of occurrences passed to builder.
     * @return builder object with updated occurrences variable.
     */

    public EventSeriesBuilder setOccurrences(int occurrences) {
      this.occurrences = occurrences;
      return this;
    }


    /**
     * sets the start date in event series builder object.
     *
     * @param startDate start date passed to builder.
     * @return event series builder object with updated start date.
     */

    public EventSeriesBuilder setStartDate(LocalDate startDate) {
      this.startDate = startDate;
      return this;
    }


    /**
     * sets the end date in the event series builder object.
     *
     * @param endDate till end date of the series passed.
     * @return event series builder object with updated till/end date.
     */

    public EventSeriesBuilder setEndDate(LocalDate endDate) {
      this.endDate = endDate;
      return this;
    }


    /**
     * sets the repeat days in the builder object.
     *
     * @param repeatDays days on which the event is going to repeat.
     * @return builder object with updated repeat days.
     */

    public EventSeriesBuilder setRepeatDays(List<DayOfWeek> repeatDays) {
      this.repeatDays = repeatDays;
      return this;
    }


    /**
     * builds the event series builder by calling the event series constructor.
     * When the build is triggered, all the events in the series are created with it.
     *
     * @return Event series object.
     */

    public EventSeries build() {
      return new EventSeriesImpl(this.eventSeriesId, this.occurrences,
          this.startDate, this.endDate, this.repeatDays);
    }
  }
}
