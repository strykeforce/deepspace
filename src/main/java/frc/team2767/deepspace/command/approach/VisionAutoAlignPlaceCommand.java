package frc.team2767.deepspace.command.approach;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.StartSide;
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
import org.strykeforce.thirdcoast.util.RateLimit;

public class VisionAutoAlignPlaceCommand extends Command implements Item {
  private static final double kP_FORWARD = -0.11; // 0.1
  private static final double kP_YAW = 0.01; // 0.00625 tuning for NT method, 0.01 pyeye
  private static final double MAX_YAW = 0.3;
  private static final double MIN_RANGE = 35.0;
  private static final double STRAFE_OUTPUT = 0.2;
  private static final double goodEnoughYaw = 1.5;

  private static final DriveSubsystem DRIVE = Robot.DRIVE;
  private static final VisionSubsystem VISION = Robot.VISION;

  private static double range;
  private static double strafe;
  private static double forwardError;
  private static double yawError;
  private static double forwardCorrection;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private double targetYaw;
  private boolean isGood = false;
  private RateLimit rateLimit;

  public VisionAutoAlignPlaceCommand() {
    requires(DRIVE);
    rateLimit = new RateLimit(0.05); // 0.015

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(this);
  }

  @Override
  protected void initialize() {
    SmartDashboard.putBoolean("Game/haveHatch", true);
    logger.info("Begin Vision Auto Align Place");
    DRIVE.setDriveMode(SwerveDrive.DriveMode.CLOSED_LOOP);
    forwardCorrection = VISION.getStrafeCorrection();
    if (VISION.startSide == StartSide.LEFT) {
      targetYaw = 180.0;
      strafe = STRAFE_OUTPUT;
    } else {
      targetYaw = 0.0;
      strafe = STRAFE_OUTPUT * -1;
    }
    logger.debug("strafe = {}", strafe);
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
    forwardError = Math.sin(Math.toRadians(VISION.getRawBearing())) * range - forwardCorrection;
    // Only take over strafe control if pyeye has a target and the robot is straight to the field
    if (isGood && onTarget) forward = forwardError * kP_FORWARD * strafe;
    else forward = 0;

    DRIVE.drive(rateLimit.apply(forward), strafe, yaw);
  }

  @Override
  protected boolean isFinished() {
    return (range <= MIN_RANGE && isGood);
  }

  @Override
  protected void end() {
    logger.info("End Auto Align Place Vision");
  }

  @NotNull
  @Override
  public String getDescription() {
    return "VisionPlaceCommand";
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
        return () -> forwardError;
        //      case COMPONENT_STRAFE:
        //        return () -> strafe;
      default:
        return () -> 2767;
    }
  }
}
