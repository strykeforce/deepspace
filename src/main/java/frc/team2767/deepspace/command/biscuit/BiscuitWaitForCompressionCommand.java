package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;

public class BiscuitWaitForCompressionCommand extends Command {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private double compression;

  public BiscuitWaitForCompressionCommand(double compression) {
    this.compression = compression;
    requires(BISCUIT);
  }

  @Override
  protected void initialize() {
    ELEVATOR.openLoopMove(-.3);
  }

  @Override
  protected boolean isFinished() {
    return (Math.abs(compression - BISCUIT.getCompression()) < .2);
  }

  @Override
  protected void end() {
    ELEVATOR.holdPosition();
  }
}
