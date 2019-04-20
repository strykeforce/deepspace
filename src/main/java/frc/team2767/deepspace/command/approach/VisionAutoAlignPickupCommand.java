package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.control.DriverControls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import java.util.Set;
import java.util.function.DoubleSupplier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.swerve.SwerveDrive;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.grapher.Measure;
import org.strykeforce.thirdcoast.telemetry.item.Item;
import org.strykeforce.thirdcoast.util.ExpoScale;
import org.strykeforce.thirdcoast.util.RateLimit;

public class VisionAutoAlignPickupCommand extends Command implements Item {
  public static final double kP_STRAFE = 0.06; // 0.1
  private static final double DRIVE_EXPO = 0.5;
  private static final double YAW_EXPO = 0.5;
  private static final double DEADBAND = 0.05;
  private static final double kP_YAW = 0.01; // 0.00625 tuning for NT method, 0.01 pyeye
  private static final double MAX_YAW = 0.3;
  private static final double MIN_RANGE = 35.0;
  private static final double FWD_SCALE = 0.5;
  private static final double FWD_SCALE_FAST = 0.5;
  private static final double goodEnoughYaw = 1.5;
  private static final double AUTON_OUTPUT = -0.35;

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;

  private static double range;
  private static double strafeError;
  private static double yawError;
  private static DriverControls controls;
  private static double strafeCorrection;
  private final ExpoScale driveExpo;
  private final ExpoScale yawExpo;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private boolean isAuton;
  private double targetYaw;
  private boolean isGood = false;
  private RateLimit rateLimit;

  public VisionAutoAlignPickupCommand() {
    requires(DRIVE);
    this.driveExpo = new ExpoScale(DEADBAND, DRIVE_EXPO);
    this.yawExpo = new ExpoScale(DEADBAND, YAW_EXPO);
    rateLimit = new RateLimit(0.04); // 0.015

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(this);
  }

  @Override
  protected void initialize() {
    isAuton = DriverStation.getInstance().isAutonomous();
    SmartDashboard.putBoolean("Game/haveHatch", false);
    logger.info("Begin Vision Auto Align Pickup");
    controls = Robot.CONTROLS.getDriverControls();
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    strafeCorrection = VISION.getStrafeCorrection();
    if (VISION.direction == FieldDirection.LEFT) {
      targetYaw = -90.0;
    } else targetYaw = 90.0;
    logger.info("Target Yaw: {}", targetYaw);
  }

  @SuppressWarnings("Duplicates")
  @Override
  protected void execute() {
    // Pyeye Method:
    VISION.queryPyeye(); // gets corrected heading and range from NT
    range = VISION.getRawRange();
    isGood = range >= 0; // check if range is good (we have a target), not -1

    // Calculate Yaw Term based on gyro
    yawError = targetYaw - Math.IEEEremainder(DRIVE.getGyro().getAngle(), 360.0);
    double yaw = kP_YAW * yawError;
    if (yaw > MAX_YAW) yaw = MAX_YAW;
    if (yaw < -MAX_YAW) yaw = -MAX_YAW;

    // Determine if actual yaw is close enough to target
    boolean onTarget = Math.abs(yawError) <= goodEnoughYaw;

    double forward;
    // forward is still normal
    if (isAuton) {
      forward = AUTON_OUTPUT;
    } else if (isGood) {
      forward = driveExpo.apply(controls.getForward()) * FWD_SCALE;
    } else {
      forward = driveExpo.apply(controls.getForward()) * FWD_SCALE_FAST;
    }

    double strafe;
    strafeError = Math.sin(Math.toRadians(VISION.getRawBearing())) * range - strafeCorrection;
    // Only take over strafe control if pyeye has a target and the robot is straight to the field
    if (isGood && onTarget) strafe = strafeError * kP_STRAFE * forward;
    else strafe = driveExpo.apply(controls.getStrafe());

    DRIVE.drive(forward, rateLimit.apply(strafe), yaw);
  }

  @Override
  protected boolean isFinished() {
    return (range <= MIN_RANGE && isGood);
  }

  @Override
  protected void end() {
    logger.info("End Auto Align Pickup Vision");
  }

  private double deadband(double value) {
    if (Math.abs(value) < DEADBAND) return 0.0;
    return value;
  }

  @NotNull
  @Override
  public String getDescription() {
    return "VisionPickupCommand";
  }

  @Override
  public int getDeviceId() {
    return 0;
  }

  @NotNull
  @Override
  public Set<Measure> getMeasures() {
    return Set.of(Measure.ANGLE, Measure.RANGE);
  }

  @NotNull
  @Override
  public String getType() {
    return "VisionCommand";
  }

  @Override
  public int compareTo(@NotNull Item item) {
    return 0;
  }

  @NotNull
  @Override
  public DoubleSupplier measurementFor(@NotNull Measure measure) {
    switch (measure) {
      case ANGLE:
        return () -> yawError;
      case RANGE:
        return () -> strafeError;
        //      case COMPONENT_STRAFE:
        //        return () -> strafe;
      default:
        return () -> 2767;
    }
  }
}
