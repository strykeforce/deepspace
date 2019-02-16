package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorPositionCommand extends Command {

  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  public ElevatorPositionCommand() {
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
