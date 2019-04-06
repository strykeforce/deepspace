package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VisionTuneCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private int tuningID;

  public VisionTuneCommand(int tuningID) {
    this.tuningID = tuningID;

    setRunWhenDisabled(true);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    VISION.enableLights(true);
    VISION.runTuning(tuningID);
  }
}
