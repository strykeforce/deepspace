package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class SmartdashboardTemperatureCommand extends Command {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;

  public SmartdashboardTemperatureCommand() {}

  @Override
  protected void execute() {
    SmartDashboard.putNumber("Game/temperature", VACUUM.getPumpTemperature());
  }

  @Override
  protected boolean isFinished() {
    return false;
  }
}
