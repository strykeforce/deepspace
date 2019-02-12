package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPosition extends Command {
  BiscuitSubsystem biscuitSubsystem = Robot.BISCUIT;
  BiscuitSubsystem.Position position;

  public BiscuitPosition(BiscuitSubsystem.Position position) {
    this.position = position;
    requires(biscuitSubsystem);
  }

  @Override
  protected void initialize() {
    biscuitSubsystem.setPosition(position);
  }

  @Override
  protected boolean isFinished() {
    return biscuitSubsystem.onTarget();
  }
}
