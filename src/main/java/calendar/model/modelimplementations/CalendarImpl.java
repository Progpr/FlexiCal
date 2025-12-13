package calendar.model.modelimplementations;


import static calendar.model.modelimplementations.EventImpl.getEventBuilder;
import static calendar.model.modelimplementations.EventSeriesImpl.getEventSeriesBuilder;

import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.Event;
import calendar.model.modelinterfaces.EventSeries;
import calendar.model.modelutility.EventKey;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Calendar implementation class.
 * Represents a calendar.
 */
public class CalendarImpl implements Calendar {

  protected String calendarName;
  private final Map<EventKey, Event> calendarStore;
  protected ZoneId timeZone;


  /**
   * A calendar constructor which initializes the calendar object.
   * The events in a calendar are stored in a hash map.
   */

  public CalendarImpl(String calendarName, ZoneId timeZone) {

    if (calendarName.isEmpty()) {
      throw new IllegalArgumentException("Calendar name cannot be empty");
    }
    if (timeZone == null) {
      throw new IllegalArgumentException("Timezone cannot be empty");
    }

    this.calendarName = calendarName;
    this.timeZone = timeZone;
    this.calendarStore = new HashMap<>();
  }

  /**
   * Constructs a deep copy of the calendar.
   *
   * @param calendar calendar which needs to be copied
   */

  public CalendarImpl(Calendar calendar) {
    this.calendarName = calendar.getCalendarName();
    this.timeZone = calendar.getCalendarTimeZone();
    this.calendarStore = new HashMap<>();

    for (Map.Entry<EventKey, Event> entry : calendar.getCalendarStore().entrySet()) {
      this.calendarStore.put(
          entry.getKey(),
          new EventImpl(entry.getValue())
      );
    }
  }

  @Override
  public String getCalendarName() {
    return this.calendarName;
  }

  @Override
  public ZoneId getCalendarTimeZone() {
    return this.timeZone;
  }

  @Override
  public Calendar modifyName(String newName) {
    this.calendarName = newName;
    return this;
  }

  @Override
  public Calendar modifyTimezone(ZoneId newTimezone) {
    this.timeZone = newTimezone;

    for (Map.Entry<EventKey, Event> entry : calendarStore.entrySet()) {

      LocalDate sourceStartDate = entry.getValue().getStartDate();
      LocalDate sourceEndDate = entry.getValue().getEndDate();
      LocalTime sourceStartTime = entry.getValue().getStartTime();
      LocalTime sourceEndTime = entry.getValue().getEndTime();

      LocalDateTime sourceStart = LocalDateTime.of(sourceStartDate, sourceStartTime);
      LocalDateTime sourceEnd = LocalDateTime.of(sourceEndDate, sourceEndTime);

      LocalDateTime targetStart = adjustForTimezone(sourceStart, newTimezone);
      LocalDateTime targetEnd = adjustForTimezone(sourceEnd, newTimezone);

      LocalDate targetStartDate = targetStart.toLocalDate();
      LocalDate targetEndDate = targetEnd.toLocalDate();

      LocalTime targetStartTime = targetStart.toLocalTime();
      LocalTime targetEndTime = targetEnd.toLocalTime();

      entry.getValue().modifyStartDate(targetStartDate);
      entry.getValue().modifyEndDate(targetEndDate);
      entry.getValue().modifyStartTime(targetStartTime);
      entry.getValue().modifyEndTime(targetEndTime);
    }


    return this;
  }

  @Override
  public Map<EventKey, Event> getCalendarStore() {
    return this.calendarStore;
  }

  /**
   * helper method to validate the event on all properties (start/end date and start/end time).
   *
   * @param event the event object
   */

  private void validateEvent(Event event) throws IllegalArgumentException {

    if (event.getStartDate().isAfter(event.getEndDate())) {
      throw new IllegalArgumentException("Start date cannot be occurring after end date");
    }

    if (event.getSubject() == null || event.getEndDate() == null
        || event.getStartDate() == null || event.getStartTime() == null
        || event.getEndTime() == null) {
      throw new IllegalArgumentException("Invalid event, field cannot be null");
    }

  }

