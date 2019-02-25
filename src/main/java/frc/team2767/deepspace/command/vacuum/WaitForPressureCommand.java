package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class WaitForPressureCommand extends Command {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private double pressure;
  private static final double CLOSE_ENOUGH = 100;

  public WaitForPressureCommand(double pressure) {
    this.pressure = pressure;
    requires(VACUUM);
  }

  @Override
  protected boolean isFinished() {
    return (Math.abs(pressure - VACUUM.getPressure()) < CLOSE_ENOUGH);
  }
}
