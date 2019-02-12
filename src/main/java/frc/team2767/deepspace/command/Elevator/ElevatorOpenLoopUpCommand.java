package frc.team2767.deepspace.command.Elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorOpenLoopUpCommand extends InstantCommand {

  private final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  public ElevatorOpenLoopUpCommand() {
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.openLoopMove(ElevatorSubsystem.Direction.UP);
  }
}
