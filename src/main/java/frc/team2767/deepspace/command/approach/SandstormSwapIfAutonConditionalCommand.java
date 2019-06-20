package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SandstormSwapIfAutonConditionalCommand extends ConditionalCommand {

  public SandstormSwapIfAutonConditionalCommand(boolean enable) {
    super(new FlipSandstormControlsCommand(enable));
  }

  @Override
  protected boolean condition() {
    return DriverStation.getInstance().isAutonomous();
  }
}
