package de.mirb.pg.java.j8f;

import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Java8LoggerTest {

  @Test
  public void logTest() {
    Logger log = Logger.getLogger(Java8LoggerTest.class.getName());
    log.setLevel(Level.INFO);
    log.severe("Start with the log messages (OLD)");
    //
    log.warning("Warn OLD message: " + computeStringAfter("Computed in: ", 2000));
    log.info("Info OLD message: " + computeStringAfter("Computed in: ", 2000));
    log.severe("this is the last OLD log message");
    //
    log.warning(() -> "Warn Java8 message: " + computeStringAfter("Computed in: ", 2000));
    log.info(() -> "Info Java8 message: " + computeStringAfter("Computed in: ", 2000));
    log.severe(() -> "this is the last and fast log message");
  }

  private String computeStringAfter(String content, int timeInMs) {
    try {
      Thread.sleep(timeInMs);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return content + timeInMs;
  }
}
