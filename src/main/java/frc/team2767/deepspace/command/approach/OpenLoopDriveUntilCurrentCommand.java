package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class OpenLoopDriveUntilCurrentCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private DriveState driveState;

  public OpenLoopDriveUntilCurrentCommand() {
    setTimeout(5.0);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    driveState = DriveState.FAST;
  }

  @Override
  protected void execute() {
    switch (driveState) {
      case FAST:
        break;
      case SLOW:
        break;
      case OUT:
        break;
      case DONE:
        break;
    }
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  private enum DriveState {
    FAST(0.2),
    SLOW(0.06),
    OUT(-0.4),
    DONE(0.0);

    private double velocity;

    DriveState(double velocity) {
      this.velocity = velocity;
    }
  }
}
