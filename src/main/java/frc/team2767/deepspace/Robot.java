package frc.team2767.deepspace;

import ch.qos.logback.classic.util.ContextInitializer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.command.sequences.pickup.SandstormHatchPickupCommandGroup;
import frc.team2767.deepspace.control.Controls;
import frc.team2767.deepspace.subsystem.*;
import frc.team2767.deepspace.subsystem.safety.SafetySubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.trapper.Session;

public class Robot extends TimedRobot {
  // Instantiate this before Subsystems because they use telemetry service.
  public static TelemetryService TELEMETRY;
  public static VisionSubsystem VISION;
  public static DriveSubsystem DRIVE;
  public static ElevatorSubsystem ELEVATOR;
  public static BiscuitSubsystem BISCUIT;
  public static IntakeSubsystem INTAKE;
  public static SafetySubsystem SAFETY;
  public static VacuumSubsystem VACUUM;
  public static ClimbSubsystem CLIMB;

  public static Controls CONTROLS;
  private static boolean isEvent;
  private static CommandGroup getHatch;

  private Logger logger;

  public static boolean isEvent() {
    return isEvent;
  }

  @Override
  public void robotInit() {
    DigitalInput di = new DigitalInput(7);
    isEvent = di.get();

    // logging configuration needs to happen before any loggers are created.
    if (isEvent) {
      System.out.println("Event flag removed - switching logging to log file");
      System.setProperty(ContextInitializer.CONFIG_FILE_PROPERTY, "logback-event.xml");
    }
    logger = LoggerFactory.getLogger(this.getClass());

    TELEMETRY = new TelemetryService(TelemetryController::new);
    DRIVE = new DriveSubsystem();
    VISION = new VisionSubsystem();
    ELEVATOR = new ElevatorSubsystem();
    BISCUIT = new BiscuitSubsystem();
    INTAKE = new IntakeSubsystem();
    SAFETY = new SafetySubsystem();
    VACUUM = new VacuumSubsystem();
    CLIMB = new ClimbSubsystem();

    // Controls initialize Commands so this should be instantiated last to prevent
    // NullPointerExceptions in commands that require() Subsystems above.
    CONTROLS = new Controls();

    getHatch = new SandstormHatchPickupCommandGroup();

    Session.INSTANCE.setBaseUrl("https://keeper.strykeforce.org");

    DRIVE.zeroYawEncoders();
    ELEVATOR.zero();
    BISCUIT.zero();
    INTAKE.zero();

    if (!isEvent) {
      TELEMETRY.start();
    }

    SmartDashboard.putBoolean("Game/SandstormPickUp", false);

    //    new SmartDashboardControls();
  }

  @Override
  public void autonomousInit() {
    DRIVE.setAngleAdjustment(true);
    getHatch.start();
  }

  @Override
  public void teleopInit() {
    VACUUM.setSolenoidsState(VacuumSubsystem.SolenoidStates.GAME_PIECE_PICKUP);
  }

  @Override
  public void disabledPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }
}
