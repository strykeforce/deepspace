package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class ClimbJogCommand extends InstantCommand {

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;
  private double percent;

  public ClimbJogCommand(double percent) {
    this.percent = percent;
  }

  @Override
  protected void initialize() {
    CLIMB.openLoop(percent);
  }
}
