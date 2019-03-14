package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class BlinkLightsCommand extends Command {

  private static final VisionSubsystem VISION = Robot.VISION;
  private final VisionSubsystem.LightPattern pattern;

  public BlinkLightsCommand(VisionSubsystem.LightPattern pattern) {
    this.pattern = pattern;
    requires(VISION); // FIXME: necessary??
  }

  @Override
  protected void initialize() {
    VISION.startLightBlink(pattern);
  }

  @Override
  protected void execute() {
    VISION.blink();
  }

  @Override
  protected boolean isFinished() {
    return VISION.isBlinkFinished();
  }
}
