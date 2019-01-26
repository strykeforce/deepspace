package frc.team2767.deepspace.command;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.util.TwistCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwistService extends InstantCommand {

  private static final DriveSubsystem swerve = Robot.DRIVE;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  protected void initialize() {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("Pyeye");
    NetworkTableEntry bearing = table.getEntry("camera_bearing");
    NetworkTableEntry range = table.getEntry("camera_range");
    double cameraAngle = (double) bearing.getNumber(0.0);
    double cameraRange = (double) range.getNumber(0.0);
    double cameraX = 13.5; // TODO: Update camera positions
    double cameraY = 0.0; // TODO: Update camera positions
    double swerveRotation = swerve.getGyro().getYaw();

    TwistCalculator twistCalculator =
        new TwistCalculator(cameraAngle, cameraRange, cameraX, cameraY, swerveRotation);

    //    logger.debug("range={} bearing={}", range.getDouble(2767), bearing.getDouble(2767));
    logger.debug("range={} heading={}", twistCalculator.getRange(), twistCalculator.getHeading());

    double targetYaw = -90.0;

    //    new TwistCommand(
    //        twistCalculator.getHeading(),
    //        (int) (DriveSubsystem.TICKS_PER_INCH * twistCalculator.getRange()),
    //        targetYaw);
  }
}
