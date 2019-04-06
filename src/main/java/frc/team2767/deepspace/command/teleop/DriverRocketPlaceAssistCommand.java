package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.DriverStation;
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
  private static final double YAW_RIGHT = -20.0;
  private static final double YAW_LEFT = 20.0;
  private static double targetYaw;
  private static double angleAdjust;
  private static boolean isAuton;

  public DriverRocketPlaceAssistCommand() {
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    DRIVE.setDriveMode(SwerveDrive.DriveMode.TELEOP);
    setAngleAdjust();
    isAuton = DriverStation.getInstance().isAutonomous();
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
    double currentAngle = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0);
    double fullAngle = DRIVE.getGyro().getAngle();
    logger.info("Current Angle: {}", currentAngle);
    if(isAuton){
     //45 - -135 = right of cargo ship
     //46 - 135 = front of cargo ship
      //136 - 180 = left of cargo ship
      // -136 - -180 = away from stuff
      if(currentAngle < 45){
        //RIGHT OF CARGO SHIP
        targetYaw = -90.0;
        angleAdjust = -90.0;
      }else if()
    }
    else {
      if (currentAngle > 90.0) {
        targetYaw = 90.0;
        angleAdjust = YAW_RIGHT;
      } else if (currentAngle < -90.0) {
        targetYaw = -90.0;
        angleAdjust = YAW_LEFT;
      } else if (currentAngle > 0.0) {
        targetYaw = 90.0;
        angleAdjust = YAW_LEFT;
      } else {
        targetYaw = -90.0;
        angleAdjust = YAW_RIGHT;
      }
    }

    logger.info("Target Yaw: {}, Current Yaw: {}", targetYaw, fullAngle);
    logger.info("Adjust Angle By: {}", angleAdjust);
    DRIVE.SetGyroOffset(angleAdjust);
    logger.info("Yaw post adjust: {}", DRIVE.getGyro().getAngle());
    double anglePostAdj = DRIVE.getGyro().getAngle();
    if (anglePostAdj > 180.0) {
      targetYaw += 360.0;
    }
    if (anglePostAdj < -180.0) {
      targetYaw -= 360.0;
    }
    logger.info("New Target Yaw: {}", targetYaw);
  }

  private void undoAngleAdjust() {
    logger.info("Undo Angle Adjustment");
    DRIVE.UndoGyroOffset();
  }
}
