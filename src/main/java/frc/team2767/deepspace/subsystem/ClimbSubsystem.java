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
  private static int setpointTicks;

  private static final int LEFT_SLAVE_ID = 50;
  private static final int RIGHT_MASTER_ID = 51;
  private static final int LEFT_KICKSTAND = 2;
  private static final int RIGHT_KICKSTAND = 3;
  private static final int GRENADE_PIN = 4;

  private TalonSRX leftSlave = new TalonSRX(LEFT_SLAVE_ID);
  private TalonSRX rightMaster = new TalonSRX(RIGHT_MASTER_ID);
  private Servo rightKickstand = new Servo(RIGHT_KICKSTAND);
  private Servo leftKickstand = new Servo(LEFT_KICKSTAND);
  private Servo grenadePin = new Servo(GRENADE_PIN);

  private static final double kClimbSpeed = 0.3;
  private static final double kLowerSuction = 0.1;
  private static final double kUnwindSpeed = -0.1;

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final String PREFS = "Climb/Servos";
  public static double leftKickstandHold;
  public static double leftKickstandRelease;
  public static double rightKickstandHold;
  public static double rightKickstandRelease;
  public static double grenadePinHold;
  public static double grenadePinRelease;
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

    grenadePin.set(grenadePinHold);
    leftKickstand.set(leftKickstandHold);
    rightKickstand.set(rightKickstandHold);
  }

  private void climbPrefs() {
    leftKickstandHold = getPrefs("L_kickstand_hold", 0.4);
    leftKickstandRelease = getPrefs("L_kickstand_release", 0.95);
    rightKickstandHold = getPrefs("R_kickstand_hold", 0.5);
    rightKickstandRelease = getPrefs("R_kickstand_release", 0.95);
    grenadePinHold = getPrefs("grenade_hold", 0.5); // FIXME
    grenadePinRelease = getPrefs("grenade_release", 0.6); // FIXME
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
    leftSlaveConfig.peakOutputReverse = 0;
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
    rightMasterConfig.peakOutputReverse = 0;
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

  public void runToPosition(double inches) {
    setpointTicks = (int) (inches * TICKS_PER_INCH + TICKS_OFFSET + ticksStart);
    climb();
  }

  public void climb() {
    openLoopUp(kClimbSpeed);
  }

  public void lowerSuctionCup() {
    openLoopUp(kLowerSuction);
  }

  public void openLoopUp(double percent) {
    leftSlave.follow(rightMaster);
    rightMaster.set(ControlMode.PercentOutput, percent);
  }

  public void stop() {
    leftSlave.follow(rightMaster);
    rightMaster.set(ControlMode.PercentOutput, 0.0);
  }

  public void unwind() {
    leftSlave.configPeakOutputReverse(-1.0);
    rightMaster.configPeakOutputReverse(-1.0);
    leftSlave.follow(rightMaster);
    rightMaster.set(ControlMode.PercentOutput, kUnwindSpeed);
  }

  public void setMaxReverse() {
    leftSlave.configPeakOutputReverse(0.0);
    rightMaster.configPeakOutputReverse(0.0);
  }

  public void releaseClimber() {
    grenadePin.set(grenadePinRelease);
  }

  public void releaseKickstand() {
    leftKickstand.set(leftKickstandRelease);
    rightKickstand.set(rightKickstandRelease);
  }

  public List getTalons() {
    return List.of(leftSlave, rightMaster);
  }

  @Override
  protected void initDefaultCommand() {}
}
