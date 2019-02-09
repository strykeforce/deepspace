package frc.team2767.deepspace.command;

import edu.wpi.first.wpilibj.command.TimedCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.Wheel;

public class TimedDriveTest extends TimedCommand {

  private static final DriveSubsystem DRIVE = Robot.DriveSubsystem;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  //  private TalonSRX[] talons;
  private Wheel[] wheels;

  public TimedDriveTest(double timeout) {
    super(timeout);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    wheels = DRIVE.getAllWheels();
    for (Wheel w : wheels) {
      w.set(0.0, 0.2);
    }

    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
  }

  @Override
  protected void execute() {}

  @Override
  protected void end() {
    DRIVE.stop();
    DRIVE.setDriveMode(SwerveDrive.DriveMode.OPEN_LOOP);
  }

  @Override
  protected void interrupted() {
    logger.debug("interrupted timed drive");
  }
}
