package frc.team2767.deepspace;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.team2767.deepspace.control.Controls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.trapper.Session;

public class Robot extends TimedRobot {
  // Instantiate this before Subsystems because they use telemetry service.
  public static final TelemetryService TELEMETRY = new TelemetryService(TelemetryController::new);

  public static final DriveSubsystem DRIVE = new DriveSubsystem();

  // Controls initialize Commands so this should be instantiated last to prevent
  // NullPointerExceptions in commands that require() Subsystems above.
  public static final Controls CONTROLS = new Controls();

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final NetworkTableInstance instance = NetworkTableInstance.getDefault();
  private NetworkTable table;

  @Override
  public void robotInit() {
    table = instance.getTable("Shuffleboard");
    Session.INSTANCE.setBaseUrl("https://keeper.strykeforce.org");
    DRIVE.zeroAzimuthEncoders();
    DRIVE.zeroGyro();
    TELEMETRY.start();
  }

  @Override
  public void teleopPeriodic() {
    NetworkTableEntry bearing = table.getEntry("bearing");
    NetworkTableEntry range = table.getEntry("range");

    logger.debug("bearing={} range={}", bearing.getDouble(2767.0), range.getDouble(2767.0));
    Scheduler.getInstance().run();
  }
}
