package frc.team2767.deepspace;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.control.Controls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.trapper.Session;

public class Robot extends TimedRobot {
  // Instantiate this before Subsystems because they use telemetry service.
  public static final TelemetryService TELEMETRY = new TelemetryService(TelemetryController::new);

  public static final DriveSubsystem DRIVE = new DriveSubsystem();
  public static final VisionSubsystem VISION = new VisionSubsystem();

  // Controls initialize Commands so this should be instantiated last to prevent
  // NullPointerExceptions in commands that require() Subsystems above.
  public static final Controls CONTROLS = new Controls();

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void robotInit() {

    Session.INSTANCE.setBaseUrl("https://keeper.strykeforce.org");

    DRIVE.zeroAzimuthEncoders();
    DRIVE.zeroGyro();
    TELEMETRY.start();

    //    new SmartDashboardControls();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }
}
