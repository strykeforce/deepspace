package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorSetPositionCommand extends InstantCommand {

  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private final double height;

  public ElevatorSetPositionCommand(double height) {
    this.height = height;
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.setPosition(height);
  }

  @Override
  protected boolean isFinished() {
    return ELEVATOR.onTarget();
  }
}
