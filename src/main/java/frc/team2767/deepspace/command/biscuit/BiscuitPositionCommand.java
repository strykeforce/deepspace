package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitPositionCommand extends Command {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public BiscuitPositionCommand() {
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    logger.info("BiscuitPosition ran");
    BISCUIT.executePlan();
  }

  @Override
  protected boolean isFinished() {
    return BISCUIT.onTarget();
  }
}
