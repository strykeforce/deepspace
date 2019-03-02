package frc.team2767.deepspace.command.twist;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.ElevatorSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import frc.team2767.deepspace.util.TwistCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CalculateTwistCommand extends InstantCommand {

  private static final Logger logger = LoggerFactory.getLogger(ElevatorSubsystem.class);
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  public CalculateTwistCommand() {
    requires(DRIVE);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    double targetYaw = VISION.getTargetYaw();
    logger.info("pyeye bearing={} range={} current gyro = {}", VISION.getRawBearing(), VISION.getRawRange(),
            DRIVE.getGyro().getAngle());

    TwistCalculator twistCalculator =
        new TwistCalculator(
            VISION.getRawBearing(),
            VISION.getRawRange(),
            VISION.getCameraX(),
            VISION.getCameraY(),
            VISION.getCameraPositionBearing(),
            DRIVE.getGyro().getAngle(),
            targetYaw);

    VISION.setCorrectedHeading(twistCalculator.getHeading());
    VISION.setCorrectedRange(twistCalculator.getRange());
  }
}
