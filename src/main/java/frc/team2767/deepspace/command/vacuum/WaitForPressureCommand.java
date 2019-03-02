package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class WaitForPressureCommand extends Command {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private final double pressure;
  private static final double CLOSE_ENOUGH = 100;
  private static final int STABLE_COUNT = 2;
  private int stableCounts = 0;

  public WaitForPressureCommand(double pressure) {
    this.pressure = pressure;
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

    return (stableCounts > STABLE_COUNT);
  }
}
