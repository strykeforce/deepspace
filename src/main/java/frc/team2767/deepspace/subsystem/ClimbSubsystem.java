package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class ClimbSubsystem extends Subsystem {

  private static final double TICKS_PER_INCH = 1738; // FIXME
  private static final double TICKS_OFFSET = TICKS_PER_INCH * 4;
  private static int ticksStart;
  private static int relStartTicks;
  private static int setpointTicks;

  private static final int LEFT_SLAVE_ID = 50;
  private static final int RIGHT_MASTER_ID = 51;
  private static final int LEFT_KICKSTAND = 2;
  private static final int RIGHT_KICKSTAND = 3;
  private static final int RATCHET_SERVO = 4;

  private TalonSRX leftSlave = new TalonSRX(LEFT_SLAVE_ID);
  private TalonSRX rightMaster = new TalonSRX(RIGHT_MASTER_ID);
  private Servo rightKickstand = new Servo(RIGHT_KICKSTAND);
  private Servo leftKickstand = new Servo(LEFT_KICKSTAND);
  private Servo ratchetServo = new Servo(RATCHET_SERVO);

  private static final double kClimbSpeed = 0.3;
  private static final double kLowerSuction = 0.1;
  private static final double kUnwindSpeed = -0.1;
  private static final double kRatchetReleaseSpeed = 0.2;
  private static final double kRaiseToHeight = -0.4;

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String PREFS = "Climb/Servos";
  public static double leftKickstandHold;
  public static double leftKickstandRelease;
  public static double rightKickstandHold;
  public static double rightKickstandRelease;
  public static double ratchetDisable;
  public static double ratchetEngage;
  private static double BACKUP = 2767;

  public ClimbSubsystem() {
    if (leftSlave == null) {
      logger.error("Climber Left Slave not present");
    }
    if (rightMaster == null) {
      logger.error("Climber Right Master not present");
    }
    climbPrefs();
    configTalon();

    ratchetServo.set(ratchetEngage);
    leftKickstand.set(leftKickstandHold);
    rightKickstand.set(rightKickstandHold);
  }

  private void climbPrefs() {
    leftKickstandHold = getPrefs("L_kickstand_hold", 0.4);
    leftKickstandRelease = getPrefs("L_kickstand_release", 0.95);
    rightKickstandHold = getPrefs("R_kickstand_hold", 0.5);
    rightKickstandRelease = getPrefs("R_kickstand_release", 0.95);
    ratchetDisable = getPrefs("ratchet_disable", 1.0);
    ratchetEngage = getPrefs("ratchet_engage", 0.5);
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

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    TalonSRXConfiguration leftSlaveConfig = new TalonSRXConfiguration();
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
    rightMasterConfig.peakOutputReverse = -1.0;
    rightMasterConfig.peakCurrentLimit = 45;
    rightMasterConfig.peakCurrentDuration = 40;
    rightMasterConfig.continuousCurrentLimit = 40;
    rightMasterConfig.voltageCompSaturation = 12;
    rightMasterConfig.voltageMeasurementFilter = 32;
    rightMasterConfig.velocityMeasurementWindow = 64;
    rightMasterConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    rightMasterConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    rightMasterConfig.forwardLimitSwitchSource = LimitSwitchSource.FeedbackConnector;
    rightMasterConfig.forwardLimitSwitchNormal = LimitSwitchNormal.NormallyOpen;

    leftSlave.configAllSettings(leftSlaveConfig, 10);
    rightMaster.configAllSettings(rightMasterConfig, 10);

    leftSlave.enableCurrentLimit(true);
    rightMaster.enableCurrentLimit(true);
    leftSlave.enableVoltageCompensation(true);
    rightMaster.enableVoltageCompensation(true);
    ticksStart = rightMaster.getSelectedSensorPosition();
    leftSlave.follow(rightMaster);

    rightMaster.configForwardLimitSwitchSource(
        LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);

    logger.info("Configured Climber Talons");

    if (!Robot.isEvent()) {
      TelemetryService telemetryService = Robot.TELEMETRY;
      telemetryService.stop();
      telemetryService.register(new TalonItem(leftSlave, "climbLeftSlave"));
      telemetryService.register(new TalonItem(rightMaster, "climbRightMaster"));
    }
  }

  public double getPosition() {
    return (rightMaster.getSelectedSensorPosition() + TICKS_OFFSET - ticksStart) / TICKS_PER_INCH;
  }

  public void openLoopMove(double percent) {
    leftSlave.follow(rightMaster);
    rightMaster.set(ControlMode.PercentOutput, percent);
  }

  public void climb() {
    openLoopMove(kClimbSpeed);
    logger.info("Climbing");
  }

  public void lowerSuctionCup() {
    openLoopMove(kLowerSuction);
    logger.info("Lowering Suction Cup");
  }

  public void stop() {
    openLoopMove(0.0);
    logger.info("Stopping Climb");
  }

  public void unwind() {
    openLoopMove(kUnwindSpeed);
  }

  public void runTicks(int ticks) {
    setpointTicks = ticks;
    relStartTicks = rightMaster.getSelectedSensorPosition();
    openLoopMove(kRatchetReleaseSpeed);
    logger.info("Running down {} ticks", setpointTicks);
  }

  public boolean onTicks() {
    return Math.abs(rightMaster.getSelectedSensorPosition() - relStartTicks) >= setpointTicks;
  }

  public int getTicks() {
    return Math.abs(rightMaster.getSelectedSensorPosition() - relStartTicks);
  }

  public void disableRatchet() {
    ratchetServo.set(ratchetDisable);
    logger.info("Ratchet Disabled");
  }

  public void enableRatchet() {
    ratchetServo.set(ratchetEngage);
    logger.info("Ratchet Enabled");
  }

  public void raiseToHeight() {
    openLoopMove(kRaiseToHeight);
    logger.info("Raising Climber");
  }

  public void releaseKickstand() {
    leftKickstand.set(leftKickstandRelease);
    rightKickstand.set(rightKickstandRelease);
    logger.info("Releasing Kickstand");
  }

  public List getTalons() {
    return List.of(leftSlave, rightMaster);
  }

  @Override
  protected void initDefaultCommand() {}
}
