package calendar.view;

import calendar.controller.GuiControllerFeatures;
import calendar.model.modelinterfaces.Event;
import calendar.model.modelutility.Location;
import calendar.model.modelutility.Status;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * this is view impl.
 */
public class JframeView extends JFrame implements ViewInterface {
  private GuiControllerFeatures guiControllerFeatures;
  private JPanel toolbarPanel;
  private JButton navigateCalendarsButton;
  private JLabel monthYearLabel;
  private JTextField searchField;
  private JPanel leftPanel;
  private JPanel centerPanel;
  private JPanel calendarPanel;
  private JPanel daysHeaderPanel;
  private JPanel daysGridPanel;
  private JButton[][] dayButtons = new JButton[6][7];
  private String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
  private JPanel mainPanel;
  private LocalDate currentDate;
  private YearMonth currentYearMonth;
  private JPanel leftContentPanel;
  private JButton createCalendarBtn;
  private JButton createEventBtn;
  private JButton createEventDialogBtn;
  private JButton createCalendarDialogBoxBtn;
  private JDialog dialog;
  private JTextField calendarNameField;
  private JTextField timeZoneField;
  private JButton calButton;
  private JCheckBox recurringCheckBox;
  private JPanel recurringPanel;
  private JLabel currentCalendarLabel;
  JDialog editEventDialog;
  JDialog createEventDialog;
  private JTextField eventNameField;
  private JLabel startDateField;
  private JTextField startDateTextField;
  private JTextField statusField;
  private JTextField endDateField;
  private JTextField startTimeField;
  private JTextField endTimeField;
  private JTextField locationField;
  private JTextField descriptionField;
  private JTextField repeatsField;
  private JTextField untilDateField;
  private JTextField timesField;
  private JRadioButton untilRadio;
  private JRadioButton timesRadio;
  private JPanel buttonPanel;
  private JScrollPane scrollPane;
  private JButton eventButton;
  private JButton editEventBtn;
  private JLabel propertyLabel;
  private JButton selectedDayButton = null;

  /**
   * view constructor.
   */
  public JframeView() {
    super("Calendar View");
    setSize(1200, 700);
    setLocation(100, 100);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    currentDate = LocalDate.now();
    currentYearMonth = YearMonth.from(currentDate);

    this.setLayout(new BorderLayout());

    createToolbar();

    mainPanel = new JPanel(new BorderLayout());

    createLeftPanel();

    createCenterPanel();

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
    splitPane.setDividerLocation(250);
    splitPane.setResizeWeight(0.0);
    mainPanel.add(splitPane, BorderLayout.CENTER);

    this.add(toolbarPanel, BorderLayout.NORTH);
    this.add(mainPanel, BorderLayout.CENTER);

    populateCalendar();

    setVisible(true);
  }

  private void createToolbar() {
    toolbarPanel = new JPanel(new BorderLayout());
    toolbarPanel.setPreferredSize(new Dimension(0, 50));
    toolbarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    toolbarPanel.setBackground(new Color(245, 245, 245));

    navigateCalendarsButton = new JButton("Navigate calendars");
    navigateCalendarsButton.setText("Navigate calendars");
    navigateCalendarsButton.setPreferredSize(new Dimension(180, 30));
    navigateCalendarsButton.setFocusPainted(false);

    currentCalendarLabel = new JLabel("Current: ");
    currentCalendarLabel.setFont(new Font("Arial", Font.BOLD, 12));
    currentCalendarLabel.setForeground(new Color(70, 70, 70));
    currentCalendarLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

    JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
    centerPanel.setBackground(new Color(245, 245, 245));

    JButton prevButton = new JButton("Previous month");
    prevButton.setText("Previous month");
    prevButton.setPreferredSize(new Dimension(150, 30));
    prevButton.setFocusPainted(false);
    prevButton.setToolTipText("Previous Month");
    prevButton.setFont(new Font("Arial", Font.BOLD, 12));
    prevButton.addActionListener(e -> previousMonth());

    JButton nextButton = new JButton("Next month");
    nextButton.setText("Next month");
    nextButton.setPreferredSize(new Dimension(150, 30));
    nextButton.setFocusPainted(false);
    nextButton.setToolTipText("Next Month");
    nextButton.setFont(new Font("Arial", Font.BOLD, 12));
    nextButton.addActionListener(e -> nextMonth());

    monthYearLabel = new JLabel("Current month", SwingConstants.CENTER);
    monthYearLabel.setFont(new Font("Arial", Font.BOLD, 18));
    monthYearLabel.setPreferredSize(new Dimension(200, 30));

    centerPanel.add(prevButton);
    centerPanel.add(monthYearLabel);
    centerPanel.add(nextButton);
    centerPanel.add(Box.createHorizontalStrut(20));

    searchField = new JTextField();
    searchField.setPreferredSize(new Dimension(200, 30));
    searchField.setToolTipText("Search events...");

    JPanel leftToolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    leftToolPanel.setBackground(new Color(245, 245, 245));
    leftToolPanel.add(navigateCalendarsButton);
    leftToolPanel.add(currentCalendarLabel);

    JPanel rightToolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    rightToolPanel.setBackground(new Color(245, 245, 245));
    rightToolPanel.add(new JLabel("Search (by event name): "));
    rightToolPanel.add(searchField);

    toolbarPanel.add(leftToolPanel, BorderLayout.WEST);
    toolbarPanel.add(centerPanel, BorderLayout.CENTER);
    toolbarPanel.add(rightToolPanel, BorderLayout.EAST);
  }

  private void createLeftPanel() {
    leftPanel = new JPanel();
    leftPanel.setBackground(new Color(250, 250, 250));
    leftPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    leftPanel.setLayout(new BorderLayout());

    leftContentPanel = new JPanel();
    leftContentPanel.setLayout(new BoxLayout(leftContentPanel, BoxLayout.Y_AXIS));
    leftContentPanel.setBackground(new Color(250, 250, 250));

    JScrollPane scrollPane = new JScrollPane(leftContentPanel);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setBorder(null);
    scrollPane.getViewport().setBackground(new Color(250, 250, 250));

    leftPanel.add(scrollPane, BorderLayout.CENTER);

    createCalendarBtn = new JButton("+ Create New Calendar");
    createCalendarDialogBoxBtn = new JButton("Create");
  }

