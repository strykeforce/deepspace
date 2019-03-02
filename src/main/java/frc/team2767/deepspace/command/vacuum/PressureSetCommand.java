package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class PressureSetCommand extends Command {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private double pressure;

  public PressureSetCommand(double pressure) {
    this.pressure = pressure;
  }

  @Override
  protected void execute() {
    SmartDashboard.putNumber("Game/temperature", VACUUM.getPumpTemperature());
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
