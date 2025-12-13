package calendar.controller;

import static calendar.commandobject.ExtractCommandHelper.parseWeekdays;

import calendar.commandobject.editcommand.EditEventsCommand;
import calendar.commandobject.editcommand.EditSeriesCommand;
import calendar.model.modelimplementations.CalendarImpl;
import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.CalendarManager;
import calendar.model.modelinterfaces.Event;
import calendar.model.modelutility.EventKey;
import calendar.utility.GenerateSeriesId;
import calendar.view.ViewInterface;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Controller for GUI calendar implementation.
 */

public class GuiControllerHandlers implements GuiControllerFeatures {
  private ViewInterface view;
  private CalendarManager calendarManager;

  /**
   * Constructs the calendar GUI controller object with connection to model.
   *
   * @param calendarManager model object for connection
   */

  public GuiControllerHandlers(CalendarManager calendarManager) {
    this.calendarManager = calendarManager;
  }

  private EventKey getEventKey(Event event) {
    return new EventKey(
        event.getSubject(),
        event.getStartDate(),
        event.getEndDate(),
        event.getStartTime(),
        event.getEndTime()
    );

  }

  private void validateEvent(Event event, Calendar currentCalendar) {
    EventKey eventKey = getEventKey(event);
    if (currentCalendar.getCalendarStore().containsKey(eventKey)) {
      view.showEventExistsError(event, false, true);
    }
    currentCalendar.saveEvent(event);
  }

  @Override
  public void setView(ViewInterface v) {
    view = v;

    view.addHandlers(this);
  }

  @Override
  public void handleNavigateCalendarClicked() {
    Map<String, Calendar> calendarManagerStore = calendarManager.getCalendarManagerStore();
    List<String> calendarNames = new ArrayList<>();

    for (Map.Entry<String, Calendar> entry : calendarManagerStore.entrySet()) {
      calendarNames.add(entry.getKey());
    }
    view.showCalendars(calendarNames);
  }

  @Override
  public void handleDateClicked() {
    Calendar currentCalendar = calendarManager.getCalendar(
        calendarManager.getCurrentCalendarName());

    LocalDate currentDate = view.getCurrentDate();
    System.out.println(currentDate);

    List<Event> events = currentCalendar.getEventsForDate(currentDate);

    view.showEventsOfTheDay(events);

  }

  @Override
  public void handleCreateEventClicked() {
    view.showCreateEventDialogBox();

  }

  @Override
  public void handleDialogBoxCreateEvent(Map<String, Object> parameterMap) {

    Calendar currentCalendar = calendarManager
        .getCalendar(calendarManager.getCurrentCalendarName());

    if (Objects.isNull(parameterMap)) {
      handleCreateEventClicked();
    }

    LocalDateTime start =
        LocalDateTime.of(LocalDate.parse(parameterMap.get("startDate").toString()),
            LocalTime.parse(parameterMap.get("startTime").toString()));

    LocalDateTime end = LocalDateTime.of(LocalDate.parse(parameterMap.get("endDate").toString()),
        LocalTime.parse(parameterMap.get("endTime").toString()));


    if (parameterMap.get("isRecurring").equals(true)) {

      String seriesId = GenerateSeriesId.generateSeriesId();

      Event event =
          currentCalendar.createEvent(parameterMap.get("name").toString(), start, end, seriesId,
              parameterMap.get("location").toString(), parameterMap.get("status").toString(),
              parameterMap.get("description").toString());

      validateEvent(event, currentCalendar);

      if (!parameterMap.get("numberOfTimes").equals(0)) {

        currentCalendar.createSeries(event,
            (Integer) parameterMap.get("numberOfTimes"),
            parseWeekdays(
                parameterMap.get("repeatsPattern").toString()),
            null);

      } else {
        LocalDate lastDate = LocalDate.parse(parameterMap.get("lastDate").toString());
        currentCalendar.createSeries(event,
            0,
            parseWeekdays(parameterMap.get("repeatsPattern").toString()),
            lastDate);
      }

    } else {
      Event event =
          currentCalendar.createEvent(parameterMap.get("name").toString(), start, end, null,
              parameterMap.get("location").toString(), parameterMap.get("status").toString(),
              parameterMap.get("description").toString());

      validateEvent(event, currentCalendar);
    }

    handleDateClicked();
  }


