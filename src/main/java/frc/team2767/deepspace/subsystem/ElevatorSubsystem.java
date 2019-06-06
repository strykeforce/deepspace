package frc.team2767.deepspace.subsystem;

import static com.ctre.phoenix.motorcontrol.ControlMode.PercentOutput;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
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
  private static final int STABLE_THRESH = 4;
  private static final String PREFS_NAME = "ElevatorSubsystem/Settings/";
  private static final VisionSubsystem VISION = Robot.VISION;
  public static double kCargoPickupPositionInches;
  public static double kHatchLowPositionInches;
  public static double kHatchMediumPositionInches;
  public static double kHatchHighPositionInches;
  public static double kStowPositionInches;
  public static double kCargoLowPositionInches;
  public static double kCargoMediumPositionInches;
  public static double kCargoPlayerPositionInches;
  public static double kCargoHighPositionInches;
  private static int kCloseEnoughTicks;
  private static int kAbsoluteZeroTicks;
  private static double kUpOutput;
  private static double kDownOutput;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final TalonSRX elevator = new TalonSRX(ID);
  private int setpointTicks;
  private int startPosition;
  private int stableCount;

  private int currentForwardLimit;
  private int currentReverseLimit;

  public ElevatorSubsystem() {

    currentForwardLimit = 0;
    currentReverseLimit = 0;

    elevatorPreferences();
    configTalon();
  }

  private void elevatorPreferences() {
    kUpOutput = getPreference("up_output", 0.35);
    kDownOutput = getPreference("down_output", -0.35);

    kCloseEnoughTicks = (int) getPreference("close_enough_ticks", 500);
    kAbsoluteZeroTicks = (int) getPreference("absolute_zero_ticks", 2064);

    kCargoPickupPositionInches = getPreference("cargo_pickup_in", 16.22);
    kHatchLowPositionInches = getPreference("hatch_low_in", 8.43);
    kHatchMediumPositionInches = getPreference("hatch_medium_in", 22.0);
    kHatchHighPositionInches = getPreference("hatch_high_in", 33.45);
    kStowPositionInches = getPreference("stow_in", 4.0);
    kCargoLowPositionInches = getPreference("cargo_low_in", 12.5);
    kCargoMediumPositionInches = getPreference("cargo_medium_in", 25.0);
    kCargoPlayerPositionInches = getPreference("cargo_player_in", 19.9);
    kCargoHighPositionInches = getPreference("cargo_high_in", 32.57);
  }

  private void configTalon() {
    TalonSRXConfiguration elevatorConfig = new TalonSRXConfiguration();
    elevatorConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;

    elevatorConfig.forwardSoftLimitEnable = true;
    elevatorConfig.reverseSoftLimitEnable = true;

    elevatorConfig.continuousCurrentLimit = 25;
    elevatorConfig.peakCurrentDuration = 40;
    elevatorConfig.peakCurrentLimit = 30;
    elevatorConfig.peakOutputForward = 1.0;
    elevatorConfig.peakOutputReverse = -1.0;
    elevatorConfig.slot0.kP = 1;
    elevatorConfig.slot0.kI = 0;
    elevatorConfig.slot0.kD = 40;
    elevatorConfig.slot0.kF = 0.25;
    elevatorConfig.slot0.integralZone = 0;
    elevatorConfig.velocityMeasurementWindow = 64; //
    elevatorConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms; //
    elevatorConfig.slot0.allowableClosedloopError = 0;
    elevatorConfig.forwardSoftLimitThreshold = 32_000; // FIXME different for comp/proto
    elevatorConfig.voltageCompSaturation = 12; //
    elevatorConfig.voltageMeasurementFilter = 32; //
    elevatorConfig.motionAcceleration = 8000;
    elevatorConfig.motionCruiseVelocity = 4000;

    // from the Safety Subsystem
    elevatorConfig.forwardSoftLimitThreshold = 33000;
    elevatorConfig.reverseSoftLimitThreshold = 0;

    elevator.configAllSettings(elevatorConfig);
    elevator.enableCurrentLimit(true);
    elevator.enableVoltageCompensation(true);

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(elevator, "Elevator"));
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

  public ElevatorLevel getElevatorLevel() {
    return VISION.elevatorLevel;
  }

  public List<TalonSRX> getTalons() {
    return List.of(elevator);
  }

  public void executePlan() {
    double newPosition =
        (elevator.getSelectedSensorPosition() + TICKS_OFFSET) / (double) TICKS_PER_INCH;

    switch (VISION.gamePiece) {
      case HATCH:
        switch (VISION.elevatorLevel) {
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
        switch (VISION.elevatorLevel) {
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
    if (Math.abs(error) > kCloseEnoughTicks) {
      stableCount = 0;
    } else {
      stableCount++;
    }

    if (stableCount > STABLE_THRESH) {
      logger.info("stableCount > {}", STABLE_THRESH);
      return true;
    }
    return false;
  }

  public boolean zero() {
    boolean didZero = false;
    if (elevator.getSensorCollection().isRevLimitSwitchClosed()) {

      int pulseWidthPosition = elevator.getSensorCollection().getPulseWidthPosition() & 0xFFF;

      logger.info(
          "Preferences zero = {} Absolute position = {}", kAbsoluteZeroTicks, pulseWidthPosition);

      int offset = (pulseWidthPosition) - kAbsoluteZeroTicks;

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

  public void openLoopMove(Direction direction) {
    if (direction == Direction.UP) {
      logger.info("moving up at {}", kUpOutput);
      elevator.set(ControlMode.PercentOutput, kUpOutput);
    } else if (direction == Direction.DOWN) {
      logger.info("moving down at {}", kDownOutput);
      elevator.set(ControlMode.PercentOutput, kDownOutput);
    }
  }

  public void openLoopMove(double output) {
    logger.info("moving at {}", output);
    elevator.set(ControlMode.PercentOutput, output);
  }

  public void stop() {
    logger.info("lift stop at elevatorPosition {}", elevator.getSelectedSensorPosition(0));
    elevator.set(PercentOutput, 0.0);
  }

  public void dump() {
    logger.info("elevator position in inches = {} ticks = {}", getPosition(), getTicks());
  }

  public double getPosition() {
    return (TICKS_OFFSET + elevator.getSelectedSensorPosition()) / (double) TICKS_PER_INCH;
  }

  @Override
  public int getTicks() {
    return elevator.getSelectedSensorPosition(0);
  }

  @SuppressWarnings("Duplicates")
  @Override
  public void setLimits(int forward, int reverse) {
    if (forward != currentForwardLimit) {
      elevator.configForwardSoftLimitThreshold(forward, 0);
      currentForwardLimit = forward;
    }

    if (reverse != currentReverseLimit) {
      elevator.configReverseSoftLimitThreshold(reverse, 0);
      currentReverseLimit = reverse;
    }
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

  @Override
  protected void initDefaultCommand() {}

  public enum Direction {
    UP,
    DOWN
  }
}
