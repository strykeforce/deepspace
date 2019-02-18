package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorExecutePlanCommand extends Command {

  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private ElevatorSubsystem.ElevatorPosition elevatorPosition;

  public ElevatorExecutePlanCommand(ElevatorSubsystem.ElevatorPosition elevatorPosition) {
    this.elevatorPosition = elevatorPosition;
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.executePlan();
  }

  @Override
  protected void execute() {
    ELEVATOR.adjustVelocity();
  }

  @Override
  protected boolean isFinished() {
    return ELEVATOR.onTarget();
  }
}