  @Override
  public void handleEditEventClicked(Map<String, Object> updatedEventData) {
    Calendar currentCalendar = calendarManager
        .getCalendar(calendarManager.getCurrentCalendarName());


    Event event = currentCalendar.getEvent(
        updatedEventData.get("name").toString(),
        LocalDate.parse(updatedEventData.get("startDate").toString()),
        LocalDate.parse(updatedEventData.get("endDate").toString()),
        LocalTime.parse(updatedEventData.get("startTime").toString()),
        LocalTime.parse(updatedEventData.get("endTime").toString())
    );

    if (event == null) {
      LocalDate currentDate = view.getCurrentDate();
      List<Event> eventsForDay = currentCalendar.getEventsForDate(currentDate);

      if (!eventsForDay.isEmpty()) {
        event = eventsForDay.get(0);
      }
    }

    if (event == null) {
      view.showEventExistsError(event, true, false);
      return;
    }

    if (event.getEventSeriesId() != null
        && (updatedEventData.containsKey("editAllEvents")
        || updatedEventData.containsKey("editThisAndFuture"))) {
      handleEditSeriesClicked(updatedEventData);
      return;
    }

    event.modifySubject(updatedEventData.get("name").toString());
    event.modifyStatus(updatedEventData.get("status").toString());
    event.modifyStartDate(LocalDate.parse(updatedEventData.get("startDate").toString()));
    event.modifyEndDate(LocalDate.parse(updatedEventData.get("endDate").toString()));
    event.modifyLocation(updatedEventData.get("location").toString());
    event.modifyDescription(updatedEventData.get("description").toString());
    event.modifyStartTime(LocalTime.parse(updatedEventData.get("startTime").toString()));
    event.modifyEndTime(LocalTime.parse(updatedEventData.get("endTime").toString()));

    handleDateClicked();
  }

  @Override
  public void handleEditSeriesClicked(Map<String, Object> updatedEventData) {

    Calendar currentCalendar = calendarManager
        .getCalendar(calendarManager.getCurrentCalendarName());

    String eventName = updatedEventData.get("name").toString();
    LocalDate startDate = LocalDate.parse(updatedEventData.get("startDate").toString());
    LocalTime startTime = LocalTime.parse(updatedEventData.get("startTime").toString());


    Event event = currentCalendar.getEvent(
        eventName,
        startDate,
        LocalDate.parse(updatedEventData.get("endDate").toString()),
        startTime,
        LocalTime.parse(updatedEventData.get("endTime").toString())
    );

    if (event == null) {
      view.showEventExistsError(event, true, false);
      return;
    }

    if (event.getEventSeriesId() == null) {
      return;
    }





    LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);


    Map<String, String> propertyUpdates = new HashMap<>();


    propertyUpdates.put("subject", updatedEventData.get("name").toString());


    LocalDateTime newStartDateTime = LocalDateTime.of(
        LocalDate.parse(updatedEventData.get("startDate").toString()),
        LocalTime.parse(updatedEventData.get("startTime").toString())
    );
    propertyUpdates.put("start", newStartDateTime.toString());


    LocalDateTime newEndDateTime = LocalDateTime.of(
        LocalDate.parse(updatedEventData.get("endDate").toString()),
        LocalTime.parse(updatedEventData.get("endTime").toString())
    );
    propertyUpdates.put("end", newEndDateTime.toString());

    propertyUpdates.put("status", updatedEventData.get("status").toString());
    propertyUpdates.put("location", updatedEventData.get("location").toString());
    propertyUpdates.put("description", updatedEventData.get("description").toString());


