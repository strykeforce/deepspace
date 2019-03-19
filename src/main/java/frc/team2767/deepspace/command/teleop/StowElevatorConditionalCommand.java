package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.command.biscuit.BiscuitSetPositionCommand;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class StowElevatorConditionalCommand extends ConditionalCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public StowElevatorConditionalCommand() {
    // ball diameter adjustment
    super(
        new CommandGroup() {
          {
            addSequential(new ElevatorSetPositionCommand(23.0));
            addSequential(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
            addSequential(new ElevatorSetPositionCommand(12.0));
          }
        },
        new CommandGroup() {
          {
            addParallel(new ElevatorSetPositionCommand(12.0));
            addParallel(new BiscuitSetPositionCommand(BiscuitSubsystem.kUpPositionDeg));
          }
        });
  }

  @Override
  protected boolean condition() {
    return (BISCUIT.getPosition() > 120 || BISCUIT.getPosition() < -120);
  }
}
