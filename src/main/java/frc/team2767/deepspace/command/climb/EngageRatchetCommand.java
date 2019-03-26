package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class EngageRatchetCommand extends InstantCommand {

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;

  private boolean enabled;

  EngageRatchetCommand(boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  protected void _initialize() {
    if (enabled) {
      CLIMB.enableRatchet();
    } else {
      CLIMB.disableRatchet();
    }
  }
}
