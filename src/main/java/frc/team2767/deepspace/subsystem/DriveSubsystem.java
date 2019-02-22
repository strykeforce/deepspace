package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.TeleOpDriveCommand;
import frc.team2767.deepspace.motion.PathController;
import frc.team2767.deepspace.motion.TwistController;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.swerve.SwerveDriveConfig;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class DriveSubsystem extends Subsystem {

  public static final double TICKS_PER_INCH = 1900; // TODO: Set Ticks per inch
  private static final double DRIVE_SETPOINT_MAX = 25_000.0;
  private static final double ROBOT_LENGTH = 21.0;
  private static final double ROBOT_WIDTH = 26.0;

  private final SwerveDrive swerve = getSwerve();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private TwistController twistController;
  private PathController pathController;

  public DriveSubsystem() {
    swerve.setFieldOriented(true);
  }

  @Override
  protected void initDefaultCommand() {
    setDefaultCommand(new TeleOpDriveCommand());
  }

  public void setDriveMode(DriveMode mode) {
    swerve.setDriveMode(mode);
  }

  public void zeroYawEncoders() {
    swerve.zeroAzimuthEncoders();
  }

  public void drive(double forward, double strafe, double yaw) {
    swerve.drive(forward, strafe, yaw);
  }

  public void stop() {
    swerve.stop();
  }

  ////////////////////////////////////////////////////////////////////////////
  // PATHFINDER
  ////////////////////////////////////////////////////////////////////////////

  public void startPath(String path, double targetYaw) {
    logger.debug("starting path");
    this.pathController = new PathController(swerve, path, targetYaw);
    pathController.start();
  }

  public boolean isPathFinished() {
    return pathController.isFinished();
  }

  public void interruptPath() {
    pathController.interrupt();
  }

  ////////////////////////////////////////////////////////////////////////////
  // TWIST
  ////////////////////////////////////////////////////////////////////////////

  public void startTwist(double heading, int distance, double targetYaw) {
    logger.debug("starting twist");
    twistController = new TwistController(swerve, heading, distance, targetYaw);
    twistController.start();
  }

  public boolean isTwistFinished() {
    return twistController.isFinished();
  }

  public void interruptTwist() {
    twistController.interrupt();
  }

  public void setWheelAzimuthPosition(List<Integer> positions) {
    Wheel[] wheels = swerve.getWheels();
    for (int i = 0; i < 4; i++) wheels[i].setAzimuthPosition(positions.get(i));
  }

  ////////////////////////////////////////////////////////////////////////////

  public void zeroGyro() {
    AHRS gyro = swerve.getGyro();
    gyro.setAngleAdjustment(0);
    double adj = gyro.getAngle() % 360;
    gyro.setAngleAdjustment(-adj);
    logger.info("resetting gyro shoulderZero ({})", adj);
  }

  public AHRS getGyro() {
    return swerve.getGyro();
  }

  public SwerveDrive getSwerveDrive() {
    return swerve;
  }

  ////////////////////////////////////////////////////////////////////////////
  // SWERVE CONFIG
  ////////////////////////////////////////////////////////////////////////////

  private SwerveDrive getSwerve() {
    SwerveDriveConfig config = new SwerveDriveConfig();
    config.wheels = getWheels();
    config.gyro = new AHRS(SPI.Port.kMXP);
    config.length = ROBOT_LENGTH;
    config.width = ROBOT_WIDTH;
    config.gyroLoggingEnabled = true;
    config.summarizeTalonErrors = false;

    return new SwerveDrive(config);
  }

  private Wheel[] getWheels() {
    TalonSRXConfiguration yawConfig = new TalonSRXConfiguration();
    yawConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    yawConfig.continuousCurrentLimit = 10;
    yawConfig.peakCurrentDuration = 0;
    yawConfig.peakCurrentLimit = 0;
    yawConfig.slot0.kP = 10.0;
    yawConfig.slot0.kI = 0.0;
    yawConfig.slot0.kD = 100.0;
    yawConfig.slot0.kF = 0.0;
    yawConfig.slot0.integralZone = 0;
    yawConfig.slot0.allowableClosedloopError = 0;
    yawConfig.motionAcceleration = 10_000;
    yawConfig.motionCruiseVelocity = 800;

    TalonSRXConfiguration driveConfig = new TalonSRXConfiguration();
    driveConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    driveConfig.continuousCurrentLimit = 40;
    driveConfig.peakCurrentDuration = 0;
    driveConfig.peakCurrentLimit = 0;
    driveConfig.slot0.kP = 0.08;
    driveConfig.slot0.kI = 0.0005;
    driveConfig.slot0.kD = 0.0;
    driveConfig.slot0.kF = 0.028;
    driveConfig.slot0.integralZone = 3000;
    driveConfig.slot0.allowableClosedloopError = 0;
    driveConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    driveConfig.velocityMeasurementWindow = 64;

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();

    Wheel[] wheels = new Wheel[4];

    for (int i = 0; i < 4; i++) {
      TalonSRX yawTalon = new TalonSRX(i);
      yawTalon.configAllSettings(yawConfig);
      yawTalon.enableCurrentLimit(true);
      yawTalon.enableVoltageCompensation(true);

      TalonSRX driveTalon = new TalonSRX(i + 10);
      driveTalon.configAllSettings(driveConfig);
      driveTalon.setNeutralMode(NeutralMode.Brake);
      driveTalon.enableCurrentLimit(true);
      driveTalon.enableVoltageCompensation(true);
      //      driveTalon.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, 10);
      //      driveTalon.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0 5, 10);

      telemetryService.register(new TalonItem(yawTalon, "Azimuth " + i));
      telemetryService.register(new TalonItem(driveTalon, "Drive " + (i + 10)));

      Wheel wheel = new Wheel(yawTalon, driveTalon, DRIVE_SETPOINT_MAX);
      wheels[i] = wheel;
    }

    return wheels;
  }

  public Wheel[] getAllWheels() {
    return swerve.getWheels();
  }
}
