package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorPositionCommand extends Command {

  private final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private ElevatorSubsystem.Position position;

  public ElevatorPositionCommand(ElevatorSubsystem.Position position) {
    this.position = position;
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.setPosition(position);
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
