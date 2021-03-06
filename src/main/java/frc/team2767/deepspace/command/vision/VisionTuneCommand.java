package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class VisionTuneCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;
  private int tuningID;

  public VisionTuneCommand(int tuningID) {
    this.tuningID = tuningID;

    setRunWhenDisabled(true);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    VISION.setCameraMode("tune");
    VISION.enableLights(true);
    VISION.runTuning(tuningID);
  }

  @Override
  protected boolean isFinished() {
    return VISION.tuneFinished();
  }

  @Override
  protected void end() {
    VISION.enableLights(false);
    VISION.setCameraMode("comp");
  }
}
