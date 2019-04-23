package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.TeleOpDriveCommand;
import frc.team2767.deepspace.motion.PathController;
import frc.team2767.deepspace.motion.TwistController;
import java.util.List;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode;
import org.strykeforce.thirdcoast.swerve.SwerveDriveConfig;
import org.strykeforce.thirdcoast.swerve.Wheel;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class DriveSubsystem extends Subsystem implements Item {

  public static final double TICKS_PER_INCH = 2500;
  public static final double TICKS_PER_TOOTH = 107.8;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final double DRIVE_SETPOINT_MAX = 25_000.0;
  private static final double ROBOT_LENGTH = 21.0;
  private static final double ROBOT_WIDTH = 26.0;
  private static final int NUM_WHEELS = 4;

  private static double offsetGyro;

  private static Wheel[] wheels;
  private final SwerveDrive swerve = configSwerve();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private TwistController twistController;
  private PathController pathController;
  private boolean isPath = false;
  private double targetYaw;
  private double yawError = 0;

  public DriveSubsystem() {
    swerve.setFieldOriented(true);
    wheels = swerve.getWheels();
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

  public double getAverageOutputCurrent() {
    double sum = 0;

    double max1 = Double.MIN_VALUE;
    double max2 = Double.MIN_VALUE;

    for (Wheel w : wheels) {
      double current = w.getDriveTalon().getOutputCurrent();
      if (current > max1) {
        max2 = max1;
        max1 = current;
      } else if (current > max2) {
        max2 = current;
      }
    }

    return (max1 + max2) / 2;
  }

  ////////////////////////////////////////////////////////////////////////////
  // PATHFINDER
  ////////////////////////////////////////////////////////////////////////////

  public void startPath(String path, double targetYaw, boolean isDriftOut) {
    this.targetYaw = targetYaw;
    logger.debug("starting path");
    this.pathController = new PathController(path, targetYaw, isDriftOut);
    pathController.start();
    isPath = true;
  }

  public boolean isPathFinished() {
    if (pathController.isFinished()) {
      isPath = false;
      return true;
    }
    return false;
  }

  public void interruptPath() {
    logger.debug("path interrupted");
    isPath = false;
    pathController.interrupt();
  }

  public void setTargetYaw(double targetYaw) {
    this.targetYaw = targetYaw;
  }

  ////////////////////////////////////////////////////////////////////////////
  // TWIST
  ////////////////////////////////////////////////////////////////////////////

  public void startTwist(double heading, int distance, double targetYaw) {
    logger.info("heading={} distance={} targetYaw={}", heading, distance, targetYaw);
    twistController = new TwistController(swerve, heading, distance, targetYaw);
    //    twistController.getPIDPrefs();
    twistController.start();
  }

  public boolean isTwistFinished() {
    if (twistController.isFinished()) {
      SmartDashboard.putBoolean("Game/twistFinished", true);
      logger.debug("end yaw = {}", Math.IEEEremainder(getGyro().getAngle(), 360));
      return true;
    }

    SmartDashboard.putBoolean("Game/twistFinished", false);
    return false;
  }

  public AHRS getGyro() {
    return swerve.getGyro();
  }

  public void interruptTwist() {
    logger.info("twist command interrupted");
    twistController.interrupt();
  }

  ////////////////////////////////////////////////////////////////////////////

  public void setWheelAzimuthPosition(List<Integer> positions) {
    Wheel[] wheels = swerve.getWheels();
    for (int i = 0; i < NUM_WHEELS; i++) wheels[i].setAzimuthPosition(positions.get(i));
  }

  public void zeroGyro() {
    AHRS gyro = swerve.getGyro();
    gyro.setAngleAdjustment(0);
    double adj = gyro.getAngle() % 360;
    gyro.setAngleAdjustment(-adj);
    logger.info("resetting gyro shoulderZero ({})", adj);
  }

  public void setAngleAdjustment(boolean startMatch) {
    AHRS gyro = swerve.getGyro();
    double adj;
    if (startMatch) {
      gyro.setAngleAdjustment(0);
      adj = -gyro.getAngle() % 360;
      adj += 90d; // Always Start with Left side forward
    } else {
      adj = gyro.getAngle() % 360;
      adj -= 90d; // Adjust Back for Teleop
    }
    gyro.setAngleAdjustment(adj);

    logger.debug("gyro now at {}", Math.IEEEremainder(gyro.getAngle(), 360));
  }

  public void setGyroOffset(double angle) {
    AHRS gyro = swerve.getGyro();
    offsetGyro = angle;
    double adj;
    adj = gyro.getAngleAdjustment();
    adj += angle;
    logger.info("gyro angle adjust = {}", adj);
    gyro.setAngleAdjustment(adj);
  }

  public void undoGyroOffset() {
    AHRS gyro = swerve.getGyro();
    double adj = gyro.getAngleAdjustment();
    adj -= offsetGyro;
    logger.info("undo gyro angle adjust = {}", adj);
    gyro.setAngleAdjustment(adj);
    offsetGyro = 0.0;
  }

  public void setWheels(double azimuth, double veocity) {
    for (Wheel w : getAllWheels()) {
      w.set(azimuth, veocity);
    }
  }

  public Wheel[] getAllWheels() {
    return swerve.getWheels();
  }

  public void setYawError(double yawError) {
    this.yawError = yawError;
  }

  public void adjustZero(int wheel, int teeth) {
    Preferences prefs = Preferences.getInstance();
    String wheelKey = SwerveDrive.getPreferenceKeyForWheel(wheel);
    int oldZero = prefs.getInt(wheelKey, 2767);
    int newZero = (int) (oldZero + teeth * TICKS_PER_TOOTH);

    prefs.putInt(wheelKey, newZero);
    swerve.zeroAzimuthEncoders();
  }

  public SwerveDrive getSwerveDrive() {
    return swerve;
  }

  ////////////////////////////////////////////////////////////////////////////
  // SWERVE CONFIG
  ////////////////////////////////////////////////////////////////////////////

  public void setAngleOrthogonalAngle() {
    double[] angles = new double[] {-90.0, 0.0, 90.0, 180.0, -180.0};
    double[] differences = new double[5];

    for (int i = 0; i < angles.length; i++) {
      differences[i] = Math.IEEEremainder(getGyro().getAngle(), 360) - angles[i];
    }

    double index;
    double minAngle = differences[0];

    for (int i = 0; i < differences.length; i++) {
      if (differences[i] < minAngle) {
        index = i;
        minAngle = differences[i];
      }
    }
  }

  public SwerveDrive getSwerve() {
    return swerve;
  }

  private SwerveDrive configSwerve() {
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
    TalonSRXConfiguration azimuthConfig = new TalonSRXConfiguration();
    azimuthConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    azimuthConfig.continuousCurrentLimit = 10;
    azimuthConfig.peakCurrentDuration = 0;
    azimuthConfig.peakCurrentLimit = 0;
    azimuthConfig.slot0.kP = 10.0;
    azimuthConfig.slot0.kI = 0.0;
    azimuthConfig.slot0.kD = 100.0;
    azimuthConfig.slot0.kF = 0.0;
    azimuthConfig.slot0.integralZone = 0;
    azimuthConfig.slot0.allowableClosedloopError = 0;
    azimuthConfig.motionAcceleration = 10_000;
    azimuthConfig.motionCruiseVelocity = 800;
    azimuthConfig.velocityMeasurementWindow = 64;
    azimuthConfig.voltageCompSaturation = 12;

    TalonSRXConfiguration driveConfig = new TalonSRXConfiguration();
    driveConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.CTRE_MagEncoder_Relative;
    driveConfig.continuousCurrentLimit = 40;
    driveConfig.peakCurrentDuration = 45;
    driveConfig.peakCurrentLimit = 40;
    driveConfig.slot0.kP = 0.05;
    driveConfig.slot0.kI = 0.0005;
    driveConfig.slot0.kD = 0.0;
    driveConfig.slot0.kF = 0.032;
    driveConfig.slot0.integralZone = 1000;
    driveConfig.slot0.maxIntegralAccumulator = 150_000;
    driveConfig.slot0.allowableClosedloopError = 0;
    driveConfig.velocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    driveConfig.velocityMeasurementWindow = 64;
    driveConfig.voltageCompSaturation = 12;

    driveConfig.slot1.kP = 0.1;
    driveConfig.slot1.kI = 0.0005;
    driveConfig.slot1.kD = 4.0;
    driveConfig.slot1.kF = 0.05;
    driveConfig.slot1.integralZone = 500;
    driveConfig.slot1.maxIntegralAccumulator = 250_000;

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();

    Wheel[] wheels = new Wheel[4];

    for (int i = 0; i < 4; i++) {
      TalonSRX azimuthTalon = new TalonSRX(i);
      azimuthTalon.configAllSettings(azimuthConfig);
      azimuthTalon.enableCurrentLimit(true);
      azimuthTalon.enableVoltageCompensation(true);
      azimuthTalon.setNeutralMode(NeutralMode.Coast);

      TalonSRX driveTalon = new TalonSRX(i + 10);
      driveTalon.configAllSettings(driveConfig);
      driveTalon.setNeutralMode(NeutralMode.Brake);
      driveTalon.enableCurrentLimit(true);
      driveTalon.enableVoltageCompensation(true);
      //      driveTalon.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, 5, 10);
      //      driveTalon.setStatusFramePeriod(StatusFrame.Status_13_Base_PIDF0 5, 10);

      telemetryService.register(new TalonItem(azimuthTalon, "Azimuth " + i));
      telemetryService.register(new TalonItem(driveTalon, "Drive " + (i + 10)));
      telemetryService.register(this);

      Wheel wheel = new Wheel(azimuthTalon, driveTalon, DRIVE_SETPOINT_MAX);
      wheels[i] = wheel;
    }

    return wheels;
  }

  @NotNull
  @Override
  public String getDescription() {
    return "Drive Subsystem";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(
        Measure.ANGLE,
        Measure.CLOSED_LOOP_ERROR,
        Measure.CLOSED_LOOP_TARGET,
        Measure.VALUE,
        Measure.COMPONENT_STRAFE,
        Measure.DISPLACEMENT_EXPECTED);
  }

  @NotNull
  @Override
  public String getType() {
    return "drive";
  }

  @Override
  public int compareTo(@NotNull Item item) {
    return 0;
  }

  @NotNull
  @Override
  public DoubleSupplier measurementFor(@NotNull Measure measure) {
    switch (measure) {
      case ANGLE:
        return () -> Math.IEEEremainder(getGyro().getAngle(), 360);
      case CLOSED_LOOP_ERROR:
        return () -> yawError; // yaw error
      case CLOSED_LOOP_TARGET:
        return () -> (isPath ? pathController.getSetpointPos() : 0.0);
      case VALUE:
        return () -> targetYaw - getGyro().getAngle();
      case DISPLACEMENT_EXPECTED:
        return () -> targetYaw;
      default:
        return () -> 2767.0;
    }
  }

  public void setSlotConfig(DriveTalonConfig slot) {
    for (Wheel w : wheels) {
      w.getDriveTalon().setIntegralAccumulator(0.0);
      w.getDriveTalon().selectProfileSlot(slot.slotId, 0);
      w.getDriveTalon().configContinuousCurrentLimit(slot.continuousCurrentLimit);
      w.getDriveTalon().configPeakCurrentDuration(slot.limitDuration);
      w.getDriveTalon().configPeakCurrentLimit(slot.peakCurrentLimit);
    }
  }

  public enum DriveTalonConfig {
    YAW_CONFIG(0, 10, 15, 40),
    DRIVE_CONFIG(1, 40, 45, 40);

    public int slotId;
    public int continuousCurrentLimit;
    public int peakCurrentLimit;
    public int limitDuration;

    DriveTalonConfig(
        int slotId, int continuousCurrentLimit, int peakCurrentLimit, int limitDuration) {
      this.slotId = slotId;
      this.continuousCurrentLimit = continuousCurrentLimit;
      this.peakCurrentLimit = peakCurrentLimit;
      this.limitDuration = limitDuration;
    }
  }
}
