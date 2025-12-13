package calendar.commandobject.editcommand;

import static calendar.commandobject.ExtractCommandHelper.editSingleEvent;
import static calendar.commandobject.ExtractCommandHelper.extractDateTimeString;
import static calendar.commandobject.ExtractCommandHelper.extractEventSubject;
import static calendar.commandobject.ExtractCommandHelper.extractNewPropertyValue;
import static calendar.commandobject.ExtractCommandHelper.extractProperty;
import static calendar.commandobject.ExtractCommandHelper.wouldCreateDuplicate;
import static calendar.utility.GenerateSeriesId.generateSeriesId;

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
 * Command object. handles the process of edit the event and all the events after it.
 */

public class EditEventsCommand implements Command {

  private final CalendarManager calendarManager;
  private final String command;

  /**
   * Constructs edit events command object.
   *
   * @param command         command in string format.
   * @param calendarManager calendarManager object
   */

  public EditEventsCommand(String command, CalendarManager calendarManager) {
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

      EventEditContext context = parseCommandContext();
      Event targetEvent = findTargetEvent(calendar, context);

      if (targetEvent == null) {
        System.out.println("Event not found with subject '" + context.subject
            + "' at " + context.dateTimeStr);
        return;
      }

      if (isStandaloneEvent(targetEvent)) {
        editStandaloneEvent(targetEvent, context);
        return;
      }

      editEventSeries(calendar, targetEvent, context);

    } catch (Exception e) {
      System.out.println("Error editing events: " + e.getMessage());
    }
  }

  /**
   * Gets the current active calendar.
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
   * Parses the command string to extract edit context.
   *
   * @return EventEditContext containing parsed command information
   */

  private EventEditContext parseCommandContext() {
    String dateTimeStr = extractDateTimeString(command);
    LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
    String subject = extractEventSubject(command);
    String property = extractProperty(command);
    String newValue = extractNewPropertyValue(command, property);

    return new EventEditContext(dateTimeStr, dateTime, subject, property, newValue);
  }

  /**
   * Finds the target event in the calendar based on the context.
   *
   * @param calendar the calendar to search in
   * @param context  the edit context containing search criteria
   * @return the target event or null if not found
   */

  private Event findTargetEvent(Calendar calendar, EventEditContext context) {
    List<Event> eventListOnDay = calendar.getEventsForDate(context.dateTime.toLocalDate());

    for (Event ev : eventListOnDay) {
      if (matchesTargetCriteria(ev, context)) {
        return ev;
      }
    }
    return null;
  }

  /**
   * Checks if an event matches the target criteria.
   *
   * @param event   the event to check
   * @param context the edit context with criteria
   * @return true if the event matches, false otherwise
   */

  private boolean matchesTargetCriteria(Event event, EventEditContext context) {
    return event.getSubject().equals(context.subject)
        && event.getStartDate().equals(context.dateTime.toLocalDate())
        && event.getStartTime().equals(context.dateTime.toLocalTime());
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
   * Edits a standalone event.
   *
   * @param event   the event to edit
   * @param context the edit context
   */

  private void editStandaloneEvent(Event event, EventEditContext context) {
    editSingleEvent(event, context.property, context.newValue);
    System.out.println("Event edited (single event, not part of a series)");
  }

  /**
   * Edits all events in a series from the target event onwards.
   *
   * @param calendar    the calendar containing the events
   * @param targetEvent the starting event for edits
   * @param context     the edit context
   */

  private void editEventSeries(Calendar calendar, Event targetEvent, EventEditContext context) {
    List<Event> eventsToEdit = collectEventsToEdit(calendar, targetEvent);

    if (!validateSeriesEdit(eventsToEdit, context, calendar)) {
      System.out.println("Edit failed: Would create duplicate event");
      return;
    }

    String newSeriesId = shouldGenerateNewSeriesId(context.property) ? generateSeriesId() : null;

    applyEditsToSeries(calendar, eventsToEdit, context, newSeriesId);

    System.out.println("Edited " + eventsToEdit.size() + " event(s) in the series");
  }

  /**
   * Collects all events in the series that should be edited.
   *
   * @param calendar    the calendar containing the events
   * @param targetEvent the starting event
   * @return list of events to edit
   */

  private List<Event> collectEventsToEdit(Calendar calendar, Event targetEvent) {
    List<Event> eventsToEdit = new ArrayList<>();
    Map<EventKey, Event> calendarStore = calendar.getCalendarStore();
    String originalSeriesId = targetEvent.getEventSeriesId();

    for (Event event : calendarStore.values()) {
      if (isPartOfSeriesFromTarget(event, originalSeriesId, targetEvent)) {
        eventsToEdit.add(event);
      }
    }

    return eventsToEdit;
  }

  /**
   * Checks if an event is part of the series from the target date onwards.
   *
   * @param event       the event to check
   * @param seriesId    the series ID to match
   * @param targetEvent the target event for date comparison
   * @return true if the event should be included, false otherwise
   */

  private boolean isPartOfSeriesFromTarget(Event event, String seriesId, Event targetEvent) {
    return event.getEventSeriesId() != null
        && event.getEventSeriesId().equals(seriesId)
        && !event.getStartDate().isBefore(targetEvent.getStartDate());
  }

  /**
   * Validates that the series edit won't create duplicates.
   *
   * @param eventsToEdit the events to be edited
   * @param context      the edit context
   * @param calendar     the calendar to check against
   * @return true if the edit is valid, false otherwise
   */

  private boolean validateSeriesEdit(List<Event> eventsToEdit, EventEditContext context,
                                     Calendar calendar) {
    for (Event event : eventsToEdit) {
      if (wouldCreateDuplicate(event, context.property, context.newValue, calendar)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Determines if a new series ID should be generated based on the property being edited.
   *
   * @param property the property being edited
   * @return true if a new series ID is needed, false otherwise
   */

  private boolean shouldGenerateNewSeriesId(String property) {
    return "start".equals(property);
  }

  /**
   * Applies edits to all events in the series.
   *
   * @param calendar     the calendar containing the events
   * @param eventsToEdit the events to edit
   * @param context      the edit context
   * @param newSeriesId  the new series ID (may be null)
   */

  private void applyEditsToSeries(Calendar calendar, List<Event> eventsToEdit,
                                  EventEditContext context, String newSeriesId) {
    for (Event event : eventsToEdit) {
      Event oldSnapshot = createEventSnapshot(calendar, event);

      editSingleEvent(event, context.property, context.newValue);

      if (newSeriesId != null) {
        event.modifySeriesId(newSeriesId);
      }

      calendar.updateEventKey(oldSnapshot, event);
    }
  }

  /**
   * Creates a snapshot of an event for updating the calendar store.
   *
   * @param calendar the calendar to use for creating the event
   * @param event    the event to snapshot
   * @return a new event instance with the same properties
   */

  private Event createEventSnapshot(Calendar calendar, Event event) {
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

  /**
   * Inner class to hold the context for an edit operation.
   */

  private static class EventEditContext {
    final String dateTimeStr;
    final LocalDateTime dateTime;
    final String subject;
    final String property;
    final String newValue;

    /**
     * Constructor for the context for an edit operation object.
     *
     * @param dateTimeStr Date time string
     * @param dateTime    Date time
     * @param subject     Subject
     * @param property    Property
     * @param newValue    New value
     */

    EventEditContext(String dateTimeStr, LocalDateTime dateTime, String subject,
                     String property, String newValue) {
      this.dateTimeStr = dateTimeStr;
      this.dateTime = dateTime;
      this.subject = subject;
      this.property = property;
      this.newValue = newValue;
    }
  }
}