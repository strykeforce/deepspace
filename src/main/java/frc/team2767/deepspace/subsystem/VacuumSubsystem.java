package frc.team2767.deepspace.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.team2767.deepspace.Robot;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.thirdcoast.telemetry.TelemetryService;
import org.strykeforce.thirdcoast.telemetry.item.TalonItem;

public class VacuumSubsystem extends Subsystem {

  private static final double COUNTS_PER_INHG = 35.533;
  private static final double COUNTS_OFFSET = 31.98;
  private static final double TEMP_OFFSET = .5;
  private static final double VOLTS_PER_CELSIUS = 0.01;
  public static final double TEMP_LIMIT = 93.33;
  public static double kBallPressureInHg;
  public static double kHatchPressureInHg;
  public static double kClimbPressureInHg;
  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private final int VACUUM_ID = 60;
  private final int STABLE_THRESHOLD = 4;
  private final int TEMPERATURE_PIN = 0;
  String PREFS_NAME = "VacuumSubsystem/Settings/";
  int BACKUP = 2767;
  private int goodEnough = 100;
  private int setpointCounts;
  private int stableCount;
  private Solenoid tridentSolenoid;
  private Solenoid climbSolenoid;
  private Solenoid pumpSolenoid;

  private TalonSRX vacuum = new TalonSRX(VACUUM_ID);
  private AnalogInput analogInput;

  public VacuumSubsystem() {

    stableCount = 0;
    setpointCounts = 0;
    tridentSolenoid = new Solenoid(0, Valve.TRIDENT.ID);
    pumpSolenoid = new Solenoid(0, Valve.PUMP.ID);
    climbSolenoid = new Solenoid(0, Valve.CLIMB.ID);

    pumpSolenoid.set(true);
    climbSolenoid.set(false);
    tridentSolenoid.set(false);

    analogInput = new AnalogInput(0);
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

  public List<TalonSRX> getTalons() {
    return List.of(vacuum);
  }

  private void vacuumPreferences() {
    kBallPressureInHg = (int) getPreference("ball_pressure_inHg", 13.17);
    kHatchPressureInHg = (int) getPreference("hatch_pressure_inHg", 16);
    kClimbPressureInHg = (int) getPreference("climb_pressure_inHg", 24.4);
  }

  @SuppressWarnings("Duplicates")
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

  @Override
  public void periodic() {
    SmartDashboard.putBoolean("Game/onTarget", onTarget());
    if (!Robot.isEvent()) SmartDashboard.putNumber("Game/Temperature", getPumpTemperature());
    if (getPumpTemperature() > TEMP_LIMIT) {
      logger.error("Vacuum overheating!");
      setPeakOutput(0);
    }
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
    logger.info("setting {} to {}", valve, state);
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

  public double getPumpTemperature() {
    double temp = (analogInput.getVoltage() - TEMP_OFFSET) * VOLTS_PER_CELSIUS;
    return temp;
  }

  public boolean onTarget() {
    double error = Math.abs(vacuum.getSelectedSensorPosition() - setpointCounts);

    if (error > goodEnough) {
      stableCount = 0;
    } else {
      stableCount++;
    }
    if (stableCount > STABLE_THRESHOLD) {
      SmartDashboard.putBoolean("Game/onTarget", true);
      return true;
    }
    return false;
  }

  public void runOpenLoop(double setpoint) {
    logger.info("running vacuum at {}", setpoint);
    vacuum.set(ControlMode.PercentOutput, setpoint);
  }

  public void dump() {
    logger.info("vacuum pressure in inHg = {} counts = {}", getPressure(), getCounts());
  }

  public double getPressure() {
    return ((vacuum.getSelectedSensorPosition() - COUNTS_OFFSET) / COUNTS_PER_INHG);
    // return vacuum.getSelectedSensorPosition();
  }

  public int getCounts() {
    return vacuum.getSelectedSensorPosition();
  }

  public void setPressure(double pressure) {
    SmartDashboard.putBoolean("Game/onTarget", false);
    setpointCounts = (int) (COUNTS_PER_INHG * pressure + COUNTS_OFFSET);
    // setpointCounts = (int) (35.5 * pressure + 32);
    logger.info("setting pressure to {}", setpointCounts);
    vacuum.set(ControlMode.Position, setpointCounts);
  }

  public void setPeakOutput(double peakOutput) {
    vacuum.configPeakOutputForward(peakOutput, 0);
  }

  public void stop() {
    logger.info("stop pump");
    SmartDashboard.putBoolean("Game/onTarget", false);
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
