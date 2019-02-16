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

  private double cameraPositionBearing = -90.0;
  private double cameraX = 0.0;
  private double cameraY = -9.0;
  private double targetYaw;

  public CalculateTwistCommand() {
  }

  @Override
  protected void initialize() {
    targetYaw = VISION.getTargetYaw();
    logger.debug("pyeye bearing={} range={}", VISION.getRawBearing(), VISION.getRawRange());
    logger.debug("current gyro = {}", DRIVE.getGyro().getAngle());

    TwistCalculator twistCalculator =
        new TwistCalculator(
            VISION.getRawBearing(),
            VISION.getRawRange(),
            cameraX,
            cameraY,
            cameraPositionBearing,
            DRIVE.getGyro().getAngle(),
            targetYaw);


    VISION.setCorrectedHeading(twistCalculator.getHeading());
    VISION.setCorrectedRange(twistCalculator.getRange());
  }
}
