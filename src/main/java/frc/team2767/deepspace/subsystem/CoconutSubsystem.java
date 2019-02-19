package frc.team2767.deepspace.subsystem;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoconutSubsystem extends Subsystem {
  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private final Preferences preferences;
  private static final double BACKUP = 0.0;

  private double openLeft;
  private double openRight;
  private double closeLeft;
  private double closeRight;

  private final Servo leftServo = new Servo(0);
  private final Servo rightServo = new Servo(1);

  public CoconutSubsystem() {
    this.preferences = Preferences.getInstance();
    coconutPreferences();
    open();
  }

  private void coconutPreferences() {
    String PREFS_NAME = "CoconutSubsystem/Settings/";
    String OPEN_LEFT = PREFS_NAME + "open_left";
    if (!preferences.containsKey(OPEN_LEFT)) preferences.putDouble(OPEN_LEFT, 0.52);

    String OPEN_RIGHT = PREFS_NAME + "open_right";
    if (!preferences.containsKey(OPEN_RIGHT)) preferences.putDouble(OPEN_RIGHT, 0.74);

    String CLOSE_LEFT = PREFS_NAME + "close_left";
    if (!preferences.containsKey(CLOSE_LEFT)) preferences.putDouble(CLOSE_LEFT, 0.6);

    String CLOSE_RIGHT = PREFS_NAME + "close_right";
    if (!preferences.containsKey(CLOSE_RIGHT)) preferences.putDouble(CLOSE_RIGHT, 0.62);

    openLeft = preferences.getDouble(OPEN_LEFT, BACKUP);
    openRight = preferences.getDouble(OPEN_RIGHT, BACKUP);
    closeLeft = preferences.getDouble(CLOSE_LEFT, BACKUP);
    closeRight = preferences.getDouble(CLOSE_RIGHT, BACKUP);

    logger.info("openLeft: {}", openLeft);
    logger.info("openRight: {}", openRight);
    logger.info("closeLeft: {}", closeLeft);
    logger.info("closeRight: {}", closeRight);
  }

  public void open() {
    leftServo.set(openLeft);
    rightServo.set(openRight);
  }

  public void close() {
    leftServo.set(closeLeft);
    rightServo.set(closeRight);
  }

  @Override
  protected void initDefaultCommand() {}
}