  @Override
  public Event createEvent(String subject, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, String eventSeriesId,
                           String location, String status, String description) {

    EventImpl.EventBuilder eventBuilder = getEventBuilder();

    if (endDateTime != null && startDateTime == null) {
      System.out.println("End date/time cannot be set without start date/time");
      return null;
    }

    if (subject != null) {
      eventBuilder.setSubject(subject);
    }

    if (startDateTime != null) {
      eventBuilder.setStartDate(startDateTime.toLocalDate());
      eventBuilder.setStartTime(startDateTime.toLocalTime());
    }

    if (endDateTime != null) {
      eventBuilder.setEndDate(endDateTime.toLocalDate());
      eventBuilder.setEndTime(endDateTime.toLocalTime());
    } else if (startDateTime != null) {
      eventBuilder.setEndDate(startDateTime.toLocalDate());
    }

    if (eventSeriesId != null) {
      eventBuilder.setEventSeriesId(eventSeriesId);
    }

    return eventBuilder.build();
  }

  /**
   * Method to create a series in the calendar from the recurring event.
   *
   * @param event       Event object
   * @param repeatTimes how many times does the event repeat for
   * @param repeatDays  which days it repeats for example, "MT" means Mondays and Tuesdays
   * @param lastDate    dummy des
   */

  @Override
  public void createSeries(Event event, int repeatTimes, List<DayOfWeek> repeatDays,
                           LocalDate lastDate) {
    EventSeriesImpl.EventSeriesBuilder eventSeriesBuilder = getEventSeriesBuilder();

    if (repeatTimes > 0) {
      eventSeriesBuilder.setOccurrences(repeatTimes);
    }
    if (lastDate != null) {
      eventSeriesBuilder.setEndDate(lastDate);
    }
    if (repeatDays != null) {
      eventSeriesBuilder.setRepeatDays(repeatDays);
    }

    EventSeries newSeriesObject = eventSeriesBuilder.build();
    newSeriesObject.constructSeriesEvents(event, this);
  }

  /**
   * Method to save the passed event in the calendar.
   *
   * @param event that represents the event object
   */

  @Override
  public void saveEvent(Event event) {
    EventKey key = new EventKey(
        event.getSubject(),
        event.getStartDate(),
        event.getEndDate(),
        event.getStartTime(),
        event.getEndTime()
    );

    if (calendarStore.containsKey(key)) {
      System.out.println("Event already exists");
      return;
    }

    validateEvent(event);

    calendarStore.put(key, event);
    System.out.println("Saved event: " + event);
  }


  /**
   * Method to get the schedule within any range of dates.
   *
   * @param startDateTime start date and time of the schedule range
   * @param endDateTime   end date and time of the schedule range
   * @return a String List of the events in the schedule.
   */

  @Override
  public List<Event> getSchedule(String startDateTime, String endDateTime) {
    LocalDateTime parsedStartDateTime = LocalDateTime.parse(startDateTime);
    LocalDateTime parsedEndDateTime = LocalDateTime.parse(endDateTime);

    LocalDate startDate = parsedStartDateTime.toLocalDate();

    ArrayList<Event> combinedEvents = new ArrayList<>();

    long days = ChronoUnit.DAYS.between(parsedStartDateTime, parsedEndDateTime);
    if (days > 0) {
      for (int i = 0; i <= days; i++) {
        LocalDate currentDate = startDate.plusDays(i);

        combinedEvents.addAll(getEventsForDate(currentDate));

      }

      return combinedEvents;
    } else if (days < 0) {
      System.out.println("End date cannot be before start date");
      return null;
    }

    return getEventsForDate(startDate);

  }

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

  @Override
  public Event getEvent(String subject, LocalDate startDate,
                        LocalDate endDate, LocalTime startTime,
                        LocalTime endTime) {

    EventKey key = new EventKey(subject, startDate, endDate, startTime, endTime);
    if (calendarStore.get(key) != null) {
      return calendarStore.get(key);
    }

    return null;

  }

