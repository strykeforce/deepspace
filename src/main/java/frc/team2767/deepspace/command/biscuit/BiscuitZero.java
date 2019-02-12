package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitZero extends InstantCommand {
  BiscuitSubsystem biscuitSubsystem = Robot.BISCUIT;

  public BiscuitZero() {
    requires(biscuitSubsystem);
  }

  @Override
  protected void initialize() {
    biscuitSubsystem.zero();
  }
}
