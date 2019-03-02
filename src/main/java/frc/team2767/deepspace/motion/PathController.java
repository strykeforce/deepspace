package frc.team2767.deepspace.motion;

import static frc.team2767.deepspace.subsystem.DriveSubsystem.TICKS_PER_INCH;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Preferences;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.swerve.Wheel;

public class PathController implements Runnable {

  private static final int NUM_WHEELS = 4;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final int PID = 0;

  @SuppressWarnings("FieldCanBeLocal")
  private final double accelerationKf = 0.0;

  private SwerveDrive DRIVE;

  @SuppressWarnings("FieldCanBeLocal")
  private double distanceKp;

  @SuppressWarnings("FieldCanBeLocal")
  private double yawKp;

  private Preferences preferences;
  private Trajectory trajectory;
  private Notifier notifier;
  private Wheel[] wheels;
  private States state;
  private double maxVelocityInSec;
  private double targetYaw;
  private double DT = 0.05;
  private int iteration;
  private int[] start;

  public PathController(SwerveDrive swerveDrive, String pathName, double targetYaw) {
    DRIVE = swerveDrive;
    this.targetYaw = targetYaw;
    wheels = DRIVE.getWheels();
    preferences = Preferences.getInstance();
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
        setPreferences();
        maxVelocityInSec = ticksPerSecMax / DriveSubsystem.TICKS_PER_INCH;
        iteration = 0;
        DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);

        for (int i = 0; i < NUM_WHEELS; i++) {
          start[i] = wheels[i].getDriveTalon().getSelectedSensorPosition(PID);
        }

        logInit();
        state = States.RUNNING;
        break;
      case RUNNING:
        logState();
        if (iteration == trajectory.length() - 1) {

          state = States.STOPPING;
        }

        Trajectory.Segment segment = trajectory.getIteration(iteration);
        double desiredVelocity = segment.velocity / (maxVelocityInSec);
        double setpointVelocity =
            desiredVelocity
                + distanceKp * distanceError(segment.position)
                + accelerationKf * segment.acceleration;
        double forward = Math.cos(segment.heading) * setpointVelocity;
        double strafe = Math.sin(segment.heading) * setpointVelocity;
        double yaw =
            -yawKp
                * (Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0)
                    - Math.toDegrees(targetYaw));
        logger.debug(
            "{} : x={} y={} forward = {} strafe = {}, dist err = {} yaw = {}",
            iteration,
            segment.x,
            segment.y,
            forward,
            strafe,
            distanceError(segment.position),
            yaw);

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

  private void setPreferences() {
    yawKp = preferences.getDouble("PathController/pathYawKp", 0.01);
    distanceKp = preferences.getDouble("PathController/pathDistKp", 0.000001);
  }

  private void logInit() {
    logger.info(
        "Path start yawKp = {} distKp = {} targetYaw = {} maxVelocity in/s = {}",
        yawKp,
        distanceKp,
        targetYaw,
        maxVelocityInSec);
  }

  private double distanceError(double position) {
    double desired = TICKS_PER_INCH * position;
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

  public void interrupt() {
    logger.info("interrupted");
    state = States.STOPPED;
  }
}
