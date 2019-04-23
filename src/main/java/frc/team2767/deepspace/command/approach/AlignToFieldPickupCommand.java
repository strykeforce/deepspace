package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.ExpoScale;

public class AlignToFieldPickupCommand extends Command {
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static DriverControls controls;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private ExpoScale driveExpo;
  private double targetYaw;
  private double yawError;

  private static final double DEADBAND = 0.05;
  private static final double DRIVE_EXPO = 0.5;
  private static final double MAX_YAW = 0.3;
  private static final double kP_YAW = 0.01;

  public AlignToFieldPickupCommand() {
    requires(DRIVE);
    driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();

    if (VISION.direction == FieldDirection.LEFT) {
      targetYaw = -90.0;
    } else targetYaw = 90.0;
    logger.info("Target Yaw: {}", targetYaw);
  }

  @Override
  protected void execute() {
    // Calculate Yaw Term based on gyro
    yawError = targetYaw - Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);
    double yaw = kP_YAW * yawError;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;

    double forward = driveExpo.apply(controls.getForward());
    double strafe = driveExpo.apply(controls.getStrafe());

    DRIVE.drive(forward, strafe, yaw);
  }

  @Override
  protected void end() {}

  @Override
  protected boolean isFinished() {
    return false;
  }
}
