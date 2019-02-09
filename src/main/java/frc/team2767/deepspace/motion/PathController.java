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

  private static SwerveDrive DRIVE;
  private static final int NUM_WHEELS = 4;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @SuppressWarnings("FieldCanBeLocal")
  private final double ACCELERATION_kF = 0.0;

  private final int PID = 0;

  @SuppressWarnings("FieldCanBeLocal")
  private double DISTANCE_kP;

  @SuppressWarnings("FieldCanBeLocal")
  private double YAW_kP;

  private Wheel[] wheels;
  private double maxVelocityInSec;
  private Trajectory trajectory;
  private Notifier notifier;
  private int iteration;
  private double targetYaw;
  private int[] start;
  private double DT = 0.05;

  private States state;

  private Preferences preferences;

  public PathController(SwerveDrive swerveDrive, String pathName) {
    DRIVE = swerveDrive;
    wheels = DRIVE.getWheels();
    preferences = Preferences.getInstance();
    File csvFile = new File("home/lvuser/deploy/paths/" + pathName + ".pf1.csv");

    trajectory = new Trajectory(csvFile);
  }

  public void start(double targetYaw) {
    start = new int[4];
    this.targetYaw = targetYaw;
    notifier = new Notifier(this);
    notifier.startPeriodic(DT);
    state = States.STARTING;
  }

  public boolean isRunning() {
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
        iteration = 1;
        DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);

        for (int i = 0; i < NUM_WHEELS; i++) {
          start[i] = wheels[i].getDriveTalon().getSelectedSensorPosition(PID);
        }

        logInit();
        state = States.RUNNING;
        break;
      case RUNNING:
        logState();
        if (iteration == trajectory.length()) {

          state = States.STOPPING;
        }

        Trajectory.Segment segment = trajectory.getIteration(iteration);
        double desiredVelocity = segment.velocity / (maxVelocityInSec);
        double setpointVelocity =
            desiredVelocity
                + DISTANCE_kP * distanceError(segment.position)
                + ACCELERATION_kF * segment.acceleration;
        double forward = Math.cos(segment.heading) * setpointVelocity;
        double strafe = Math.sin(segment.heading) * setpointVelocity;
        double yaw =
            -YAW_kP
                * (Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0)
                    - Math.toDegrees(targetYaw));
        logger.debug(
            "x={} y={} forward = {} strafe = {}, dist err = {} yaw = {}",
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
        DRIVE.stop();
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
    logger.debug("{}", state);
  }

  private void setPreferences() {
    YAW_kP = preferences.getDouble("PathController/pathYawKp", 0.01);
    DISTANCE_kP = preferences.getDouble("PathController/pathDistKp", 0.0000002);
  }

  private void logInit() {
    logger.debug("Path start");
    logger.debug("yawKp = {} distKp = {}", YAW_kP, DISTANCE_kP);
    logger.debug("targetYaw = {}", targetYaw);
    logger.debug("maxVelocity in/s = {}", maxVelocityInSec);
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
    logger.debug("interrupted");
    state = States.STOPPED;
  }
}