    String dateTimeString = startDateTime.toString();
    for (Map.Entry<String, String> entry : propertyUpdates.entrySet()) {
      String property = entry.getKey();
      String newValue = entry.getValue();
      String command;

      boolean editAllEvents = updatedEventData.containsKey("editAllEvents")
          && (boolean) updatedEventData.get("editAllEvents");
      boolean editThisAndFuture = updatedEventData.containsKey("editThisAndFuture")
          && (boolean) updatedEventData.get("editThisAndFuture");

      if (editAllEvents) {
        command = "edit series " + property + " " + eventName
            + " from " + dateTimeString + " with " + newValue;

        System.out.println(command);
        EditSeriesCommand editSeriesCommand = new EditSeriesCommand(command, calendarManager);
        editSeriesCommand.execute();
      } else if (editThisAndFuture) {
        command = "edit events " + property + " " + eventName
            + " from " + dateTimeString + " with " + newValue;

        System.out.println(command);
        EditEventsCommand editEventsCommand = new EditEventsCommand(command, calendarManager);
        editEventsCommand.execute();
      }
    }

    handleDateClicked();
  }


  @Override
  public void handleCreateCalendarClicked() {
    view.showCreateCalendarDialogBox();
  }

  @Override
  public void handleCreateCalendarDialogBoxClicked(String calendarName, String timeZone) {
    if (calendarManager.getCalendarManagerStore().containsKey(calendarName)) {
      view.showCalendarNameExistsError(calendarName, false, null);
      return;
    }

    Calendar newlyCreatedCalendar = calendarManager.createCalendar(calendarName, timeZone);
    calendarManager.saveCalendar(newlyCreatedCalendar);
    handleNavigateCalendarClicked();
  }

  @Override
  public void handleCalendarClicked(String calendarName) {
    view.showCalendarOptionsMenu(calendarName, 0, 0);
  }

  @Override
  public String getCurrentCalendarName() {
    return calendarManager.getCurrentCalendarName();
  }

  @Override
  public String getCurrentCalendarTimezone() {
    Calendar currentCalendar =
        calendarManager.getCalendar(calendarManager.getCurrentCalendarName());
    return currentCalendar.getCalendarTimeZone().toString();
  }

  @Override
  public String getCalendarTimezone(String calendarName) {
    Calendar calendar = calendarManager.getCalendar(calendarName);
    if (calendar != null) {
      return calendar.getCalendarTimeZone().toString();
    }
    return "Unknown";
  }

  @Override
  public void handleUseCalendarClicked(String calendarName) {
    calendarManager.setCurrentCalendarNameAs(calendarName);
  }

  @Override
  public void handleEditCalendarNameClicked(String oldName, String newName) {
    if (!oldName.equals(newName)
        && calendarManager.getCalendarManagerStore().containsKey(newName)) {
      view.showCalendarNameExistsError(newName, true, oldName);
      return;
    }

    Calendar calendar = calendarManager.getCalendar(oldName);
    String newCalendarName = calendar.modifyName(newName).toString();
    Calendar entry = new CalendarImpl(calendar);
    calendarManager.getCalendarManagerStore().remove(oldName);
    calendarManager.getCalendarManagerStore().put(newName, entry);

    handleNavigateCalendarClicked();
  }

  @Override
  public void handleEditCalendarTimezoneClicked(String calendarName, String newTimezone) {

    Calendar calendar = calendarManager.getCalendar(calendarName);
    calendar.modifyTimezone(ZoneId.of(newTimezone));
  }

  @Override
  public void handleSearchEvents(String searchTerm) {
    Calendar currentCalendar = calendarManager.getCalendar(
        calendarManager.getCurrentCalendarName());

    List<Event> searchResults = new ArrayList<>();
    for (Event event : currentCalendar.getCalendarStore().values()) {
      if (event.getSubject().equalsIgnoreCase(searchTerm)) {
        searchResults.add(event);
      }
    }

    view.showSearchResults(searchResults, searchTerm);
  }
}