package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HoldHeadingUntilCompressionCommand extends Command {
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final VisionSubsystem VISION = Robot.VISION;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static DriveState driveState;
  private static double strafe;
  private static double outInitTime;

  private static final double STRAFE_OUTPUT = 0.1;
  private static final double OUT_TIME_SEC = 0.25;

  public HoldHeadingUntilCompressionCommand() {
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    driveState = DriveState.PLACE;
    logger.info("Hold Heading Until Compression");
  }

  @Override
  protected void execute() {
    switch (driveState) {
      case PLACE:
        if (VISION.startSide == StartSide.LEFT) {
          strafe = STRAFE_OUTPUT;
        } else {
          strafe = STRAFE_OUTPUT * -1;
        }
        DRIVE.drive(0.0, strafe, 0.0);
        if (BISCUIT.isCompressed()) {
          driveState = DriveState.OUT;
          VISION.enableLights(false);
          VACUUM.setSolenoidsState(VacuumSubsystem.SolenoidStates.PRESSURE_ACCUMULATE);
          SmartDashboard.putBoolean("Game/haveHatch", false);
          outInitTime = Timer.getFPGATimestamp();
          logger.info("Compression reached");
        }
        break;
      case OUT:
        if (VISION.startSide == StartSide.LEFT) {
          strafe = STRAFE_OUTPUT * -1;
        } else {
          strafe = STRAFE_OUTPUT;
        }
        DRIVE.drive(0.0, strafe, 0.0);
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
    DRIVE.stop();
  }

  private enum DriveState {
    PLACE,
    OUT,
    DONE;

    DriveState() {}
  }
}
