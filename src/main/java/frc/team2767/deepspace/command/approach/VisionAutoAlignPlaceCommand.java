package frc.team2767.deepspace.command.approach;

import static frc.team2767.deepspace.subsystem.FieldDirection.LEFT;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.util.RateLimit;

public class VisionAutoAlignPlaceCommand extends Command {
  private static final double kP_STRAFE = 0.07; // 0.11
  private static final double kP_YAW = 0.01; // 0.00625 tuning for NT method, 0.01 pyeye
  private static final double MAX_YAW = 0.3;
  private static final double goodEnoughYaw = 1.5;
  private static final double MIN_RANGE = 35.0;
  private static final double FORWARD_OUTPUT = 0.30;

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final double gyroOffset;
  private double range;
  private double forward;
  private double strafeError;
  private double yawError;
  private double strafeCorrection;
  private double targetYaw;
  private boolean isGood = false;
  private RateLimit strafeRateLimit;

  public VisionAutoAlignPlaceCommand(double gyroOffset) {
    requires(DRIVE);
    this.gyroOffset = gyroOffset;
    strafeRateLimit = new RateLimit(0.05); // 0.015
  }

  @Override
  protected void initialize() {
    DRIVE.setGyroOffset(gyroOffset);
    SmartDashboard.putBoolean("Game/haveHatch", true);
    logger.info("Begin Vision Auto Align Place");
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    strafeCorrection = VISION.getStrafeCorrection();
    if (VISION.direction == LEFT) {
      targetYaw = 90.0;
    } else {
      targetYaw = -90.0;
    }
    DRIVE.setTargetYaw(targetYaw);
    forward = FORWARD_OUTPUT;
    logger.debug("forward = {}", forward);
    logger.info("target yaw: {}", targetYaw);
  }

  @SuppressWarnings("Duplicates")
  @Override
  protected void execute() {
    // Pyeye Method:
    VISION.queryPyeye(); // gets corrected heading and range from NT
    range = VISION.getRawRange();
    isGood = range >= 0; // check if range is good (we have a target), not -1

    // Calculate Yaw Term based on gyro
    yawError = targetYaw - Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0);
    DRIVE.setYawError(yawError);
    double yaw = kP_YAW * yawError;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;

    // Determine if actual yaw is close enough to target
    boolean onTarget = Math.abs(yawError) <= goodEnoughYaw;

    double strafe;
    strafeError = Math.sin(Math.toRadians(VISION.getCorrectedBearing())) * range - strafeCorrection;
    VISION.setStrafeError(strafeError);
    logger.info("strafe error = {}", strafeError);
    // Only take over strafe control if pyeye has a target and the robot is straight to the field
    if (isGood && onTarget) strafe = strafeError * kP_STRAFE * forward;
    else strafe = 0;

    DRIVE.drive(forward, strafeRateLimit.apply(strafe), yaw);
  }

  @Override
  protected boolean isFinished() {
    return (range <= MIN_RANGE && isGood);
  }

  @Override
  protected void end() {
    logger.info("End Auto Align Place Vision");
  }
}
