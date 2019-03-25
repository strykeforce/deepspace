package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class ClimbSubsystem extends Subsystem {

  // max 843
  // min 139

  private static final int LEFT_SLAVE_ID = 50;
  private static final int RIGHT_MASTER_ID = 51;
  private static final int LEFT_KICKSTAND = 2;
  private static final int RIGHT_KICKSTAND = 3;
  private static final int RATCHET_SERVO = 4;
  private static final double STRINGPOT_PER_IN = 1; // FIXME
  private static final double STRINGPOT_OFFSET = 0; // FIXME

  public static double kSealVelocity;
  public static double kJogUpVelocity = -300;
  public static double kJogDownVelocity = 300;
  private static final String PREFS = "ClimbSubsystem/Settings/";
  private int stringPotSetpoint;
  private static double kStringPotCloseEnoughUnits;
  public static double kHabHoverIn;
  public static double kLowReleaseIn;
  public static double kHighReleaseIn;
  public static double kClimbIn;
  public static double kTooLowIn;
  private static double kLeftKickstandHold;
  private static double kLeftKickstandRelease;
  private static double kRightKickstandHold;
  private static double kRightKickstandRelease;
  private static double kRatchetDisable;
  private static double kRatchetEngage;
  private static double BACKUP = 2767;
  private final TalonSRX leftSlave = new TalonSRX(LEFT_SLAVE_ID);
  private final TalonSRX rightMaster = new TalonSRX(RIGHT_MASTER_ID);
  private final Servo rightKickstandServo = new Servo(RIGHT_KICKSTAND);
  private final Servo leftKickstandServo = new Servo(LEFT_KICKSTAND);
  private final Servo ratchetServo = new Servo(RATCHET_SERVO);
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  public static boolean isReleased;

  public ClimbSubsystem() {
    climbPrefs();
    configTalon();
    isReleased = false;
    ratchetServo.set(kRatchetEngage);
    leftKickstandServo.set(kLeftKickstandHold);
    rightKickstandServo.set(kRightKickstandHold);
  }

  private void climbPrefs() {
    kHabHoverIn = getPrefs("low_position_in", 0); // FIXME
    kLowReleaseIn = getPrefs("medium_position_in", 0); // FIXME
    kHighReleaseIn = getPrefs("high_position_in", 0); // FIXME
    kClimbIn = getPrefs("climb_position_in", 0); // FIXME
    kTooLowIn = getPrefs("too_low_position_in", 0); // FIXME
    kSealVelocity = getPrefs("seal_velocity", 200);
    kStringPotCloseEnoughUnits = getPrefs("close_onough", 0); // FIXME

    kLeftKickstandHold = getPrefs("L_kickstand_hold", 0.4);
    kLeftKickstandRelease = getPrefs("L_kickstand_release", 0.95);
    kRightKickstandHold = getPrefs("R_kickstand_hold", 0.5);
    kRightKickstandRelease = getPrefs("R_kickstand_release", 0.95);
    kRatchetDisable = getPrefs("ratchet_disable", 1.0);
    kRatchetEngage = getPrefs("ratchet_engage", 0.5);
  }

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    TalonSRXConfiguration leftSlaveConfig = new TalonSRXConfiguration();
    leftSlaveConfig.slot0.kP = 0.80;
    leftSlaveConfig.slot0.kI = 0;
    leftSlaveConfig.slot0.kD = 30;
    leftSlaveConfig.slot0.kF = 0.6;
    leftSlaveConfig.peakOutputReverse = -1.0;
    leftSlaveConfig.peakCurrentLimit = 45;
    leftSlaveConfig.peakCurrentDuration = 40;
    leftSlaveConfig.continuousCurrentLimit = 40;
    leftSlaveConfig.voltageCompSaturation = 12;
    leftSlaveConfig.voltageMeasurementFilter = 32;
    leftSlaveConfig.velocityMeasurementWindow = 64;
    leftSlaveConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    leftSlaveConfig.forwardLimitSwitchSource = LimitSwitchSource.RemoteTalonSRX;
    leftSlaveConfig.forwardLimitSwitchNormal = LimitSwitchNormal.NormallyOpen;

    TalonSRXConfiguration rightMasterConfig = new TalonSRXConfiguration();
    rightMasterConfig.slot0.kP = 0.80;
    rightMasterConfig.slot0.kI = 0;
    rightMasterConfig.slot0.kD = 30;
    rightMasterConfig.slot0.kF = 0.6;
    rightMasterConfig.slot0.integralZone = 0;
    rightMasterConfig.peakOutputReverse = -1.0;
    rightMasterConfig.peakCurrentLimit = 45;
    rightMasterConfig.peakCurrentDuration = 40;
    rightMasterConfig.continuousCurrentLimit = 40;
    rightMasterConfig.voltageCompSaturation = 12;
    rightMasterConfig.voltageMeasurementFilter = 32;
    rightMasterConfig.velocityMeasurementWindow = 64;
    rightMasterConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    rightMasterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.Analog;
    rightMasterConfig.reverseSoftLimitThreshold = 842;
    rightMasterConfig.forwardSoftLimitThreshold = 148;
    rightMasterConfig.forwardLimitSwitchSource = LimitSwitchSource.FeedbackConnector;
    rightMasterConfig.forwardLimitSwitchNormal = LimitSwitchNormal.NormallyOpen;
    rightMasterConfig.reverseLimitSwitchSource = LimitSwitchSource.FeedbackConnector;
    rightMasterConfig.reverseLimitSwitchNormal = LimitSwitchNormal.NormallyOpen;

    leftSlave.configAllSettings(leftSlaveConfig, 10);
    rightMaster.configAllSettings(rightMasterConfig, 10);

    leftSlave.enableCurrentLimit(true);
    rightMaster.enableCurrentLimit(true);
    leftSlave.enableVoltageCompensation(true);
    rightMaster.enableVoltageCompensation(true);
    leftSlave.follow(rightMaster);

    // FIXME ?
    rightMaster.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);

    logger.info("Configured Climber Talons");

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(leftSlave, "climbLeftSlave"));
    telemetryService.register(new TalonItem(rightMaster, "climbRightMaster"));
  }

  @SuppressWarnings("Duplicates")
  private double getPrefs(String name, double defaultValue) {
    String prefName = PREFS + name;
    Preferences preferences = Preferences.getInstance();
    if (!preferences.containsKey(prefName)) {
      preferences.putDouble(prefName, defaultValue);
    }
    double pref = preferences.getDouble(prefName, BACKUP);
    logger.info("{} = {}", name, pref);
    return pref;
  }

  public void openLoopMove(double velocity) {
    leftSlave.follow(rightMaster);
    rightMaster.set(ControlMode.Velocity, velocity);
  }

  public void stop() {
    openLoopMove(0.0);
    logger.info("Stopping Climb");
  }

  public void setHeight(double height) {
    stringPotSetpoint = (int) (height * STRINGPOT_PER_IN + STRINGPOT_OFFSET);
    leftSlave.follow(rightMaster);
    rightMaster.set(ControlMode.MotionMagic, stringPotSetpoint);
  }

  public boolean onStringPot() {
    return Math.abs(getStringPot() - stringPotSetpoint) < kStringPotCloseEnoughUnits;
  }

  public int getStringPot() {
    return rightMaster.getSensorCollection().getAnalogIn();
  }

  public double getHeight() {
    return (getStringPot() + STRINGPOT_OFFSET) / STRINGPOT_PER_IN;
  }

  public void disableRatchet() {
    ratchetServo.set(kRatchetDisable);
    logger.info("Ratchet disabled");
  }

  public void enableRatchet() {
    ratchetServo.set(kRatchetEngage);
    logger.info("Ratchet enabled");
  }

  public void releaseKickstand() {
    leftKickstandServo.set(kLeftKickstandRelease);
    rightKickstandServo.set(kRightKickstandRelease);
    logger.info("Releasing kickstand");
  }

  @Override
  protected void initDefaultCommand() {}
}
