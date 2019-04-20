package frc.team2767.deepspace.motion;

import edu.wpi.first.wpilibj.Notifier;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.Wheel;

public class PathController implements Runnable {

  private static final int NUM_WHEELS = 4;
  private static final int TICKS_PER_INCH = 2300;
  private static final DriveSubsystem DRIVE = Robot.DRIVE;

  @SuppressWarnings("FieldCanBeLocal")
  private static final double yawKp = 0.03; // 0.03

  private static final double yawHoldKp = 0.01;

  private static final double percentToDone = 0.60;
  private static final double DT = 0.04;
  private static final double GOOD_ENOUGH_YAW = 0.5;
  //  private static final double RATE_CAP = 0.35;
  //  private static final RateLimit rateLimit = new RateLimit(0.015);
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final int PID = 0;
  private double yawDelta;
  private double currentYawTarget;
  private Trajectory trajectory;
  private Notifier notifier;
  private Wheel[] wheels;
  private States state;
  private double maxVelocityInSec;
  private double targetYaw;
  private int iteration;
  private int[] start;
  private boolean isYawing = true;

  public PathController(String pathName, double targetYaw) {
    this.targetYaw = targetYaw;
    wheels = DRIVE.getAllWheels();
    File csvFile = new File("home/lvuser/deploy/paths/" + pathName + ".pf1.csv");

    trajectory = new Trajectory(csvFile);
  }

  public void start() {
    start = new int[4];
    notifier = new Notifier(this);
    notifier.startPeriodic(DT);
    state = States.STARTING;
  }

  public boolean isFinished() {
    return state == States.STOPPED;
  }

  @Override
  public void run() {

    switch (state) {
      case STARTING:
        logState();
        double ticksPerSecMax = wheels[0].getDriveSetpointMax() * 10.0;
        maxVelocityInSec = ticksPerSecMax / TICKS_PER_INCH;
        iteration = 0;
        DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);

        for (int i = 0; i < NUM_WHEELS; i++) {
          start[i] = wheels[i].getDriveTalon().getSelectedSensorPosition(PID);
        }

        currentYawTarget = Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360);
        yawDelta = (targetYaw - currentYawTarget) / (trajectory.length() * percentToDone);

        logger.debug("yaw delta = {}", yawDelta);

        logInit();
        state = States.RUNNING;
        break;
      case RUNNING:
        if (iteration == trajectory.length() - 1) {
          state = States.STOPPING;
        }

        if (isYawing) {
          currentYawTarget += yawDelta;
        } else {
          currentYawTarget = targetYaw;
        }

        Trajectory.Segment segment = trajectory.getIteration(iteration);

        double setpointVelocity = (segment.velocity) / maxVelocityInSec;

        double forward = Math.cos(segment.heading) * setpointVelocity;
        double strafe = Math.sin(segment.heading) * setpointVelocity;

        double yaw = (isYawing ? yawKp * getYawError() : yawHoldKp * getYawError());

        if (forward > 1d || strafe > 1d) logger.warn("forward = {} strafe = {}", forward, strafe);

        DRIVE.drive(forward, strafe, yaw);
        iteration++;
        break;
      case STOPPING:
        DRIVE.setDriveMode(SwerveDrive.DriveMode.OPEN_LOOP);
        logState();
        state = States.STOPPED;
        break;
      case STOPPED:
        logState();
        DRIVE.stop();
        notifier.close();
        break;
    }
  }

  private void logState() {
    logger.info("{}", state);
  }

  private void logInit() {
    logger.info(
        "Path start yawKp = {} targetYaw = {} maxVelocity in/s = {}",
        yawKp,
        targetYaw,
        maxVelocityInSec);
  }

  public double getYawError() {
    if (!isYawing
        || Math.abs(targetYaw - Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360))
            < GOOD_ENOUGH_YAW) {
      isYawing = false;
      return targetYaw - (Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0));
    }

    return currentYawTarget - (Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0));
  }

  private double distanceError(double position) {
    return TICKS_PER_INCH * position - getDistance();
  }

  private double getDistance() {
    double distance = 0;
    for (int i = 0; i < NUM_WHEELS; i++) {
      distance += Math.abs(wheels[i].getDriveTalon().getSelectedSensorPosition(PID) - start[i]);
    }
    distance /= 4;
    return distance;
  }

  public void interrupt() {
    logger.info("interrupted");
    state = States.STOPPED;
  }
}
