package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.safety.Limitable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;

public class BiscuitSubsystem extends Subsystem implements Limitable {
  private int CLOSE_ENOUGH = 50; // FIXME
  private final int BISCUIT_ID = 40;
  private final int TICKS_PER_REV = 12300;
  private int LOW_ENCODER_LIMIT = -6150; // FIXME
  private int HIGH_ENCODER_LIMIT = 6150; // FIXME

  private static final String KEY_BASE = "BiscuitSubsystem/Position/";
  private static final int BACKUP = 2767;
  private static Preferences preferences = Preferences.getInstance();

  String absoluteZeroKey = KEY_BASE + "ABS_ZERO";
  String lowLimitKey = KEY_BASE + "LOW_LIMIT";
  String highLimitKey = KEY_BASE + "HIGH_LIMIT";
  String closeEnoughKey = KEY_BASE + "CLOSE_ENOUGH";

  DriveSubsystem driveSubsystem = Robot.DriveSubsystem;
  TelemetryService telemetryService = Robot.TELEMETRY;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  public FieldDirections plannedDirection;

  int zero = 0;
  int target = 0;

  public Position pos; // temporary to open preferences

  TalonSRX biscuit = new TalonSRX(BISCUIT_ID);
  TalonSRXConfiguration biscuitConfig = new TalonSRXConfiguration();

  public BiscuitSubsystem() {
    telemetryService.register(biscuit);
    biscuitConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    biscuitConfig.forwardSoftLimitThreshold = HIGH_ENCODER_LIMIT;
    biscuitConfig.reverseSoftLimitThreshold = LOW_ENCODER_LIMIT;
    biscuitConfig.forwardSoftLimitEnable = true;
    biscuitConfig.reverseSoftLimitEnable = true;

    biscuitConfig.slot0.kP = 1.0;
    biscuitConfig.slot0.kI = 0.0;
    biscuitConfig.slot0.kD = 0.0;
    biscuitConfig.slot0.kF = 0.65;
    biscuitConfig.slot0.integralZone = 0;

    biscuitConfig.peakCurrentDuration = 40;
    biscuitConfig.peakCurrentLimit = 25;
    biscuitConfig.continuousCurrentLimit = 20;
    biscuitConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    biscuitConfig.velocityMeasurementWindow = 64;

    biscuitConfig.voltageCompSaturation = 12;
    biscuitConfig.voltageMeasurementFilter = 32;

    biscuitConfig.motionCruiseVelocity = 1_000;
    biscuitConfig.motionAcceleration = 2_000;

    biscuit.configAllSettings(biscuitConfig);

    biscuitPreferences();
  }

  @Override
  protected void initDefaultCommand() {}

  @Override
  public void setLimits(int forward, int reverse) {}


  @Override
  public int getPosition() {
    return biscuit.getSelectedSensorPosition() % TICKS_PER_REV;
  }

  public void biscuitPreferences() {
    // FIXME actually set from preference

    if (!preferences.containsKey(closeEnoughKey)) preferences.putInt(closeEnoughKey, BACKUP);
    if (!preferences.containsKey(absoluteZeroKey)) preferences.putInt(absoluteZeroKey, BACKUP);
    if (!preferences.containsKey(lowLimitKey)) preferences.putInt(lowLimitKey, BACKUP);
    if (!preferences.containsKey(highLimitKey)) preferences.putInt(highLimitKey, BACKUP);

    CLOSE_ENOUGH = preferences.getInt(closeEnoughKey, BACKUP);
    LOW_ENCODER_LIMIT = preferences.getInt(lowLimitKey, BACKUP);
    HIGH_ENCODER_LIMIT = preferences.getInt(highLimitKey, BACKUP);
  }

  public void zero() {
    if (!preferences.containsKey(absoluteZeroKey)) preferences.putInt(absoluteZeroKey, BACKUP);
    int absoluteZero = preferences.getInt(absoluteZeroKey, BACKUP);

    zero = biscuit.getSensorCollection().getPulseWidthPosition() - absoluteZero;
    biscuit.setSelectedSensorPosition(zero);
  }

  public double getGyroAngle() {
    AHRS gyro = driveSubsystem.getGyro();
    double angle = gyro.getYaw();
    return angle;
  }

  public void setPosition(Position position) {
    double angle = getGyroAngle();
    switch (position) {
      case PLACE:
        if (plannedDirection == FieldDirections.FIELD_RIGHT && Math.abs(angle) < 90
            || plannedDirection == FieldDirections.FIELD_LEFT && Math.abs(angle) > 90) {
          target = Position.RIGHT.encoderPosition;
        } else {
          target = Position.LEFT.encoderPosition;
        }
        break;
      case PICKUP:
        if (plannedDirection == FieldDirections.FIELD_SOUTH && angle > 0
            || plannedDirection == FieldDirections.FIELD_NORTH && angle < 0) {
          target = Position.RIGHT.encoderPosition;
        } else {
          target = Position.LEFT.encoderPosition;
        }
        break;
      default:
        target = position.encoderPosition;
        break;
    }
    biscuit.set(ControlMode.Position, target);
  }

  public boolean onTarget() {
    if (Math.abs(biscuit.getSelectedSensorPosition() - target) < CLOSE_ENOUGH) {
      return true;
    } else {
      return false;
    }
  }

  public void runOpenLoop(double power) {
    biscuit.set(ControlMode.PercentOutput, power);
  }

  public void stop() {
    biscuit.set(ControlMode.PercentOutput, 0);
  }

  public enum Position {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    BACK_STOP_L,
    BACK_STOP_R,
    PLACE,
    PICKUP;
    // FIXME need set
    final int encoderPosition;

    Position() {
      String positionKey = KEY_BASE + this.name();
      if (!preferences.containsKey(positionKey)) preferences.putInt(positionKey, BACKUP);
      this.encoderPosition = preferences.getInt(positionKey, BACKUP);
    }
  }

  public enum FieldDirections {
    FIELD_LEFT,
    FIELD_RIGHT,
    FIELD_NORTH,
    FIELD_SOUTH
  }
}
