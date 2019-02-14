package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPositionCommand extends Command {
  BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  BiscuitSubsystem.Position position;

  public BiscuitPositionCommand(BiscuitSubsystem.Position position) {
    this.position = position;
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    BISCUIT.setPosition();
  }

  @Override
  protected boolean isFinished() {
    return BISCUIT.onTarget();
  }
}
