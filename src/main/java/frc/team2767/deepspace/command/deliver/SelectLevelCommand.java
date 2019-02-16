package frc.team2767.deepspace.command.deliver;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorLevel;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SelectLevelCommand extends InstantCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;

  private ElevatorLevel level;

  public SelectLevelCommand(ElevatorLevel level) {
    this.level = level;
    requires(BISCUIT);
    requires(VISION);
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    BISCUIT.setTargetLevel(level);
    ELEVATOR.setElevatorLevel(level);
    VISION.setElevatorLevel(level);
  }
}
