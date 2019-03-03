package frc.team2767.deepspace.command.vacuum;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class VacuumCurrentLimitCommand extends InstantCommand {

  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private final double limit;

  public VacuumCurrentLimitCommand(double limit) {
    this.limit = limit;
  }

  @Override
  protected void initialize() {
    VACUUM.setPeakOutput(limit);
  }
}
