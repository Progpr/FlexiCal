package calendar.commandobject.editcommand;

import static calendar.commandobject.ExtractCommandHelper.editSingleEvent;
import static calendar.commandobject.ExtractCommandHelper.extractDateTimeString;
import static calendar.commandobject.ExtractCommandHelper.extractEventSubject;
import static calendar.commandobject.ExtractCommandHelper.extractNewPropertyValue;
import static calendar.commandobject.ExtractCommandHelper.extractProperty;
import static calendar.commandobject.ExtractCommandHelper.wouldCreateDuplicate;

import calendar.commandobject.Command;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import calendar.model.modelutility.EventKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Command object to process, when entire series gets edited.
 */

public class EditSeriesCommand implements Command {
  private CalendarManager calendarManager;
  private String command;

  /**
   * Constructs edit series command object.
   *
   * @param command         command in string format.
   * @param calendarManager calendarManager object.
   */

  public EditSeriesCommand(String command, CalendarManager calendarManager) {
    this.command = command;
    this.calendarManager = calendarManager;
  }

  @Override
  public void execute() {
    try {
      Calendar calendar = getActiveCalendar();
      if (calendar == null) {
        return;
      }

      Event targetEvent = findTargetEvent(calendar);
      if (targetEvent == null) {
        return;
      }

      String property = extractProperty(command);
      String newValue = extractNewPropertyValue(command, property);

      if (isStandaloneEvent(targetEvent)) {
        handleStandaloneEvent(targetEvent, property, newValue, calendar);
        return;
      }

      handleSeriesEdit(targetEvent, property, newValue, calendar);

    } catch (Exception e) {
      System.out.println("Error editing series: " + e.getMessage());
    }
  }

  /**
   * Gets the currently active calendar.
   *
   * @return the active calendar or null if it doesn't exist
   */

  private Calendar getActiveCalendar() {
    Calendar calendar = calendarManager.getCalendar(calendarManager.getCurrentCalendarName());
    if (calendar == null) {
      System.out.println("Calendar Doesn't Exist");
    }
    return calendar;
  }

  /**
   * Finds the target event based on the command parameters.
   *
   * @param calendar the calendar to search in
   * @return the target event or null if not found
   */

  private Event findTargetEvent(Calendar calendar) {
    String dateTimeStr = extractDateTimeString(command);
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
    String subject = extractEventSubject(command);

    Event targetEvent = findEventByDateTimeAndSubject(calendar, dateTime, subject);

    if (targetEvent == null) {
      System.out.println("Event not found with subject '" + subject + "' at " + dateTimeStr);
    }

    return targetEvent;
  }

  /**
   * Finds an event by date, time, and subject.
   *
   * @param calendar the calendar to search in
   * @param dateTime the date and time of the event
   * @param subject  the subject of the event
   * @return the matching event or null if not found
   */

  private Event findEventByDateTimeAndSubject(Calendar calendar, LocalDateTime dateTime,
                                              String subject) {
    List<Event> eventListOnDay = calendar.getEventsForDate(dateTime.toLocalDate());

    for (Event ev : eventListOnDay) {
      if (isMatchingEvent(ev, subject, dateTime)) {
        return ev;
      }
    }
    return null;
  }

  /**
   * Checks if an event matches the given criteria.
   *
   * @param event    the event to check
   * @param subject  the expected subject
   * @param dateTime the expected date and time
   * @return true if the event matches, false otherwise
   */

  private boolean isMatchingEvent(Event event, String subject, LocalDateTime dateTime) {
    return event.getSubject().equals(subject)
        && event.getStartDate().equals(dateTime.toLocalDate())
        && event.getStartTime().equals(dateTime.toLocalTime());
  }

  /**
   * Checks if an event is standalone (not part of a series).
   *
   * @param event the event to check
   * @return true if the event is standalone, false otherwise
   */

  private boolean isStandaloneEvent(Event event) {
    return event.getEventSeriesId() == null;
  }

  /**
   * Handles editing of a standalone event.
   *
   * @param event    the event to edit
   * @param property the property to edit
   * @param newValue the new value for the property
   * @param calendar the calendar containing the event
   */

