package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class LightsOnCommand extends InstantCommand {

  private final VisionSubsystem VISION = Robot.VisionSubsystem;

  public LightsOnCommand() {
    requires(VISION);
  }

  @Override
  protected void initialize() {
    VISION.enableLights(true);
  }
}
