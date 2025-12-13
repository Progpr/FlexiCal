package calendar.model.modelutility;

import calendar.model.modelinterfaces.Calendar;
import calendar.model.modelinterfaces.Event;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Class to export the calendar in either iCal or CSV format.
 *
 */

public class ExportCalendar {

  private String filename;
  private Calendar calendar;
  private String format;
  private String[] validFormats = {"ics", "csv"};

  /**
   * Constructor to construct the exportcalendar object.
   *
   * @param filename of the exported file
   * @param calendar calendar object
   */

  public ExportCalendar(String filename, Calendar calendar) {
    this.filename = filename;
    this.format = filename.substring(filename.lastIndexOf('.') + 1);
    this.calendar = calendar;
  }

  /**
   * chooses between if to export in ics or csv.
   */

  public void exportCalendar() {
    switch (format) {
      case "ics":
        exportToIcal();
        break;
      case "csv":
        exportToCsv();
        break;
      case "ical":
        exportToIcal();
        break;

      default:
        System.out.println("Invalid format");
        break;
    }
  }



  void exportToCsv() {

    try {
      FileWriter writer = new FileWriter(filename);
      writer.append("Subject, Start Date, End Date, Start Time, End Time");
      writer.append("\n");

      for (Map.Entry<EventKey, Event> entry : calendar.getCalendarStore().entrySet()) {
        writer.append(entry.getValue().getSubject() + "," + entry.getValue().getStartDate() + ","
            + entry.getValue().getEndDate() + "," + entry.getValue().getStartTime()
            + "," + entry.getValue().getEndTime());
        writer.append("\n");
      }
      writer.flush();
      System.out.println("Calendar successfully exported to CSV as: "
          + new File(filename).getAbsolutePath());

    } catch (IOException e) {
      System.out.println(e.getMessage());
      return;
    }
  }

  void exportToIcal() {
    try {
      FileWriter writer = new FileWriter(filename);

      writer.append("BEGIN:VCALENDAR\r\n");
      writer.append("VERSION:2.0\r\n");
      writer.append("PRODID:-//Edith//EN\r\n");

      for (Map.Entry<EventKey, Event> entry : calendar.getCalendarStore().entrySet()) {
        Event event = entry.getValue();

        writer.append("BEGIN:VEVENT\r\n");

        String startDate = event.getStartDate().toString().replace("-", "");
        String endDate = event.getEndDate().toString().replace("-", "");
        String startTime = event.getStartTime().toString().replace(":", "") + "00";
        String endTime = event.getEndTime().toString().replace(":", "") + "00";

        writer.append("UID:" + System.currentTimeMillis() + "-" + event.hashCode() + "\r\n");
        writer.append("DTSTAMP:" + startDate + "T" + startTime + "\r\n");
        writer.append("DTSTART:" + startDate + "T" + startTime + "\r\n");
        writer.append("DTEND:" + endDate + "T" + endTime + "\r\n");
        writer.append("SUMMARY: " + event.getSubject() + "\r\n");
        writer.append("DESCRIPTION: " + event.getDescription() + "\r\n");
        writer.append("LOCATION: " + event.getLocation() + "\r\n");
        writer.append("STATUS:" + event.getStatus() + "\r\n");
        writer.append("END:VEVENT\r\n");
      }

      writer.append("END:VCALENDAR\r\n");

      writer.flush();
      writer.close();
      System.out.println("Calendar successfully exported to iCal as: "
          + new File(filename).getAbsolutePath());

    } catch (IOException e) {
      System.out.println("Error exporting to iCal: " + e.getMessage());
      return;
    }
  }
}
