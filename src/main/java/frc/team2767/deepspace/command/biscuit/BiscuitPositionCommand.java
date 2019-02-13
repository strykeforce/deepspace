package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPositionCommand extends Command {
  BiscuitSubsystem biscuitSubsystem = Robot.BISCUIT;
  BiscuitSubsystem.Position position;

  public BiscuitPositionCommand(BiscuitSubsystem.Position position) {
    this.position = position;
    requires(biscuitSubsystem);
  }

  @Override
  protected void initialize() {
    biscuitSubsystem.setPosition();
  }

  @Override
  protected boolean isFinished() {
    return biscuitSubsystem.onTarget();
  }
}
