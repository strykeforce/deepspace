package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.Wheel;

public class OpenLoopDriveUntilSuctionCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final double CLOSE_ENOUGH = 4; // inHg
  private static final int STABLE_COUNT = 3;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final double pressure;
  private int stableCounts = 0;
  private double velocity;
  private Wheel[] wheels;

  public OpenLoopDriveUntilSuctionCommand(double pressure, double velocity) {
    this(pressure, velocity, VISION.getCorrectedHeading());
  }

  public OpenLoopDriveUntilSuctionCommand(double pressure, double velocity, double heading) {
    wheels = DRIVE.getAllWheels();
    this.velocity = velocity;
    this.pressure = pressure;
    //    this.yaw = yaw;
    setTimeout(5.0);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    DRIVE.setDriveMode(SwerveDrive.DriveMode.OPEN_LOOP);
    double direction = 0.25;

    if (VISION.direction == FieldDirection.LEFT) {
      direction *= -1;
    }

    for (Wheel w : wheels) {
      w.set(direction, velocity);
    }
    stableCounts = 0;
  }

  @Override
  protected boolean isFinished() {
    if (Math.abs(pressure - VACUUM.getPressure()) < CLOSE_ENOUGH) stableCounts++;
    else stableCounts = 0;

    if (stableCounts > STABLE_COUNT || isTimedOut()) {
      DRIVE.stop();
      return true;
    }

    return false;
  }

  @Override
  protected void end() {
    if (isTimedOut()) {
      logger.info("Timed Out");
    } else logger.info("Pressure Reached");
  }
}
