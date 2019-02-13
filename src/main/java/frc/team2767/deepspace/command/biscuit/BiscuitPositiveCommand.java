package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitPositiveCommand extends InstantCommand {
  BiscuitSubsystem biscuitSubsystem = Robot.BISCUIT;

  public BiscuitPositiveCommand() {
    requires(biscuitSubsystem);
  }

  @Override
  protected void initialize() {
    System.out.println("BiscuitPositiveCommand");
    biscuitSubsystem.runOpenLoop(.07);
  }
}
