package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;

public class FlipSandstormControlsCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final boolean enable;

  public FlipSandstormControlsCommand(boolean enable) {
    this.enable = enable;
  }

  @Override
  protected void initialize() {
    DRIVE.sandstormAxisFlip(enable);
  }
}
