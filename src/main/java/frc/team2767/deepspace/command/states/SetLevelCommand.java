package frc.team2767.deepspace.command.states;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SetLevelCommand extends InstantCommand {
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  private ElevatorLevel level;

  public SetLevelCommand(ElevatorLevel level) {
    this.level = level;
    requires(VISION);
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.setElevatorLevel(level);
    VISION.setElevatorLevel(level);
  }
}
