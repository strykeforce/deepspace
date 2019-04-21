package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class HoldHeadingUntilCompressionCommand extends Command {
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final double FORWARD_OUTPUT = 0.2;
  private static final double OUT_TIME_SEC = 0.075;
  private static final double PAUSE_TIME = 0.2;
  private static DriveState driveState;
  private static double forward;
  private static double outInitTime;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double pauseInitTime;

  public HoldHeadingUntilCompressionCommand() {
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    forward = FORWARD_OUTPUT;
    driveState = DriveState.PLACE;
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    logger.info("Hold Heading Until Compression");
    DRIVE.drive(forward, 0.0, 0.0);
  }

  @Override
  protected void execute() {
    switch (driveState) {
      case PLACE:
        if (BISCUIT.isCompressed()) {
          driveState = DriveState.PAUSE;
          DRIVE.stop();
          VACUUM.setSolenoidsState(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE);
          logger.info("Compression reached");
          pauseInitTime = Timer.getFPGATimestamp();
        }
        break;
      case PAUSE:
        logger.debug("{} - {} > {}", Timer.getFPGATimestamp(), pauseInitTime, PAUSE_TIME);
        if (Timer.getFPGATimestamp() - pauseInitTime > PAUSE_TIME) {
          DRIVE.drive(-forward * 2.0, 0.0, 0.0);
          driveState = DriveState.OUT;
          logger.debug("pause hatch place");
          VISION.enableLights(false);
          SmartDashboard.putBoolean("Game/haveHatch", false);
          outInitTime = Timer.getFPGATimestamp();
        }
        break;

      case OUT:
        if (Timer.getFPGATimestamp() - outInitTime > OUT_TIME_SEC) {
          driveState = DriveState.DONE;
          logger.info("Done Auto Hatch Place");
        }
        break;
    }
  }

  @Override
  protected boolean isFinished() {
    return driveState == DriveState.DONE;
  }

  @Override
  protected void end() {
    DRIVE.undoGyroOffset();
    //    DRIVE.stop();
  }

  private enum DriveState {
    PLACE,
    PAUSE,
    OUT,
    DONE;

    DriveState() {}
  }
}
