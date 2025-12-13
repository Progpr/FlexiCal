import calendar.controller.CalendarController;
import calendar.controller.ControllerImpl;
import calendar.controller.GuiControllerFeatures;
import calendar.controller.GuiControllerHandlers;
import calendar.model.modelimplementations.CalendarManagerImpl;
import calendar.view.JframeView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.ZoneId;
import javax.swing.SwingUtilities;

/**
 * Program runner.
 */
public class CalendarRunner {

  /**
   * The main method placeholder.
   */
  public static void main(String[] args) {
    String mode = "";
    String commandFile = "";
    ZoneId zoneId = ZoneId.of("America/New_York");

    if (args.length == 0) {
      launchGui();
      return;
    }

    if (args.length >= 2 && args[0].equals("--mode")) {
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("--mode") && i + 1 < args.length) {

          if (args[i + 1].equals("interactive")) {
            mode = "interactive";
            break;
          } else if (args[i + 1].equals("headless")) {
            mode = "headless";
            if (i + 2 >= args.length) {
              System.out.println("In headless mode you must specify a txt command file");
              return;
            }
            commandFile = args[i + 2];
            break;
          } else {
            System.out.println("Invalid mode. Usage:");
            System.out.println(
                "  java -jar calendar.jar  - Launch GUI mode");
            System.out.println(
                "  java -jar calendar.jar --mode interactive  - Launch interactive mode");
            System.out.println(
                "  java -jar calendar.jar --mode headless file.txt - Launch headless mode");
            return;
          }
        }
      }
    } else if (args.length == 1 || (args.length >= 1 && !args[0].equals("--mode"))) {
      System.out.println("Usage:");
      System.out.println("  java -jar calendar.jar           - Launch GUI mode");
      System.out.println(
          "  java -jar calendar.jar --mode interactive       - Launch interactive mode");
      System.out.println(
          "  java -jar calendar.jar --mode headless file.txt - Launch headless mode");
      return;
    }

    if (mode.equals("interactive") || mode.equals("headless")) {
      Readable in = new BufferedReader(new InputStreamReader(System.in));
      Appendable out = System.out;
      CalendarController controller =
          new ControllerImpl(new CalendarManagerImpl(), in, out, mode, commandFile);
      controller.go();
    } else if (mode.isEmpty()) {
      launchGui();
    }
  }

  /**
   * Launch the GUI mode.
   */
  private static void launchGui() {
    System.out.println("Launching GUI mode...");

    SwingUtilities.invokeLater(() -> {
      try {
        JframeView frame = new JframeView();

        CalendarManagerImpl model = new CalendarManagerImpl("default", "America/New_York");
        GuiControllerFeatures guiController = new GuiControllerHandlers(model);
        guiController.setView(frame);
        frame.setVisible(true);

      } catch (Exception e) {
        System.err.println("Error launching GUI: " + e.getMessage());
        e.printStackTrace();
        System.exit(1);
      }
    });
  }
}