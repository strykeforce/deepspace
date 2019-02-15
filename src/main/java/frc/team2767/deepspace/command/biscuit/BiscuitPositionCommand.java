package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPositionCommand extends Command {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public BiscuitPositionCommand() {
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
