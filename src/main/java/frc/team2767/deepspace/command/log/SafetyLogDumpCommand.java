package frc.team2767.deepspace.command.log;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.safety.SafetySubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafetyLogDumpCommand extends InstantCommand {

  private static final SafetySubsystem SAFETY = Robot.SAFETY;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public SafetyLogDumpCommand() {
    setRunWhenDisabled(true);
  }

  @Override
  protected void initialize() {
    logger.debug(
        "BEGIN SAFETY LOG DUMP \n {} \n\n{}\n END SAFETY LOG DUMP",
        SAFETY.toString(),
        Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360));
  }
}
