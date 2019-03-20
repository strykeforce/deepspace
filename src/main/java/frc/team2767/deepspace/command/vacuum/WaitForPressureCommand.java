package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitForPressureCommand extends Command {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final double DIFFERENTIAL = 13; // inHg
  private static final int STABLE_COUNT = 3;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double initialPressure;
  private int stableCounts = 0;

  public WaitForPressureCommand() {
    setTimeout(2.0);
    requires(VACUUM);
  }

  @Override
  protected void initialize() {
    initialPressure = VACUUM.getPressure();
    stableCounts = 0;
  }

  @Override
  protected boolean isFinished() {
    if (Math.abs(VACUUM.getPressure() - initialPressure) > DIFFERENTIAL) stableCounts++;
    else stableCounts = 0;

    return ((stableCounts > STABLE_COUNT) || isTimedOut());
  }

  @Override
  protected void end() {
    if (isTimedOut()) {
      logger.info("Timed Out");
    } else logger.info("Pressure Reached");
  }
}