  /**
   * Updates the key of an existing event in the calendarStore
   * when any identifying fields (subject, date, or time) change.
   *
   * @param oldEvent     the event before modification (to build old key)
   * @param updatedEvent the same event after modification
   */
  @Override
  public void updateEventKey(Event oldEvent, Event updatedEvent) {
    EventKey oldKey = new EventKey(
        oldEvent.getSubject(),
        oldEvent.getStartDate(),
        oldEvent.getEndDate(),
        oldEvent.getStartTime(),
        oldEvent.getEndTime()
    );

    calendarStore.remove(oldKey);

    EventKey newKey = new EventKey(
        updatedEvent.getSubject(),
        updatedEvent.getStartDate(),
        updatedEvent.getEndDate(),
        updatedEvent.getStartTime(),
        updatedEvent.getEndTime()
    );

    calendarStore.put(newKey, updatedEvent);

  }


  /**
   * Method to get event/s on only the given date.
   *
   * @param date datetime to check
   * @return String List of events on that date
   */

  @Override
  public List<Event> getEventsForDate(LocalDate date) {

    List<Event> events = new ArrayList<>();
    for (Event event : calendarStore.values()) {
      if (event.getStartDate().equals(date)) {
        events.add(event);
      }
    }
    return events;
  }


  /**
   * Method prints either "Busy" if events are scheduled otherwise "Available".
   *
   * @param dateTime the start date and time string
   * @return "Busy" or "Available"
   */

  @Override
  public String showStatus(String dateTime) {

    LocalDateTime parsedStartDateTime = LocalDateTime.parse(dateTime);

    LocalDate startDate = parsedStartDateTime.toLocalDate();
    LocalTime startTime = parsedStartDateTime.toLocalTime();

    for (Event event : calendarStore.values()) {
      if (startDate.equals(event.getStartDate())
          && startTime.equals(event.getStartTime())) {
        return "Busy";
      }
    }

    return "Available";
  }


  @Override
  public String toString() {
    return "Calendar Name: " + getCalendarName() + " Timezone: " + getCalendarTimeZone();
  }

  /**
   * Adjusts a datetime from source calendar timezone to target calendar timezone.
   */
  private LocalDateTime adjustForTimezone(LocalDateTime sourceDateTime, ZoneId targetTimezone) {
    ZonedDateTime sourceZoned = ZonedDateTime.of(sourceDateTime, this.timeZone);
    ZonedDateTime targetZoned =
        sourceZoned.withZoneSameInstant(targetTimezone);
    return targetZoned.toLocalDateTime();
  }

  /**
   * Creates a copy of an event with a new subject and optional series ID.
   */
  private Event createEventCopy(Event original, Calendar targetCalendar,
                                LocalDateTime targetStart, LocalDateTime targetEnd,
                                String seriesId) {
    String copiedSubject = appendCopySuffix(original.getSubject());

    return targetCalendar.createEvent(
        copiedSubject,
        targetStart,
        targetEnd,
        seriesId,
        original.getLocation(),
        original.getStatus(),
        original.getDescription()
    );
  }

  /**
   * Appends "_copy" suffix if not already present.
   */
  private String appendCopySuffix(String subject) {
    return subject.endsWith("_copy") ? subject : subject + "_copy";
  }

  /**
   * Generates a new series ID for copied series.
   */
  private String generateNewSeriesId(String originalSeriesId) {
    return calendar.utility.GenerateSeriesId.generateSeriesId();
  }

  /**
   * Calculates the target datetime based on day offset from source range.
   */
  private LocalDateTime calculateTargetDateTime(LocalDate eventDate, LocalTime eventTime,
                                                LocalDate rangeStart, LocalDate targetStart) {
    long dayOffset = ChronoUnit.DAYS.between(rangeStart, eventDate);
    if (dayOffset < 0) {
      dayOffset = 0;
    }

    LocalDate targetDate = targetStart.plusDays(dayOffset);
    return LocalDateTime.of(targetDate, eventTime);
  }

