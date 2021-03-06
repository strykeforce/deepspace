package frc.team2767.deepspace.command.teleop;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.Action;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterruptCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public InterruptCommand() {
    requires(DRIVE);
    requires(BISCUIT);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    logger.info("INTERRUPT INIT");
    if (DriverStation.getInstance().isAutonomous()) {
      DRIVE.sandstormAxisFlip(true);
    }

    DRIVE.setSlotConfig(DriveSubsystem.DriveTalonConfig.DRIVE_CONFIG);
    DRIVE.undoGyroOffset();
    BISCUIT.setMotionMagicAccel(BiscuitSubsystem.kSlowAccel);
    VISION.setAction(Action.PLACE);
    VISION.enableLights(false);
  }
}
