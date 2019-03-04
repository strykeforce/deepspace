package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitForPressureCommand extends Command {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private final double pressure;
  private static final double CLOSE_ENOUGH = 4; // inHg
  private static final int STABLE_COUNT = 3;
  private int stableCounts = 0;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public WaitForPressureCommand(double pressure) {
    this.pressure = pressure;
    setTimeout(2.0);
    requires(VACUUM);
  }

  @Override
  protected void initialize() {
    stableCounts = 0;
  }

  @Override
  protected boolean isFinished() {
    if (Math.abs(pressure - VACUUM.getPressure()) < CLOSE_ENOUGH) stableCounts++;
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
