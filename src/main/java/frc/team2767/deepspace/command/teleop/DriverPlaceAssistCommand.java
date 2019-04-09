package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.util.ExpoScale;

public class DriverPlaceAssistCommand extends Command {
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private final ExpoScale driveExpo;
  private static DriverControls controls;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final double DRIVE_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double kP_YAW = 0.01; // 0.00625
  private static final double kP_STRAFE = 0.05;
  private static final double MAX_YAW = 0.3;
  private static final double YAW_RIGHT = -30.0;
  private static final double YAW_LEFT = 30.0;
  private static final double goodEnoughYaw = 1.0;
  private static final double MIN_RANGE = 20.0;
  private static final double FWD_SCALE = 0.3;

  private static double targetYaw;
  private static double angleAdjust;
  private static boolean isAuton;
  private static double range;
  private static double bearing;
  private static boolean isGood;
  private static boolean onTarget;
  private static double strafeCorrection;

  public DriverPlaceAssistCommand() {
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    DRIVE.setDriveMode(SwerveDrive.DriveMode.TELEOP);
    isAuton = DriverStation.getInstance().isAutonomous();
    strafeCorrection = VISION.getStrafeCorrection();
    setAngleAdjust();
    logger.info("Is Auton: {}", isAuton);
  }

  @Override
  protected void execute() {
    double forward = driveExpo.apply(controls.getForward());

    double yawError = targetYaw - Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0);
    double yaw = (yawError) * kP_YAW;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;

    double strafe = driveExpo.apply(controls.getStrafe());

    DRIVE.drive(forward, strafe, yaw);
  }

  @Override
  protected boolean isFinished() {
    if (isAuton && isGood) {
      return range <= MIN_RANGE;
    }
    return false;
  }

  @Override
  protected void end() {
    if (!isAuton) {
      undoAngleAdjust();
    }
  }

  private void setAngleAdjust() {
    double currentAngle = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0);
    double fullAngle = DRIVE.getGyro().getAngle();
    logger.info("Current Angle: {}", currentAngle);

    // IN AUTON ONLY USE THIS FOR CARGO SHIP
    if (isAuton) {
      if (currentAngle <= 45 && currentAngle > -135) {
        // RIGHT OF CARGO SHIP: -135 to 45
        targetYaw = 90.0;
        angleAdjust = 90.0;
      } else if (currentAngle > 45 && currentAngle <= 135) {
        // FRONT OF CARGO SHIP: 45 to 135
        targetYaw = 90.0;
        angleAdjust = 0.0;
      } else {
        // LEFT OF CARGO SHIP: 135 to 180 OR -135 to -180
        targetYaw = 90.0;
        angleAdjust = -90.0;
      }
    }

    // IN TELEOP ONLY USE THIS FOR THE ROCKET
    else {
      if (currentAngle > 90.0) {
        // ROBOT LEFT TO ROCKET (RIGHT) FRONT, RIGHT TO ROCKET (LEFT) BACK
        targetYaw = 90.0;
        angleAdjust = YAW_RIGHT;
      } else if (currentAngle < -90.0) {
        // ROBOT RIGHT TO ROCKET (LEFT) FRONT, LEFT TO ROCKET (RIGHT) BACK
        targetYaw = -90.0;
        angleAdjust = YAW_LEFT;
      } else if (currentAngle > 0.0) {
        // ROBOT LEFT TO ROCKET (LEFT) FRONT, RIGHT TO ROCKET (RIGHT) BACK
        targetYaw = 90.0;
        angleAdjust = YAW_LEFT;
      } else {
        // ROBOT RIGHT TO ROCKET (RIGHT) FRONT, LEFT TO ROCKET (LEFT) BACK
        targetYaw = -90.0;
        angleAdjust = YAW_RIGHT;
      }
    }

    logger.info("Target Yaw: {}, Current Yaw: {}", targetYaw, fullAngle);
    logger.info("Adjust Angle By: {}", angleAdjust);
    DRIVE.setGyroOffset(angleAdjust);
    logger.info("Yaw post adjust: {}", DRIVE.getGyro().getAngle());
  }

  private void undoAngleAdjust() {
    logger.info("Undo Angle Adjustment");
    DRIVE.undoGyroOffset();
  }
}
