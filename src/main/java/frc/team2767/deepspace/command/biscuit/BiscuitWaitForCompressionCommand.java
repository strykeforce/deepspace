package frc.team2767.deepspace.command.biscuit;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BiscuitWaitForCompressionCommand extends Command {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final ElevatorSubsystem ELEVATOR = Robot.ELEVATOR;
  private double compression;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public BiscuitWaitForCompressionCommand(double compression) {
    this.compression = compression;
    setTimeout(2.0);
    requires(BISCUIT);
    requires(ELEVATOR);
  }

  @Override
  protected void initialize() {
    ELEVATOR.openLoopMove(-.3);
  }

  @Override
  protected boolean isFinished() {
    return ((Math.abs(compression - BISCUIT.getCompression()) < .2) || isTimedOut());
  }

  @Override
  protected void end() {
    if (isTimedOut()) {
      logger.info("Timed Out");
    }
    ELEVATOR.holdPosition();
  }
}
