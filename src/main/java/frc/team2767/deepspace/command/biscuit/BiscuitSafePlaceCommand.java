package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitSafePlaceCommand extends ConditionalCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static double biscuitPosition;

  public BiscuitSafePlaceCommand() {
    super(new BiscuitSetPositionCommand(0));
  }

  @Override
  protected boolean condition() {
    biscuitPosition = Math.abs(BISCUIT.getPosition());
    return (biscuitPosition > 110 && biscuitPosition < 250);
  }
}
