package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class ReleaseClimbCommand extends Command {
  // Runs Down 4000 ticks to pull pin
  // Disables ratchet after going at least 50 ticks

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final int NUM_TICKS = 4000; // FIXME
  private boolean ratchetDisabled = false;

  public ReleaseClimbCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    CLIMB.runTicks(NUM_TICKS);
  }

  @Override
  protected void execute() {
    if (!ratchetDisabled && CLIMB.getTicks() > 50) {
      CLIMB.disableRatchet();
      ratchetDisabled = true;
    }
  }

  @Override
  protected boolean isFinished() {
    return CLIMB.onTicks();
  }

  @Override
  protected void end() {
    CLIMB.stop();
  }
}
