package frc.team2767.deepspace.command;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.ExpoScale;

public final class TeleOpDriveCommand extends Command {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static DriverControls controls;

  private static final double DEADBAND = 0.05;
  private static final double YAW_EXPO = 0.5;
  private static final double DRIVE_EXPO = 0.5;
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
    double forward = driveExpo.apply(controls.getForward());
    double strafe = driveExpo.apply(controls.getStrafe());
    double azimuth = yawExpo.apply(controls.getYaw());

    DRIVE.drive(forward, strafe, azimuth);
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
