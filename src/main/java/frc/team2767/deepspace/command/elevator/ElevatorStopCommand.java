package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorStopCommand extends InstantCommand {

  private final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  public ElevatorStopCommand() {
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.stop();
  }
}
