package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class ClimbSubsystem extends Subsystem implements Item {

  // max 843
  // min 139

  // 2500 = %1000

  private static final int LEFT_SLAVE_ID = 50;
  private static final int RIGHT_MASTER_ID = 51;
  private static final int LEFT_KICKSTAND = 2;
  private static final int RIGHT_KICKSTAND = 3;
  private static final int RATCHET_SERVO = 4;
  private static final String PREFS = "ClimbSubsystem/Settings/";
  private static final double BACKUP = 2767;
  public static int kSealOutputVelocity;
  public static double kJogUpPercent = -0.20;
  public static double kJogDownPercent = 0.20;
  public static int kDownVelocity = 2500;
  public static int kUpVelocity = -2500;
  public static int kDownClimbVelocity = 1750;
  public static int kHabHover;
  public static int kLowRelease;
  public static int kHighRelease;
  public static int kClimb;
  public static int kTooLowIn;
  public static boolean isReleased;
  private static double kLeftKickstandHold;
  private static double kLeftKickstandRelease;
  private static double kRightKickstandHold;
  private static double kRightKickstandRelease;
  private static double kRatchetDisable;
  private static double kRatchetEngage;
  private final TalonSRX leftSlave = new TalonSRX(LEFT_SLAVE_ID);
  private final TalonSRX rightMaster = new TalonSRX(RIGHT_MASTER_ID);
  private final Servo rightKickstandServo = new Servo(RIGHT_KICKSTAND);
  private final Servo leftKickstandServo = new Servo(LEFT_KICKSTAND);
  private final Servo ratchetServo = new Servo(RATCHET_SERVO);
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public ClimbSubsystem() {
    climbPrefs();
    configTalon();
    isReleased = false;
    ratchetServo.set(kRatchetEngage);
    leftKickstandServo.set(kLeftKickstandHold);
    rightKickstandServo.set(kRightKickstandHold);
  }

  private void climbPrefs() {
    kHabHover = (int) getPrefs("low_position", 209);
    kLowRelease = (int) getPrefs("medium_position", 679);
    kHighRelease = (int) getPrefs("high_position", 189);
    kClimb = (int) getPrefs("climb_position", 884);
    kTooLowIn = (int) getPrefs("too_low_position", 240);
    kSealOutputVelocity = (int) getPrefs("seal_velocity", 300);

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
    leftSlaveConfig.peakOutputReverse = -1.0;
    leftSlaveConfig.peakCurrentLimit = 45;
    leftSlaveConfig.peakCurrentDuration = 40;
    leftSlaveConfig.continuousCurrentLimit = 40;
    leftSlaveConfig.voltageCompSaturation = 12;
    leftSlaveConfig.voltageMeasurementFilter = 32;

    TalonSRXConfiguration rightMasterConfig = new TalonSRXConfiguration();
    rightMasterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    rightMasterConfig.slot0.kP = 0.80;
    rightMasterConfig.slot0.kI = 0;
    rightMasterConfig.slot0.kD = 30;
    rightMasterConfig.slot0.kF = 0.6;
    //    rightMasterConfig.slot0.kP = 0.0;
    //    rightMasterConfig.slot0.kI = 0;
    //    rightMasterConfig.slot0.kD = 0;
    //    rightMasterConfig.slot0.kF = 0.4;
    rightMasterConfig.slot0.integralZone = 0;

    rightMasterConfig.slot1.kP = 0.0;
    rightMasterConfig.slot1.kI = 0.0;
    rightMasterConfig.slot1.kD = 0.0;
    rightMasterConfig.slot1.kF = 0.4;

    rightMasterConfig.peakOutputReverse = -1.0;
    rightMasterConfig.peakCurrentLimit = 45;
    rightMasterConfig.peakCurrentDuration = 40;
    rightMasterConfig.continuousCurrentLimit = 40;
    rightMasterConfig.voltageCompSaturation = 12;
    rightMasterConfig.voltageMeasurementFilter = 32;
    rightMasterConfig.velocityMeasurementWindow = 64;
    rightMasterConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    rightMasterConfig.forwardLimitSwitchSource = LimitSwitchSource.FeedbackConnector;
    rightMasterConfig.forwardLimitSwitchNormal = LimitSwitchNormal.NormallyOpen;
    rightMasterConfig.reverseLimitSwitchSource = LimitSwitchSource.FeedbackConnector;
    rightMasterConfig.reverseLimitSwitchNormal = LimitSwitchNormal.NormallyOpen;

    leftSlave.configAllSettings(leftSlaveConfig, 10);
    rightMaster.configAllSettings(rightMasterConfig, 10);

    rightMaster.setNeutralMode(NeutralMode.Brake);
    leftSlave.setNeutralMode(NeutralMode.Brake);

    rightMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, 10);
    rightMaster.configOpenloopRamp(0.1);
    rightMaster.enableCurrentLimit(true);
    rightMaster.enableVoltageCompensation(true);
    rightMaster.configForwardSoftLimitEnable(false);
    rightMaster.configReverseSoftLimitEnable(false);
    leftSlave.enableCurrentLimit(true);
    leftSlave.enableVoltageCompensation(true);
    leftSlave.follow(rightMaster);

    logger.info("Configured Climber Talons");

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(leftSlave, "climbLeftSlave"));
    telemetryService.register(new TalonItem(rightMaster, "climbRightMaster"));
    telemetryService.register(this);
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

  public void setSlowTalonConfig(boolean isSlow) {
    leftSlave.follow(rightMaster);
    if (isSlow) {
      rightMaster.selectProfileSlot(0, 0);
      logger.debug("selected profile slot = {}", 0);
    } else {
      rightMaster.selectProfileSlot(1, 0);
      logger.debug("selected profile slot = {}", 1);
    }
    rightMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_4_AinTempVbat, 10);
  }

  public void stop() {
    openLoop(0.0);
    logger.info("Stopping Climb");
  }

  public void openLoop(double percent) {
    leftSlave.follow(rightMaster);
    rightMaster.set(ControlMode.PercentOutput, percent);
  }

  public void setVelocity(int velocity) {
    leftSlave.follow(rightMaster);
    rightMaster.set(ControlMode.Velocity, velocity);
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

  @NotNull
  @Override
  public String getDescription() {
    return "climber";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(Measure.ANALOG_IN_RAW);
  }

  @NotNull
  @Override
  public String getType() {
    return "climb";
  }

  @Override
  public int compareTo(@NotNull Item item) {
    return 0;
  }

  @NotNull
  @Override
  public DoubleSupplier measurementFor(@NotNull Measure measure) {
    switch (measure) {
      case ANALOG_IN_RAW:
        return this::getStringPotPosition;
      default:
        return () -> 2767.0;
    }
  }

  public double getStringPotPosition() {
    return rightMaster.getSensorCollection().getAnalogInRaw();
  }
}
