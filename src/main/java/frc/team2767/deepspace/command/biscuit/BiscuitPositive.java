package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPositive extends InstantCommand {
  BiscuitSubsystem biscuitSubsystem = Robot.BiscuitSubsystem;

  public BiscuitPositive() {
    requires(biscuitSubsystem);
  }

  @Override
  protected void initialize() {
    System.out.println("BiscuitPositive");
    biscuitSubsystem.runOpenLoop(.15);
  }
}