  /**
   * Groups events by series ID, separating standalone events.
   */
  private EventGroups groupEventsBySeries(List<Event> events) {
    Map<String, List<Event>> seriesGroups = new HashMap<>();
    List<Event> standaloneEvents = new ArrayList<>();

    for (Event event : events) {
      String seriesId = event.getEventSeriesId();
      if (seriesId != null && !seriesId.isEmpty()) {
        seriesGroups.computeIfAbsent(seriesId, k -> new ArrayList<>()).add(event);
      } else {
        standaloneEvents.add(event);
      }
    }

    return new EventGroups(seriesGroups, standaloneEvents);
  }

  /**
   * Attempts to save an event, handling conflicts.
   */

  private boolean saveEventIfNoConflict(Event event, Calendar targetCalendar) {
    if (!targetCalendar.hasConflict(event)) {
      targetCalendar.saveEvent(event);
      return true;
    } else {
      System.out.println("Conflict detected for event: " + event.getSubject());
      return false;
    }
  }

  /**
   * Gets all events that overlap with a date range.
   */
  private List<Event> getEventsInRange(LocalDate startDate, LocalDate endDate) {
    List<Event> eventsInRange = new ArrayList<>();

    for (Event event : calendarStore.values()) {
      if (eventOverlapsRange(event, startDate, endDate)) {
        eventsInRange.add(event);
      }
    }

    return eventsInRange;
  }

  /**
   * Checks if an event overlaps with a date range.
   */
  private boolean eventOverlapsRange(Event event, LocalDate rangeStart, LocalDate rangeEnd) {
    LocalDate eventStart = event.getStartDate();
    LocalDate eventEnd = event.getEndDate();
    return !(eventEnd.isBefore(rangeStart) || eventStart.isAfter(rangeEnd));
  }

  @Override
  public Event copyEvent(Event event, Calendar targetCalendar, LocalDateTime targetDateTime) {
    validateCopyParameters(event, targetCalendar, targetDateTime);

    Duration eventDuration = calculateEventDuration(event);
    ZoneId targetTimezone = targetCalendar.getCalendarTimeZone();
    LocalDateTime adjustedStart = adjustForTimezone(targetDateTime, targetTimezone);
    LocalDateTime adjustedEnd = adjustedStart.plus(eventDuration);

    Event copiedEvent = createEventCopy(event, targetCalendar,
        adjustedStart, adjustedEnd, null);

    if (saveEventIfNoConflict(copiedEvent, targetCalendar)) {
      notifyIfSeriesEventCopiedAsStandalone(event);
      return copiedEvent;
    }
    return null;
  }

  @Override
  public Event copyEventSingle(Event event, Calendar targetCalendar, LocalDateTime targetDateTime) {
    validateCopyParameters(event, targetCalendar, targetDateTime);

    Duration eventDuration = calculateEventDuration(event);
    ZoneId targetTimezone = targetCalendar.getCalendarTimeZone();
    LocalDateTime adjustedEnd = targetDateTime.plus(eventDuration);

    Event copiedEvent = createEventCopy(event, targetCalendar,
        targetDateTime, adjustedEnd, null);

    if (saveEventIfNoConflict(copiedEvent, targetCalendar)) {
      notifyIfSeriesEventCopiedAsStandalone(event);
      return copiedEvent;
    }
    return null;
  }

  @Override
  public List<Event> copyEventsOnDate(LocalDate sourceDate, Calendar targetCalendar,
                                      LocalDate targetDate) {
    validateDateCopyParameters(sourceDate, targetCalendar, targetDate);

    List<Event> eventsToCopy = getEventsForDate(sourceDate);
    EventGroups groups = groupEventsBySeries(eventsToCopy);
    List<Event> copiedEvents = new ArrayList<>();

    copiedEvents.addAll(copyStandaloneEvents(groups.standaloneEvents,
        targetCalendar, sourceDate, targetDate));

    copiedEvents.addAll(copySeriesGroups(groups.seriesGroups,
        targetCalendar, sourceDate, targetDate));

    System.out.println("Successfully copied " + copiedEvents.size() + " events.");
    return copiedEvents;
  }

