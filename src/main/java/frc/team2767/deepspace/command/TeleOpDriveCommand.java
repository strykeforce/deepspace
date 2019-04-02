package frc.team2767.deepspace.command;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.ExpoScale;

public final class TeleOpDriveCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final double DEADBAND = 0.05;
  private static final double YAW_EXPO = 0.5;
  private static final double DRIVE_EXPO = 0.5;
  private static final double kP = 0.1;
  private static DriverControls controls;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ExpoScale yawExpo;
  private final ExpoScale driveExpo;

  public TeleOpDriveCommand() {
    requires(DRIVE);
    this.yawExpo = new ExpoScale(DEADBAND, YAW_EXPO);
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    logger.info("teleop drive init");
    DRIVE.setDriveMode(TELEOP);
  }

  @Override
  protected void execute() {
    /*VISION.queryPyeye(); // gets corrected heading and range from NT

    double maxYawVelocity = 0.3; // max yaw input

    double error = VISION.getCorrectedHeading();
    boolean isGood =
        VISION.getCorrectedRange() >= 0; // check if range is good (we have a target), not -1*/
    double yaw;

    /*if (isGood) {

      yaw = error * kP; // corrected heading is error from camera center

      // normalize yaw
      if (yaw > maxYawVelocity) {
        yaw = maxYawVelocity;
      } else if (yaw < -maxYawVelocity) {
        yaw = -maxYawVelocity;
      }
    } else {*/
     yaw = yawExpo.apply(controls.getYaw());
    //}

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
    DRIVE.drive(0.0, 0.0, 0.0);
  }

  private double deadband(double value) {
    if (Math.abs(value) < DEADBAND) return 0.0;
    return value;
  }
}
