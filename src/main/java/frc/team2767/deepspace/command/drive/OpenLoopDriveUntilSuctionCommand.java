package frc.team2767.deepspace.command.drive;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenLoopDriveUntilSuctionCommand extends Command {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VacuumSubsystem VACUUM = Robot.VACUUM;
  private static final double CLOSE_ENOUGH = 4; // inHg
  private static final int STABLE_COUNT = 3;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final double pressure;
  private int stableCounts = 0;
  private double forward;
  private double strafe;
  private double yaw;

  public OpenLoopDriveUntilSuctionCommand(double pressure, double forward, double strafe) {
    this(pressure, forward, strafe, 0.0);
  }

  public OpenLoopDriveUntilSuctionCommand(
      double pressure, double forward, double strafe, double yaw) {
    this.pressure = pressure;
    this.forward = forward;
    this.strafe = strafe;
    this.yaw = yaw;
    setTimeout(5.0);
    requires(DRIVE);
  }

  @Override
  protected void initialize() {
    stableCounts = 0;
  }

  @Override
  protected void execute() {
    DRIVE.drive(forward, strafe, yaw);
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
