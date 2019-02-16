package frc.team2767.deepspace.command.log;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitStateLogDumpCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public BiscuitStateLogDumpCommand() {
    setRunWhenDisabled(true);
  }

  @Override
  protected void initialize() {
    logger.debug(
        "BEGIN BISCUIT STATE LOG DUMP\n" + BISCUIT.toString() + "\nEND BISCUIT STATE LOG DUMP");
  }
}
