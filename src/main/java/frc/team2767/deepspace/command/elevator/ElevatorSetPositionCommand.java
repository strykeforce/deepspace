package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorSetPositionCommand extends InstantCommand {

  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private final ElevatorSubsystem.ElevatorPosition position;

  public ElevatorSetPositionCommand(ElevatorSubsystem.ElevatorPosition position) {
    this.position = position;
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.setElevatorPosition(position);
  }

  @Override
  protected boolean isFinished() {
    return ELEVATOR.onTarget();
  }
}
