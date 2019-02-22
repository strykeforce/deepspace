package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitExecutePlanCommand extends Command {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public BiscuitExecutePlanCommand() {
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    BISCUIT.executePlan();
  }

  @Override
  protected boolean isFinished() {
    return BISCUIT.onTarget();
  }
}
