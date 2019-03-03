package frc.team2767.deepspace.command.log;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class VacuumDumpCommand extends InstantCommand {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;

  @Override
  protected void initialize() {
    VACUUM.dump();
  }
}
