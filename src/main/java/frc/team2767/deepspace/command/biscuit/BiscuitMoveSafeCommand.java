package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitMoveSafeCommand extends ConditionalCommand {
  BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public BiscuitMoveSafeCommand() {
    super(new BiscuitSetPositionCommand(0));
  }

  @Override
  protected boolean condition() {
    return (BISCUIT.getPosition() > 120 || BISCUIT.getPosition() < -120);
  }
}
