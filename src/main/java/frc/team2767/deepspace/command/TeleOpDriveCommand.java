package frc.team2767.deepspace.command;

import static org.strykeforce.thirdcoast.swerve.SwerveDrive.DriveMode.TELEOP;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TeleOpDriveCommand extends Command {
  private static final double DEADBAND = 0.05;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final DriveSubsystem DRIVE = Robot.DriveSubsystem;
  private static DriverControls controls;

  public TeleOpDriveCommand() {
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    DRIVE.setDriveMode(TELEOP);
  }

  @Override
  protected void execute() {
    double forward = deadband(controls.getForward());
    double strafe = deadband(controls.getStrafe());
    double azimuth = deadband(controls.getYaw());

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
