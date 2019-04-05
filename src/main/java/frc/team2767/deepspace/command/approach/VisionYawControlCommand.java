package frc.team2767.deepspace.command.approach;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;
import org.strykeforce.thirdcoast.util.ExpoScale;

public class VisionYawControlCommand extends Command implements Item {
  public static final double kP_STRAFE = 0.05;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final double DRIVE_EXPO = 0.5;
  private static final double YAW_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double kP_YAW = 0.01; // 0.00625 tuning for NT method, 0.01 pyeye
  private static final double MAX_YAW = 0.3;
  private static final double transferSlope = 1.2449;
  private static final double transferIntercept = -4.3949;
  private static final double goodEnoughYaw = 1.0;
  private static double range;
  private static double strafeError;
  private static double yawError;
  private static DriverControls controls;
  private final ExpoScale driveExpo;
  private final ExpoScale yawExpo;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  Preferences preferences;
  private NetworkTableEntry yawEntry;
  private double targetYaw;

  public VisionYawControlCommand() {
    requires(DRIVE);
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
    this.yawExpo = new ExpoScale(DEADBAND, YAW_EXPO);

    NetworkTableInstance instance = NetworkTableInstance.getDefault();
    NetworkTable table = instance.getTable("Pyeye");
    yawEntry = table.getEntry("target_yaw");
    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(this);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    targetYaw = yawEntry.getDouble(0.0);
    logger.info("Target Yaw: {}", targetYaw);
  }

  @Override
  protected void execute() {
    // Pyeye Method:
    VISION.queryPyeye(); // gets corrected heading and range from NT
    // yawError = VISION.getRawBearing();
    logger.info("error: {}", yawError);
    range = VISION.getRawRange();
    boolean isGood = range >= 0; // check if range is good (we have a target), not -1
    // double yaw;

    /*if (isGood) {
      yaw = kP_YAW * yawError; // corrected heading is error from camera center

      // normalize yaw
      if (yaw > MAX_YAW) {
        yaw = MAX_YAW;
      } else if (yaw < -MAX_YAW) {
        yaw = -MAX_YAW;
      }
    } else {
      yaw = yawExpo.apply(controls.getYaw());
    }*/

    // NT Input Method
    yawError = targetYaw - DRIVE.getGyro().getAngle();
    double yaw = kP_YAW * yawError;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;

    boolean onTarget = Math.abs(yawError) <= goodEnoughYaw;

    // Strafe correction
    range = range * transferSlope + transferIntercept;

    strafeError = Math.sin(Math.toRadians(VISION.getRawBearing())) * range;

    // forward and strafe are still normal
    double forward = driveExpo.apply(controls.getForward());
    double strafe;
    if (isGood && onTarget) strafe = strafeError * kP_STRAFE * forward;
    else strafe = driveExpo.apply(controls.getStrafe());
    // double strafe = driveExpo.apply(controls.getStrafe());

    DRIVE.drive(forward, strafe, yaw);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    DRIVE.stop();
  }

  private double deadband(double value) {
    if (Math.abs(value) < DEADBAND) return 0.0;
    return value;
  }

  @NotNull
  @Override
  public String getDescription() {
    return "VisionYawCommand";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(Measure.ANGLE, Measure.RANGE);
  }

  @NotNull
  @Override
  public String getType() {
    return "VisionCommand";
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
        return () -> yawError;
      case RANGE:
        return () -> strafeError;
      default:
        return () -> 2767;
    }
  }
}
