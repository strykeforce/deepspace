package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.team2767.deepspace.Robot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class VacuumSubsystem extends Subsystem {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  String PREFS_NAME = "VacuumSubsystem/Settings/";
  int BACKUP = 2767;
  private final int VACUUM_ID = 60;
  public static double kBallPressure;
  public static double kHatchPressure;
  public static double kClimbPressure;

  private int goodEnough = 100;
  private int setpoint;

  private Solenoid tridentSolenoid;
  private Solenoid climbSolenoid;
  private Solenoid pumpSolenoid;

  private TalonSRX vacuum = new TalonSRX(VACUUM_ID);

  public VacuumSubsystem() {
    tridentSolenoid = new Solenoid(0, Valve.TRIDENT.ID);
    pumpSolenoid = new Solenoid(0, Valve.PUMP.ID);
    climbSolenoid = new Solenoid(0, Valve.CLIMB.ID);

    pumpSolenoid.set(true);
    configTalon();
    vacuumPreferences();
  }

  //        Count	  psi	  in Hg
  //        500	    6.4	  13
  //        600	    7.9	  16
  //        700	    9.3	  19
  //        800	    10.8	22
  //        900	    11.8	24

  @SuppressWarnings("Duplicates")
  private void configTalon() {
    // FIXME: set max fwd/rev voltage

    TalonSRXConfiguration vacuumConfig = new TalonSRXConfiguration();
    vacuumConfig.primaryPID.selectedFeedbackSensor = FeedbackDevice.Analog;
    vacuumConfig.continuousCurrentLimit = 25;
    vacuumConfig.peakCurrentDuration = 40;
    vacuumConfig.peakCurrentLimit = 30;
    vacuumConfig.slot0.kP = 16;
    vacuumConfig.slot0.kI = 0;
    vacuumConfig.slot0.kD = 150;
    vacuumConfig.slot0.kF = 0;
    vacuumConfig.slot0.integralZone = 0;
    vacuumConfig.slot0.allowableClosedloopError = 0;
    vacuumConfig.voltageCompSaturation = 12;
    vacuumConfig.voltageMeasurementFilter = 32;
    vacuumConfig.peakOutputForward = 1.0;
    vacuumConfig.peakOutputReverse = 0.0;

    vacuum.configAllSettings(vacuumConfig);
    vacuum.enableCurrentLimit(true);
    vacuum.enableVoltageCompensation(true);
    logger.debug("configured vacuum talon");

    TelemetryService telemetryService = Robot.TELEMETRY;
    telemetryService.stop();
    telemetryService.register(new TalonItem(vacuum, "Vacuum"));
  }

  private void vacuumPreferences() {
    kBallPressure = (int) getPreference("ball_pressure", 500);
    kHatchPressure = (int) getPreference("hatch_pressure", 600);
    kClimbPressure = (int) getPreference("climb_pressure", 900);
  }

  private double getPreference(String name, double defaultValue) {
    String prefName = PREFS_NAME + name;
    Preferences preferences = Preferences.getInstance();
    if (!preferences.containsKey(prefName)) {
      preferences.putDouble(prefName, defaultValue);
    }
    double pref = preferences.getDouble(prefName, BACKUP);
    logger.info("{}={}", name, pref);
    return pref;
  }

  public Solenoid getTridentSolenoid() {
    return tridentSolenoid;
  }

  public Solenoid getPumpSolenoid() {
    return pumpSolenoid;
  }

  public Solenoid getClimbSolenoid() {
    return climbSolenoid;
  }

  public void setSolenoid(Valve valve, boolean state) {
    logger.debug("setting {} to {}", valve, state);
    switch (valve) {
      case CLIMB:
        climbSolenoid.set(state);
        return;
      case PUMP:
        pumpSolenoid.set(state);
        return;
      case TRIDENT:
        tridentSolenoid.set(state);
        return;
      default:
        logger.warn("could not set {} to {}", valve, state);
    }
  }

  public boolean onTarget(double pressure) {
    if (Math.abs(vacuum.getSelectedSensorPosition() - pressure) < goodEnough) {
      logger.debug("on target");
      return true;
    }
    return false;
  }

  public void runOpenLoop(double setpoint) {
    logger.debug("running vacuum at {}", setpoint);
    vacuum.set(ControlMode.PercentOutput, setpoint);
  }

  public int getPressure() {
    return vacuum.getSelectedSensorPosition();
  }

  public void setPressure(double pressure) {
    setpoint = (int) (35.5 * pressure + 32);
    logger.debug("setting pressure to {}", setpoint);
    vacuum.set(ControlMode.Position, setpoint);
  }

  public void stop() {
    logger.debug("stop pump");
    vacuum.set(ControlMode.Position, 0);
  }

  @Override
  protected void initDefaultCommand() {}

  public enum Valve {
    TRIDENT(0),
    CLIMB(2),
    PUMP(1);

    public final int ID;

    Valve(int id) {
      this.ID = id;
    }
  }
}
