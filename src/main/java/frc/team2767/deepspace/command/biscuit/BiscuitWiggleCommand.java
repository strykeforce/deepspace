package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitWiggleCommand extends Command {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private int downR;
  private int count = 0;
  private int period = 1000;
  private int currentTicks;

  public BiscuitWiggleCommand() {
    currentTicks = 500;
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    downR = BISCUIT.getTicks();
  }

  @Override
  protected void execute() {

    int mod = count % period;

    if (mod == 0) {
      currentTicks *= -1;
      BISCUIT.setPosition(currentTicks + downR);
    }

    count++;
  }

  @Override
  protected boolean isFinished() {
    return count > period;
  }

  @Override
  protected void end() {
    BISCUIT.setPosition(downR);
  }
}
