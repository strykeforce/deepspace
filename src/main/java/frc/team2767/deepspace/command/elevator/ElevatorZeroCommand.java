package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorZeroCommand extends Command {

  private final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  public ElevatorZeroCommand() {
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.positionToZero();
  }

  @Override
  protected boolean isFinished() {
    return ELEVATOR.onZero() || isTimedOut();
  }

  @Override
  protected void end() {
    ELEVATOR.zeroPosition();
  }
}
