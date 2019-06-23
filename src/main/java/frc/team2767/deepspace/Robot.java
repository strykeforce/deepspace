package frc.team2767.deepspace;

import ch.qos.logback.classic.util.ContextInitializer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.command.approach.sandstorm.SandstormCommandGroup;
import frc.team2767.deepspace.command.sequences.pickup.SandstormHatchPickupCommandGroup;
import frc.team2767.deepspace.control.AutonChooser;
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
  public static StartLevel startLevel = StartLevel.ONE;
  public static boolean isAuton;
  private static AutonChooser AUTON;
  private static boolean isEvent;
  private static CommandGroup sandstorm;
  private static CommandGroup noAutoSandstorm;
  private Logger logger;

  public static boolean isEvent() {
    return isEvent;
  }

  @Override
  public void robotInit() {
    isAuton = true;
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

    // not a subsystem
    AUTON = new AutonChooser();

    Session.INSTANCE.setBaseUrl("https://keeper.strykeforce.org");

    DRIVE.zeroYawEncoders();
    ELEVATOR.zero();
    BISCUIT.zero();
    INTAKE.zero();

    sandstorm = new SandstormCommandGroup();
    noAutoSandstorm = new SandstormHatchPickupCommandGroup();

    SmartDashboard.putBoolean("Game/SandstormPickUp", false);
    SmartDashboard.putBoolean("Game/haveHatch", false);
    SmartDashboard.putBoolean("Game/climbOnTarget", false);
    SmartDashboard.putBoolean("Game/climbPrecheck", false);

    // must be last
    if (!isEvent) {
      TELEMETRY.start();
    }
  }

  @Override
  public void disabledInit() {
    AUTON.reset();
  }

  @Override
  public void autonomousInit() {
    BISCUIT.setPosition(BISCUIT.getPosition());
    DRIVE.sandstormAxisFlip(true);
    DRIVE.setAngleAdjustment(true);
    if (isAuton) {
      //            sandstorm.start();
    } else {
      noAutoSandstorm.start();
    }
  }

  @Override
  public void teleopInit() {
    DRIVE.sandstormAxisFlip(false);
    VACUUM.setSolenoidsState(VacuumSubsystem.SolenoidStates.GAME_PIECE_PICKUP);
  }

  @Override
  public void disabledPeriodic() {
    AUTON.checkSwitch();
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

  public enum StartLevel {
    ONE,
    TWO;
  }
}