  private void handleStandaloneEvent(Event event, String property, String newValue,
                                     Calendar calendar) {
    if (wouldCreateDuplicate(event, property, newValue, calendar)) {
      System.out.println("Edit failed: Would create duplicate event");
      return;
    }
    editSingleEvent(event, property, newValue);
    System.out.println("Event edited (single event, not part of a series)");
  }

  /**
   * Handles editing of a series of events.
   *
   * @param targetEvent the target event in the series
   * @param property    the property to edit
   * @param newValue    the new value for the property
   * @param calendar    the calendar containing the events
   */

  private void handleSeriesEdit(Event targetEvent, String property, String newValue,
                                Calendar calendar) {
    List<Event> eventsToEdit = collectSeriesEvents(targetEvent, calendar);

    if (!validateSeriesEdit(eventsToEdit, property, newValue, calendar)) {
      return;
    }

    if (property.equals("start")) {
      editSeriesStartTime(eventsToEdit, newValue, calendar);
    } else {
      editSeriesProperty(eventsToEdit, property, newValue, calendar);
    }

    System.out.println("Edited " + eventsToEdit.size() + " event(s) in the series");
  }

  /**
   * Collects all events in the same series.
   *
   * @param targetEvent the target event
   * @param calendar    the calendar to search in
   * @return list of events in the same series
   */

  private List<Event> collectSeriesEvents(Event targetEvent, Calendar calendar) {
    List<Event> eventsToEdit = new ArrayList<>();
    Map<EventKey, Event> calendarStore = calendar.getCalendarStore();
    String originalSeriesId = targetEvent.getEventSeriesId();

    for (Event event : calendarStore.values()) {
      if (event.getEventSeriesId() != null
          && event.getEventSeriesId().equals(originalSeriesId)) {
        eventsToEdit.add(event);
      }
    }

    return eventsToEdit;
  }

  /**
   * Validates if the series edit would create any duplicates.
   *
   * @param eventsToEdit the events to be edited
   * @param property     the property being edited
   * @param newValue     the new value
   * @param calendar     the calendar
   * @return true if edit is valid, false if it would create duplicates
   */

  private boolean validateSeriesEdit(List<Event> eventsToEdit, String property,
                                     String newValue, Calendar calendar) {
    for (Event event : eventsToEdit) {
      if (wouldCreateDuplicate(event, property, newValue, calendar)) {
        System.out.println("Edit failed: Would create duplicate event");
        return false;
      }
    }
    return true;
  }

  /**
   * Edits the start time for all events in a series.
   *
   * @param eventsToEdit the events to edit
   * @param newValue     the new start time value
   * @param calendar     the calendar containing the events
   */

  private void editSeriesStartTime(List<Event> eventsToEdit, String newValue, Calendar calendar) {
    LocalDateTime newDateTime = LocalDateTime.parse(newValue);

    for (Event event : eventsToEdit) {
      Event oldSnapshot = createEventSnapshot(event);

      LocalDateTime newEventDateTime = LocalDateTime.of(
          event.getStartDate(),
          newDateTime.toLocalTime()
      );
      editSingleEvent(event, "start", newEventDateTime.toString());

      calendar.updateEventKey(oldSnapshot, event);
    }
  }

  /**
   * Edits a property for all events in a series.
   *
   * @param eventsToEdit the events to edit
   * @param property     the property to edit
   * @param newValue     the new value
   * @param calendar     the calendar containing the events
   */

  private void editSeriesProperty(List<Event> eventsToEdit, String property,
                                  String newValue, Calendar calendar) {
    for (Event event : eventsToEdit) {
      Event oldSnapshot = createEventSnapshot(event);
      editSingleEvent(event, property, newValue);
      calendar.updateEventKey(oldSnapshot, event);
    }
  }

  /**
   * Creates a snapshot of an event for update tracking.
   *
   * @param event the event to snapshot
   * @return a new event instance with the same properties
   */

  private Event createEventSnapshot(Event event) {
    Calendar calendar = calendarManager.getCalendar(calendarManager.getCurrentCalendarName());
    return calendar.createEvent(
        event.getSubject(),
        LocalDateTime.of(event.getStartDate(), event.getStartTime()),
        LocalDateTime.of(event.getEndDate(), event.getEndTime()),
        event.getEventSeriesId(),
        null,
        null,
        null
    );
  }
}