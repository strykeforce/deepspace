package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitSetPositionCommand extends Command {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final BiscuitSubsystem.BiscuitPosition position;

  public BiscuitSetPositionCommand(BiscuitSubsystem.BiscuitPosition position) {
    this.position = position;
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    BISCUIT.setPosition(position);
  }

  @Override
  protected boolean isFinished() {
    return BISCUIT.onTarget();
  }
}
