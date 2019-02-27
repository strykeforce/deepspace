package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.ConditionalCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class MoveToSafePositionCommand extends ConditionalCommand {

  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  public MoveToSafePositionCommand() {
    super(new ElevatorSetPositionCommand(ElevatorSubsystem.kHatchLowPositionInches));
  }

  @Override
  protected boolean condition() {
    return ELEVATOR.getPosition() < ElevatorSubsystem.kHatchLowPositionInches;
  }
}
