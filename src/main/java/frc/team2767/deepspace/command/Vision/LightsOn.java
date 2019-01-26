package frc.team2767.deepspace.command.Vision;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class LightsOn extends InstantCommand {

  private final VisionSubsystem VISION = Robot.VISION;

  public LightsOn() {
    requires(VISION);
  }

  @Override
  protected void initialize() {
    VISION.enableLights(true);
  }
}