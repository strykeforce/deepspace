package frc.team2767.deepspace.command.elevator;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class ElevatorPlanCommand extends InstantCommand {

  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private int position;

  public ElevatorPlanCommand(int position) {
    this.position = position;
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.plannedLevel = position;
  }
}
