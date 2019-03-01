package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class VacuumStopCommand extends InstantCommand {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;

  public VacuumStopCommand() {}

  @Override
  protected void initialize() {
    VACUUM.stop();
  }
}
