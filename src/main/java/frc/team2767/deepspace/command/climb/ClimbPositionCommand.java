package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class ClimbPositionCommand extends Command {
  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  double height;

  public ClimbPositionCommand(double height) {
    this.height = height;
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    CLIMB.setHeight(height);
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
