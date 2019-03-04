package frc.team2767.deepspace.command.log;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SandstormHatchIndicator extends InstantCommand {
  private boolean state;

  public SandstormHatchIndicator(boolean current) {
    state = current;
  }

  @Override
  protected void initialize() {
    SmartDashboard.putBoolean("Game/SandstormPickUp", state);
  }
}
