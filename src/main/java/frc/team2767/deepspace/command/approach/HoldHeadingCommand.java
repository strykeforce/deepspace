package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.ExpoScale;

public class HoldHeadingCommand extends Command {
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static DriverControls controls;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private static double forward;
  private static ExpoScale driveExpo;

  private static final double FWD_SCALE = 0.3;
  private static final double DRIVE_EXPO = 0.5;
  private static final double DEADBAND = 0.05;

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    logger.info("Hold Heading, Hatch Place");
  }

  @Override
  protected void execute() {
    forward = driveExpo.apply(controls.getForward()) * FWD_SCALE;
    DRIVE.drive(forward, 0.0, 0.0);
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
