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
  private static DriverControls controls;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;

  private final ExpoScale driveExpo;
  private final ExpoScale yawExpo;
  private NetworkTableEntry yawEntry;

  private static final double DRIVE_EXPO = 0.5;
  private static final double YAW_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double kP = 0.01; // 0.00625 tuning for NT method
  private static final double MAX_YAW = 0.3;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  Preferences preferences;

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
    DRIVE.setDriveMode(SwerveDrive.DriveMode.TELEOP);
    targetYaw = yawEntry.getDouble(0.0);
    logger.info("Target Yaw: {}", targetYaw);
  }

  @Override
  protected void execute() {
    // Pyeye Method:
    VISION.queryPyeye(); // gets corrected heading and range from NT
    double error = VISION.getRawBearing();
    logger.info("error: {}", error);
    // boolean isGood = error >= 0; // check if range is good (we have a target), not -1
    boolean isGood = true;
    double yaw;

    if (isGood) {
      yaw = kP * error; // corrected heading is error from camera center

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
    double yaw = kP * error;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;
    */
    // forward and strafe are still normal
    double forward = driveExpo.apply(controls.getForward());
    double strafe = driveExpo.apply(controls.getStrafe());

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
}
