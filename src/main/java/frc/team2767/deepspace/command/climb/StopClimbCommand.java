package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class StopClimbCommand extends InstantCommand {
  private static final ClimbSubsystem CLIMB = Robot.CLIMB;

  public StopClimbCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {

    CLIMB.stop();
  }
}
