package frc.team2767.deepspace.command.log;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitDumpCommand extends InstantCommand {
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;

  @Override
  protected void initialize() {
    BISCUIT.dump();
  }
}