  @Override
  public List<Event> copyEventsBetween(LocalDate startDate, LocalDate endDate,
                                       Calendar targetCalendar, LocalDate targetStartDate) {
    validateRangeCopyParameters(startDate, endDate, targetCalendar, targetStartDate);

    List<Event> eventsInRange = getEventsInRange(startDate, endDate);
    EventGroups groups = groupEventsBySeries(eventsInRange);
    List<Event> copiedEvents = new ArrayList<>();

    copiedEvents.addAll(copyStandaloneEventsInRange(groups.standaloneEvents,
        targetCalendar, startDate, targetStartDate));

    copiedEvents.addAll(copySeriesGroupsInRange(groups.seriesGroups,
        targetCalendar, startDate, targetStartDate));

    printCopySummary(copiedEvents.size(), groups);
    return copiedEvents;
  }

  @Override
  public boolean hasConflict(Event newEvent) {
    if (newEvent == null) {
      return false;
    }

    LocalDateTime newStart = LocalDateTime.of(newEvent.getStartDate(), newEvent.getStartTime());
    LocalDateTime newEnd = LocalDateTime.of(newEvent.getEndDate(), newEvent.getEndTime());

    return calendarStore.values().stream()
        .anyMatch(existing -> eventsOverlap(existing, newStart, newEnd));
  }


  /**
   * calculates event duration based on start time and end time.
   *
   * @param event to get the start time and end time from source event
   * @return duration of the event
   */

  private Duration calculateEventDuration(Event event) {
    LocalDateTime start = LocalDateTime.of(event.getStartDate(), event.getStartTime());
    LocalDateTime end = LocalDateTime.of(event.getEndDate(), event.getEndTime());
    return Duration.between(start, end);
  }

  /**
   * Validates single event copy parameters if any is null.
   *
   * @param event    event parameter to validate
   * @param target   target calendar parameter to validate
   * @param dateTime date time parameter to validate
   */

  private void validateCopyParameters(Event event, Calendar target, LocalDateTime dateTime) {
    if (event == null || target == null || dateTime == null) {
      throw new IllegalArgumentException(
          "Event, target calendar, and target date/time cannot be null");
    }
  }

  /**
   * Validates on date copy parameters if any is null.
   *
   * @param source     source local date to validate
   * @param target     target calendar to validate
   * @param targetDate target date to validate
   */

  private void validateDateCopyParameters(LocalDate source, Calendar target, LocalDate targetDate) {
    if (source == null || target == null || targetDate == null) {
      throw new IllegalArgumentException(
          "Source date, target calendar, and target date cannot be null");
    }
  }

  /**
   * Validates copy between date parameters to see if any is null.
   *
   * @param start       start date to validate
   * @param end         end date to validate
   * @param target      target calendar to validate
   * @param targetStart target start date to validate
   */

  private void validateRangeCopyParameters(LocalDate start, LocalDate end, Calendar target,
                                           LocalDate targetStart) {
    if (start == null || end == null || target == null || targetStart == null) {
      throw new IllegalArgumentException("All parameters must be non-null");
    }
    if (start.isAfter(end)) {
      throw new IllegalArgumentException("Start date must be before or equal to end date");
    }
  }

  /**
   * Prints in command when an event was part of the series but was copied as a single event.
   *
   * @param event the copied event
   */

  private void notifyIfSeriesEventCopiedAsStandalone(Event event) {
    if (event.getEventSeriesId() != null && !event.getEventSeriesId().isEmpty()) {
      System.out.println("Note: Original event was part of series. Copied as standalone event.");
    }
  }

  /**
   * Checks if the new start date times and end date times are overlapping with existing event.
   *
   * @param existing the existing event
   * @param newStart new start date time
   * @param newEnd   new end date time
   * @return boolean if overlaps or not
   */

  private boolean eventsOverlap(Event existing, LocalDateTime newStart, LocalDateTime newEnd) {
    LocalDateTime existingStart =
        LocalDateTime.of(existing.getStartDate(), existing.getStartTime());
    LocalDateTime existingEnd = LocalDateTime.of(existing.getEndDate(), existing.getEndTime());

    return !(newEnd.isBefore(existingStart)
        ||
        newEnd.equals(existingStart)
        ||
        newStart.isAfter(existingEnd)
        ||
        newStart.equals(existingEnd));
  }

