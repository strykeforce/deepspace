package frc.team2767.deepspace.command;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.util.TwistCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwistSetupCommand extends InstantCommand {

  private static final DriveSubsystem DRIVE = Robot.DriveSubsystem;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  public void initialize() {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("Pyeye");
    NetworkTableEntry bearing = table.getEntry("camera_bearing");
    NetworkTableEntry range = table.getEntry("camera_range");

    Preferences preferences = Preferences.getInstance();

    double cameraAngle = (double) bearing.getNumber(0.0);
    double cameraRange = (double) range.getNumber(0.0);
    double cameraX = 0.0;
    double cameraY = -9.0;
    double cameraPositionBearing = -90.0;
    double swerveRotation = DRIVE.getGyro().getYaw();

    TwistCalculator twistCalculator =
        new TwistCalculator(
            cameraAngle, cameraRange, cameraX, cameraY, cameraPositionBearing, swerveRotation);

    logger.debug("range={} heading={}", twistCalculator.getRange(), twistCalculator.getHeading());

    double targetYaw = preferences.getDouble("targetYaw", 0.0) + 90.0;
    logger.debug("targetYaw={}", targetYaw);

    Command twist =
        new TwistCommand(
            twistCalculator.getHeading(),
            (int) (DriveSubsystem.TICKS_PER_INCH * twistCalculator.getRange()),
            targetYaw);

    //    twist.start();
  }
}
