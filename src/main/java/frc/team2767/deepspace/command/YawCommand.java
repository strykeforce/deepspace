package frc.team2767.deepspace.command;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.PIDCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;

// This command needs to be PID tuned using the SmartDashboard and then integrated
// into your sequences. You have a couple of ways you can do it:
//
// - add a setpoint parameter to the constructor (static)
// - pull the setpoint in the initialize() method (dynamic)
//
// in either case, use this with: setSetpoint(double setpoint)

public class YawCommand extends PIDCommand {

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private final AHRS gyro = DRIVE.getGyro();
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private static final int STABLE_THRESHOLD = 4;
  private int count;

  public YawCommand() {
    this(0.0);
  }

  public YawCommand(double yawSetpoint) {
    super(0.018, 0.0, 0.0, DRIVE);
    setInputRange(-180.0, 180.0);
    PIDController controller = getPIDController();
    controller.setContinuous();
    controller.setOutputRange(-1.0, 1.0);
    controller.setAbsoluteTolerance(2.0);
    controller.setSetpoint(yawSetpoint);
    count = 0;
    logger.debug("construct yaw command");
  }

  @Override
  protected void initialize() {
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    logger.debug("start angle = {}", returnPIDInput());
  }

  @Override
  protected double returnPIDInput() {
    return Math.IEEEremainder(gyro.getAngle(), 360.0);
  }

  @Override
  protected void usePIDOutput(double output) {
    DRIVE.drive(0.0, 0.0, output);
  }

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
    logger.debug("end angle = {}", returnPIDInput());
  }
}
