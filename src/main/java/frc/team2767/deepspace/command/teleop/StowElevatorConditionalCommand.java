package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class StowElevatorConditionalCommand extends ConditionalCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public StowElevatorConditionalCommand() {
    super(new ElevatorSetPositionCommand(22));
  }

  @Override
  protected boolean condition() {
    return (BISCUIT.getPosition() > 120 || BISCUIT.getPosition() < -120);
  }
}