  private void createCenterPanel() {
    centerPanel = new JPanel(new BorderLayout());
    centerPanel.setBackground(Color.WHITE);

    calendarPanel = new JPanel(new BorderLayout());
    calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    createDaysHeader();

    createDaysGrid();

    calendarPanel.add(daysHeaderPanel, BorderLayout.NORTH);
    calendarPanel.add(daysGridPanel, BorderLayout.CENTER);

    centerPanel.add(calendarPanel, BorderLayout.CENTER);
  }

  private void createDaysHeader() {
    daysHeaderPanel = new JPanel(new GridLayout(1, 7));
    daysHeaderPanel.setPreferredSize(new Dimension(0, 30));
    daysHeaderPanel.setBackground(new Color(240, 240, 240));

    for (String day : daysOfWeek) {
      JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
      dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
      dayLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));
      daysHeaderPanel.add(dayLabel);
    }
  }

  private void createDaysGrid() {
    daysGridPanel = new JPanel(new GridLayout(6, 7, 1, 1)); // 6 weeks, 7 days, with gaps
    daysGridPanel.setBackground(Color.LIGHT_GRAY); // Gap color

    for (int week = 0; week < 6; week++) {
      for (int day = 0; day < 7; day++) {
        JButton dayButton = new JButton();
        dayButton.setBackground(Color.WHITE);
        dayButton.setFocusPainted(false);
        dayButton.setFont(new Font("Arial", Font.PLAIN, 14));
        dayButton.setVerticalAlignment(SwingConstants.TOP);
        dayButton.setHorizontalAlignment(SwingConstants.LEFT);
        dayButton.setMargin(new Insets(5, 5, 5, 5));


        dayButtons[week][day] = dayButton;
        daysGridPanel.add(dayButton);
      }
    }
  }

  private void populateCalendar() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
    monthYearLabel.setText(currentYearMonth.format(formatter));

    for (int week = 0; week < 6; week++) {
      for (int day = 0; day < 7; day++) {
        dayButtons[week][day].setText("");
        dayButtons[week][day].setEnabled(false);
        dayButtons[week][day].setBackground(Color.WHITE);
      }
    }

    LocalDate firstOfMonth = currentYearMonth.atDay(1);
    int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Convert to Sunday=0

    int daysInMonth = currentYearMonth.lengthOfMonth();

    int currentDay = 1;
    boolean started = false;

    for (int week = 0; week < 6; week++) {
      for (int day = 0; day < 7; day++) {
        if (!started && day == firstDayOfWeek) {
          started = true;
        }

        if (started && currentDay <= daysInMonth) {
          dayButtons[week][day].setText(String.valueOf(currentDay));
          dayButtons[week][day].setEnabled(true);

          if (currentDate.getYear() == currentYearMonth.getYear()
              && currentDate.getMonthValue() == currentYearMonth.getMonthValue()
              && currentDate.getDayOfMonth() == currentDay) {
            dayButtons[week][day].setBackground(new Color(200, 220, 255));
            dayButtons[week][day].setFont(new Font("Arial", Font.BOLD, 14));
            selectedDayButton = dayButtons[week][day];
          }

          currentDay++;
        }
      }
    }
  }

  private void handleDialogBoxEditEvent(JTextField newValueField,
                                        JComboBox<String> propertyComboBox) {
    editEventBtn.addActionListener(e -> {
      String selectedProperty = (String) propertyComboBox.getSelectedItem();
      String newValue = newValueField.getText().trim();

      if (newValue.isEmpty()) {
        JOptionPane.showMessageDialog(editEventDialog,
            "Please enter a new value",
            "Invalid Input",
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      Map<String, Object> eventData = eventParameterConstructor(selectedProperty, newValue);

      editEventDialog.dispose();

      if (guiControllerFeatures != null) {
        guiControllerFeatures.handleEditEventClicked(eventData);
      }
    });

  }

  private void handleDialogBoxCreateEvent() {
    createEventDialogBtn.addActionListener(e -> {









      boolean isRecurring = recurringCheckBox.isSelected();
      String repeatsPattern = "";
      String untilDate = "";
      int numberOfTimes = 0;

      if (isRecurring) {
        repeatsPattern = repeatsField.getText().trim();

        if (untilRadio.isSelected()) {
          untilDate = untilDateField.getText().trim();
        } else if (timesRadio.isSelected()) {
          String timesText = timesField.getText().trim();
          try {
            numberOfTimes = Integer.parseInt(timesText);
          } catch (NumberFormatException ex) {
            numberOfTimes = 0;
          }
        }
      }


      String eventName = eventNameField.getText().trim();
      if (eventName.isEmpty()) {
        JOptionPane.showMessageDialog(createEventDialog,
            "Please enter an event name",
            "Invalid Input",
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      Map<String, Object> eventData = new HashMap<>();
      eventData.put("name", eventName);
      String startDate = startDateField.getText();
      eventData.put("startDate", startDate);
      String endDate = endDateField.getText().trim();
      eventData.put("endDate", endDate);
      String startTime = startTimeField.getText().trim();
      eventData.put("startTime", startTime);
      String endTime = endTimeField.getText().trim();
      String location = locationField.getText().trim();
      eventData.put("endTime", endTime);
      eventData.put("location", location);
      String description = descriptionField.getText().trim();

      eventData.put("description", description);
      eventData.put("isRecurring", isRecurring);
      eventData.put("repeatsPattern", repeatsPattern);
      eventData.put("untilDate", untilDate);
      eventData.put("numberOfTimes", numberOfTimes);
      String status = statusField.getText().trim();
      eventData.put("status", status);
      String lastDate = untilDateField.getText().trim();
      eventData.put("lastDate", lastDate);


      createEventDialog.dispose();


      if (guiControllerFeatures != null) {
        guiControllerFeatures.handleDialogBoxCreateEvent(eventData);

      }

      System.out.println("Event Created: " + eventData);
    });

  }

  /**
   * test.
   *
   * @param mainPanel test
   */
  private void setupRecurringEventOptions(JPanel mainPanel) {

    recurringCheckBox = new JCheckBox("Recurring Event");
    recurringCheckBox.setFont(new Font("Arial", Font.BOLD, 12));
    recurringCheckBox.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(recurringCheckBox);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));


    recurringPanel = new JPanel();
    recurringPanel.setLayout(new BoxLayout(recurringPanel, BoxLayout.Y_AXIS));
    recurringPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));
    recurringPanel.setVisible(false);
  }

  /**
   * test.
   *
   * @param basicEventFeilds test
   */
  private void setupBasicEventFeilds(JPanel basicEventFeilds) {

    JPanel basicFieldsPanel = new JPanel(new GridLayout(8, 2, 10, 10));

    JLabel nameLabel = new JLabel("Event Name:");
    nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(nameLabel);

    eventNameField = new JTextField();
    eventNameField.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(eventNameField);

    JLabel statusLabel = new JLabel("Status:");
    statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(statusLabel);

    statusField = new JTextField("Private");
    statusField.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(statusField);

    JLabel dateLabel = new JLabel("Start Date (YYYY-MM-DD):");
    dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(dateLabel);

    startDateField = new JLabel(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    startDateField.setFont(new Font("Arial", Font.PLAIN, 12));
    startDateField.setText(getCurrentDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    basicFieldsPanel.add(startDateField);

    JLabel endDateLabel = new JLabel("End Date (YYYY-MM-DD):");
    endDateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(endDateLabel);

    endDateField = new JTextField(currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    endDateField.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(endDateField);

    JLabel startTimeLabel = new JLabel("Start Time (hh:mm):");
    startTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(startTimeLabel);

    startTimeField = new JTextField("08:00");
    startTimeField.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(startTimeField);

    JLabel endTimeLabel = new JLabel("End Time (hh:mm):");
    endTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(endTimeLabel);

    endTimeField = new JTextField("17:00");
    endTimeField.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(endTimeField);

    JLabel locationLabel = new JLabel("Location:");
    locationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(locationLabel);

    locationField = new JTextField("Online");
    locationField.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(locationField);

    JLabel descriptionLabel = new JLabel("Description:");
    descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(descriptionLabel);

    descriptionField = new JTextField("This is s sample description");
    descriptionField.setFont(new Font("Arial", Font.PLAIN, 12));
    basicFieldsPanel.add(descriptionField);


    basicEventFeilds.add(basicFieldsPanel);
    basicEventFeilds.add(Box.createRigidArea(new Dimension(0, 15)));

  }

  /**
   * test.
   */
  public void previousMonth() {
    currentYearMonth = currentYearMonth.minusMonths(1);
    selectedDayButton = null;
    populateCalendar();
  }

  /**
   * test.
   */
  public void nextMonth() {
    currentYearMonth = currentYearMonth.plusMonths(1);
    selectedDayButton = null;
    populateCalendar();
  }

  @Override
  public void showCalendars(List<String> calendars) {

    leftContentPanel.removeAll();

    if (calendars == null || calendars.isEmpty()) {
      JLabel noCalLabel = new JLabel("No calendars available");
      noCalLabel.setForeground(Color.GRAY);
      noCalLabel.setFont(new Font("Arial", Font.ITALIC, 12));
      noCalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
      leftContentPanel.add(noCalLabel);
    } else {
      for (String calendar : calendars) {
        calButton = new JButton(calendar);
        calButton.setText(calendar);
        calButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calButton.setMaximumSize(new Dimension(200, 35));
        calButton.setPreferredSize(new Dimension(180, 35));
        calButton.setMinimumSize(new Dimension(150, 35));

        calButton.setBackground(Color.WHITE);
        calButton.setFocusPainted(false);
        calButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        calButton.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent evt) {
            calButton.setBackground(new Color(245, 245, 245));
          }

          public void mouseExited(java.awt.event.MouseEvent evt) {
            calButton.setBackground(Color.WHITE);
          }
        });

        calButton.addActionListener(e -> {
          System.out.println("Calendar clicked: " + calendar);
          if (guiControllerFeatures != null) {
            guiControllerFeatures.handleCalendarClicked(calendar);
          }
        });
        leftContentPanel.add(calButton);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      }
    }

    leftContentPanel.add(Box.createVerticalGlue());


    createCalendarBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    createCalendarBtn.setMaximumSize(new Dimension(200, 35));
    createCalendarBtn.setPreferredSize(new Dimension(180, 35));
    createCalendarBtn.setBackground(new Color(70, 130, 180));
    createCalendarBtn.setForeground(Color.BLACK);
    createCalendarBtn.setFocusPainted(false);
    createCalendarBtn.setFont(new Font("Arial", Font.BOLD, 12));
    createCalendarBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    createCalendarBtn.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        createCalendarBtn.setBackground(new Color(100, 149, 237));
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        createCalendarBtn.setBackground(new Color(70, 130, 180));
      }
    });


    leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    leftContentPanel.add(createCalendarBtn);
    leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    leftContentPanel.revalidate();
    leftContentPanel.repaint();
    leftPanel.revalidate();
    leftPanel.repaint();
  }

  @Override
  public void showCreateCalendarDialogBox() {
    dialog = new JDialog(this, "Create New Calendar", true);
    dialog.setSize(400, 200);
    dialog.setLocationRelativeTo(this);
    dialog.setLayout(new BorderLayout());

    JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
    inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel nameLabel = new JLabel("Calendar Name:");
    nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    inputPanel.add(nameLabel);

    calendarNameField = new JTextField();
    calendarNameField.setFont(new Font("Arial", Font.PLAIN, 12));
    inputPanel.add(calendarNameField);

    JLabel timeZoneLabel = new JLabel("Time Zone (Area/Location):");
    timeZoneLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    inputPanel.add(timeZoneLabel);

    timeZoneField = new JTextField("America/New_York");
    timeZoneField.setFont(new Font("Arial", Font.PLAIN, 12));
    inputPanel.add(timeZoneField);

    inputPanel.add(new JLabel(""));
    JLabel exampleLabel = new JLabel("e.g., America/New_York, Europe/London, UTC");
    exampleLabel.setFont(new Font("Arial", Font.ITALIC, 10));
    exampleLabel.setForeground(Color.GRAY);
    inputPanel.add(exampleLabel);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));


    createCalendarDialogBoxBtn.setPreferredSize(new Dimension(100, 30));
    createCalendarDialogBoxBtn.setBackground(Color.BLACK);
    createCalendarDialogBoxBtn.setForeground(Color.BLACK);
    createCalendarDialogBoxBtn.setFont(new Font("Arial", Font.BOLD, 12));
    createCalendarDialogBoxBtn.setFocusPainted(false);


    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.setPreferredSize(new Dimension(100, 30));
    cancelBtn.setFont(new Font("Arial", Font.PLAIN, 12));
    cancelBtn.addActionListener(evt -> dialog.dispose());

    buttonPanel.add(createCalendarDialogBoxBtn);
    buttonPanel.add(cancelBtn);

    dialog.add(inputPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setVisible(true);
  }

  @Override
  public void showCalendarNameExistsError(String calendarName, boolean isEditing, String oldName) {
    JOptionPane.showMessageDialog(this,
        "A calendar with the name '" + calendarName
            + "' already exists. Please choose a different name.",
        "Calendar Already Exists",
        JOptionPane.ERROR_MESSAGE);

    if (isEditing) {
      String newName = JOptionPane.showInputDialog(
          this,
          "Enter new name for calendar '" + oldName + "':",
          "Edit Calendar Name",
          JOptionPane.PLAIN_MESSAGE
      );

      if (newName != null && !newName.trim().isEmpty()) {
        guiControllerFeatures.handleEditCalendarNameClicked(oldName, newName.trim());
      }
    } else {
      showCreateCalendarDialogBox();
    }
  }

  @Override
  public void showEventExistsError(Event event, boolean isEditing, boolean eventExists) {

    if (eventExists) {

      if (isEditing) {
        JOptionPane.showMessageDialog(this,
            "Event already exists.",
            "Event with the same properties exists.",
            JOptionPane.ERROR_MESSAGE);
        showEditEventDialogBox(event);
      } else {
        JOptionPane.showMessageDialog(this,
            "Event already exists.",
            "Event with the same properties exists.",
            JOptionPane.ERROR_MESSAGE);
        showCreateEventDialogBox();
      }

    } else {
      if (isEditing) {

        showEditEventDialogBox(event);

      }
      showCreateEventDialogBox();
    }
  }

  @Override
  public void showCalendarOptionsMenu(String calendarName, int x, int y) {


    JMenuItem useCalendarItem = new JMenuItem("Use Calendar");
    JMenuItem editNameItem = new JMenuItem("Edit Calendar Name");
    JMenuItem editTimezoneItem = new JMenuItem("Edit Calendar Timezone");

    useCalendarItem.addActionListener(e -> {
      if (guiControllerFeatures != null) {
        guiControllerFeatures.handleUseCalendarClicked(calendarName);
        String timezone = guiControllerFeatures.getCalendarTimezone(calendarName);
        updateCurrentCalendarLabel(calendarName, timezone);
      }
    });

    editNameItem.addActionListener(e -> {
      if (guiControllerFeatures != null) {
        String newName = JOptionPane.showInputDialog(
            this,
            "Enter new name for calendar '" + calendarName + "':",
            "Edit Calendar Name",
            JOptionPane.PLAIN_MESSAGE
        );

        if (newName != null && !newName.trim().isEmpty()) {
          guiControllerFeatures.handleEditCalendarNameClicked(calendarName, newName.trim());
          if (calendarName.equals(guiControllerFeatures.getCurrentCalendarName())) {
            String timezone = guiControllerFeatures.getCurrentCalendarTimezone();
            updateCurrentCalendarLabel(newName.trim(), timezone);
          }
        }
      }
    });

    editTimezoneItem.addActionListener(e -> {
      if (guiControllerFeatures != null) {
        String newTimezone = JOptionPane.showInputDialog(
            this,
            "Enter new timezone for calendar '" + calendarName + "' (e.g., America/New_York):",
            "Edit Calendar Timezone",
            JOptionPane.PLAIN_MESSAGE
        );

        if (newTimezone != null && !newTimezone.trim().isEmpty()) {
          try {
            ZoneId.of(newTimezone.trim());
            guiControllerFeatures.handleEditCalendarTimezoneClicked(calendarName,
                newTimezone.trim());
            if (calendarName.equals(guiControllerFeatures.getCurrentCalendarName())) {
              updateCurrentCalendarLabel(calendarName, newTimezone.trim());
            }
          } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Invalid timezone format. Please use format like "
                    + "'America/New_York' or 'UTC'.\nError: "
                    +
                    ex.getMessage(),
                "Invalid Timezone",
                JOptionPane.ERROR_MESSAGE
            );
          }
        }
      }
    });

    JPopupMenu popupMenu = new JPopupMenu();
    popupMenu.add(useCalendarItem);
    popupMenu.addSeparator();
    popupMenu.add(editNameItem);
    popupMenu.add(editTimezoneItem);

    popupMenu.show(leftContentPanel, x, y);
  }

  @Override
  public void updateCurrentCalendarLabel(String calendarName, String timezone) {
    if (calendarName != null && !calendarName.isEmpty()) {
      currentCalendarLabel.setText("Current: " + calendarName + " (" + timezone + ")");
    } else {
      currentCalendarLabel.setText("Current: None");
    }
  }

  @Override
  public void showEventsOfTheDay(List<Event> events) {

    leftContentPanel.removeAll();

    if (events == null || events.isEmpty()) {
      JLabel noEventsLabel = new JLabel("No events for this day");
      noEventsLabel.setForeground(Color.GRAY);
      noEventsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
      noEventsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
      leftContentPanel.add(noEventsLabel);
    } else {
      JLabel eventsHeaderLabel = new JLabel("Events for Selected Day");
      eventsHeaderLabel.setFont(new Font("Arial", Font.BOLD, 14));
      eventsHeaderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      leftContentPanel.add(eventsHeaderLabel);
      leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

      for (Event event : events) {
        eventButton = new JButton(event.toString());
        eventButton.setText(event.toString());
        eventButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventButton.setMaximumSize(new Dimension(200, 35));
        eventButton.setPreferredSize(new Dimension(180, 35));
        eventButton.setMinimumSize(new Dimension(150, 35));

        eventButton.setBackground(Color.BLUE);
        eventButton.setFocusPainted(false);
        eventButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        eventButton.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent evt) {
            eventButton.setBackground(new Color(245, 245, 245));
          }

          public void mouseExited(java.awt.event.MouseEvent evt) {
            eventButton.setBackground(Color.BLUE);
          }
        });

        eventButton.addActionListener(e -> {
          if (event.getEventSeriesId() != null) {

            showEditSeriesDialogBox(event);

          } else {
            showEditEventDialogBox(event);
          }
        });

        leftContentPanel.add(eventButton);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      }
    }

    leftContentPanel.add(Box.createVerticalGlue());

    createEventBtn = new JButton("+ Create New Event");
    createEventBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    createEventBtn.setMaximumSize(new Dimension(200, 35));
    createEventBtn.setPreferredSize(new Dimension(180, 35));
    createEventBtn.setBackground(Color.BLACK);
    createEventBtn.setFocusPainted(false);
    createEventBtn.setFont(new Font("Arial", Font.BOLD, 12));
    createEventBtn.setBorder(BorderFactory
        .createEmptyBorder(5, 10, 5, 10));

    createEventBtn.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseEntered(java.awt.event.MouseEvent evt) {
        createEventBtn.setBackground(new Color(100, 149, 237));
      }

      public void mouseExited(java.awt.event.MouseEvent evt) {
        createEventBtn.setBackground(new Color(70, 130, 180));
      }
    });

    createEventBtn.addActionListener(e -> {
      showCreateEventDialogBox();
    });

    leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    leftContentPanel.add(createEventBtn);
    leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));


    leftContentPanel.revalidate();
    leftContentPanel.repaint();
    leftPanel.revalidate();
    leftPanel.repaint();
  }

  @Override
  public LocalDate getCurrentDate() {

    return this.currentDate;

  }

  @Override
  public void showEditEventDialogBox(Event event) {

    eventNameField = new JTextField(event.getSubject());
    statusField = new JTextField(event.getStatus());
    startDateTextField = new JTextField(event.getStartDate().toString());
    endDateField = new JTextField(event.getEndDate().toString());
    startTimeField = new JTextField(event.getStartTime().toString());
    endTimeField = new JTextField(event.getEndTime().toString());
    locationField = new JTextField(event.getLocation().toString());
    descriptionField = new JTextField(event.getDescription());


    editEventDialog = new JDialog(this, "Edit Event", true);
    editEventDialog.setSize(500, 450);
    editEventDialog.setLocationRelativeTo(this);
    editEventDialog.setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


    JLabel titleLabel = new JLabel("Current Event Details");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(titleLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));


    JPanel currentValuesPanel = new JPanel(new GridLayout(8, 2, 10, 5));
    currentValuesPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));


    currentValuesPanel.add(new JLabel("Event Name:"));
    JLabel nameValueLabel = new JLabel(eventNameField.getText());
    nameValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(nameValueLabel);

    currentValuesPanel.add(new JLabel("Status:"));
    JLabel statusValueLabel = new JLabel(statusField.getText());
    statusValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(statusValueLabel);

    currentValuesPanel.add(new JLabel("Start Date:"));
    JLabel startDateValueLabel = new JLabel(startDateTextField.getText());
    startDateValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(startDateValueLabel);

    currentValuesPanel.add(new JLabel("End Date:"));
    JLabel endDateValueLabel = new JLabel(endDateField.getText());
    endDateValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(endDateValueLabel);

    currentValuesPanel.add(new JLabel("Start Time:"));
    JLabel startTimeValueLabel = new JLabel(startTimeField.getText());
    startTimeValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(startTimeValueLabel);

    currentValuesPanel.add(new JLabel("End Time:"));
    JLabel endTimeValueLabel = new JLabel(endTimeField.getText());
    endTimeValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(endTimeValueLabel);

    currentValuesPanel.add(new JLabel("Location:"));
    JLabel locationValueLabel = new JLabel(locationField.getText());
    locationValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(locationValueLabel);

    currentValuesPanel.add(new JLabel("Description:"));
    JLabel descriptionValueLabel = new JLabel(descriptionField.getText());
    descriptionValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(descriptionValueLabel);

    mainPanel.add(currentValuesPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));


    JSeparator separator = new JSeparator();
    mainPanel.add(separator);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));


    JLabel editLabel = new JLabel("Select Property to Edit:");
    editLabel.setFont(new Font("Arial", Font.BOLD, 12));
    editLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(editLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));


    JPanel editPanel = new JPanel(new GridLayout(2, 2, 10, 10));

    propertyLabel = new JLabel("Property:");
    editPanel.add(propertyLabel);

    String[] properties = {"Event Name", "Status", "Start Date", "End Date",
        "Start Time", "End Time", "Location", "Description"};
    JComboBox<String> propertyComboBox = new JComboBox<>(properties);
    editPanel.add(propertyComboBox);

    JLabel newValueLabel = new JLabel("New Value:");
    editPanel.add(newValueLabel);

    JTextField newValueField = new JTextField();
    editPanel.add(newValueField);

    mainPanel.add(editPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));




    editEventBtn = new JButton("Update");
    editEventBtn.setPreferredSize(new Dimension(100, 30));
    editEventBtn.setBackground(new Color(70, 130, 180));
    editEventBtn.setForeground(Color.BLACK);
    editEventBtn.setFont(new Font("Arial", Font.BOLD, 12));

    handleDialogBoxEditEvent(newValueField, propertyComboBox);


    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.setPreferredSize(new Dimension(100, 30));
    cancelBtn.setFont(new Font("Arial", Font.PLAIN, 12));
    cancelBtn.addActionListener(evt -> editEventDialog.dispose());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(editEventBtn);
    buttonPanel.add(cancelBtn);


    JScrollPane scrollPane = new JScrollPane(mainPanel);
    scrollPane.setBorder(null);

    editEventDialog.add(scrollPane, BorderLayout.CENTER);
    editEventDialog.add(buttonPanel, BorderLayout.SOUTH);

    editEventDialog.setVisible(true);
  }

  @Override
  public void showCreateEventDialogBox() {

    createEventDialog = new JDialog(this, "Create New Event", true);
    createEventDialog.setSize(450, 500);
    createEventDialog.setLocationRelativeTo(this);
    createEventDialog.setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    setupBasicEventFeilds(mainPanel);

    setupRecurringEventOptions(mainPanel);



    JLabel repeatsLabel = new JLabel("Repeats:");
    repeatsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    repeatsLabel.setPreferredSize(new Dimension(80, 25));
    repeatsField = new JTextField();
    repeatsField.setFont(new Font("Arial", Font.PLAIN, 12));
    repeatsField.setPreferredSize(new Dimension(250, 25));
    repeatsField.setToolTipText("e.g., Daily, Weekly, Monthly");
    JPanel repeatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    repeatsPanel.add(repeatsLabel);
    repeatsPanel.add(repeatsField);
    recurringPanel.add(repeatsPanel);

    recurringPanel.add(Box.createRigidArea(new Dimension(0, 10)));






    untilRadio = new JRadioButton("Until:");
    untilRadio.setFont(new Font("Arial", Font.PLAIN, 12));
    untilRadio.setPreferredSize(new Dimension(80, 25));
    untilDateField = new JTextField();
    untilDateField.setFont(new Font("Arial", Font.PLAIN, 12));
    untilDateField.setPreferredSize(new Dimension(150, 25));
    untilDateField.setToolTipText("YYYY-MM-DD");
    untilDateField.setEnabled(false);
    JPanel untilPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    untilPanel.add(untilRadio);
    untilPanel.add(untilDateField);
    recurringPanel.add(untilPanel);



    timesRadio = new JRadioButton("No. of times:");
    timesRadio.setFont(new Font("Arial", Font.PLAIN, 12));
    timesRadio.setPreferredSize(new Dimension(110, 25));
    timesField = new JTextField();
    timesField.setFont(new Font("Arial", Font.PLAIN, 12));
    timesField.setPreferredSize(new Dimension(80, 25));
    timesField.setEnabled(false);
    JPanel timesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    timesPanel.add(timesRadio);
    timesPanel.add(timesField);
    recurringPanel.add(timesPanel);

    ButtonGroup endConditionGroup = new ButtonGroup();
    endConditionGroup.add(untilRadio);
    endConditionGroup.add(timesRadio);


    untilRadio.addActionListener(e -> {
      untilDateField.setEnabled(true);
      timesField.setEnabled(false);
      timesField.setText("");
    });

    timesRadio.addActionListener(e -> {
      untilDateField.setEnabled(false);
      untilDateField.setText("");
      timesField.setEnabled(true);
    });

    mainPanel.add(recurringPanel);


    recurringCheckBox.addActionListener(e -> {
      boolean isChecked = recurringCheckBox.isSelected();
      recurringPanel.setVisible(isChecked);
      if (!isChecked) {

        repeatsField.setText("");
        untilDateField.setText("");
        timesField.setText("");
        endConditionGroup.clearSelection();
        untilDateField.setEnabled(false);
        timesField.setEnabled(false);
      }
      createEventDialog.pack();
      createEventDialog.setLocationRelativeTo(this);
    });




    createEventDialogBtn = new JButton("Create Event");
    createEventDialogBtn.setPreferredSize(new Dimension(120, 30));
    createEventDialogBtn.setBackground(Color.BLACK);
    createEventDialogBtn.setForeground(Color.BLACK);
    createEventDialogBtn.setFont(new Font("Arial", Font.BOLD, 12));
    createEventDialogBtn.setFocusPainted(false);

    handleDialogBoxCreateEvent();

    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.setPreferredSize(new Dimension(100, 30));
    cancelBtn.setFont(new Font("Arial", Font.PLAIN, 12));
    cancelBtn.addActionListener(evt -> createEventDialog.dispose());

    buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
    buttonPanel.add(createEventDialogBtn);
    buttonPanel.add(cancelBtn);

    scrollPane = new JScrollPane(mainPanel);
    scrollPane.setBorder(null);

    createEventDialog.add(scrollPane, BorderLayout.CENTER);
    createEventDialog.add(buttonPanel, BorderLayout.SOUTH);

    createEventDialog.setVisible(true);
  }

  @Override
  public void showSearchResults(List<Event> searchResults, String searchTerm) {
    leftContentPanel.removeAll();

    if (searchResults == null || searchResults.isEmpty()) {
      JLabel noResultsLabel = new JLabel("No events found for: " + searchTerm);
      noResultsLabel.setForeground(Color.GRAY);
      noResultsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
      noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftContentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
      leftContentPanel.add(noResultsLabel);
    } else {
      JLabel searchHeaderLabel = new JLabel("Search Results: " + searchTerm);
      searchHeaderLabel.setFont(new Font("Arial", Font.BOLD, 14));
      searchHeaderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
      leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
      leftContentPanel.add(searchHeaderLabel);
      leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

      for (Event event : searchResults) {
        JButton eventButton = new JButton(event.toString());
        eventButton.setText(event.toString());
        eventButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventButton.setMaximumSize(new Dimension(200, 35));
        eventButton.setPreferredSize(new Dimension(180, 35));
        eventButton.setMinimumSize(new Dimension(150, 35));

        eventButton.setBackground(Color.WHITE);
        eventButton.setFocusPainted(false);
        eventButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        eventButton.addMouseListener(new java.awt.event.MouseAdapter() {
          public void mouseEntered(java.awt.event.MouseEvent evt) {
            eventButton.setBackground(new Color(245, 245, 245));
          }

          public void mouseExited(java.awt.event.MouseEvent evt) {
            eventButton.setBackground(Color.WHITE);
          }
        });

        eventButton.addActionListener(e -> {
          showEditEventDialogBox(event);
        });

        leftContentPanel.add(eventButton);
        leftContentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
      }
    }

    leftContentPanel.add(Box.createVerticalGlue());

    JButton clearSearchBtn = new JButton("Clear Search");
    clearSearchBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
    clearSearchBtn.setMaximumSize(new Dimension(200, 35));
    clearSearchBtn.setPreferredSize(new Dimension(180, 35));
    clearSearchBtn.setBackground(new Color(100, 100, 100));
    clearSearchBtn.setForeground(Color.WHITE);
    clearSearchBtn.setFocusPainted(false);
    clearSearchBtn.setFont(new Font("Arial", Font.BOLD, 12));
    clearSearchBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

    clearSearchBtn.addActionListener(e -> {
      searchField.setText("");

      if (guiControllerFeatures != null) {
        guiControllerFeatures.handleDateClicked();
      }
    });

    leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    leftContentPanel.add(clearSearchBtn);
    leftContentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    leftContentPanel.revalidate();
    leftContentPanel.repaint();
  }

  @Override
  public void showEditSeriesDialogBox(Event event) {

    eventNameField = new JTextField(event.getSubject());
    statusField = new JTextField(event.getStatus());
    startDateTextField = new JTextField(event.getStartDate().toString());
    endDateField = new JTextField(event.getEndDate().toString());
    startTimeField = new JTextField(event.getStartTime().toString());
    endTimeField = new JTextField(event.getEndTime().toString());
    locationField = new JTextField(event.getLocation().toString());
    descriptionField = new JTextField(event.getDescription());

    editEventDialog = new JDialog(this, "Edit Series", true);
    editEventDialog.setSize(500, 520);
    editEventDialog.setLocationRelativeTo(this);
    editEventDialog.setLayout(new BorderLayout());

    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


    JLabel titleLabel = new JLabel("Current Event Details");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(titleLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));


    JPanel currentValuesPanel = new JPanel(new GridLayout(8, 2, 10, 5));
    currentValuesPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    currentValuesPanel.add(new JLabel("Event Name:"));
    JLabel nameValueLabel = new JLabel(eventNameField.getText());
    nameValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(nameValueLabel);

    currentValuesPanel.add(new JLabel("Status:"));
    JLabel statusValueLabel = new JLabel(statusField.getText());
    statusValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(statusValueLabel);

    currentValuesPanel.add(new JLabel("Start Date:"));
    JLabel startDateValueLabel = new JLabel(startDateTextField.getText());
    startDateValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(startDateValueLabel);

    currentValuesPanel.add(new JLabel("End Date:"));
    JLabel endDateValueLabel = new JLabel(endDateField.getText());
    endDateValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(endDateValueLabel);

    currentValuesPanel.add(new JLabel("Start Time:"));
    JLabel startTimeValueLabel = new JLabel(startTimeField.getText());
    startTimeValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(startTimeValueLabel);

    currentValuesPanel.add(new JLabel("End Time:"));
    JLabel endTimeValueLabel = new JLabel(endTimeField.getText());
    endTimeValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(endTimeValueLabel);

    currentValuesPanel.add(new JLabel("Location:"));
    JLabel locationValueLabel = new JLabel(locationField.getText());
    locationValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(locationValueLabel);

    currentValuesPanel.add(new JLabel("Description:"));
    JLabel descriptionValueLabel = new JLabel(descriptionField.getText());
    descriptionValueLabel.setFont(new Font("Arial", Font.PLAIN, 12));
    currentValuesPanel.add(descriptionValueLabel);

    mainPanel.add(currentValuesPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));


    JSeparator separator = new JSeparator();
    mainPanel.add(separator);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));


    JLabel editScopeLabel = new JLabel("Edit Scope:");
    editScopeLabel.setFont(new Font("Arial", Font.BOLD, 12));
    editScopeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    mainPanel.add(editScopeLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    JPanel radioPanel = new JPanel();
    radioPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
    radioPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
        BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    JRadioButton thisAndFutureRadio = new JRadioButton(
        "Make changes to this event and all events after this in the series");
    thisAndFutureRadio.setFont(new Font("Arial", Font.PLAIN, 12));
    thisAndFutureRadio.setSelected(true);

    JRadioButton allEventsRadio = new JRadioButton(
        "Make changes to all events in the series");
    allEventsRadio.setFont(new Font("Arial", Font.PLAIN, 12));

    ButtonGroup editScopeGroup = new ButtonGroup();
    editScopeGroup.add(thisAndFutureRadio);
    editScopeGroup.add(allEventsRadio);

    radioPanel.add(thisAndFutureRadio);
    radioPanel.add(Box.createRigidArea(new Dimension(0, 8)));
    radioPanel.add(allEventsRadio);

    mainPanel.add(radioPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));


    JLabel editLabel = new JLabel("Select Property to Edit:");
    editLabel.setFont(new Font("Arial", Font.BOLD, 12));
    editLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    mainPanel.add(editLabel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

    JPanel editPanel = new JPanel(new GridLayout(2, 2, 10, 10));

    propertyLabel = new JLabel("Property:");
    editPanel.add(propertyLabel);

    String[] properties = {"Event Name", "Status", "Start Date", "End Date",
        "Start Time", "End Time", "Location", "Description"};
    JComboBox<String> propertyComboBox = new JComboBox<>(properties);
    editPanel.add(propertyComboBox);

    JLabel newValueLabel = new JLabel("New Value:");
    editPanel.add(newValueLabel);

    JTextField newValueField = new JTextField();
    editPanel.add(newValueField);

    mainPanel.add(editPanel);
    mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));




    editEventBtn = new JButton("Update");
    editEventBtn.setPreferredSize(new Dimension(100, 30));
    editEventBtn.setBackground(new Color(70, 130, 180));
    editEventBtn.setForeground(Color.BLACK);
    editEventBtn.setFont(new Font("Arial", Font.BOLD, 12));


    handleDialogBoxEditSeriesEvent(newValueField, propertyComboBox, thisAndFutureRadio,
        allEventsRadio);

    JButton cancelBtn = new JButton("Cancel");
    cancelBtn.setPreferredSize(new Dimension(100, 30));
    cancelBtn.setFont(new Font("Arial", Font.PLAIN, 12));
    cancelBtn.addActionListener(evt -> editEventDialog.dispose());

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(editEventBtn);
    buttonPanel.add(cancelBtn);


    JScrollPane scrollPane = new JScrollPane(mainPanel);
    scrollPane.setBorder(null);

    editEventDialog.add(scrollPane, BorderLayout.CENTER);
    editEventDialog.add(buttonPanel, BorderLayout.SOUTH);

    editEventDialog.setVisible(true);
  }

  private void handleDialogBoxEditSeriesEvent(JTextField newValueField,
                                              JComboBox<String> propertyComboBox,
                                              JRadioButton thisAndFutureRadio,
                                              JRadioButton allEventsRadio) {
    editEventBtn.addActionListener(e -> {
      String selectedProperty = (String) propertyComboBox.getSelectedItem();
      String newValue = newValueField.getText().trim();

      if (newValue.isEmpty()) {
        JOptionPane.showMessageDialog(editEventDialog,
            "Please enter a new value",
            "Invalid Input",
            JOptionPane.WARNING_MESSAGE);
        return;
      }


      Map<String, Object> eventData = eventParameterConstructor(selectedProperty, newValue);

      eventData.put("editAllEvents", allEventsRadio.isSelected());
      eventData.put("editThisAndFuture", thisAndFutureRadio.isSelected());

      editEventDialog.dispose();

      if (guiControllerFeatures != null) {
        guiControllerFeatures.handleEditEventClicked(eventData);
      }
    });
  }

  @Override
  public void addHandlers(GuiControllerFeatures guiHandlers) {
    this.guiControllerFeatures = guiHandlers;

    for (int week = 0; week < 6; week++) {
      for (int day = 0; day < 7; day++) {

        dayButtons[week][day].addActionListener(e -> {
          JButton source = (JButton) e.getSource();
          String dayText = source.getText();
          if (!dayText.isEmpty()) {
            if (selectedDayButton != null && selectedDayButton != source) {
              selectedDayButton.setBackground(Color.WHITE);
              selectedDayButton.setFont(new Font("Arial", Font.PLAIN, 14));
            }

            source.setBackground(new Color(200, 220, 255));
            source.setFont(new Font("Arial", Font.BOLD, 14));
            selectedDayButton = source;

            int dayOfMonth = Integer.parseInt(dayText);
            this.currentDate = currentYearMonth.atDay(dayOfMonth);
            guiHandlers.handleDateClicked();
          }
        });

      }

    }


    navigateCalendarsButton.addActionListener(e -> {
      guiHandlers.handleNavigateCalendarClicked();
    });

    createCalendarBtn.addActionListener(e -> {
      guiHandlers.handleCreateCalendarClicked();
    });

    createCalendarDialogBoxBtn.addActionListener(evt -> {
      String calendarName = calendarNameField.getText().trim();
      String timeZoneInput = timeZoneField.getText().trim();

      if (calendarName.isEmpty()) {
        JOptionPane.showMessageDialog(dialog,
            "Please enter a calendar name",
            "Invalid Input",
            JOptionPane.WARNING_MESSAGE);
        return;
      }

      try {
        ZoneId.of(timeZoneInput);
        dialog.dispose();

        if (guiHandlers != null) {
          guiHandlers.handleCreateCalendarDialogBoxClicked(calendarName, timeZoneInput);
        }
      } catch (Exception ex) {
        JOptionPane.showMessageDialog(dialog,
            "Invalid timezone format. Please use format like 'America/New_York' or 'UTC'.\n"
                +
                "Error: " + ex.getMessage(),
            "Invalid Timezone",
            JOptionPane.ERROR_MESSAGE);
      }
    });

    searchField.addActionListener(e -> {
      String searchTerm = searchField.getText().trim();
      if (!searchTerm.isEmpty()) {
        guiHandlers.handleSearchEvents(searchTerm);
      }
    });

    String currentCalendar = guiHandlers.getCurrentCalendarName();
    String currentTimezone = guiHandlers.getCurrentCalendarTimezone();
    updateCurrentCalendarLabel(currentCalendar, currentTimezone);
  }

  private Map<String, Object> eventParameterConstructor(String selectedProperty, String newValue) {
    switch (selectedProperty) {
      case "Event Name":
        eventNameField.setText(newValue);
        break;
      case "Status":
        if (newValue.equalsIgnoreCase(Status.Private.name())
            ||
            newValue.equalsIgnoreCase(Status.Public.name())) {
          statusField.setText(newValue);
        } else {
          JOptionPane.showMessageDialog(editEventDialog,
              "Either 'Private' or 'Public' is allowed",
              "Invalid Input",
              JOptionPane.WARNING_MESSAGE);
        }
        break;
      case "Start Date":
        startDateField.setText(newValue);
        break;
      case "End Date":
        endDateField.setText(newValue);
        break;
      case "Start Time":
        startTimeField.setText(newValue);
        break;
      case "End Time":
        endTimeField.setText(newValue);
        break;
      case "Location":
        if (newValue.equalsIgnoreCase(Location.Online.name())
            ||
            newValue.equalsIgnoreCase(Location.Physical.name())) {
          locationField.setText(newValue);
        } else {
          JOptionPane.showMessageDialog(editEventDialog,
              "Either 'Physical' or 'Online' is allowed",
              "Invalid Input",
              JOptionPane.WARNING_MESSAGE);
        }
        break;
      case "Description":
        descriptionField.setText(newValue);
        break;
      default:
        System.out.println("");
    }


    Map<String, Object> eventData = new HashMap<>();
    eventData.put("name", eventNameField.getText().trim());
    eventData.put("startDate", startDateField.getText());
    eventData.put("endDate", endDateField.getText().trim());
    eventData.put("startTime", startTimeField.getText().trim());
    eventData.put("endTime", endTimeField.getText().trim());
    eventData.put("location", locationField.getText().trim());
    eventData.put("description", descriptionField.getText().trim());
    eventData.put("status", statusField.getText().trim());

    return eventData;
  }
}

