package frc.team2767.deepspace;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.team2767.deepspace.control.Controls;
import frc.team2767.deepspace.subsystem.DriveSubsystem;
import frc.team2767.deepspace.subsystem.CargoIntakeSubsystem;
import frc.team2767.deepspace.subsystem.VisionSubsystem;
import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryController;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.trapper.Session;

public class Robot extends TimedRobot {
  // Instantiate this before Subsystems because they use telemetry service.
  public static final TelemetryService TELEMETRY = new TelemetryService(TelemetryController::new);

  public static final CargoIntakeSubsystem CargoIntakeSubsystem = new CargoIntakeSubsystem();
  public static final DriveSubsystem DriveSubsystem = new DriveSubsystem();
  public static final VisionSubsystem VisionSubsystem = new VisionSubsystem();

  // Controls initialize Commands so this should be instantiated last to prevent
  // NullPointerExceptions in commands that require() Subsystems above.
  public static final Controls CONTROLS = new Controls();

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Override
  public void robotInit() {

    Session.INSTANCE.setBaseUrl("https://keeper.strykeforce.org");

    DriveSubsystem.zeroYawEncoders();
    DriveSubsystem.zeroGyro();
    TELEMETRY.start();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }
}