  /**
   * Copies all the standalone events to target calendar on that date,
   * called from copy on date method.
   *
   * @param events     list of standalone events to copied to target calendar
   * @param target     target calendar to which the events need to copied.
   * @param sourceDate source date
   * @param targetDate target date to which the event need to be copied.
   * @return list of events copied
   */

  private List<Event> copyStandaloneEvents(List<Event> events, Calendar target,
                                           LocalDate sourceDate, LocalDate targetDate) {
    List<Event> copied = new ArrayList<>();
    for (Event event : events) {
      LocalDateTime targetDateTime = LocalDateTime.of(targetDate, event.getStartTime());
      Event copiedEvent = copyEvent(event, target, targetDateTime);
      if (copiedEvent != null) {
        copied.add(copiedEvent);
      }
    }
    return copied;
  }

  /**
   * Copies all the standalone events to target calendar between dates,
   * called from copy between dates method.
   *
   * @param events      list of standalone events which need to copied
   * @param target      target calendar to which the events need to copied
   * @param rangeStart  start date of the between range
   * @param targetStart end date of the between range
   * @return list of copied events
   */

  private List<Event> copyStandaloneEventsInRange(List<Event> events, Calendar target,
                                                  LocalDate rangeStart, LocalDate targetStart) {
    List<Event> copied = new ArrayList<>();
    for (Event event : events) {
      LocalDateTime targetDateTime = calculateTargetDateTime(
          event.getStartDate(), event.getStartTime(), rangeStart, targetStart);
      Event copiedEvent = copyEvent(event, target, targetDateTime);
      if (copiedEvent != null) {
        copied.add(copiedEvent);
      }
    }
    return copied;
  }

  /**
   * Copies events in series, when copy on date is called.
   *
   * @param seriesGroups Map of series and al the events in series.
   * @param target       target calendar to which the events need to copied
   * @param sourceDate   source date
   * @param targetDate   target date to which copied
   * @return list of copied events
   */

  private List<Event> copySeriesGroups(Map<String, List<Event>> seriesGroups, Calendar target,
                                       LocalDate sourceDate, LocalDate targetDate) {
    List<Event> copied = new ArrayList<>();

    for (Map.Entry<String, List<Event>> entry : seriesGroups.entrySet()) {
      List<Event> seriesEvents = entry.getValue();

      if (seriesEvents.size() == 1) {
        Event event = seriesEvents.get(0);
        LocalDateTime targetDateTime = LocalDateTime.of(targetDate, event.getStartTime());
        Event copiedEvent = copyEvent(event, target, targetDateTime);
        if (copiedEvent != null) {
          copied.add(copiedEvent);
        }
      } else {
        copied.addAll(copyMultipleSeriesEvents(seriesEvents, entry.getKey(),
            target, sourceDate, targetDate));
      }
    }
    return copied;
  }

  /**
   * Copies events in series, when copy between dates is called.
   *
   * @param seriesGroups Map of series and al the events in series.
   * @param target       target calendar to which the events need to copied
   * @param rangeStart   range start
   * @param targetStart  target start date
   * @return list of copied events
   */

  private List<Event> copySeriesGroupsInRange(Map<String, List<Event>> seriesGroups,
                                              Calendar target,
                                              LocalDate rangeStart, LocalDate targetStart) {
    List<Event> copied = new ArrayList<>();

    for (Map.Entry<String, List<Event>> entry : seriesGroups.entrySet()) {
      List<Event> seriesEvents = sortEventsByDate(entry.getValue());

      if (seriesEvents.size() == 1) {
        Event event = seriesEvents.get(0);
        LocalDateTime targetDateTime = calculateTargetDateTime(
            event.getStartDate(), event.getStartTime(), rangeStart, targetStart);
        Event copiedEvent = copyEvent(event, target, targetDateTime);
        if (copiedEvent != null) {
          copied.add(copiedEvent);
          System.out.println("Copied single event from series '" + entry.getKey()
              + "' as standalone");
        }
      } else {
        String newSeriesId = generateNewSeriesId(entry.getKey());
        System.out.println("Creating new series '"
            +
            newSeriesId
            +
            "' with " + seriesEvents.size()
            +
            " events");

        for (Event event : seriesEvents) {
          Event copiedEvent = copyEventWithSeries(event, target, rangeStart,
              targetStart, newSeriesId);
          if (copiedEvent != null) {
            copied.add(copiedEvent);
          }
        }
      }
    }
    return copied;
  }

