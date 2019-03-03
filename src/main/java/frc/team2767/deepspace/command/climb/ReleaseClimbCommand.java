package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class ReleaseClimbCommand extends Command {
  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private static final int NUM_TICKS = 100; // FIXME

  public ReleaseClimbCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    CLIMB.disableRatchet();
    CLIMB.runTicks(NUM_TICKS);
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
