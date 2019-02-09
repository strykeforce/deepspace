package frc.team2767.deepspace.motion;

import edu.wpi.first.wpilibj.Notifier;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.Wheel;

import java.io.File;

public class PathController implements Runnable {

  private static final DriveSubsystem DRIVE = Robot.DriveSubsystem;
  private static final int NUM_WHEELS = 4;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @SuppressWarnings("FieldCanBeLocal")
  private final double DISTANCE_kP = 0.0000002;

  @SuppressWarnings("FieldCanBeLocal")
  private final double ACCELERATION_kF = 0.0;

  @SuppressWarnings("FieldCanBeLocal")
  private final double YAW_kP = 0.001;

  private final Wheel[] wheels;
  private final int PID = 0;
  private double maxVelocityInSec;
  private Trajectory trajectory;
  private String pathName;
  private Notifier notifier;
  private int iteration;
  private double targetYaw;
  private int[] start;
  private boolean running;
  private double DT = 0.05;

  public PathController(String pathName) {
    //    action = new Action("Path", )
    this.pathName = pathName;
    wheels = DRIVE.getAllWheels();
    start = new int[4];

    File csvFile = new File("home/lvuser/deploy/paths/" + pathName + ".pf1.csv");

    trajectory = new Trajectory(csvFile);
  }

  public void start(double targetYaw) {
    double ticksPerSecMax = wheels[0].getDriveSetpointMax() * 10.0;
    double TICKS_PER_INCH = DriveSubsystem.TICKS_PER_INCH;
    maxVelocityInSec = ticksPerSecMax / TICKS_PER_INCH;

    logger.debug("Path start");
    logger.debug("tps max = {}", ticksPerSecMax);
    logger.debug("maxVelocity in/s = {}", maxVelocityInSec);
    for (int i = 0; i < NUM_WHEELS; i++) {
      start[i] = wheels[i].getDriveTalon().getSelectedSensorPosition(PID);
    }

    this.targetYaw = targetYaw;
    notifier = new Notifier(this);
    notifier.startPeriodic(DT);
    iteration = 1;
    running = true;
  }

  public boolean isRunning() {
    return running;
  }

  @Override
  public void run() {
    if (iteration == trajectory.length()) {
      stop();
      return;
    }

    Trajectory.Segment segment = trajectory.getIteration(iteration);

    double desiredVelocity = segment.velocity / (maxVelocityInSec);

    double setpointVelocity =
        desiredVelocity
            + DISTANCE_kP * distanceError(segment.position)
            + ACCELERATION_kF * segment.acceleration;

    double forward = Math.cos(segment.heading) * setpointVelocity;
    double strafe = Math.sin(segment.heading) * setpointVelocity;

    logger.debug(
        "x={} y={} forward = {} strafe = {}, dist err = {}",
        segment.x,
        segment.y,
        forward,
        strafe,
        DISTANCE_kP * distanceError(segment.position));

    double yaw =
        YAW_kP
            * (Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0) - Math.toDegrees(targetYaw));

    if (forward > 1d || strafe > 1d) logger.warn("forward = {} strafe = {}", forward, strafe);

    DRIVE.drive(forward, strafe, yaw);
    iteration++;
  }

  public void stop() {
    if (!running) return;
    logger.info("FINISH path {}", pathName);
    DRIVE.stop();
    running = false;
  }

  private double distanceError(double position) {
    double desired = DriveSubsystem.TICKS_PER_INCH * position;
    return desired - getDistance();
  }

  private double getDistance() {
    double distance = 0;
    for (int i = 0; i < NUM_WHEELS; i++) {
      distance += Math.abs(wheels[i].getDriveTalon().getSelectedSensorPosition(PID) - start[i]);
    }
    distance /= 4;
    return distance;
  }
}
