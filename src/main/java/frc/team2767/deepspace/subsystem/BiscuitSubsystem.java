package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.health.Zeroable;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class BiscuitSubsystem extends Subsystem implements Limitable, Zeroable, Item {

  public static final double PLACE_RIGHT = 45.0;
  public static final double PLACE_LEFT = -45.0;
  public static final double PICKUP_RIGHT = 65.0;
  public static final double PICKUP_LEFT = -65.0;
  public static final int kSlowAccel = 2_500;
  public static final int kFastAccel = 16_000;
  private static final String PREFS = "BiscuitSubsystem/Position/";
  private static final int BACKUP = 2767;
  private static final int BISCUIT_ID = 40;
  private static final double TICKS_PER_DEGREE = 34.1;
  private static final double TICKS_OFFSET = 0;
  private static final int KRAKEN_ACTUATE_ID = 5;
  private static final int KRAKEN_LOCK_ID = 6;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  public static double kUpPositionDeg;
  public static double kLeftPositionDeg;
  public static double kRightPositionDeg;
  public static double kBackStopLeftPositionDeg;
  public static double kBackStopRightPositionDeg;
  public static double kTiltUpLeftPositionDeg;
  public static double kTiltUpRightPositionDeg;
  public static double kDownPosition = 179;
  private static double kDownRightPositionDeg;
  private static double kDownLeftPositionDeg;
  private static double kLeft270PositionDeg;
  private static double kRight270PositionDeg;
  private static double kLeft270TiltPositionDeg;
  private static double kRight270TiltPositionDeg;
  private static int kCloseEnoughTicks;
  private static int kAbsoluteZeroTicks;
  private static double kKrakenRelease;
  private static double kKrakenHide;
  private static double kKrakenLock;
  private static double kKrakenUnlock;
  private static double kCompressionLimit;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double targetBiscuitPositionDeg = 0;
  private TalonSRX biscuit = new TalonSRX(BISCUIT_ID);
  private Servo krakenActuate = new Servo(KRAKEN_ACTUATE_ID);
  private Servo krakenLock = new Servo(KRAKEN_LOCK_ID);
  private int setpointTicks;

  private int graphCount;

  private int currentForwardLimit;
  private int currentReverseLimit;

  public BiscuitSubsystem() {

    currentForwardLimit = 0;
    currentReverseLimit = 0;

    biscuitPreferences();
    configTalon();
    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);
  }

  private void biscuitPreferences() {
    // ticks
    kAbsoluteZeroTicks = (int) getPreference("absolute_zero_ticks", 2450);
    kCloseEnoughTicks = (int) getPreference("close_enough_ticks", 50);

    // degrees
    kUpPositionDeg = getPreference("up_deg", 0);
    kDownRightPositionDeg = getPreference("down_R_deg", 179);
    kDownLeftPositionDeg = getPreference("down_L_deg", -179);
    kLeftPositionDeg = getPreference("left_deg", -86.5);
    kRightPositionDeg = getPreference("right_deg", 87);
    kBackStopLeftPositionDeg = getPreference("backstop_L_deg", -135);
    kBackStopRightPositionDeg = getPreference("backstop_R_deg", 135);
    kTiltUpLeftPositionDeg = getPreference("tilt_up_L_deg", -65);
    kTiltUpRightPositionDeg = getPreference("tilt_up_R_deg", 64);
    kLeft270PositionDeg = getPreference("left_270_deg", 270);
    kRight270PositionDeg = getPreference("right_270_deg", -270);
    kLeft270TiltPositionDeg = getPreference("tilt_270_L_deg", 295);
    kRight270TiltPositionDeg = getPreference("tilt_270_R_deg", -295);
    kKrakenRelease = getPreference("kraken_release", 0.0);
    kKrakenHide = getPreference("kraken_hide", 0.9);
    kKrakenLock = getPreference("kraken_lock", 0.0);
    kKrakenUnlock = getPreference("kraken_unlock", 0.0);
    kCompressionLimit = getPreference("compression_limit", 215.0);
  }

  private void configTalon() {
    TalonSRXConfiguration biscuitConfig = new TalonSRXConfiguration();
    biscuitConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    biscuitConfig.forwardSoftLimitEnable = true;
    biscuitConfig.reverseSoftLimitEnable = true;
    biscuitConfig.peakOutputForward = 1.0;
    biscuitConfig.peakOutputReverse = -1.0;
    biscuitConfig.slot0.kP = 1.0;
    biscuitConfig.slot0.kI = 0.0;
    biscuitConfig.slot0.kD = 0.0;
    biscuitConfig.slot0.kF = 0.65;
    biscuitConfig.slot0.allowableClosedloopError = 0;
    biscuitConfig.slot0.integralZone = 0;
    biscuitConfig.peakCurrentDuration = 40;
    biscuitConfig.peakCurrentLimit = 25;
    biscuitConfig.continuousCurrentLimit = 20;
    biscuitConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    biscuitConfig.velocityMeasurementWindow = 64;
    biscuitConfig.voltageCompSaturation = 12;
    biscuitConfig.voltageMeasurementFilter = 32;
    biscuitConfig.motionCruiseVelocity = 1_000; // 1000
    biscuitConfig.motionAcceleration = kSlowAccel; // 2500 16000
    biscuitConfig.clearPositionOnLimitF = false;
    biscuitConfig.clearPositionOnLimitR = false;
    biscuitConfig.clearPositionOnQuadIdx = false;

    // from the Safety Subsystem
    biscuitConfig.forwardSoftLimitThreshold = 1000;
    biscuitConfig.reverseSoftLimitThreshold = -1000;

    biscuit.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, 5);
    biscuit.enableCurrentLimit(true);
    biscuit.enableVoltageCompensation(true);
    biscuit.configAllSettings(biscuitConfig);

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(biscuit, "Biscuit"));
    telemetryService.register(this);
  }

  @SuppressWarnings("Duplicates")
  private double getPreference(String name, double defaultValue) {
    String prefName = PREFS + name;
    Preferences preferences = Preferences.getInstance();
    if (!preferences.containsKey(prefName)) {
      preferences.putDouble(prefName, defaultValue);
    }
    double pref = preferences.getDouble(prefName, BACKUP);
    logger.info("{}={}", name, pref);
    return pref;
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public String toString() {
    return "states="
        + "\n\t"
        + "current game piece = "
        + VISION.gamePiece.name()
        + "\n\t"
        + "target level = "
        + VISION.elevatorLevel.name()
        + "\n\t"
        + "target direction = "
        + VISION.direction.name()
        + "\n\t"
        + "target position = "
        + targetBiscuitPositionDeg;
  }

  public List<TalonSRX> getTalons() {
    return List.of(biscuit);
  }

  public boolean zero() {
    boolean didZero = false;
    if (!biscuit.getSensorCollection().isFwdLimitSwitchClosed()) {
      int absPos = biscuit.getSensorCollection().getPulseWidthPosition() & 0xFFF;
      logger.info("Preferences zero = {} Absolute position = {}", kAbsoluteZeroTicks, absPos);

      // appears backwards because absolute and relative encoders are out-of-phase in hardware
      int offset = kAbsoluteZeroTicks - absPos;

      biscuit.setSelectedSensorPosition(offset);
      logger.info("New relative position = {}", offset);
      didZero = true;
    } else {
      logger.error("Biscuit zero failed - biscuit not vertical");
      biscuit.configPeakOutputForward(0, 0);
      biscuit.configPeakOutputReverse(0, 0);
    }

    biscuit.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.Disabled);

    return didZero;
  }

  @SuppressWarnings("Duplicates")
  public void executePlan() {
    targetBiscuitPositionDeg = selectAngle();

    if (targetBiscuitPositionDeg != 2767) {
      logger.debug(
          "plan running: level = {} gp = {} action = {}",
          VISION.elevatorLevel,
          VISION.gamePiece,
          VISION.action);

      setPosition(targetBiscuitPositionDeg);
    } else {
      logger.warn("Biscuit execution failed");
    }
  }

  @SuppressWarnings("Duplicates")
  public double selectAngle() {
    if (VISION.action == Action.PLACE
        && VISION.gamePiece == GamePiece.CARGO
        && VISION.elevatorLevel == ElevatorLevel.THREE) {
      if (VISION.direction == FieldDirection.LEFT) {
        logger.debug("tilt up left");
        return kTiltUpLeftPositionDeg;
      }
      if (VISION.direction == FieldDirection.RIGHT) {
        logger.debug("tilt up right");
        return kTiltUpRightPositionDeg;
      }
      logger.warn("Direction not set");
      return 2767.0;

    } else if (VISION.action == Action.PLACE) {
      if (VISION.direction == FieldDirection.LEFT) {
        logger.debug("left");
        return kLeftPositionDeg;
      }

      if (VISION.direction == FieldDirection.RIGHT) {
        logger.debug("right");
        return kRightPositionDeg;
      }

      logger.warn("Direction not set");
      return 2767.0;
    }

    if (VISION.action == Action.PICKUP) {
      double bearing = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);
      if (bearing <= 0) {
        if (VISION.gamePiece == GamePiece.CARGO) {
          logger.debug("backstop right");
          return kBackStopRightPositionDeg;
        }

        if (VISION.gamePiece == GamePiece.HATCH) {
          logger.debug("left hatch");
          return kLeftPositionDeg;
        }
      } else {
        if (VISION.gamePiece == GamePiece.CARGO) {
          logger.debug("backstop right");
          return kBackStopLeftPositionDeg;
        }

        if (VISION.gamePiece == GamePiece.HATCH) {
          logger.debug("right hatch");
          return kRightPositionDeg;
        }
      }
    }

    logger.warn("Direction not set");
    return 2767.0;
  }

  public double getPosition() {
    return (TICKS_OFFSET - biscuit.getSelectedSensorPosition()) / TICKS_PER_DEGREE;
  }

  public void setPosition(double angle) {
    if (angle == kDownPosition && getPosition() < 0) {
      angle = kDownLeftPositionDeg;
      logger.info("Left down");
    } else if (angle == kDownPosition) {
      angle = kDownRightPositionDeg;
      logger.info("Right down");
    }
    // 270 Wrap Allow
    if (angle == kRightPositionDeg && getPosition() < -120) {
      angle = kRight270PositionDeg;
      logger.info("270 Wrap Right");
    }
    if (angle == kLeftPositionDeg && getPosition() > 120) {
      angle = kLeft270PositionDeg;
      logger.info("270 Wrap Left");
    }

    if (angle == kTiltUpLeftPositionDeg && getPosition() > 120) {
      angle = kLeft270TiltPositionDeg;
      logger.info("270 Wrap Tilt Up Left");
    }

    if (angle == kTiltUpRightPositionDeg && getPosition() < -120) {
      angle = kRight270TiltPositionDeg;
      logger.info("270 Wrap Tilt Up Right");
    }
    setpointTicks = (int) (TICKS_OFFSET - angle * TICKS_PER_DEGREE);
    logger.info("set position in degrees = {} in ticks = {}", angle, setpointTicks);
    biscuit.set(ControlMode.MotionMagic, setpointTicks);
  }

  public boolean isCompressed() {
    return biscuit.getSensorCollection().getAnalogInRaw() < kCompressionLimit;
  }

  public boolean onTarget() {
    int current = biscuit.getSelectedSensorPosition();
    if (Math.abs(current - setpointTicks) < kCloseEnoughTicks) {
      logger.info("onTarget: current = {} target = {}", current, setpointTicks);
      return true;
    }

    return false;
  }

  public void setMotionMagicAccel(int accel) {
    logger.info("Setting Biscuit Motion Magic Accel to: {}", accel);
    biscuit.configMotionAcceleration(accel);
  }

  public void runOpenLoop(double power) {
    biscuit.set(ControlMode.PercentOutput, power);
  }

  public void stop() {
    biscuit.set(ControlMode.PercentOutput, 0);
  }

  public void dump() {
    logger.info(
        "biscuit position in degrees = {} biscuit position in ticks = {}",
        getPosition(),
        getTicks());
  }

  @Override
  public int getTicks() {
    return biscuit.getSelectedSensorPosition();
  }

  @SuppressWarnings("Duplicates")
  @Override
  public void setLimits(int forward, int reverse) {
    if (biscuit.hasResetOccurred()) {
      logger.warn("BISCUIT TALON RESET");
    }
    graphCount = 0;
    if (forward != currentForwardLimit) {
      biscuit.configForwardSoftLimitThreshold(forward, 0);
      currentForwardLimit = forward;
      graphCount++;
    }

    if (reverse != currentReverseLimit) {
      biscuit.configReverseSoftLimitThreshold(reverse, 0);
      currentReverseLimit = reverse;
      graphCount += 2;
    }
  }

  // --------------------------KRAKEN---------------------------------
  public void releaseKraken(boolean release) {
    logger.info("Releasing Kraken: {}", release);
    double position = release ? kKrakenRelease : kKrakenHide;
    krakenActuate.set(position);
  }

  public void lockKraken(boolean lock) {
    logger.info("Locking Kraken: {}", lock);
    double position = lock ? kKrakenLock : kKrakenUnlock;
  }

  // --------------------------GRAPHER---------------------------------
  @NotNull
  @Override
  public String getDescription() {
    return "Biscuit subsystem";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(Measure.VALUE, Measure.ANALOG_IN_RAW);
  }

  @NotNull
  @Override
  public String getType() {
    return "biscuit";
  }

  @Override
  public int compareTo(@NotNull Item item) {
    return 0;
  }

  @NotNull
  @Override
  public DoubleSupplier measurementFor(@NotNull Measure measure) {
    switch (measure) {
      case VALUE:
        return () -> graphCount;
      case ANALOG_IN_RAW:
        return () -> getCompression();
      default:
        return () -> 2767.0;
    }
  }

  public double getCompression() {
    return biscuit.getSensorCollection().getAnalogInRaw();
  }

  public enum Angle {
    LEFT,
    RIGHT,
  }
}
