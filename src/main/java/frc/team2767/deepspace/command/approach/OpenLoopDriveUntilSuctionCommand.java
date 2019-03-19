package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class OpenLoopDriveUntilSuctionCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final double TRANSITION_PRESSURE_DIFFERENCE = 3.0;
  private static final double HATCH_SEAL_GOOD_ENOUGH = 10.0;
  private static final double OUT_DRIVE_SECONDS = 0.25;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double direction;
  private double initialPressure;
  private DriveState driveState;
  private double outDriveInitTime;

  public OpenLoopDriveUntilSuctionCommand() {
    setTimeout(5.0);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    driveState = DriveState.FAST;
    if (HATCH_SEAL_GOOD_ENOUGH < TRANSITION_PRESSURE_DIFFERENCE) {
      logger.warn("Transition pressures not set correctly");
    }

    initialPressure = VACUUM.getPressure();
    logger.debug("init pressure = {}", initialPressure);
    DRIVE.setDriveMode(SwerveDrive.DriveMode.OPEN_LOOP);
    direction = 0.25;

    if (Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360) < 0) {
      direction *= -1;
    }
  }

  @Override
  protected void execute() {
    switch (driveState) {
      case FAST:
        DRIVE.setWheels(direction, DriveState.FAST.velocity);

        if (VACUUM.getPressure() - initialPressure > TRANSITION_PRESSURE_DIFFERENCE) {
          logger.debug("current pressure = {}", VACUUM.getPressure());
          driveState = DriveState.SLOW;
        }
        break;

      case SLOW:
        DRIVE.setWheels(direction, DriveState.SLOW.velocity);
        if (VACUUM.getPressure() - initialPressure > HATCH_SEAL_GOOD_ENOUGH) {
          logger.debug("pressure reached: current pressure = {}", VACUUM.getPressure());
          outDriveInitTime = Timer.getFPGATimestamp();
          driveState = DriveState.OUT;
        }
        break;

      case OUT:
        DRIVE.setWheels(direction, DriveState.OUT.velocity);
        if (Timer.getFPGATimestamp() - outDriveInitTime > OUT_DRIVE_SECONDS) {
          driveState = DriveState.DONE;
        }
    }
  }

  @Override
  protected boolean isFinished() {
    return driveState == DriveState.DONE;
  }

  @Override
  protected void end() {
    if (isTimedOut()) {
      logger.info("Timed Out");
    }
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
