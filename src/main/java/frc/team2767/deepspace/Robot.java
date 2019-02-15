package frc.team2767.deepspace;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.team2767.deepspace.control.Controls;
import frc.team2767.deepspace.subsystem.*;
import frc.team2767.deepspace.subsystem.health.HealthCheckSubsystem;
import frc.team2767.deepspace.subsystem.safety.SafetySubsystem;
import frc.team2767.deepspace.subsystem.safety.VacuumSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.trapper.Session;

public class Robot extends TimedRobot {
  // Instantiate this before Subsystems because they use telemetry service.
  public static final TelemetryService TELEMETRY = new TelemetryService(TelemetryController::new);

  public static DriveSubsystem DRIVE;
  public static VisionSubsystem VISION;
  public static ElevatorSubsystem ELEVATOR;
  public static BiscuitSubsystem BISCUIT;
  public static IntakeSubsystem INTAKE;
  public static HealthCheckSubsystem HEALTHCHECK;
  public static SafetySubsystem SAFETY;
  public static VacuumSubsystem VACUUM;

  public static Controls CONTROLS;

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void robotInit() {
    DRIVE = new DriveSubsystem();
    VISION = new VisionSubsystem();
    ELEVATOR = new ElevatorSubsystem();
    BISCUIT = new BiscuitSubsystem();
    INTAKE = new IntakeSubsystem();
    SAFETY = new SafetySubsystem();
    HEALTHCHECK = new HealthCheckSubsystem();
    VACUUM = new VacuumSubsystem();

    // Controls initialize Commands so this should be instantiated last to prevent
    // NullPointerExceptions in commands that require() Subsystems above.
    CONTROLS = new Controls();

    Session.INSTANCE.setBaseUrl("https://keeper.strykeforce.org");

    DRIVE.zeroYawEncoders();
    DRIVE.zeroGyro();
    TELEMETRY.start();

    //    new SmartDashboardControls();
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }
}
