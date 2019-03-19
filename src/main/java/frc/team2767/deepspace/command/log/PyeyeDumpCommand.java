package frc.team2767.deepspace.command.log;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class PyeyeDumpCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;

  public PyeyeDumpCommand() {}

  @Override
  protected void initialize() {
    VISION.pyeyeDump();
  }
}
