package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class PressureSetCommand extends Command {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private VacuumSubsystem.VacuumPressure pressure;

  public PressureSetCommand(VacuumSubsystem.VacuumPressure pressure) {
    this.pressure = pressure;
  }

  @Override
  protected void initialize() {
    VACUUM.setPressure(pressure);
  }

  @Override
  protected boolean isFinished() {
    return VACUUM.onTarget();
  }
}
