package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class RaiseClimbCommand extends Command {
  private static final ClimbSubsystem CLIMB = Robot.CLIMB;

  public RaiseClimbCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    CLIMB.runStringPot(ClimbSubsystem.kHighReleaseIn);
  }

  @Override
  protected boolean isFinished() {
    return CLIMB.onStringPot();
  }

  @Override
  protected void end() {
    CLIMB.stop();
  }
}
