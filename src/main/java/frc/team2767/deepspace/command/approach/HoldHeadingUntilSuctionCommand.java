package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
  private static final double HATCH_SEAL_GOOD_ENOUGH = 7.5;
  private static final double FWD_SCALE = 0.2;
  private static final double DRIVE_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double OUT_TIME_SEC = 0.6;
  private static final double AUTON_DRIVE_PERCENT = 0.25;
  private static DriverControls controls;
  private static DriveState driveState;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double initialPressure;
  private double forward;
  private ExpoScale driveExpo;
  private double currentPressure;
  private double outInitTime;
  private boolean isAuton;

  public HoldHeadingUntilSuctionCommand() {
    requires(DRIVE);
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
  }

  @Override
  protected void initialize() {
    isAuton = DriverStation.getInstance().isAutonomous();
    controls = Robot.CONTROLS.getDriverControls();
    driveState = DriveState.SEAL;
    initialPressure = VACUUM.getPressure();
    logger.info("Hold Heading, Initial Pressure: {}", initialPressure);
  }

  @Override
  protected void execute() {
    switch (driveState) {
      case SEAL:
        forward =
            isAuton ? -AUTON_DRIVE_PERCENT : driveExpo.apply(controls.getForward()) * FWD_SCALE;
        DRIVE.drive(forward, 0.0, 0.0);
        currentPressure = VACUUM.getPressure();
        if ((currentPressure - initialPressure) > HATCH_SEAL_GOOD_ENOUGH) {
          driveState = DriveState.OUT;
          VISION.enableLights(false);
          SmartDashboard.putBoolean("Game/haveHatch", true);
          outInitTime = Timer.getFPGATimestamp();
          logger.info("Have Seal");
        }
        break;
      case OUT:
        forward =
            isAuton ? AUTON_DRIVE_PERCENT : driveExpo.apply(controls.getForward()) * FWD_SCALE;
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
