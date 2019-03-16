package frc.team2767.deepspace.command.approach;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.PIDCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

public class YawToTargetCommand extends PIDCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;
  private static final int STABLE_THRESHOLD = 2;
  private final AHRS gyro = DRIVE.getGyro();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private int count;
  private PIDController controller;

  public YawToTargetCommand() {
    super(0.01, 0.0, 0.0, DRIVE);
    setInputRange(-180.0, 180.0);
    controller = getPIDController();
    controller.setContinuous();
    controller.setOutputRange(-1.0, 1.0);
    controller.setAbsoluteTolerance(1.0);
    count = 0;
    logger.debug("construct yaw command");
  }

  @Override
  protected void initialize() {
    double setpoint =
        Math.IEEEremainder(VISION.getCorrectedHeading() - VISION.getCameraPositionBearing(), 360);
    controller.setSetpoint(setpoint);
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    logger.debug(
        "start angle = {} setpoint = {}", returnPIDInput(), getPIDController().getSetpoint());
  }

  @Override
  protected double returnPIDInput() {
    return Math.IEEEremainder(gyro.getAngle(), 360.0);
  }

  @Override
  protected void usePIDOutput(double output) {
    DRIVE.drive(0.0, 0.0, output);
  }

  @SuppressWarnings("Duplicates")
  @Override
  protected boolean isFinished() {
    if (count > STABLE_THRESHOLD) {
      count = 0;
      return true;
    }

    if (getPIDController().onTarget()) {
      count++;
    }

    return false;
  }

  @Override
  protected void end() {
    logger.debug(
        "end angle = {} error = {}",
        returnPIDInput(),
        Math.IEEEremainder(getPIDController().getSetpoint() - DRIVE.getGyro().getAngle(), 360));
  }
}
