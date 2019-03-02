package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;

public class BiscuitWaitForCompressionCommand extends Command {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private double compression;

  public BiscuitWaitForCompressionCommand(double compression) {
    this.compression = compression;
    requires(BISCUIT);
  }

  @Override
  protected boolean isFinished() {
    return (Math.abs(compression - BISCUIT.getCompression()) < .2);
  }
}
