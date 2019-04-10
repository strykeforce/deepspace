package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class ReleaseKrakenCommand extends InstantCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private boolean release;

  public ReleaseKrakenCommand(boolean release) {
    this.release = release;
  }

  @Override
  protected void initialize() {
    BISCUIT.releaseKraken(release);
  }
}
