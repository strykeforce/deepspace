package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.util.ExpoScale;

public class HoldHeadingUntilSuctionCommand extends Command {
  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static DriverControls controls;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static DriveState driveState;
  private static double initialPressure;
  private static double forward;
  private static ExpoScale driveExpo;
  private static double currentPressure;
  private static double outInitTime;

  private static final double HATCH_SEAL_GOOD_ENOUGH = 7.5;
  private static final double FWD_SCALE = 0.3;
  private static final double DRIVE_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double OUT_TIME_SEC = 0.25;

  public HoldHeadingUntilSuctionCommand() {
    requires(DRIVE);
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
  }

  @Override
  protected void initialize() {
    controls = Robot.CONTROLS.getDriverControls();
    driveState = DriveState.SEAL;
    initialPressure = VACUUM.getPressure();
    logger.info("Hold Heading, Initial Pressure: {}", initialPressure);
  }

  @Override
  protected void execute() {
    switch (driveState) {
      case SEAL:
        forward = driveExpo.apply(controls.getForward()) * FWD_SCALE;
        DRIVE.drive(forward, 0.0, 0.0);
        currentPressure = VACUUM.getPressure();
        if ((currentPressure - initialPressure) > HATCH_SEAL_GOOD_ENOUGH) {
          driveState = DriveState.OUT;
          VISION.enableLights(false);
          outInitTime = Timer.getFPGATimestamp();
          logger.info("Have Seal");
        }
        break;
      case OUT:
        forward = driveExpo.apply(controls.getForward()) * FWD_SCALE;
        DRIVE.drive(forward, 0.0, 0.0);
        if (Timer.getFPGATimestamp() - outInitTime > OUT_TIME_SEC) {
          driveState = DriveState.DONE;
          logger.info("Done Auto Hatch Pickup");
        }
        break;
    }
  }

  @Override
  protected boolean isFinished() {
    return driveState == DriveState.DONE;
  }

  private enum DriveState {
    SEAL,
    OUT,
    DONE;

    DriveState() {}
  }
}
