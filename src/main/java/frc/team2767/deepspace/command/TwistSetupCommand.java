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
    double cameraPositionBearing = -90.0;
    double cameraX = 0.0;
    double cameraY = -9.0;

    logger.debug("pyeye bearing={} range={}", cameraAngle, cameraRange);
    logger.debug("current gyro = {}", DRIVE.getGyro().getAngle());

    //    double targetYaw = preferences.getDouble("targetYaw", 0);

    double targetYaw = 0.0;
    TwistCalculator twistCalculator =
        new TwistCalculator(
            cameraAngle,
            cameraRange,
            cameraX,
            cameraY,
            cameraPositionBearing,
            DRIVE.getGyro().getAngle(),
            targetYaw);
    //
    //    logger.debug("range={} heading={}", twistCalculator.getRange(),
    // twistCalculator.getHeading());

    logger.debug("targetYaw={}", targetYaw);

    double distanceSafetyAdjustment = preferences.getDouble("safetyDistance", 10.0);

    double cameraDistanceFromRobotEdge = 6.0;
    Command twist =
        new TwistCommand(
            twistCalculator.getHeading(),
            (int)
                (DriveSubsystem.TICKS_PER_INCH
                    * (twistCalculator.getRange()
                        - distanceSafetyAdjustment
                        - cameraDistanceFromRobotEdge)),
            targetYaw);

    //    Command twist = new TwistCommand(-118.0, (int) (DriveSubsystem.TICKS_PER_INCH * 80),
    // targetYaw);
    twist.start();
  }
}
