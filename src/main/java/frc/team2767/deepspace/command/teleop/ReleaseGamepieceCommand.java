package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class ReleaseGamepieceCommand extends InstantCommand {
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  public ReleaseGamepieceCommand() {
    // Requires Drive to interrupt rocket driver assist
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    VACUUM.setSolenoidsState(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE);
  }
}
