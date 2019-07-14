package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.Controls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import frc.team2767.deepspace.util.AutoPlaceSideChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.util.ExpoScale;
import org.strykeforce.thirdcoast.util.RateLimit;

public class VisionAutoAlignPlaceCommand extends Command {
  private static final double kP_STRAFE = 0.1; // 0.11
  private static final double kP_YAW = 0.01; // 0.00625 tuning for NT method, 0.01 pyeye
  private static final double MAX_YAW = 0.3;
  private static final double goodEnoughYaw = 1.5;
  private static final double MIN_RANGE = 35.0;
  private static final double FORWARD_SCALE = 0.35;
  private static final double DEADBAND = 0.05;
  private static final double DRIVE_EXPO = 0.5;

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static Controls CONTROLS;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private AutoPlaceSideChooser autoPlaceSideChooser = new AutoPlaceSideChooser();
  private double range;
  private double forward;
  private double strafeCorrection;
  private double targetYaw;
  private boolean isGood = false;
  private RateLimit strafeRateLimit;
  private final ExpoScale driveExpo;

  public VisionAutoAlignPlaceCommand() {
    requires(DRIVE);
    strafeRateLimit = new RateLimit(0.05); // 0.015
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
  }

  @Override
  protected void initialize() {
    double gyroOffset =
        autoPlaceSideChooser.determineGyroOffset(
            VISION.direction, Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0));
    targetYaw = autoPlaceSideChooser.determineTargetYaw(VISION.direction);

    logger.debug("offset = {} target = {}", gyroOffset, targetYaw);

    CONTROLS = Robot.CONTROLS;
    DRIVE.setGyroOffset(gyroOffset);
    SmartDashboard.putBoolean("Game/haveHatch", true);
    logger.info("Begin Vision Auto Align Place");
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    strafeCorrection = VISION.getStrafeCorrection();

    DRIVE.setTargetYaw(targetYaw);
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
    double yawError = targetYaw - Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0);
    DRIVE.setYawError(yawError);
    double yaw = kP_YAW * yawError;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;

    // Determine if actual yaw is close enough to target
    boolean onTarget = Math.abs(yawError) <= goodEnoughYaw;

    double strafe;
    double strafeError =
        Math.sin(Math.toRadians(VISION.getCorrectedBearing())) * range - strafeCorrection;
    VISION.setStrafeError(strafeError);
    forward = driveExpo.apply(CONTROLS.getDriverControls().getForward()) * FORWARD_SCALE;

    // Only take over strafe control if pyeye has a target and the robot is straight to the field
    if (isGood && onTarget) strafe = strafeError * kP_STRAFE * forward;
    else strafe = driveExpo.apply(CONTROLS.getDriverControls().getStrafe());

    strafe = strafeRateLimit.apply(strafe);

    DRIVE.setGraphableStrafe(strafe);
    logger.info("strafe = {}", strafe);

    DRIVE.drive(forward, strafe, yaw);
  }

  @Override
  protected boolean isFinished() {
    return (range <= MIN_RANGE && isGood);
  }

  @Override
  protected void interrupted() {
    DRIVE.undoGyroOffset();
  }

  @Override
  protected void end() {
    logger.info("End Auto Align Place Vision");
  }
}
