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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.util.ExpoScale;

public class VisionYawControlCommand extends Command {
  public static final double kP_STRAFE = 0.01;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final double DRIVE_EXPO = 0.5;
  private static final double YAW_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double kP_YAW = 0.01; // 0.00625 tuning for NT method
  private static final double MAX_YAW = 0.3;
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
    double yawError = VISION.getRawBearing();
    logger.info("error: {}", yawError);
    // boolean isGood = error >= 0; // check if range is good (we have a target), not -1
    boolean isGood = true;
    double yaw;

    double strafeError =
        Math.sin(
                Math.toRadians(
                    VISION.getRawBearing()
                        - VISION.getCameraPositionBearing()
                        - Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360)))
            / VISION.getRawRange();

    if (isGood) {
      yaw = kP_YAW * yawError; // corrected heading is error from camera center

      // normalize yaw
      if (yaw > MAX_YAW) {
        yaw = MAX_YAW;
      } else if (yaw < -MAX_YAW) {
        yaw = -MAX_YAW;
      }
    } else {
      yaw = yawExpo.apply(controls.getYaw());
    }

    // NT Input Method
    /*double error = targetYaw - DRIVE.getGyro().getAngle();
    double yaw = kP_YAW * error;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;
    */
    // forward and strafe are still normal
    double forward = driveExpo.apply(controls.getForward());
    double strafe = strafeError * kP_STRAFE * forward;

    DRIVE.drive(forward, strafe, deadband(yaw));
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
}
