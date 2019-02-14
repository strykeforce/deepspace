package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitZeroCommand extends InstantCommand {
  BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public BiscuitZeroCommand() {
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    BISCUIT.zero();
  }
}