  /**
   * Copies multiple events from the series.
   *
   * @param events           list of events which need to copied
   * @param originalSeriesId the original series id
   * @param target           target calendar to which the events need to copied
   * @param sourceDate       source date
   * @param targetDate       target date to which to be copied
   * @return list of copied events
   */

  private List<Event> copyMultipleSeriesEvents(List<Event> events, String originalSeriesId,
                                               Calendar target, LocalDate sourceDate,
                                               LocalDate targetDate) {
    List<Event> copied = new ArrayList<>();
    String newSeriesId = generateNewSeriesId(originalSeriesId);

    System.out.println(
        "Creating new series '" + newSeriesId + "' with " + events.size() + " events");

    for (Event event : events) {
      LocalDateTime targetDateTime = LocalDateTime.of(targetDate, event.getStartTime());
      Duration duration = calculateEventDuration(event);

      Event copiedEvent = createEventCopy(event, target, targetDateTime,
          targetDateTime.plus(duration), newSeriesId);
      if (saveEventIfNoConflict(copiedEvent, target)) {
        copied.add(copiedEvent);
      }
    }
    return copied;
  }

  /**
   * Copies the events within series, as new events in series are being created
   * with new series id.
   *
   * @param event       event to be copied
   * @param target      target calendar to which the events need to copied
   * @param rangeStart  start range date
   * @param targetStart target start range date
   * @param seriesId    series Id
   * @return event copied
   */

  private Event copyEventWithSeries(Event event, Calendar target, LocalDate rangeStart,
                                    LocalDate targetStart, String seriesId) {
    LocalDateTime targetDateTime = calculateTargetDateTime(
        event.getStartDate(), event.getStartTime(), rangeStart, targetStart);
    ZoneId targetTimeZone = target.getCalendarTimeZone();
    LocalDateTime adjustedStart = adjustForTimezone(targetDateTime, targetTimeZone);

    long daysDuration = ChronoUnit.DAYS.between(event.getStartDate(), event.getEndDate());
    LocalDateTime targetEnd = adjustedStart.plusDays(daysDuration)
        .with(event.getEndTime());
    LocalDateTime adjustedEnd = adjustForTimezone(targetEnd, targetTimeZone);

    Event copiedEvent = createEventCopy(event, target, adjustedStart, adjustedEnd, seriesId);
    return saveEventIfNoConflict(copiedEvent, target) ? copiedEvent : null;
  }

  /**
   * Helper method which sorts the events by date.
   *
   * @param events events list to be sorted
   * @return sorted events list
   */

  private List<Event> sortEventsByDate(List<Event> events) {
    events.sort((e1, e2) -> {
      int dateComp = e1.getStartDate().compareTo(e2.getStartDate());
      return dateComp != 0 ? dateComp : e1.getStartTime().compareTo(e2.getStartTime());
    });
    return events;
  }

  /**
   * To print the summary of all copied events in command UI.
   *
   * @param totalCopied total copied events count
   * @param groups      event groups to print standalone events copied and series events copied
   */

  private void printCopySummary(int totalCopied, EventGroups groups) {
    System.out.println("\nCopy Summary:");
    System.out.println("- Total events copied: " + totalCopied);
    System.out.println("- Standalone events: " + groups.standaloneEvents.size());
    System.out.println("- Series processed: " + groups.seriesGroups.size());
  }

  /**
   * Inner helper class to group events of one series together, and standalone events together.
   */

  private static class EventGroups {
    final Map<String, List<Event>> seriesGroups;
    final List<Event> standaloneEvents;

    /**
     * Constructor to construct the event grouping object.
     *
     * @param seriesGroups     Map of series and their events
     * @param standaloneEvents list of standalone events
     */

    EventGroups(Map<String, List<Event>> seriesGroups, List<Event> standaloneEvents) {
      this.seriesGroups = seriesGroups;
      this.standaloneEvents = standaloneEvents;
    }
  }
}

