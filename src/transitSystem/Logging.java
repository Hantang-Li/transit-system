package transitSystem;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/** The logging class that keep track all the activities of the system. */
final class Logging {

  /** The logger of the class. */
  private static Logger log;

  /** The only logging object that write the log. */
  private static Logging instance = new Logging();

  /** Create a log to write all the information. */
  private Logging() {
    try {
      // get the logger transitSystem.
      log = Logger.getLogger("transitSystem");
      log.setLevel(Level.ALL);
      // create a new file handler.
      FileHandler fileHandler;
      fileHandler = new FileHandler("./log.txt", true);
      fileHandler.setLevel(Level.ALL);
      // Set the format of the file.
      fileHandler.setFormatter(
          new Formatter() {
            @Override
            public String format(LogRecord record) {
              return record.getLevel() + ": " + record.getMessage() + System.lineSeparator();
            }
          });
      log.addHandler(fileHandler);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get the logger of the class.
   */
  static Logging getLogger() {
    return instance;
  }

  /**
   * Log the message to the file.
   *
   * @param level The level of the message.
   * @param msg The message itself.
   */
  void log(Level level, String msg) {
    log.log(level, msg);
  }

  /**
   * Log the message to the file.
   *
   * @param level The level of the message.
   * @param msg The message itself.
   * @param e The exception.
   */
  void log(Level level, String msg, Throwable e) {
    log.log(level, msg, e);
  }
}
