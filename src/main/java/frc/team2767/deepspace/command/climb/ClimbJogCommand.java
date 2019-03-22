package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class ClimbJogCommand extends InstantCommand {
  ClimbSubsystem CLIMB = Robot.CLIMB;
  double power;

  public ClimbJogCommand (double power) {
    this.power = power;
  }

  @Override
  protected void _initialize() {
    CLIMB.openLoopMove(power);
  }
}
