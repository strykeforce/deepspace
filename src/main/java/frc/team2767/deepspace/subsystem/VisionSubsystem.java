package frc.team2767.deepspace.subsystem;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.command.Subsystem;

public class VisionSubsystem extends Subsystem {

  private final DigitalOutput lightsOutput = new DigitalOutput(0);

  public VisionSubsystem() {
    lightsOutput.set(true);
  }

  public void enableLights(boolean state) {
    lightsOutput.set(!state);
  }

  @Override
  protected void initDefaultCommand() {}
}
