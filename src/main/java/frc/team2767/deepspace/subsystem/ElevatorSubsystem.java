package frc.team2767.deepspace.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.health.Zeroable;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class ElevatorSubsystem extends Subsystem implements Limitable, Zeroable {
  private static final int ID = 30;
  private static final int BACKUP = 2767;
  private static final int TICKS_PER_INCH = 1120;
  private static final int TICKS_OFFSET = 4 * TICKS_PER_INCH;
  public static double kCargoPickupPositionInches;
  public static double kHatchLowPositionInches;
  public static double kHatchMediumPositionInches;
  public static double kHatchHighPositionInches;
  public static double kStowPositionInches;
  public static double kCargoLowPositionInches;
  public static double kCargoMediumPositionInches;
  public static double kCargoPlayerPositionInches;
  public static double kCargoHighPositionInches;
  private final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private final int STABLE_THRESH = 4;
  private final String PREFS_NAME = "ElevatorSubsystem/Settings/";
  private final TalonSRX elevator = new TalonSRX(ID);
  private ElevatorLevel elevatorLevel = ElevatorLevel.NOTSET;
  private GamePiece currentGamepiece = GamePiece.NOTSET;
  private int setpointTicks;
  private double kUpOutput;
  private double kDownOutput;
  private int kCloseEnoughTicks;
  private int kAbsoluteZeroTicks;
  private int startPosition;
  private int stableCount;

  public ElevatorSubsystem() {

    if (elevator == null) {
      logger.error("Talon not present");
    }

    elevatorPreferences();
    configTalon();
    logger.info("");
  }

  private void elevatorPreferences() {
    kUpOutput = getPreference("up_output", 0.2);
    kDownOutput = getPreference("down_output", -0.2);

    kCloseEnoughTicks = (int) getPreference("close_enough_ticks", 500);
    kAbsoluteZeroTicks = (int) getPreference("absolute_zero_ticks", 1854);

    kCargoPickupPositionInches = getPreference("cargo_pickup_in", 16.22);
    kHatchLowPositionInches = getPreference("hatch_low_in", 8.5);
    kHatchMediumPositionInches = getPreference("hatch_medium_in", 24.0);
    kHatchHighPositionInches = getPreference("hatch_high_in", 32.5);
    kStowPositionInches = getPreference("stow_in", 4.0);
    kCargoLowPositionInches = getPreference("cargo_low_in", 11.0);
    kCargoMediumPositionInches = getPreference("cargo_medium_in", 22.0);
    kCargoPlayerPositionInches = getPreference("cargo_player_in", 19.9);
    kCargoHighPositionInches = getPreference("cargo_high_in", 32.5);
  }

  private void configTalon() {
    TalonSRXConfiguration elevatorConfig = new TalonSRXConfiguration();
    elevatorConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;

    elevatorConfig.forwardSoftLimitEnable = true;
    elevatorConfig.reverseSoftLimitEnable = true;

    elevatorConfig.continuousCurrentLimit = 20;
    elevatorConfig.peakCurrentDuration = 40;
    elevatorConfig.peakCurrentLimit = 25;
    elevatorConfig.peakOutputForward = 1.0;
    elevatorConfig.peakOutputReverse = -1.0;
    elevatorConfig.slot0.kP = 1;
    elevatorConfig.slot0.kI = 0;
    elevatorConfig.slot0.kD = 40;
    elevatorConfig.slot0.kF = 0.25;
    elevatorConfig.slot0.integralZone = 0;
    elevatorConfig.velocityMeasurementWindow = 64;
    elevatorConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    elevatorConfig.slot0.allowableClosedloopError = 0;
    elevatorConfig.forwardSoftLimitThreshold = 32_000; // FIXME different for comp/proto
    elevatorConfig.voltageCompSaturation = 12;
    elevatorConfig.voltageMeasurementFilter = 32;
    elevatorConfig.motionAcceleration = 8000;
    elevatorConfig.motionCruiseVelocity = 4000;

    elevator.configAllSettings(elevatorConfig);
    elevator.enableCurrentLimit(true);
    elevator.enableVoltageCompensation(true);

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(elevator, "Elevator"));
  }

  public List<TalonSRX> getTalons() {
    return List.of(elevator);
  }

  @SuppressWarnings("Duplicates")
  private double getPreference(String name, double defaultValue) {
    String prefName = PREFS_NAME + name;
    Preferences preferences = Preferences.getInstance();
    if (!preferences.containsKey(prefName)) {
      preferences.putDouble(prefName, defaultValue);
    }
    double pref = preferences.getDouble(prefName, BACKUP);
    logger.info("{}={}", name, pref);
    return pref;
  }

  @Override
  public int getTicks() {
    return elevator.getSelectedSensorPosition(0);
  }

  @Override
  public void setLimits(int forward, int reverse) {
    elevator.configForwardSoftLimitThreshold(forward, 0);
    elevator.configReverseSoftLimitThreshold(reverse, 0);
  }

  public double getPosition() {
    return (TICKS_OFFSET + elevator.getSelectedSensorPosition()) / TICKS_PER_INCH;
  }

  public void setPosition(double height) {
    setpointTicks = (int) (height * TICKS_PER_INCH) - TICKS_OFFSET;

    startPosition = elevator.getSelectedSensorPosition(0);
    logger.info(
        "setting elevatorPosition = {} ({} in.), starting at {}",
        setpointTicks,
        height,
        startPosition);

    elevator.set(ControlMode.MotionMagic, setpointTicks);
  }

  public void executePlan() {
    currentGamepiece = VISION.gamePiece;
    elevatorLevel = VISION.elevatorLevel;
    double newPosition = (elevator.getSelectedSensorPosition() + TICKS_OFFSET) / TICKS_PER_INCH;

    switch (currentGamepiece) {
      case HATCH:
        switch (elevatorLevel) {
          case ONE:
            newPosition = kHatchLowPositionInches;
            break;
          case TWO:
            newPosition = kHatchMediumPositionInches;
            break;
          case THREE:
            newPosition = kHatchHighPositionInches;
            break;
          case NOTSET:
            logger.warn("level not set");
        }
        break;
      case CARGO:
        switch (elevatorLevel) {
          case ONE:
            newPosition = kCargoLowPositionInches;
            break;
          case TWO:
            newPosition = kCargoMediumPositionInches;
            break;
          case THREE:
            newPosition = kCargoHighPositionInches;
            break;
          case NOTSET:
            logger.warn("level not set");
            break;
        }
        break;
      case NOTSET:
        logger.warn("no cargo set");
        break;
    }

    setPosition(newPosition);
  }

  @SuppressWarnings("Duplicates")
  public boolean onTarget() {
    int error = setpointTicks - elevator.getSelectedSensorPosition(0);
    if (Math.abs(error) > kCloseEnoughTicks) stableCount = 0;
    else stableCount++;
    if (stableCount > STABLE_THRESH) {
      logger.debug("stableCount > {}", STABLE_THRESH);
      return true;
    }
    return false;
  }

  public boolean zero() {
    boolean didZero = false;
    if (elevator.getSensorCollection().isRevLimitSwitchClosed()) {
      logger.info("Preferences zero = {}", kAbsoluteZeroTicks);
      logger.info("Relative position = {}", elevator.getSelectedSensorPosition());
      logger.info(
          "Absolute position = {}", elevator.getSensorCollection().getPulseWidthPosition() & 0xFFF);

      int offset =
          elevator.getSensorCollection().getPulseWidthPosition() & 0xFFF - kAbsoluteZeroTicks;
      elevator.setSelectedSensorPosition(offset);
      logger.info("New relative position = {}", offset);
      didZero = true;
    } else {
      logger.error("Elevator zero failed - elevator not at bottom");
      elevator.configPeakOutputForward(0, 0);
      elevator.configPeakOutputReverse(0, 0);
    }
    return didZero;
  }

  public void positionToZero() {
    elevator.configPeakCurrentLimit(2);
    elevator.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    logger.info("positioning to zero elevatorPosition");
    elevator.set(PercentOutput, kDownOutput);
  }

  public boolean onZero() {
    return elevator.getSensorCollection().isRevLimitSwitchClosed();
  }

  public void zeroPosition() {
    elevator.selectProfileSlot(0, 0);
    int zero = elevator.getSensorCollection().getPulseWidthPosition() & 0xFFF - kAbsoluteZeroTicks;
    elevator.setSelectedSensorPosition(zero);

    // elevator.set(ControlMode.MotionMagic, setpoint);
    logger.info("lift elevatorPosition zeroed to = {}", zero);

    elevator.configPeakCurrentLimit(25);
    elevator.enableCurrentLimit(true);
    elevator.configReverseLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
  }

  public void openLoopMove(Direction direction) {
    if (direction.equals(Direction.UP)) {
      logger.info("moving up at {}", kUpOutput);
      elevator.set(ControlMode.PercentOutput, kUpOutput);
      //      elevator.set(ControlMode.PercentOutput, 0.2);
    } else if (direction.equals(Direction.DOWN)) {
      logger.info("moving down at {}", kDownOutput);
      elevator.set(ControlMode.PercentOutput, kDownOutput);
      //      elevator.set(ControlMode.PercentOutput, -0.2);
    }
  }

  public void stop() {
    logger.info("lift stop at elevatorPosition {}", elevator.getSelectedSensorPosition(0));
    elevator.set(PercentOutput, 0.0);
  }

  public void dump() {
    logger.info("elevator position in inches = {}", getPosition());
    logger.info("elevator position in ticks = {}", getTicks());
  }

  @Override
  protected void initDefaultCommand() {}

  public enum Direction {
    UP,
    DOWN
  }
}
