package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitStopCommand extends InstantCommand {
  BiscuitSubsystem biscuitSubsystem = Robot.BISCUIT;

  public BiscuitStopCommand() {
    requires(biscuitSubsystem);
  }

  @Override
  protected void initialize() {
    biscuitSubsystem.stop();
  }
}
