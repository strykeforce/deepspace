package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitStop extends InstantCommand {
  BiscuitSubsystem biscuitSubsystem = Robot.BiscuitSubsystem;

  public BiscuitStop() {
    requires(biscuitSubsystem);
  }

  @Override
  protected void initialize() {
    biscuitSubsystem.stop();
  }
}
