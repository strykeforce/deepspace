package frc.team2767.deepspace.command;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import frc.team2767.deepspace.util.VectorRateLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.ExpoScale;

public final class CameraDriveCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final double DEADBAND = 0.05;
  private static final double YAW_EXPO = 0.65; // 0.5
  private static final double DRIVE_EXPO = 0.65; // 0.5
  private static final double VECTOR_LIMIT = 0.05;
  private static final double kP = 0.1;
  private static final double MAX_FWD_STR = 0.7;
  private static final double MAX_YAW = 0.6;
  private static DriverControls controls;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final ExpoScale yawExpo;
  private final ExpoScale driveExpo;
  private final VectorRateLimit vectorLimit;

  public CameraDriveCommand() {
    requires(DRIVE);
    requires(VISION);
    this.yawExpo = new ExpoScale(DEADBAND, YAW_EXPO);
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
    this.vectorLimit = new VectorRateLimit(VECTOR_LIMIT);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    logger.info("teleop drive init");
    DRIVE.setDriveMode(TELEOP);
  }

  @Override
  protected void execute() {
    double yaw;
    yaw = yawExpo.apply(VISION.getYawFactor());
    double forward = driveExpo.apply(controls.getForward());
    double strafe = driveExpo.apply(controls.getStrafe());

    // double forward = controls.getForward();
    // double strafe = controls.getStrafe();

    forward = maximum(forward, MAX_FWD_STR);
    strafe = maximum(strafe, MAX_FWD_STR);
    yaw = maximum(yaw, MAX_YAW);

    double[] output = vectorLimit.apply(forward, strafe);

    DRIVE.drive(output[0], output[1], yaw);
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

  private double maximum(double value, double max) {
    if (value > max) return max;
    else if (value < -max) return -max;
    else return value;
  }
}
