package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitZeroCommand extends InstantCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static boolean didZero;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public BiscuitZeroCommand() {
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    didZero = BISCUIT.zero();
    logger.info("Emergency Re-Zero Biscuit: {}", didZero);
  }
}
