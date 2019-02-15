package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPositiveCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  public BiscuitPositiveCommand() {
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    BISCUIT.runOpenLoop(.15);
  }
}
