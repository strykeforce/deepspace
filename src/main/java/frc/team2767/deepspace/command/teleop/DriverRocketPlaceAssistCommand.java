package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.util.ExpoScale;

public class DriverRocketPlaceAssistCommand extends Command {
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final ExpoScale driveExpo;
  private static DriverControls controls;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final double DRIVE_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double kP = 0.00625;
  private static final double MAX_YAW = 0.3;
  private static final double YAW_RIGHT = -30.0;
  private static final double YAW_LEFT = 30.0;
  private static double targetYaw;
  private static double angleAdjust;

  public DriverRocketPlaceAssistCommand() {
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    DRIVE.setDriveMode(SwerveDrive.DriveMode.TELEOP);
    setAngleAdjust();
  }

  @Override
  protected void execute() {
    double forward = driveExpo.apply(controls.getForward());
    double strafe = driveExpo.apply(controls.getStrafe());
    double yaw = (targetYaw - DRIVE.getGyro().getAngle()) * kP;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;

    DRIVE.drive(forward, strafe, yaw);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    undoAngleAdjust();
  }

  private void setAngleAdjust() {
    double currentAngle = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);
    if (currentAngle > 0) {
      logger.info("Adjust Angle By: {}", YAW_LEFT);
      DRIVE.RocketAngleAdjust(YAW_LEFT);
      angleAdjust = YAW_LEFT;
      targetYaw = 90.0;
    } else {
      logger.info("Adjust Angle By: {}", YAW_RIGHT);
      DRIVE.RocketAngleAdjust(YAW_RIGHT);
      angleAdjust = YAW_RIGHT;
      targetYaw = -90.0;
    }
  }

  private void undoAngleAdjust() {
    logger.info("Undo Angle Adjustment");
    DRIVE.RocketAngleAdjust(-angleAdjust);
  }
}
