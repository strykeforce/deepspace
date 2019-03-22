package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class OpenLoopDriveUntilCurrentCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final double TRANSITION_CURRENT = 20.0;
  private static final int CURRENT_STABLE_COUNT = 2;
  private static final double CURRENT_IGNORE = 0.3;
  private static final double DIRECTION = -0.25;
  private static final double OUT_DRIVE_SECONDS = 1.0;
  private static final double SOLENOID_DELAY = 0.2;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private DriveState driveState;
  private double initBlankTime;
  private double initialCurrent;
  private double outDriveInitTime;
  private double solenoidDelayInit;
  private int currentStableCount;

  public OpenLoopDriveUntilCurrentCommand() {
    setTimeout(5.0);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    driveState = DriveState.FAST;
    initialCurrent = DRIVE.getAverageOutputCurrent();
    currentStableCount = 0;
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    initBlankTime = Timer.getFPGATimestamp();
  }

  @Override
  protected void execute() {
    switch (driveState) {
      case FAST:
        DRIVE.setWheels(DIRECTION, DriveState.FAST.velocity);
        double averageCurrent = DRIVE.getAverageOutputCurrent();
        if (Timer.getFPGATimestamp() - initBlankTime > CURRENT_IGNORE
            && averageCurrent > initialCurrent + TRANSITION_CURRENT) {
          currentStableCount++;
        } else {
          currentStableCount = 0;
        }

        if (currentStableCount > CURRENT_STABLE_COUNT) {
          driveState = DriveState.CLOSE_SOLENOID;
          logger.debug("set state to {}, current = {}", driveState, averageCurrent);
        }
        break;
      case CLOSE_SOLENOID:
        DRIVE.setWheels(DIRECTION, DriveState.CLOSE_SOLENOID.velocity);
        VACUUM.setSolenoidsState(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE);
        solenoidDelayInit = Timer.getFPGATimestamp();
        driveState = DriveState.WAIT;
      case WAIT:
        if (Timer.getFPGATimestamp() - solenoidDelayInit > SOLENOID_DELAY) {
          driveState = DriveState.OUT;
          outDriveInitTime = Timer.getFPGATimestamp();
          logger.debug("set state to {}", driveState);
        }
        break;
      case OUT:
        DRIVE.setWheels(DIRECTION, DriveState.OUT.velocity);
        if (Timer.getFPGATimestamp() - outDriveInitTime > OUT_DRIVE_SECONDS) {
          driveState = DriveState.DONE;
          DRIVE.setWheels(0.0, 0.0);
          logger.debug("set state to {}", driveState);
        }
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
    FAST(0.25),
    CLOSE_SOLENOID(0.08),
    WAIT(0.06),
    OUT(-0.7),
    DONE(0.0);

    private double velocity;

    DriveState(double velocity) {
      this.velocity = velocity;
    }
  }
}
