package frc.team2767.deepspace.command.states;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SetLevelCommand extends InstantCommand {
  private static final VisionSubsystem VISION = Robot.VISION;

  private ElevatorLevel level;

  public SetLevelCommand(ElevatorLevel level) {
    this.level = level;
  }

  @Override
  protected void initialize() {
    VISION.setElevatorLevel(level);
  }
}
